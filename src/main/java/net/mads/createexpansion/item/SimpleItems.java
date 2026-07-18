package net.mads.createexpansion.item;

import java.util.List;

public final class SimpleItems {
    public static final List<SimpleItemDefinition> ALL = List.of(
            item("spool", "Spool"),
            item("terracotta_nugget", "Terracotta Nugget Mold"),
            item("terracotta_ingot", "Terracotta Ingot Mold"),
            item("terracotta_plate", "Terracotta Plate Mold"),
            item("terracotta_rod", "Terracotta Rod Mold"),
            item("terracotta_long_rod", "Terracotta Long Rod Mold"),
            item("terracotta_bolt", "Terracotta Bolt Mold"),
            item("terracotta_screw", "Terracotta Screw Mold"),
            item("terracotta_ring", "Terracotta Ring Mold"),
            item("terracotta_small_ring", "Terracotta Small Ring Mold"),
            item("terracotta_large_ring", "Terracotta Large Ring Mold"),
            item("terracotta_gear", "Terracotta Gear Mold"),
            item("terracotta_small_gear", "Terracotta Small Gear Mold"),
            item("terracotta_bearing_ball", "Terracotta Bearing Ball Mold"),
            item("terracotta_bearing", "Terracotta Bearing Mold"),
            item("terracotta_rotor", "Terracotta Rotor Mold")
    );

    private SimpleItems() {
    }

    private static SimpleItemDefinition item(String id, String displayName) {
        return new SimpleItemDefinition(id, displayName);
    }
}
