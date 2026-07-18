package net.mads.createexpansion.material;

public enum MaterialPart {
    ORE("ore", "Ore", Kind.BLOCK),
    DEEPSLATE_ORE("deepslate_ore", "Deepslate Ore", Kind.BLOCK),
    DIORITE_ORE("diorite_ore", "Diorite Ore", Kind.BLOCK),
    ANDESITE_ORE("andesite_ore", "Andesite Ore", Kind.BLOCK),
    GRANITE_ORE("granite_ore", "Granite Ore", Kind.BLOCK),
    TUFF_ORE("tuff_ore", "Tuff Ore", Kind.BLOCK),
    NETHERRACK_ORE("netherrack_ore", "Netherrack Ore", Kind.BLOCK),
    BLACKSTONE_ORE("blackstone_ore", "Blackstone Ore", Kind.BLOCK),
    END_STONE_ORE("end_stone_ore", "End Stone Ore", Kind.BLOCK),
    RAW_ORE("raw_ore", "Raw Ore", Kind.ITEM),
    RAW_BLOCK("raw_block", "Raw Block", Kind.BLOCK),

    INGOT("ingot", "Ingot", Kind.ITEM),
    NUGGET("nugget", "Nugget", Kind.ITEM),
    BLOCK("block", "Block", Kind.BLOCK),
    DUST("dust", "Dust", Kind.ITEM),
    TINY_DUST("tiny_dust", "Tiny Dust", Kind.ITEM),
    SMALL_DUST("small_dust", "Small Dust", Kind.ITEM),
    IMPURE_DUST("impure_dust", "Impure Dust", Kind.ITEM),
    CRUSHED_ORE("crushed_ore", "Crushed Ore", Kind.ITEM),
    REFINED_ORE("refined_ore", "Refined Ore", Kind.ITEM),
    RAW_CHUNK("raw_chunk", "Raw Chunk", Kind.ITEM),
    GEM("gem", "Gem", Kind.ITEM),
    FLAWLESS_GEM("flawless_gem", "Flawless Gem", Kind.ITEM),
    EXQUISITE_GEM("exquisite_gem", "Exquisite Gem", Kind.ITEM),

    PLATE("plate", "Plate", Kind.ITEM),
    DOUBLE_PLATE("double_plate", "Double Plate", Kind.ITEM),
    FOIL("foil", "Foil", Kind.ITEM),
    ROD("rod", "Rod", Kind.ITEM),
    LONG_ROD("long_rod", "Long Rod", Kind.ITEM),
    BOLT("bolt", "Bolt", Kind.ITEM),
    SCREW("screw", "Screw", Kind.ITEM),
    WIRE("wire", "Wire", Kind.ITEM),
    FINE_WIRE("fine_wire", "Fine Wire", Kind.ITEM),
    RING("ring", "Ring", Kind.ITEM),
    SMALL_RING("small_ring", "Small Ring", Kind.ITEM),
    LARGE_RING("large_ring", "Large Ring", Kind.ITEM),

    GEAR("gear", "Gear", Kind.ITEM),
    SMALL_GEAR("small_gear", "Small Gear", Kind.ITEM),
    LARGE_GEAR("large_gear", "Large Gear", Kind.ITEM),
    BEARING_BALL("bearing_ball", "Bearing Ball", Kind.ITEM),
    BEARING("bearing", "Bearing", Kind.ITEM),
    SPRING("spring", "Spring", Kind.ITEM),
    COIL("coil", "Coil", Kind.ITEM),
    ROTOR("rotor", "Rotor", Kind.ITEM),
    TOOL_HEAD_BUZZ_SAW("tool_head_buzz_saw", "Buzz Saw Tool Head", Kind.ITEM),
    FRAME("frame", "Frame", Kind.BLOCK),
    CASING("casing", "Casing", Kind.BLOCK),
    MACHINE_HULL("machine_hull", "Machine Hull", Kind.BLOCK),

