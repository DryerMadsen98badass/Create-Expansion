package net.mads.createexpansion.worldgen;

import java.util.List;

import static net.mads.createexpansion.material.IndustrialMaterials.*;
import static net.mads.createexpansion.worldgen.BiomeGroup.*;

final class OreDepositDefinitions {
    private OreDepositDefinitions() {
    }

    static final int GRID_SIZE_CHUNKS = 9;
    static final int DEPOSIT_SIZE_CHUNKS = 3;
    static final int CHANCE_SCALE = 1000;
    private static final int DEFAULT_HORIZONTAL_RADIUS = 24;
    private static final int END_SURFACE_HORIZONTAL_RADIUS = 42;

    static final List<OreDeposit> ALL = List.of(
            deposit("coal", 100, -32, 96, 18, 0.50,
                    groups(COMMON_OVERWORLD, PLAINS, FORESTS, TAIGA, MOUNTAINS),
                    layer(COAL, 75), layer(CARBON, 15), layer(GRAPHITE, 10)),
            deposit("iron", 95, -48, 64, 18, 0.50,
                    groups(COMMON_OVERWORLD, PLAINS, FORESTS, TAIGA, MOUNTAINS, CAVES),
                    layer(HEMATITE, 45), layer(MAGNETITE, 30), layer(GOETHITE, 15), layer(PYRITE, 5), layer(COAL, 5)),
            deposit("copper", 85, -32, 96, 18, 0.45,
                    groups(MOUNTAINS, BADLANDS, CAVES),
                    layer(CHALCOPYRITE, 45), layer(MALACHITE, 25), layer(CUPRITE, 15), layer(CHALCOCITE, 10), layer(PYRITE, 5)),
            deposit("tin", 55, -48, 72, 16, 0.40,
                    groups(TAIGA, SNOWY, MOUNTAINS, FORESTS),
                    layer(CASSITERITE, 65), layer(STANNITE, 20), layer(QUARTZ, 10), layer(WOLFRAMITE, 5)),
            deposit("zinc_lead", 60, -48, 72, 16, 0.40,
                    groups(MOUNTAINS, FORESTS, CAVES),
                    layer(SPHALERITE, 45), layer(GALENA, 25), layer(SMITHSONITE, 15), layer(ANGLESITE, 10), layer(PYRITE, 5)),
            deposit("silver", 22, -64, 32, 14, 0.12,
                    groups(MOUNTAINS, CAVES, SNOWY),
                    layer(ACANTHITE, 45), layer(PROUSTITE, 25), layer(GALENA, 20), layer(NATIVE_GOLD, 5), layer(QUARTZ, 5)),
            deposit("gold", 24, -64, 48, 14, 0.12,
                    groups(BADLANDS, MOUNTAINS, RIVERS),
                    layer(NATIVE_GOLD, 45), layer(ELECTRUM, 25), layer(PYRITE, 20), layer(QUARTZ, 10)),
            deposit("aluminum", 50, -16, 96, 18, 0.35,
                    groups(BADLANDS, JUNGLES, DESERTS, MOUNTAINS),
                    layer(BAUXITE, 55), layer(GIBBSITE, 20), layer(BOEHMITE, 15), layer(CORUNDUM, 10)),
            deposit("nickel_cobalt", 28, -64, 24, 15, 0.12,
                    groups(MOUNTAINS, CAVES),
                    layer(PENTLANDITE, 45), layer(COBALTITE, 25), layer(MILLERITE, 15), layer(SKUTTERUDITE, 10), layer(PYRITE, 5)),
            deposit("manganese", 28, -48, 64, 15, 0.14,
                    groups(SWAMPS, RIVERS, CAVES, BADLANDS),
                    layer(PYROLUSITE, 45), layer(RHODOCHROSITE, 30), layer(MANGANITE, 20), layer(HEMATITE, 5)),
            deposit("chromium", 18, -64, 32, 14, 0.10,
                    groups(MOUNTAINS, CAVES),
                    layer(CHROMITE, 60), layer(MAGNETITE, 25), layer(ILMENITE, 15)),
            deposit("tungsten", 9, -64, 8, 12, 0.06,
                    groups(MOUNTAINS, DEEP_DARK, CAVES),
                    layer(WOLFRAMITE, 55), layer(SCHEELITE, 35), layer(MOLYBDENITE, 10)),
            deposit("molybdenum", 9, -64, 8, 12, 0.055,
                    groups(MOUNTAINS, CAVES),
                    layer(MOLYBDENITE, 60), layer(WULFENITE, 25), layer(SCHEELITE, 15)),
            deposit("platinum", 4, -64, 0, 10, 0.04,
                    groups(CAVES, MOUNTAINS),
                    layer(NATIVE_PLATINUM, 45), layer(SPERRYLITE, 25), layer(COOPERITE, 20), layer(BRAGGITE, 10)),
            deposit("titanium", 14, -48, 48, 13, 0.06,
                    groups(BEACHES, MOUNTAINS, BADLANDS),
                    layer(RUTILE, 45), layer(ILMENITE, 35), layer(ANATASE, 20)),
            deposit("uranium", 8, -64, 16, 12, 0.045,
                    groups(DESERTS, BADLANDS, DEEP_DARK, CAVES),
                    layer(URANINITE, 55), layer(PITCHBLENDE, 35), layer(CARNOTITE, 10)),
            deposit("thorium_rare_earth", 4, -48, 16, 10, 0.035,
                    groups(MOUNTAINS, BEACHES, CAVES),
                    layer(MONAZITE, 40), layer(THORITE, 25), layer(BASTNASITE, 20), layer(XENOTIME, 15)),
            deposit("salt", 45, -16, 80, 16, 0.50,
                    groups(DESERTS, OCEANS, BEACHES),
                    layer(HALITE, 65), layer(SYLVITE, 25), layer(CARNALLITE, 10)),
            deposit("carbonate", 50, -32, 96, 18, 0.50,
                    groups(RIVERS, CAVES, MOUNTAINS, PLAINS),
                    layer(CALCITE, 50), layer(DOLOMITE, 35), layer(MAGNESITE, 15)),
            deposit("gypsum", 35, -16, 80, 16, 0.45,
                    groups(DESERTS, BADLANDS, CAVES),
                    layer(GYPSUM, 65), layer(CALCITE, 25), layer(SULFUR, 10)),
            deposit("apatite_fluorite", 25, -48, 64, 14, 0.25,
                    groups(MOUNTAINS, CAVES),
                    layer(APATITE, 45), layer(FLUORITE, 35), layer(CALCITE, 15), layer(PHOSPHORUS, 5)),
            deposit("lithium", 16, -32, 80, 14, 0.14,
                    groups(MOUNTAINS, DESERTS, BADLANDS),
                    layer(SPODUMENE, 45), layer(LEPIDOLITE, 30), layer(PETALITE, 20), layer(QUARTZ, 5)),
            deposit("lapis", 18, -64, 32, 13, 0.18,
                    groups(CAVES, MOUNTAINS),
                    layer(LAZURITE, 55), layer(SODALITE, 30), layer(PYRITE, 10), layer(CALCITE, 5)),
            deposit("diamond", 5, -64, 0, 10, 0.06,
                    groups(DEEP_DARK, CAVES),
                    layer(DIAMOND, 45), layer(GRAPHITE, 30), layer(CARBON, 15), layer(COAL, 7), layer(REDSTONE, 3)),
            deposit("emerald", 7, -32, 96, 10, 0.05,
                    groups(MOUNTAINS),
                    layer(EMERALD, 55), layer(BERYLLIUM, 20), layer(QUARTZ, 15), layer(TOPAZ, 10)),
            deposit("ruby_sapphire", 8, -64, 24, 12, 0.06,
                    groups(MOUNTAINS, DEEP_DARK, CAVES),
                    layer(CORUNDUM, 45), layer(RUBY, 18), layer(SAPPHIRE, 18), layer(ILMENITE, 10), layer(CHROMITE, 9)),
            deposit("topaz_quartz", 12, -48, 64, 12, 0.10,
                    groups(MOUNTAINS, DESERTS),
                    layer(TOPAZ, 40), layer(QUARTZ, 40), layer(FLUORITE, 20)),
            deposit("nether_coal_sulfur", 30, 8, 96, 16, 0.35,
                    groups(NETHER),
                    layer(COAL, 35), layer(CARBON, 20), layer(SULFUR, 25), layer(PYRITE, 20)),
            deposit("nether_quartz", 60, 8, 120, 18, 0.50,
                    groups(NETHER),
                    layer(QUARTZ, 75), layer(SULFUR, 15), layer(NATIVE_GOLD, 10)),
            deposit("nether_gold", 38, 8, 96, 15, 0.45,
                    groups(NETHER),
                    layer(NATIVE_GOLD, 50), layer(CALAVERITE, 20), layer(ELECTRUM, 15), layer(PYRITE, 15)),
            deposit("nether_sulfur", 35, 8, 96, 14, 0.45,
                    groups(NETHER),
                    layer(SULFUR, 50), layer(PYRITE, 25), layer(SPHALERITE, 15), layer(GALENA, 10)),
            deposit("basalt_heavy", 24, 8, 96, 14, 0.18,
                    groups(BASALT_DELTAS),
                    layer(MAGNETITE, 40), layer(ILMENITE, 30), layer(CHROMITE, 20), layer(VANADIUM, 10)),
            deposit("nether_copper_sulfide", 22, 8, 96, 14, 0.18,
                    groups(NETHER),
                    layer(CHALCOPYRITE, 45), layer(CHALCOCITE, 25), layer(PYRITE, 20), layer(SULFUR, 10)),
            deposit("nether_nickel_cobalt", 16, 8, 80, 13, 0.12,
                    groups(BASALT_DELTAS),
                    layer(PENTLANDITE, 40), layer(COBALTITE, 25), layer(MILLERITE, 20), layer(SKUTTERUDITE, 15)),
            deposit("nether_tungsten_moly", 12, 4, 64, 12, 0.10,
                    groups(NETHER),
                    layer(WOLFRAMITE, 35), layer(SCHEELITE, 30), layer(MOLYBDENITE, 25), layer(WULFENITE, 10)),
            deposit("nether_platinum", 6, 4, 48, 10, 0.06,
                    groups(BASALT_DELTAS),
                    layer(NATIVE_PLATINUM, 35), layer(SPERRYLITE, 25), layer(COOPERITE, 25), layer(BRAGGITE, 15)),
            deposit("nether_salt_potash", 16, 16, 96, 13, 0.16,
                    groups(NETHER),
                    layer(SYLVITE, 40), layer(CARNALLITE, 35), layer(HALITE, 25)),
            endSurfaceDeposit("end_titanium", 18, 0.55,
                    layer(RUTILE, 45), layer(ILMENITE, 25), layer(TITANIUM, 20), layer(ANATASE, 10)),
            endSurfaceDeposit("end_rare_earth", 14, 0.55,
                    layer(XENOTIME, 35), layer(MONAZITE, 30), layer(BASTNASITE, 25), layer(YTTRIUM, 5), layer(YTTERBIUM, 5)),
            endSurfaceDeposit("end_crystal", 10, 0.45,
                    layer(QUARTZ, 35), layer(TOPAZ, 25), layer(SAPPHIRE, 20), layer(DIAMOND, 20)),
            endSurfaceDeposit("end_thorium", 7, 0.35,
                    layer(THORITE, 45), layer(MONAZITE, 30), layer(XENOTIME, 25)),
            endSurfaceDeposit("end_uranium", 5, 0.30,
                    layer(URANINITE, 45), layer(PITCHBLENDE, 35), layer(CARNOTITE, 20)),
            endSurfaceDeposit("end_platinum", 5, 0.30,
                    layer(NATIVE_PLATINUM, 35), layer(SPERRYLITE, 25), layer(COOPERITE, 25), layer(BRAGGITE, 15)),
            endSurfaceDeposit("end_tungsten", 7, 0.35,
                    layer(SCHEELITE, 40), layer(WOLFRAMITE, 30), layer(MOLYBDENITE, 20), layer(WULFENITE, 10)),
            endSurfaceDeposit("end_lithium_beryllium", 8, 0.35,
                    layer(PETALITE, 30), layer(LEPIDOLITE, 25), layer(SPODUMENE, 25), layer(BERYLLIUM, 20)),
            endSurfaceDeposit("end_gem", 6, 0.30,
                    layer(DIAMOND, 25), layer(SAPPHIRE, 25), layer(RUBY, 20), layer(TOPAZ, 20), layer(QUARTZ, 10))
    );

