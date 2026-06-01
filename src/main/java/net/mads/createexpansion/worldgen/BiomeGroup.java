package net.mads.createexpansion.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

enum BiomeGroup {
    OVERWORLD,
    COMMON_OVERWORLD,
    PLAINS,
    FORESTS,
    TAIGA,
    JUNGLES,
    MOUNTAINS,
    BADLANDS,
    DESERTS,
    SWAMPS,
    RIVERS,
    BEACHES,
    OCEANS,
    SNOWY,
    CAVES,
    DEEP_DARK,
    NETHER,
    BASALT_DELTAS,
    END;

    boolean matches(Holder<Biome> biome) {
        String path = biome.unwrapKey()
                .map(key -> key.location().getPath())
                .orElse("");

        return switch (this) {
            case OVERWORLD -> biome.is(BiomeTags.IS_OVERWORLD);
            case COMMON_OVERWORLD -> biome.is(BiomeTags.IS_OVERWORLD)
                    && !biome.is(BiomeTags.IS_OCEAN)
                    && !biome.is(BiomeTags.IS_DEEP_OCEAN)
                    && !biome.is(BiomeTags.IS_BEACH);
            case PLAINS -> path.contains("plains") || path.equals("meadow") || path.equals("cherry_grove");
            case FORESTS -> biome.is(BiomeTags.IS_FOREST);
            case TAIGA -> biome.is(BiomeTags.IS_TAIGA);
            case JUNGLES -> biome.is(BiomeTags.IS_JUNGLE);
            case MOUNTAINS -> biome.is(BiomeTags.IS_MOUNTAIN)
                    || biome.is(BiomeTags.IS_HILL)
                    || path.contains("peaks")
                    || path.contains("slopes")
                    || path.equals("grove");
            case BADLANDS -> biome.is(BiomeTags.IS_BADLANDS);
            case DESERTS -> path.equals("desert");
            case SWAMPS -> path.contains("swamp");
            case RIVERS -> biome.is(BiomeTags.IS_RIVER);
            case BEACHES -> biome.is(BiomeTags.IS_BEACH);
            case OCEANS -> biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN);
            case SNOWY -> path.contains("snowy")
                    || path.contains("frozen")
                    || path.equals("ice_spikes")
                    || path.equals("grove");
            case CAVES -> path.equals("dripstone_caves")
                    || path.equals("lush_caves")
                    || path.equals("deep_dark");
            case DEEP_DARK -> path.equals("deep_dark");
            case NETHER -> biome.is(BiomeTags.IS_NETHER);
            case BASALT_DELTAS -> path.equals("basalt_deltas");
            case END -> biome.is(BiomeTags.IS_END);
        };
    }
}