    MOLTEN_FLUID("molten_fluid", "Molten Fluid", Kind.FLUID),
    CAST_INGOT("cast_ingot", "Cast Ingot", Kind.ITEM),
    CAST_NUGGET("cast_nugget", "Cast Nugget", Kind.ITEM),
    CAST_BLOCK("cast_block", "Cast Block", Kind.ITEM),
    CAST_PLATE("cast_plate", "Cast Plate", Kind.ITEM),
    CAST_ROD("cast_rod", "Cast Rod", Kind.ITEM),
    CAST_LONG_ROD("cast_long_rod", "Cast Long Rod", Kind.ITEM),
    CAST_BOLT("cast_bolt", "Cast Bolt", Kind.ITEM),
    CAST_SCREW("cast_screw", "Cast Screw", Kind.ITEM),
    CAST_RING("cast_ring", "Cast Ring", Kind.ITEM),
    CAST_SMALL_RING("cast_small_ring", "Cast Small Ring", Kind.ITEM),
    CAST_LARGE_RING("cast_large_ring", "Cast Large Ring", Kind.ITEM),
    CAST_GEAR("cast_gear", "Cast Gear", Kind.ITEM),
    CAST_SMALL_GEAR("cast_small_gear", "Cast Small Gear", Kind.ITEM),
    CAST_BEARING_BALL("cast_bearing_ball", "Cast Bearing Ball", Kind.ITEM),
    CAST_BEARING("cast_bearing", "Cast Bearing", Kind.ITEM),
    CAST_ROTOR("cast_rotor", "Cast Rotor", Kind.ITEM),
    CAST_NUGGET_MOLD("cast_nugget_mold", "Cast Nugget Mold", Kind.ITEM),
 //   CAST_BLOCK_MOLD("cast_block_mold", "Cast Block Mold", Kind.ITEM),
    CAST_BEARING_BALL_MOLD("cast_bearing_ball_mold", "Cast Bearing Ball Mold", Kind.ITEM),
    CAST_ROTOR_MOLD("cast_rotor_mold", "Cast Rotor Mold", Kind.ITEM),
    CAST_INGOT_MOLD("cast_ingot_mold", "Cast Ingot Mold", Kind.ITEM),
    CAST_PLATE_MOLD("cast_plate_mold", "Cast Plate Mold", Kind.ITEM),
    CAST_ROD_MOLD("cast_rod_mold", "Cast Rod Mold", Kind.ITEM),
    CAST_LONG_ROD_MOLD("cast_long_rod_mold", "Cast Long Rod Mold", Kind.ITEM),
    CAST_BOLT_MOLD("cast_bolt_mold", "Cast Bolt Mold", Kind.ITEM),
    CAST_RING_MOLD("cast_ring_mold", "Cast Ring Mold", Kind.ITEM),
    CAST_SMALL_RING_MOLD("cast_small_ring_mold", "Cast Small Ring Mold", Kind.ITEM),
    CAST_LARGE_RING_MOLD("cast_large_ring_mold", "Cast Large Ring Mold", Kind.ITEM),
    CAST_GEAR_MOLD("cast_gear_mold", "Cast Gear Mold", Kind.ITEM),
    CAST_SMALL_GEAR_MOLD("cast_small_gear_mold", "Cast Small Gear Mold", Kind.ITEM),
    CAST_BEARING_MOLD("cast_bearing_mold", "Cast Bearing Mold", Kind.ITEM),
    CAST_SCREW_MOLD("cast_screw_mold", "Cast Screw Mold", Kind.ITEM),
    HOT_CAST_NUGGET_MOLD("hot_cast_nugget_mold", "Hot Cast Nugget Mold", Kind.ITEM),
  //  HOT_CAST_BLOCK_MOLD("hot_cast_block_mold", "Hot Cast Block Mold", Kind.ITEM),
    HOT_CAST_BEARING_BALL_MOLD("hot_cast_bearing_ball_mold", "Hot Cast Bearing Ball Mold", Kind.ITEM),
    HOT_CAST_ROTOR_MOLD("hot_cast_rotor_mold", "Hot Cast Rotor Mold", Kind.ITEM),
    HOT_CAST_INGOT_MOLD("hot_cast_ingot_mold", "Hot Cast Ingot Mold", Kind.ITEM),
    HOT_CAST_PLATE_MOLD("hot_cast_plate_mold", "Hot Cast Plate Mold", Kind.ITEM),
    HOT_CAST_ROD_MOLD("hot_cast_rod_mold", "Hot Cast Rod Mold", Kind.ITEM),
    HOT_CAST_LONG_ROD_MOLD("hot_cast_long_rod_mold", "Hot Cast Long Rod Mold", Kind.ITEM),
    HOT_CAST_BOLT_MOLD("hot_cast_bolt_mold", "Hot Cast Bolt Mold", Kind.ITEM),
    HOT_CAST_RING_MOLD("hot_cast_ring_mold", "Hot Cast Ring Mold", Kind.ITEM),
    HOT_CAST_SMALL_RING_MOLD("hot_cast_small_ring_mold", "Hot Cast Small Ring Mold", Kind.ITEM),
    HOT_CAST_LARGE_RING_MOLD("hot_cast_large_ring_mold", "Hot Cast Large Ring Mold", Kind.ITEM),
    HOT_CAST_GEAR_MOLD("hot_cast_gear_mold", "Hot Cast Gear Mold", Kind.ITEM),
    HOT_CAST_SMALL_GEAR_MOLD("hot_cast_small_gear_mold", "Hot Cast Small Gear Mold", Kind.ITEM),
    HOT_CAST_BEARING_MOLD("hot_cast_bearing_mold", "Hot Cast Bearing Mold", Kind.ITEM),
    HOT_CAST_SCREW_MOLD("hot_cast_screw_mold", "Hot Cast Screw Mold", Kind.ITEM),

