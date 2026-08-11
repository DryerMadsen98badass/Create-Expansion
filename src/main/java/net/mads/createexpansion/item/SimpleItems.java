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
            item("terracotta_rotor", "Terracotta Rotor Mold"),
            item("coal_coke", "Coal Coke").furnaceFuel(32),
            item("firebrick", "Firebrick"),
            item("silica_brick", "Silica Brick"),
            item("unfired_firebrick", "Unfired Firebrick"),
            item("unfired_silica_brick", "Unfired Silica Brick"),
            item("drill_head", "Drill Head"),
            item("mesh", "Mesh"),
            item("bucket_clay", "Unfired Bucket"),
            item("biomass_briquette", "Biomass Briquette").furnaceFuel(12),
            item("biomass", "Biomass"),
            item("wet_biomass", "Wet Biomass"),
            item("plant_wax", "Plant Wax"),
            item("plant_fiber", "Plant Fiber"),
            item("empty_glue", "Empty Glue"),
            item("stainless_bronze_hand", "Stainless Bronze Hand"),

            itemColor("seared_dust", "Seared Dust", "item/material_sets/dull/dust", 0x3b3b3b),
            itemColor("seared_brick", "Seared Brick", "item/material_sets/dull/ingot_hot", 0x3b3b3b),
            itemColor("treated_leather", "Treated Leather", "item/standalone/materials/leather", 0x5b0000)
    );

    private SimpleItems() {
    }

    private static SimpleItemDefinition item(
            String id,
            String displayName
    ) {
        return new SimpleItemDefinition(
                id,
                displayName
        );
    }

    private static SimpleItemDefinition itemColor(
            String id,
            String displayName,
            String texture,
            int color
    ) {
        return new SimpleItemDefinition(
                id,
                displayName,
                texture,
                color
        );
    }
}