package net.mads.createexpansion.multiblock;

import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record MultiblockMatchResult(
        boolean matched,
        String variant,
        int variantLevel,
        MachineTier tier,
        List<BlockPos> positions,
        Map<MultiblockAbility, List<BlockPos>> abilityPositions,
        Map<BlockPos, ResourceLocation> overlays
) {
    public static MultiblockMatchResult failed() {
        return new MultiblockMatchResult(false, "", 0, null, List.of(), Map.of(), Map.of());
    }
}