    OXIDE_DUST("oxide_dust", "Oxide Dust", Kind.ITEM),
    SULFIDE_DUST("sulfide_dust", "Sulfide Dust", Kind.ITEM),
    CHLORIDE_DUST("chloride_dust", "Chloride Dust", Kind.ITEM),
    PURIFIED_DUST("purified_dust", "Purified Dust", Kind.ITEM),
    WASHED_CRUSHED_ORE("washed_crushed_ore", "Washed Crushed Ore", Kind.ITEM),
    SLURRY("slurry", "Slurry", Kind.FLUID),
    SOLUTION("solution", "Solution", Kind.FLUID),

    REINFORCED_PLATE("reinforced_plate", "Reinforced Plate", Kind.ITEM),
    DENSE_PLATE("dense_plate", "Dense Plate", Kind.ITEM),
    HEAT_EXCHANGER_PLATE("heat_exchanger_plate", "Heat Exchanger Plate", Kind.ITEM);

    private final String id;
    private final String displayName;
    private final Kind kind;

    MaterialPart(String id, String displayName, Kind kind) {
        this.id = id;
        this.displayName = displayName;
        this.kind = kind;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Kind kind() {
        return kind;
    }

    public boolean isItem() {
        return kind == Kind.ITEM;
    }

    public boolean isBlock() {
        return kind == Kind.BLOCK;
    }

    public boolean isFluid() {
        return kind == Kind.FLUID;
    }

    public String registryName(IndustrialMaterial material) {
        return material.id() + "_" + id;
    }

    public String readableName(IndustrialMaterial material) {
        String stonePrefix = oreStonePrefix();
        if (stonePrefix != null) {
            return stonePrefix + " " + material.displayName() + " Ore";
        }

        if (this == RAW_ORE) {
            return "Raw " + material.displayName();
        }

        if (this == RAW_BLOCK) {
            return "Block of Raw " + material.displayName();
        }

        if (this == BLOCK) {
            return "Block of " + material.displayName();
        }

        if (this == MOLTEN_FLUID) {
            return "Molten " + material.displayName();
        }

        return material.displayName() + " " + displayName;
    }

    private String oreStonePrefix() {
        return switch (this) {
            case DEEPSLATE_ORE -> "Deepslate";
            case DIORITE_ORE -> "Diorite";
            case ANDESITE_ORE -> "Andesite";
            case GRANITE_ORE -> "Granite";
            case TUFF_ORE -> "Tuff";
            case NETHERRACK_ORE -> "Netherrack";
            case BLACKSTONE_ORE -> "Blackstone";
            case END_STONE_ORE -> "End Stone";
            default -> null;
        };
    }

    public enum Kind {
        ITEM,
        BLOCK,
        FLUID
    }
}
