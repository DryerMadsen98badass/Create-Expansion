package net.mads.createexpansion.multiblock;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeLogicDefinition;
import net.mads.createexpansion.recipe.CERecipeTypeDefinition;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class MultiblockDefinition {
    private final String id;
    private final String displayName;
    private final MultiblockControllerDefinition controllerDefinition;
    private final ResourceLocation controllerId;
    private final List<ResourceLocation> recipeTypes;
    private final List<ResourceLocation> logicIds;
    private final List<PatternVariant> variants;
    private final Map<Character, MultiblockPredicate> predicates;
    private final Set<MultiblockAbility> requiredRecipeAbilities;
    private final char controllerSymbol;
    private final MultiblockVisualization visualization;

    private MultiblockDefinition(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.controllerDefinition = builder.controllerDefinition;
        this.controllerId = builder.controllerId;
        this.recipeTypes = List.copyOf(builder.recipeTypes);
        this.logicIds = List.copyOf(builder.logicIds);
        this.predicates = Map.copyOf(builder.predicates);
        this.requiredRecipeAbilities = builder.requiredRecipeAbilities.isEmpty() ? Set.of() : EnumSet.copyOf(builder.requiredRecipeAbilities);
        this.controllerSymbol = builder.controllerSymbol;
        this.visualization = builder.visualization;
        this.variants = builder.variants.stream()
                .sorted(Comparator.comparingInt(PatternVariant::variantLevel).reversed())
                .toList();
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static Builder controller(String controllerId) {
        ResourceLocation id = MultiblockRegistry.id(controllerId);
        return new Builder(id.getPath()).controller(controllerId);
    }

    public static Builder controller(MultiblockControllerDefinition controller) {
        return new Builder(controller.registryName()).controller(controller);
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public ResourceLocation controllerId() {
        return controllerId;
    }

    public MultiblockControllerDefinition controller() {
        return controllerDefinition;
    }

    public List<ResourceLocation> recipeTypes() {
        return recipeTypes;
    }

    public List<ResourceLocation> logicIds() {
        return logicIds;
    }

    public List<PatternVariant> variants() {
        return variants;
    }

    public MultiblockVisualization visualization() {
        return visualization;
    }

    public List<MultiblockPredicate.CountRequirement> countRequirements(char symbol) {
        MultiblockPredicate predicate = predicates.get(symbol);
        return predicate == null ? List.of() : predicate.countRequirements();
    }

    public Set<MultiblockAbility> requiredRecipeAbilities() {
        return requiredRecipeAbilities;
    }

    public MultiblockMatchResult tryMatch(Level level, BlockPos controllerPos, Direction facing) {
        for (PatternVariant variant : variants) {
            MultiblockMatchResult result = tryMatchVariant(level, controllerPos, facing, variant);
            if (result.matched()) {
                return result;
            }
        }

        return MultiblockMatchResult.failed();
    }

    private MultiblockMatchResult tryMatchVariant(Level level, BlockPos controllerPos, Direction facing, PatternVariant variant) {
        for (LocalPos controllerLocalPos : findControllerPositions(variant)) {
            MultiblockMatchResult result = matchFromController(level, controllerPos, facing, variant, controllerLocalPos);
            if (result.matched()) {
                return result;
            }
        }

        return MultiblockMatchResult.failed();
    }

    private List<LocalPos> findControllerPositions(PatternVariant variant) {
        List<LocalPos> positions = new ArrayList<>();
        for (int x = 0; x < variant.layers().size(); x++) {
            MultiblockPattern.Row[] rows = variant.layers().get(x);
            for (int y = 0; y < rows.length; y++) {
                char[] symbols = rows[y].symbols();
                for (int z = 0; z < symbols.length; z++) {
                    if (symbols[z] == controllerSymbol) {
                        positions.add(new LocalPos(x, y, z));
                    }
                }
            }
        }

        if (positions.isEmpty()) {
            positions.add(new LocalPos(0, 0, 0));
        }
        return positions;
    }

    private MultiblockMatchResult matchFromController(Level level, BlockPos controllerPos, Direction facing, PatternVariant variant, LocalPos controllerLocalPos) {
        MachineTier formedTier = null;
        List<BlockPos> positions = new ArrayList<>();
        Map<MultiblockAbility, List<BlockPos>> abilityPositions = new EnumMap<>(MultiblockAbility.class);
        Map<String, Integer> countMatches = new HashMap<>();
        Map<BlockPos, ResourceLocation> overlays = new HashMap<>();

        for (int x = 0; x < variant.layers().size(); x++) {
            MultiblockPattern.Row[] rows = variant.layers().get(x);
            for (int y = 0; y < rows.length; y++) {
                char[] symbols = rows[y].symbols();
                for (int z = 0; z < symbols.length; z++) {
                    char symbol = symbols[z];
                    BlockPos worldPos = rotate(controllerPos, facing, x - controllerLocalPos.x, y - controllerLocalPos.y, z - controllerLocalPos.z);
                    MultiblockPredicate predicate = symbol == controllerSymbol ? MultiblockPredicates.block(controllerId.toString()) : predicates.get(symbol);
                    if (predicate == null) {
                        return MultiblockMatchResult.failed();
                    }

                    BlockState state = level.getBlockState(worldPos);
                    MultiblockPredicate.Match match = predicate.match(level, worldPos, state);
                    if (!match.matches()) {
                        return MultiblockMatchResult.failed();
                    }

                    positions.add(worldPos);
                    formedTier = MultiblockPredicates.lowestTier(formedTier, match.tier());
                    for (MultiblockAbility ability : match.abilities()) {
                        abilityPositions.computeIfAbsent(ability, ignored -> new ArrayList<>()).add(worldPos);
                    }
                    match.counts().forEach((key, count) -> countMatches.merge(key, count, Integer::sum));
                    if (match.overlayTexture() != null) {
                        overlays.put(worldPos, match.overlayTexture());
                    }
                }
            }
        }

        if (!hasRequiredRecipeAbilities(abilityPositions)) {
            return MultiblockMatchResult.failed();
        }
        if (!hasRequiredCounts(countMatches)) {
            return MultiblockMatchResult.failed();
        }

        return new MultiblockMatchResult(true, variant.id(), variant.variantLevel(), formedTier, List.copyOf(positions), copyAbilities(abilityPositions), Map.copyOf(overlays));
    }

    private boolean hasRequiredRecipeAbilities(Map<MultiblockAbility, List<BlockPos>> abilityPositions) {
        for (MultiblockAbility ability : requiredRecipeAbilities) {
            if (!abilityPositions.containsKey(ability)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasRequiredCounts(Map<String, Integer> countMatches) {
        for (MultiblockPredicate predicate : predicates.values()) {
            for (MultiblockPredicate.CountRequirement requirement : predicate.countRequirements()) {
                int count = countMatches.getOrDefault(requirement.key(), 0);
                if (requirement.hasMinimum() && count < requirement.min()) {
                    return false;
                }
                if (requirement.hasMaximum() && count > requirement.max()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Map<MultiblockAbility, List<BlockPos>> copyAbilities(Map<MultiblockAbility, List<BlockPos>> abilityPositions) {
        Map<MultiblockAbility, List<BlockPos>> copied = new EnumMap<>(MultiblockAbility.class);
        abilityPositions.forEach((ability, positions) -> copied.put(ability, List.copyOf(positions)));
        return copied;
    }

    private static BlockPos rotate(BlockPos origin, Direction facing, int localX, int localY, int localZ) {
        Direction right = facing.getClockWise();
        Direction forward = facing.getOpposite();
        return origin
                .relative(right, localX)
                .above(localY)
                .relative(forward, localZ);
    }

    private record LocalPos(int x, int y, int z) {
    }

    public static final class Builder {
        private final String id;
        private String displayName;
        private MultiblockControllerDefinition controllerDefinition;
        private ResourceLocation controllerId;
        private final List<ResourceLocation> recipeTypes = new ArrayList<>();
        private final List<CERecipeTypeDefinition> recipeTypeDefinitions = new ArrayList<>();
        private final List<ResourceLocation> logicIds = new ArrayList<>();
        private final List<PatternVariant> variants = new ArrayList<>();
        private final Map<Character, MultiblockPredicate> rawPredicates = new HashMap<>();
        private final Map<Character, MultiblockPredicate> predicates = new HashMap<>();
        private final Set<MultiblockAbility> requiredRecipeAbilities = EnumSet.noneOf(MultiblockAbility.class);
        private char controllerSymbol = MultiblockPattern.controller;
        private MultiblockVisualization visualization = MultiblockVisualization.empty();

        private Builder(String id) {
            this.id = id;
            this.displayName = id;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder controller(String controllerId) {
            ResourceLocation id = MultiblockRegistry.id(controllerId);
            this.controllerId = id;
            this.controllerDefinition = MultiblockControllerDefinition.of(
                    id.getPath(),
                    id.getPath(),
                    "block/machines/ino/casing",
                    "block/machines/overlay/foundry/foundry_off",
                    "block/machines/overlay/foundry/foundry_on"
            );
            return this;
        }

        public Builder controller(MultiblockControllerDefinition controller) {
            this.controllerDefinition = controller;
            this.controllerId = controller.id();
            return this;
        }

        public Builder controllerSymbol(char controllerSymbol) {
            this.controllerSymbol = controllerSymbol;
            return this;
        }

        public Builder recipeType(ResourceLocation recipeType) {
            this.recipeTypes.add(recipeType);
            CERecipeTypeDefinition knownType = CERecipeTypes.byId(recipeType);
            if (knownType != null && !this.recipeTypeDefinitions.contains(knownType)) {
                this.recipeTypeDefinitions.add(knownType);
            }
            return this;
        }

        public Builder recipeType(CERecipeTypeDefinition recipeType) {
            this.recipeTypes.add(recipeType.id());
            if (!this.recipeTypeDefinitions.contains(recipeType)) {
                this.recipeTypeDefinitions.add(recipeType);
            }
            return this;
        }

        public Builder recipeType(String recipeType) {
            return recipeType(MultiblockRegistry.id(recipeType));
        }

        public Builder recipeTypes(ResourceLocation... recipeTypes) {
            for (ResourceLocation recipeType : recipeTypes) {
                recipeType(recipeType);
            }
            return this;
        }

        public Builder recipeTypes(CERecipeTypeDefinition... recipeTypes) {
            for (CERecipeTypeDefinition recipeType : recipeTypes) {
                recipeType(recipeType);
            }
            return this;
        }

        public Builder recipeTypes(String... recipeTypes) {
            for (String recipeType : recipeTypes) {
                recipeType(recipeType);
            }
            return this;
        }

        public Builder logic(CERecipeLogicDefinition logic) {
            this.logicIds.add(logic.id());
            return this;
        }

        public Builder logic(String logicId) {
            this.logicIds.add(ResourceLocation.parse(logicId.contains(":") ? logicId : "create_expansion:" + logicId));
            return this;
        }

        public Builder variant(String id, Consumer<MultiblockPattern.VariantBuilder> builderConsumer) {
            MultiblockPattern.VariantBuilder patternBuilder = new MultiblockPattern.VariantBuilder();
            builderConsumer.accept(patternBuilder);
            this.variants.add(patternBuilder.build(id));
            return this;
        }

        public Builder visualization(Consumer<MultiblockVisualization.Builder> builderConsumer) {
            MultiblockVisualization.Builder builder = MultiblockVisualization.builder();
            builderConsumer.accept(builder);
            this.visualization = this.visualization.merge(builder.build());
            return this;
        }

        public Builder where(char symbol, MultiblockPredicate predicate) {
            this.rawPredicates.put(symbol, predicate);
            MultiblockVisualization.SymbolInfo info = predicate.visualizationInfo();
            if (info != null) {
                this.visualization = this.visualization.withSymbol(symbol, info);
            }
            return this;
        }

        public Builder where(char symbol, String blockId) {
            return where(symbol, MultiblockPredicates.block(blockId));
        }

        public Builder where(char symbol, MultiblockAbility ability) {
            return where(symbol, MultiblockPredicates.ability(ability));
        }

        public Builder where(char symbol, MultiblockAbility firstAbility, MultiblockAbility... extraAbilities) {
            MultiblockAbility[] abilities = new MultiblockAbility[extraAbilities.length + 1];
            abilities[0] = firstAbility;
            System.arraycopy(extraAbilities, 0, abilities, 1, extraAbilities.length);
            return where(symbol, MultiblockPredicates.anyAbility(abilities));
        }

        public MultiblockDefinition build() {
            if (controllerId == null) {
                throw new IllegalStateException("Multiblock " + id + " is missing a controller");
            }
            if (variants.isEmpty()) {
                throw new IllegalStateException("Multiblock " + id + " has no variants");
            }
            resolvePredicates();
            return new MultiblockDefinition(this);
        }

        private void resolvePredicates() {
            this.predicates.clear();
            this.requiredRecipeAbilities.clear();
            Set<MultiblockAbility> neededAbilities = neededRecipeAbilities();

            for (Map.Entry<Character, MultiblockPredicate> entry : rawPredicates.entrySet()) {
                MultiblockPredicate predicate = entry.getValue();
                if (predicate instanceof MultiblockPredicates.RecipeTypeAwarePredicate recipeTypeAwarePredicate) {
                    predicate = recipeTypeAwarePredicate.bindRecipeAbilities(neededAbilities);
                    if (recipeTypeAwarePredicate.requiresRecipeAbilities()) {
                        this.requiredRecipeAbilities.addAll(neededAbilities);
                    }
                }

                this.predicates.put(entry.getKey(), predicate);
                MultiblockVisualization.SymbolInfo info = predicate.visualizationInfo();
                if (info != null) {
                    this.visualization = this.visualization.withSymbol(entry.getKey(), info);
                }
            }
        }

        private Set<MultiblockAbility> neededRecipeAbilities() {
            EnumSet<MultiblockAbility> abilities = EnumSet.noneOf(MultiblockAbility.class);
            for (CERecipeTypeDefinition recipeType : recipeTypeDefinitions) {
                if (recipeType.maxItemInputs() > 0) {
                    abilities.add(MultiblockAbility.ITEM_INPUT);
                }
                if (recipeType.maxItemOutputs() > 0) {
                    abilities.add(MultiblockAbility.ITEM_OUTPUT);
                }
                if (recipeType.maxFluidInputs() > 0) {
                    abilities.add(MultiblockAbility.FLUID_INPUT);
                }
                if (recipeType.maxFluidOutputs() > 0) {
                    abilities.add(MultiblockAbility.FLUID_OUTPUT);
                }
                if (recipeType.usesRpm()) {
                    abilities.add(MultiblockAbility.KINETIC_INPUT);
                }

                switch (recipeType.energyMode()) {
                    case CONSUMES -> abilities.add(MultiblockAbility.ENERGY_INPUT);
                    case GENERATES -> abilities.add(MultiblockAbility.ENERGY_OUTPUT);
                    case BOTH -> {
                        abilities.add(MultiblockAbility.ENERGY_INPUT);
                        abilities.add(MultiblockAbility.ENERGY_OUTPUT);
                    }
                    case NONE -> {
                    }
                }
            }
            return abilities;
        }
    }
}
