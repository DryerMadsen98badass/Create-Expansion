package net.mads.createexpansion.worldgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;

final class OreDepositPlacement {
    private OreDepositPlacement() {
    }

    static int chooseCenterY(WorldGenLevel level, OreDeposit deposit, int centerX, int centerZ, RandomSource random) {
        int terrainY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, centerX, centerZ) - 1;
        return chooseCenterY(level.getMinBuildHeight(), level.getMaxBuildHeight(), terrainY, deposit, random);
    }

    static int chooseCenterY(ServerLevel level, OreDeposit deposit, int centerX, int centerZ, RandomSource random) {
        int terrainY = level.getChunkSource().getGenerator().getBaseHeight(
                centerX,
                centerZ,
                Heightmap.Types.OCEAN_FLOOR_WG,
                level,
                level.getChunkSource().randomState()
        ) - 1;
        return chooseCenterY(level.getMinBuildHeight(), level.getMaxBuildHeight(), terrainY, deposit, random);
    }

    private static int chooseCenterY(int minBuildHeight, int maxBuildHeight, int terrainY, OreDeposit deposit, RandomSource random) {
        int minY = Math.max(minBuildHeight, deposit.minY());
        int maxY = Math.min(maxBuildHeight - 1, deposit.maxY());

        if (terrainY > minBuildHeight) {
            int undergroundMargin = Math.max(6, deposit.verticalRadius() / 2);
            maxY = Math.min(maxY, terrainY - undergroundMargin);
        }

        if (maxY < minY) {
            maxY = Math.min(maxBuildHeight - 1, terrainY - 4);
            minY = Math.max(minBuildHeight, maxY - deposit.verticalRadius());
        }

        if (maxY < minY) {
            return Mth.clamp(minY, minBuildHeight, maxBuildHeight - 1);
        }

        return Mth.nextInt(random, minY, maxY);
    }
}
