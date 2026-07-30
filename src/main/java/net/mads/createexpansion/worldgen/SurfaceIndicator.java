package net.mads.createexpansion.worldgen;

enum SurfaceIndicator {
    LAVA_POOL(2),
    STONE_SPOT(7),
    DEAD_SOIL(6),
    GRAVEL_PATCH(6),
    CRACKED_GROUND(4),
    CRYSTAL_SPOT(2),
    DEAD_PLANTS(4),
    BOULDER_CLUSTER(5),
    NETHER_SULFUR_CRUST(7),
    NETHER_ASH_PATCH(6),
    NETHER_BASALT_SPOT(6),
    NETHER_GOLD_FLECKS(3);

    private final int weight;

    SurfaceIndicator(int weight) {
        this.weight = weight;
    }

    int weight() {
        return weight;
    }
}