    static final List<OreDeposit> VILLAGE_BONUS = List.of(
            deposit("village_coal", 100, -32, 48, 14, 0.70, groups(OVERWORLD),
                    layer(COAL, 82), layer(GRAPHITE, 17), layer(DIAMOND, 1)),
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
        return new OreDeposit(id, weight, minY, maxY, DEFAULT_HORIZONTAL_RADIUS, verticalRadius, density, false, biomeGroups, defaultIndicators(id), List.of(layers));
    }

    private static OreDeposit endSurfaceDeposit(
            String id,
            int weight,
            double density,
            OreDepositLayer... layers
    ) {
        return new OreDeposit(id, weight, 16, 160, END_SURFACE_HORIZONTAL_RADIUS, 18, density, true, groups(END), defaultIndicators(id), List.of(layers));
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
            case "zinc_lead" -> List.of(
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
            case "thorium_rare_earth", "lithium", "fluorite_apatite", "apatite_fluorite", "lapis" -> List.of(
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
            case "topaz", "topaz_quartz", "ruby_sapphire", "emerald", "diamond", "end_crystal", "end_gem" -> List.of(
                    SurfaceIndicator.CRYSTAL_SPOT,
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.BOULDER_CLUSTER
            );
            case "nether_coal_sulfur", "nether_sulfur", "nether_copper_sulfide" -> List.of(
                    SurfaceIndicator.NETHER_SULFUR_CRUST,
                    SurfaceIndicator.NETHER_ASH_PATCH,
                    SurfaceIndicator.NETHER_BASALT_SPOT
            );
            case "basalt_heavy", "nether_nickel_cobalt", "nether_platinum" -> List.of(
                    SurfaceIndicator.NETHER_BASALT_SPOT,
                    SurfaceIndicator.NETHER_ASH_PATCH
            );
            case "nether_quartz", "nether_gold" -> List.of(
                    SurfaceIndicator.NETHER_GOLD_FLECKS,
                    SurfaceIndicator.NETHER_ASH_PATCH,
                    SurfaceIndicator.NETHER_SULFUR_CRUST
            );
            case "nether_tungsten_moly" -> List.of(
                    SurfaceIndicator.NETHER_BASALT_SPOT,
                    SurfaceIndicator.NETHER_SULFUR_CRUST
            );
            case "nether_salt_potash" -> List.of(
                    SurfaceIndicator.NETHER_ASH_PATCH,
                    SurfaceIndicator.NETHER_SULFUR_CRUST
            );
            case "end_titanium", "end_rare_earth", "end_thorium", "end_uranium",
                    "end_platinum", "end_tungsten", "end_lithium_beryllium" -> List.of(
                    SurfaceIndicator.STONE_SPOT,
                    SurfaceIndicator.CRYSTAL_SPOT,
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

}
