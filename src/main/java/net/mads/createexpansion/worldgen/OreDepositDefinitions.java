package net.mads.createexpansion.worldgen;

import java.util.List;

import static net.mads.createexpansion.material.IndustrialMaterials.*;
import static net.mads.createexpansion.worldgen.BiomeGroup.*;

final class OreDepositDefinitions {
    private OreDepositDefinitions() {
    }

    static final int GRID_SIZE_CHUNKS = 19;
    static final int DEPOSIT_SIZE_CHUNKS = 3;
    static final int CHANCE_SCALE = 1000;

    static final List<OreDeposit> ALL = List.of(
            deposit("coal", 110, -32, 96, 18, 0.50,
                    groups(COMMON_OVERWORLD, PLAINS, FORESTS, TAIGA, MOUNTAINS),
                    layerCoal(100)),
            deposit("iron", 100, -48, 64, 18, 0.50,
                    groups(COMMON_OVERWORLD, PLAINS, FORESTS, TAIGA, MOUNTAINS, CAVES),
                    layer(HEMATITE, 70), layer(MAGNETITE, 25), layer(PYRITE, 5)),
            deposit("copper", 90, -32, 96, 18, 0.50,
                    groups(MOUNTAINS, BADLANDS, CAVES),
                    layer(CHALCOPYRITE, 65), layer(MALACHITE, 20), layer(CUPRITE, 10), layer(CHALCOCITE, 5)),
            deposit("tin", 60, -48, 72, 16, 0.50,
                    groups(TAIGA, SNOWY, MOUNTAINS, FORESTS),
                    layer(CASSITERITE, 80), layer(STANNITE, 15), layer(WOLFRAMITE, 5)),
            deposit("zinc", 65, -48, 72, 16, 0.50,
                    groups(MOUNTAINS, FORESTS, CAVES),
                    layer(SPHALERITE, 75), layer(SMITHSONITE, 18), layer(ZINCITE, 7)),
            deposit("lead_silver", 28, -64, 32, 16, 0.10,
                    groups(MOUNTAINS, CAVES, SNOWY),
                    layer(GALENA, 70), layer(ANGLESITE, 15), layer(ACANTHITE, 12), layer(CERUSSITE, 3)),
            deposit("silver", 22, -64, 24, 14, 0.10,
                    groups(MOUNTAINS, CAVES, SNOWY),
                    layer(ACANTHITE, 65), layer(CHLORARGYRITE, 20), layer(PROUSTITE, 12), layer(NATIVE_GOLD, 3)),
            deposit("aluminum", 55, -16, 96, 18, 0.10,
                    groups(BADLANDS, JUNGLES, DESERTS, MOUNTAINS),
                    layer(BAUXITE, 70), layer(GIBBSITE, 15), layer(BOEHMITE, 10), layer(CORUNDUM, 5)),
            deposit("nickel_cobalt", 35, -64, 24, 15, 0.08,
                    groups(MOUNTAINS, CAVES),
                    layer(PENTLANDITE, 65), layer(COBALTITE, 20), layer(MILLERITE, 10), layer(SKUTTERUDITE, 5)),
            deposit("titanium", 30, -48, 64, 15, 0.08,
                    groups(BEACHES, MOUNTAINS, BADLANDS),
                    layer(ILMENITE, 65), layer(RUTILE, 25), layer(ANATASE, 10)),
            deposit("chromium", 25, -64, 32, 14, 0.075,
                    groups(MOUNTAINS, CAVES),
                    layer(CHROMITE, 75), layer(PYROLUSITE, 15), layer(MANGANITE, 10)),
            deposit("manganese", 30, -48, 64, 15, 0.08,
                    groups(SWAMPS, RIVERS, CAVES, BADLANDS),
                    layer(PYROLUSITE, 65), layer(RHODOCHROSITE, 25), layer(MANGANITE, 10)),
            deposit("tungsten", 18, -64, 16, 13, 0.065,
                    groups(MOUNTAINS, DEEP_DARK, CAVES),
                    layer(WOLFRAMITE, 70), layer(SCHEELITE, 25), layer(MOLYBDENITE, 5)),
            deposit("molybdenum", 16, -64, 24, 13, 0.06,
                    groups(MOUNTAINS, CAVES),
                    layer(MOLYBDENITE, 75), layer(WULFENITE, 20), layer(SCHEELITE, 5)),
            deposit("gold", 22, -64, 48, 14, 0.065,
                    groups(BADLANDS, MOUNTAINS, RIVERS),
                    layer(NATIVE_GOLD, 70), layer(CALAVERITE, 20), layer(ELECTRUM, 10)),
            deposit("platinum", 8, -64, 0, 12, 0.05,
                    groups(CAVES, MOUNTAINS),
                    layer(NATIVE_PLATINUM, 65), layer(SPERRYLITE, 20), layer(COOPERITE, 10), layer(BRAGGITE, 5)),
            deposit("uranium", 8, -64, 16, 12, 0.045,
                    groups(DESERTS, BADLANDS, DEEP_DARK, CAVES),
                    layer(URANINITE, 55), layer(PITCHBLENDE, 35), layer(CARNOTITE, 10)),
            deposit("thorium_rare_earth", 12, -48, 48, 14, 0.055,
                    groups(MOUNTAINS, BEACHES, CAVES),
                    layer(MONAZITE, 55), layer(THORITE, 20), layer(BASTNASITE, 15), layer(XENOTIME, 10)),
            deposit("lithium", 18, -32, 80, 14, 0.50,
                    groups(MOUNTAINS, DESERTS, BADLANDS),
                    layer(SPODUMENE, 60), layer(LEPIDOLITE, 25), layer(PETALITE, 15)),
            deposit("salt", 45, -16, 80, 16, 0.50,
                    groups(DESERTS, OCEANS, BEACHES),
                    layer(HALITE, 70), layer(SYLVITE, 20), layer(CARNALLITE, 10)),
            deposit("carbonate", 50, -32, 96, 18, 0.50,
                    groups(RIVERS, CAVES, MOUNTAINS, PLAINS),
                    layer(CALCITE, 55), layer(DOLOMITE, 30), layer(MAGNESITE, 15)),
            deposit("gypsum", 35, -16, 80, 16, 0.50,
                    groups(DESERTS, BADLANDS, CAVES),
                    layer(GYPSUM, 70), layer(CALCITE, 30)),
            deposit("fluorite_apatite", 25, -48, 64, 14, 0.50,
                    groups(MOUNTAINS, CAVES),
                    layer(FLUORITE, 55), layer(APATITE, 35), layer(CALCITE, 10)),
            deposit("quartz", 35, -48, 96, 16, 0.09,
                    groups(MOUNTAINS, CAVES),
                    layer(QUARTZ, 80), layer(FLUORITE, 12), layer(TOPAZ, 8)),
            deposit("topaz", 12, -48, 48, 12, 0.055,
                    groups(MOUNTAINS, DESERTS),
                    layer(TOPAZ, 70), layer(QUARTZ, 25), layer(FLUORITE, 5)),
            deposit("ruby_sapphire", 10, -64, 24, 12, 0.05,
                    groups(MOUNTAINS, DEEP_DARK, CAVES),
                    layer(CORUNDUM, 55), layer(RUBY, 18), layer(SAPPHIRE, 18), layer(CHROMITE, 5), layer(ILMENITE, 4)),
            deposit("emerald", 8, -32, 96, 10, 0.04,
                    groups(MOUNTAINS),
                    layer(EMERALD, 70), layer(QUARTZ, 25), layer(TOPAZ, 5)),
            deposit("diamond", 4, -64, 0, 10, 0.035,
                    groups(DEEP_DARK, CAVES),
                    layer(DIAMOND, 60), layer(GRAPHITE, 35), layer(REDSTONE, 5)),
            deposit("lapis", 20, -64, 32, 13, 0.50,
                    groups(CAVES, MOUNTAINS),
                    layer(LAZURITE, 65), layer(SODALITE, 25), layer(PYRITE, 10)),
            deposit("nether_quartz", 60, 8, 120, 18, 0.50,
                    groups(NETHER),
                    layer(QUARTZ, 80), layer(SULFUR, 12), layer(NATIVE_GOLD, 8)),
            deposit("nether_gold", 35, 8, 96, 15, 0.50,
                    groups(NETHER),
                    layer(NATIVE_GOLD, 70), layer(CALAVERITE, 20), layer(PYRITE, 10)),
            deposit("basalt_heavy", 20, 8, 96, 14, 0.075,
                    groups(BASALT_DELTAS),
                    layer(MAGNETITE, 50), layer(ILMENITE, 30), layer(CHROMITE, 20)),
            deposit("nether_sulfur", 25, 8, 96, 14, 0.50,
                    groups(NETHER),
                    layer(SULFUR, 60), layer(PYRITE, 30), layer(SPHALERITE, 10)),
            deposit("end_titanium", 20, 16, 96, 14, 0.075,
                    groups(END),
                    layer(RUTILE, 60), layer(ILMENITE, 30), layer(TITANIUM, 10)),
            deposit("end_rare_earth", 15, 16, 96, 13, 0.50,
                    groups(END),
                    layer(XENOTIME, 45), layer(MONAZITE, 35), layer(BASTNASITE, 20)),
            deposit("end_crystal", 10, 16, 96, 12, 0.055,
                    groups(END),
                    layer(QUARTZ, 45), layer(TOPAZ, 25), layer(SAPPHIRE, 20), layer(DIAMOND, 10))
    );

