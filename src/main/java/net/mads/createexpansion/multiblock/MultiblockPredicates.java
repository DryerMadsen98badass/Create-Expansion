package net.mads.createexpansion.multiblock;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachineCasingBlock;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class MultiblockPredicates {
    public static final NeededAbilities needed = new NeededAbilities();
    private static final Set<MultiblockAbility> MACHINE_IO_ABILITIES = EnumSet.of(
            MultiblockAbility.ITEM_INPUT,
            MultiblockAbility.ITEM_OUTPUT,
            MultiblockAbility.FLUID_INPUT,
            MultiblockAbility.FLUID_OUTPUT,
            MultiblockAbility.ENERGY_INPUT,
            MultiblockAbility.ENERGY_OUTPUT,
            MultiblockAbility.KINETIC_INPUT,
            MultiblockAbility.KINETIC_OUTPUT,
            MultiblockAbility.IO_INTERFACE
    );

    private MultiblockPredicates() {
    }

    public static MultiblockPredicate air() {
        return (level, pos, state) -> state.isAir() ? MultiblockPredicate.Match.success() : MultiblockPredicate.Match.failed();
    }

    public static PredicateBuilder block(String blockId) {
        return new PredicateBuilder(MultiblockRegistry.id(blockId));
    }

    public static MultiblockPredicate or(MultiblockPredicate first, MultiblockPredicate second) {
        return (first instanceof RecipeTypeAwarePredicate || second instanceof RecipeTypeAwarePredicate)
                ? new RecipeAwareOrPredicate(first, second)
                : new OrPredicate(first, second);
    }

    public static MultiblockPredicate overlay(MultiblockPredicate predicate, String texture) {
        return new OverlayPredicate(predicate, MultiblockRegistry.id(texture));
    }

    public static PredicateBuilder where(String blockId) {
        return new PredicateBuilder(MultiblockRegistry.id(blockId));
    }

    public static MultiblockPredicate ability(MultiblockAbility ability) {
        return new AbilityPredicate(EnumSet.of(ability), false);
    }

    public static MultiblockPredicate ability(NeededAbilities needed) {
        return new NeededAbilityPredicate(Set.of());
    }

    public static MultiblockPredicate anyAbility(MultiblockAbility... abilities) {
        return new AbilityPredicate(EnumSet.copyOf(Arrays.asList(abilities)), true);
    }

    public static MultiblockPredicate tieredBlocks(TieredBlock... blocks) {
        return new TieredBlocksPredicate(List.of(blocks), false);
    }

    public static TieredBlock tiered(String blockId, MachineTier tier) {
        return new TieredBlock(MultiblockRegistry.id(blockId), tier);
    }

    public static MultiblockPredicate tieredMachineCasings() {
        List<TieredBlock> blocks = new ArrayList<>();
        for (MachineTier tier : MachineTier.ALL) {
            blocks.add(tiered(CreateExpansion.MOD_ID + ":" + tier.casingRegistryName(), tier));
        }
        return new TieredBlocksPredicate(blocks, true);
    }

    public static MultiblockPredicate anyMachineCasing() {
        return new AnyMachineCasingPredicate();
    }

    public record TieredBlock(ResourceLocation blockId, MachineTier tier) {
        boolean matches(BlockState state) {
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            return block == state.getBlock();
        }
    }

    public static final class NeededAbilities {
        private NeededAbilities() {
        }
    }

    public interface RecipeTypeAwarePredicate {
        MultiblockPredicate bindRecipeAbilities(Set<MultiblockAbility> abilities);

        boolean requiresRecipeAbilities();
    }

    public static final class PredicateBuilder implements MultiblockPredicate {
        private final ResourceLocation blockId;
        private final String countKey;
        private final Set<MultiblockAbility> requiredAbilities = EnumSet.noneOf(MultiblockAbility.class);
        private final Set<MultiblockAbility> anyAbilities = EnumSet.noneOf(MultiblockAbility.class);
        private int minimum = -1;
        private int maximum = -1;
        private MachineTier tier;

        private PredicateBuilder(ResourceLocation blockId) {
            this.blockId = blockId;
            this.countKey = "block:" + blockId + ":" + Integer.toHexString(System.identityHashCode(this));
        }

        public PredicateBuilder tier(MachineTier tier) {
            this.tier = tier;
            return this;
        }

        public PredicateBuilder ability(MultiblockAbility... abilities) {
            requiredAbilities.addAll(Arrays.asList(abilities));
            return this;
        }

        public PredicateBuilder anyAbility(MultiblockAbility... abilities) {
            anyAbilities.addAll(Arrays.asList(abilities));
            return this;
        }

        public PredicateBuilder min(int minimum) {
            if (minimum < 0) {
                throw new IllegalArgumentException("Minimum cannot be negative");
            }
            this.minimum = minimum;
            return this;
        }

        public PredicateBuilder max(int maximum) {
            if (maximum < 0) {
                throw new IllegalArgumentException("Maximum cannot be negative");
            }
            this.maximum = maximum;
            return this;
        }

        @Override
        public MultiblockPredicate.Match match(Level level, BlockPos pos, BlockState state) {
            if (BuiltInRegistries.BLOCK.get(blockId) != state.getBlock()) {
                return MultiblockPredicate.Match.failed();
            }

            if (requiredAbilities.isEmpty() && anyAbilities.isEmpty()) {
                return countedMatch(tier, Set.of());
            }

            if (!(level.getBlockEntity(pos) instanceof MultiblockPart part)) {
                return MultiblockPredicate.Match.failed();
            }

            if (!part.abilities().containsAll(requiredAbilities)) {
                return MultiblockPredicate.Match.failed();
            }

            if (!anyAbilities.isEmpty() && anyAbilities.stream().noneMatch(part::hasAbility)) {
                return MultiblockPredicate.Match.failed();
            }

            return countedMatch(lowestTier(tier, part.partTier()), part.abilities());
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            if (tier != null) {
                return MultiblockVisualization.SymbolInfo.tieredBlock(new TieredBlock(blockId, tier));
            }
            return MultiblockVisualization.SymbolInfo.block(blockId);
        }

        @Override
        public List<MultiblockPredicate.CountRequirement> countRequirements() {
            if (minimum < 0 && maximum < 0) {
                return List.of();
            }
            return List.of(new MultiblockPredicate.CountRequirement(countKey, blockId, Math.max(0, minimum), maximum));
        }

        private MultiblockPredicate.Match countedMatch(MachineTier tier, Set<MultiblockAbility> abilities) {
            return minimum >= 0 || maximum >= 0
                    ? MultiblockPredicate.Match.counted(tier, abilities, countKey)
                    : abilities.isEmpty()
                    ? (tier == null ? MultiblockPredicate.Match.success() : MultiblockPredicate.Match.tiered(tier))
                    : MultiblockPredicate.Match.abilities(abilities, tier);
        }
    }

    private record AbilityPredicate(Set<MultiblockAbility> abilities, boolean any) implements MultiblockPredicate {
        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            if (!(level.getBlockEntity(pos) instanceof MultiblockPart part)) {
                return Match.failed();
            }

            boolean matches = any
                    ? abilities.stream().anyMatch(part::hasAbility)
                    : part.abilities().containsAll(abilities);
            return matches ? Match.abilities(part.abilities(), part.partTier()) : Match.failed();
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return any
                    ? MultiblockVisualization.SymbolInfo.anyAbility(abilities)
                    : MultiblockVisualization.SymbolInfo.requiredAbility(abilities);
        }
    }

    private record NeededAbilityPredicate(Set<MultiblockAbility> acceptedAbilities, Set<MultiblockAbility> requiredAbilities) implements MultiblockPredicate, RecipeTypeAwarePredicate {
        private NeededAbilityPredicate(Set<MultiblockAbility> requiredAbilities) {
            this(MACHINE_IO_ABILITIES, requiredAbilities);
        }

        @Override
        public MultiblockPredicate bindRecipeAbilities(Set<MultiblockAbility> abilities) {
            Set<MultiblockAbility> accepted = abilities.isEmpty() ? MACHINE_IO_ABILITIES : EnumSet.copyOf(abilities);
            return new NeededAbilityPredicate(accepted, accepted);
        }

        @Override
        public boolean requiresRecipeAbilities() {
            return true;
        }

        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            if (!(level.getBlockEntity(pos) instanceof MultiblockPart part)) {
                return Match.failed();
            }

            boolean matches = acceptedAbilities.stream().anyMatch(part::hasAbility);
            return matches ? Match.abilities(part.abilities(), part.partTier()) : Match.failed();
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return MultiblockVisualization.SymbolInfo.anyAbility(acceptedAbilities);
        }
    }

    private record TieredBlocksPredicate(List<TieredBlock> blocks, boolean machineCasings) implements MultiblockPredicate {
        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            for (TieredBlock entry : blocks) {
                if (entry.matches(state)) {
                    return Match.tiered(entry.tier());
                }
            }

            return Match.failed();
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return machineCasings
                    ? MultiblockVisualization.SymbolInfo.machineCasings()
                    : MultiblockVisualization.SymbolInfo.tieredBlocks(blocks);
        }
    }

    private record OrPredicate(MultiblockPredicate left, MultiblockPredicate right) implements MultiblockPredicate {
        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            Match leftMatch = left.match(level, pos, state);
            return leftMatch.matches() ? leftMatch : right.match(level, pos, state);
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return mergeInfo(left.visualizationInfo(), right.visualizationInfo());
        }

        @Override
        public List<MultiblockPredicate.CountRequirement> countRequirements() {
            return mergeRequirements(left, right);
        }
    }

    private static final class RecipeAwareOrPredicate implements MultiblockPredicate, RecipeTypeAwarePredicate {
        private final MultiblockPredicate left;
        private final MultiblockPredicate right;

        private RecipeAwareOrPredicate(MultiblockPredicate left, MultiblockPredicate right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            Match leftMatch = left.match(level, pos, state);
            return leftMatch.matches() ? leftMatch : right.match(level, pos, state);
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return mergeInfo(left.visualizationInfo(), right.visualizationInfo());
        }

        @Override
        public MultiblockPredicate bindRecipeAbilities(Set<MultiblockAbility> abilities) {
            MultiblockPredicate boundLeft = bindIfRecipeAware(left, abilities);
            MultiblockPredicate boundRight = bindIfRecipeAware(right, abilities);
            return boundLeft == left && boundRight == right ? this : new RecipeAwareOrPredicate(boundLeft, boundRight);
        }

        @Override
        public boolean requiresRecipeAbilities() {
            return false;
        }

        private static MultiblockPredicate bindIfRecipeAware(MultiblockPredicate predicate, Set<MultiblockAbility> abilities) {
            return predicate instanceof RecipeTypeAwarePredicate recipeAware ? recipeAware.bindRecipeAbilities(abilities) : predicate;
        }

        private static boolean requiresRecipeAbilities(MultiblockPredicate predicate) {
            return predicate instanceof RecipeTypeAwarePredicate recipeAware && recipeAware.requiresRecipeAbilities();
        }

        @Override
        public List<MultiblockPredicate.CountRequirement> countRequirements() {
            return mergeRequirements(left, right);
        }
    }

    private static final class AnyMachineCasingPredicate implements MultiblockPredicate {
        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof MachineCasingBlock casing
                    ? Match.tiered(casing.tier())
                    : Match.failed();
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return MultiblockVisualization.SymbolInfo.machineCasings();
        }
    }

    private record OverlayPredicate(MultiblockPredicate predicate, ResourceLocation texture) implements MultiblockPredicate, RecipeTypeAwarePredicate {
        @Override
        public Match match(Level level, BlockPos pos, BlockState state) {
            return predicate.match(level, pos, state).withOverlay(texture);
        }

        @Override
        public MultiblockVisualization.SymbolInfo visualizationInfo() {
            return predicate.visualizationInfo();
        }

        @Override
        public List<MultiblockPredicate.CountRequirement> countRequirements() {
            return predicate.countRequirements();
        }

        @Override
        public MultiblockPredicate bindRecipeAbilities(Set<MultiblockAbility> abilities) {
            MultiblockPredicate bound = bindIfRecipeAware(predicate, abilities);
            return bound == predicate ? this : new OverlayPredicate(bound, texture);
        }

        @Override
        public boolean requiresRecipeAbilities() {
            return predicate instanceof RecipeTypeAwarePredicate recipeAware && recipeAware.requiresRecipeAbilities();
        }

        private static MultiblockPredicate bindIfRecipeAware(MultiblockPredicate predicate, Set<MultiblockAbility> abilities) {
            return predicate instanceof RecipeTypeAwarePredicate recipeAware ? recipeAware.bindRecipeAbilities(abilities) : predicate;
        }
    }

    public static MachineTier lowestTier(MachineTier current, MachineTier next) {
        if (current == null) {
            return next;
        }
        if (next == null) {
            return current;
        }
        return MachineTierStats.tierIndex(next) < MachineTierStats.tierIndex(current) ? next : current;
    }

    private static MultiblockVisualization.SymbolInfo mergeInfo(MultiblockVisualization.SymbolInfo left, MultiblockVisualization.SymbolInfo right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return left.merge(right);
    }

    private static List<MultiblockPredicate.CountRequirement> mergeRequirements(MultiblockPredicate left, MultiblockPredicate right) {
        List<MultiblockPredicate.CountRequirement> requirements = new ArrayList<>();
        requirements.addAll(left.countRequirements());
        requirements.addAll(right.countRequirements());
        return requirements.stream().filter(Objects::nonNull).toList();
    }
}
