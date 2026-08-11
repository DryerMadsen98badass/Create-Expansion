package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MultiblockPredicate {
    Match match(Level level, BlockPos pos, BlockState state);

    default MultiblockVisualization.SymbolInfo visualizationInfo() {
        return null;
    }

    default List<CountRequirement> countRequirements() {
        return List.of();
    }

    default MultiblockPredicate or(MultiblockPredicate other) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.or(this, other);
    }

    /**
     * Uses a block model as the formed overlay for matching machine ports.
     * Example: create_expansion:block/bronze_machine_casing loads
     * assets/create_expansion/models/block/bronze_machine_casing.json.
     */
    default MultiblockPredicate overlay(String model) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.overlay(this, model);
    }

    default MultiblockPredicate min(int minimum) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.min(this, minimum);
    }

    default MultiblockPredicate max(int maximum) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.max(this, maximum);
    }

    default MultiblockPredicate onlyTier(MachineTier tier) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.onlyTier(this, tier);
    }

    default MultiblockPredicate Tier(MachineTier tier) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.tierUpTo(this, tier);
    }

    /** Marks this ITEM_INPUT predicate as one ordered sequenced input position. */
    default MultiblockPredicate sequentialInput(int index) {
        return net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.sequentialInput(this, index);
    }

    /** Returns the ordered sequenced-input index, or 0 when this predicate is not sequenced. */
    default int sequentialInputIndex() {
        return 0;
    }

    /** Returns whether this predicate permits a build candidate of the supplied machine tier. */
    default boolean allowsBuildTier(MachineTier tier) {
        return true;
    }

    record CountRequirement(String key, ResourceLocation blockId, int min, int max) {
        public boolean hasMinimum() {
            return min > 0;
        }

        public boolean hasMaximum() {
            return max >= 0;
        }
    }

    record Match(boolean matches, MachineTier tier, Set<MultiblockAbility> abilities, Map<String, Integer> counts, ResourceLocation overlayModel) {
        public static Match failed() {
            return new Match(false, null, Set.of(), Map.of(), null);
        }

        public static Match success() {
            return new Match(true, null, Set.of(), Map.of(), null);
        }

        public static Match tiered(MachineTier tier) {
            return new Match(true, tier, Set.of(), Map.of(), null);
        }

        public static Match abilities(Set<MultiblockAbility> abilities, MachineTier tier) {
            return new Match(true, tier, abilities, Map.of(), null);
        }

        public static Match counted(MachineTier tier, Set<MultiblockAbility> abilities, String key) {
            return new Match(true, tier, abilities, Map.of(key, 1), null);
        }

        public Match withOverlay(ResourceLocation overlayModel) {
            return matches ? new Match(true, tier, abilities, counts, overlayModel) : this;
        }
    }
}
