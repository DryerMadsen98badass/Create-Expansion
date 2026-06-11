package net.mads.createexpansion.multiblock;

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
        return net.mads.createexpansion.multiblock.MultiblockPredicates.or(this, other);
    }

    default MultiblockPredicate overlay(String texture) {
        return net.mads.createexpansion.multiblock.MultiblockPredicates.overlay(this, texture);
    }

    record CountRequirement(String key, ResourceLocation blockId, int min, int max) {
        public boolean hasMinimum() {
            return min > 0;
        }

        public boolean hasMaximum() {
            return max >= 0;
        }
    }

    record Match(boolean matches, MachineTier tier, Set<MultiblockAbility> abilities, Map<String, Integer> counts, ResourceLocation overlayTexture) {
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

        public Match withOverlay(ResourceLocation overlayTexture) {
            return matches ? new Match(true, tier, abilities, counts, overlayTexture) : this;
        }
    }
}
