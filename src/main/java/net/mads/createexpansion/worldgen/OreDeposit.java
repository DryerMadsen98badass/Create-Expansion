package net.mads.createexpansion.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

record OreDeposit(
        String id,
        int weight,
        int minY,
        int maxY,
        int verticalRadius,
        double density,
        List<BiomeGroup> biomeGroups,
        List<SurfaceIndicator> surfaceIndicators,
        List<OreDepositLayer> layers
) {
    OreDeposit {
        if (weight <= 0) {
            throw new IllegalArgumentException("Ore deposit weight must be positive");
        }

        if (minY > maxY) {
            throw new IllegalArgumentException("Ore deposit minY cannot be above maxY");
        }

        if (verticalRadius <= 0) {
            throw new IllegalArgumentException("Ore deposit vertical radius must be positive");
        }

        if (density <= 0.0 || density > 1.0) {
            throw new IllegalArgumentException("Ore deposit density must be between 0 and 1");
        }

        surfaceIndicators = List.copyOf(surfaceIndicators);
    }

    boolean matchesAny(List<Holder<Biome>> biomes) {
        for (Holder<Biome> biome : biomes) {
            for (BiomeGroup group : biomeGroups) {
                if (group.matches(biome)) {
                    return true;
                }
            }
        }

        return false;
    }

    int layerWeight() {
        return layers.stream().mapToInt(OreDepositLayer::weight).sum();
    }
}