    static final List<OreDeposit> VILLAGE_BONUS = List.of(
            deposit("village_coal", 100, -32, 48, 14, 0.70, groups(OVERWORLD), layerCoal(100)),
            deposit("village_copper", 100, -32, 48, 14, 0.50, groups(OVERWORLD), layer(CHALCOPYRITE, 75), layer(MALACHITE, 25)),
            deposit("village_iron", 100, -32, 48, 14, 0.50, groups(OVERWORLD), layer(HEMATITE, 75), layer(MAGNETITE, 25)),
            deposit("village_tin", 100, -32, 48, 14, 0.50, groups(OVERWORLD), layer(CASSITERITE, 85), layer(STANNITE, 15)),
            deposit("village_zinc", 100, -32, 48, 14, 0.50, groups(OVERWORLD), layer(SPHALERITE, 80), layer(SMITHSONITE, 20))
    );

    private static OreDeposit deposit(
            String id,
            int weight,
            int minY,
            int maxY,
            int verticalRadius,
            double density,
            List<BiomeGroup> biomeGroups,
            OreDepositLayer... layers
    ) {
        return new OreDeposit(id, weight, minY, maxY, verticalRadius, density, biomeGroups, defaultIndicators(id), List.of(layers));
    }

    private static List<SurfaceIndicator> defaultIndicators(String id) {
        return switch (id) {
            case "coal" -> List.of(
                    SurfaceIndicator.DEAD_SOIL,
                    SurfaceIndicator.DEAD_PLANTS,
                    SurfaceIndicator.GRAVEL_PATCH,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "iron", "tin", "zinc", "lead_silver", "silver", "aluminum", "nickel_cobalt",
                    "chromium", "manganese", "molybdenum" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.GRAVEL_PATCH,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "copper" -> List.of(
                    SurfaceIndicator.GRAVEL_PATCH,
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.DEAD_SOIL
            );
            case "titanium", "tungsten", "platinum" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.BOULDER_CLUSTER,
                    SurfaceIndicator.CRACKED_GROUND
            );
            case "gold" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.GRAVEL_PATCH,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "uranium" -> List.of(
                    SurfaceIndicator.DEAD_SOIL,
                    SurfaceIndicator.DEAD_PLANTS,
                    SurfaceIndicator.CRACKED_GROUND
            );
            case "thorium_rare_earth", "lithium", "fluorite_apatite", "lapis" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.GRAVEL_PATCH,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "salt", "gypsum" -> List.of(
                    SurfaceIndicator.DEAD_SOIL,
                    SurfaceIndicator.GRAVEL_PATCH
            );
            case "carbonate", "quartz" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "topaz", "ruby_sapphire", "emerald", "diamond", "end_crystal" -> List.of(
                    SurfaceIndicator.CRYSTAL_SPOT,
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "nether_sulfur", "basalt_heavy" -> List.of(
                    SurfaceIndicator.LAVA_POOL,
                    SurfaceIndicator.CRACKED_GROUND,
                    SurfaceIndicator.STONE_SPOT
            );
            case "nether_quartz", "nether_gold" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.GRAVEL_PATCH
            );
            case "end_titanium", "end_rare_earth" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            default -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.GRAVEL_PATCH
            );
        };
    }

    private static List<BiomeGroup> groups(BiomeGroup... groups) {
        return List.of(groups);
    }

    private static OreDepositLayer layer(net.mads.createexpansion.material.IndustrialMaterial material, int weight) {
        return OreDepositLayer.material(material, weight);
    }

    private static OreDepositLayer layerCoal(int weight) {
        return OreDepositLayer.coal(weight);
    }
}
