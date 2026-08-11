package net.mads.createexpansion.block;

import java.util.List;

public final class SimpleBlocks {

    public static final List<SimpleBlockDefinition> ALL =
            List.of(
                    block("firebricks", "Firebricks").mineableWith(MiningTool.PICKAXE).iron(),
                    block("coal_coke_block", "Coal Coke Block").mineableWith(MiningTool.PICKAXE).wood(),
                    block("silica_bricks", "Silica Bricks").mineableWith(MiningTool.PICKAXE).iron(),
                    block("tempered_glass", "Tempered Glass").mineableWith(MiningTool.PICKAXE, MiningTool.AXE).stone(),
                    block("hot_tempered_glass", "Hot Tempered Glass").mineableWith(MiningTool.PICKAXE, MiningTool.AXE).stone(),

                    blockColor("treated_wood", "Treated Wood", "minecraft:block/oak_planks", 0xFF6B4526).mineableWith(MiningTool.AXE).stone().all(),



                    //Brass
                    blockColor("brass_machine_casing", "Brass Machine Casing", "block/casings/casing/variants/casings/variant_10", 0xFFE5B94C).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("sturdy_brass_casing", "Sturdy Brass Casing", "block/casings/casing/variants/casings/variant_5", 0xFFEEC168).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("sealed_brass_casing", "Sealed Brass Casing", "block/casings/casing/variants/casings/variant_7", 0xFFD6A93F).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("brass_gearbox_casing", "Brass Gearbox Casing", "block/casings/casing/variants/gearbox/variant_1", 0xFFE5B94C).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("brass_pipe_casing", "Brass Pipe Casing", "block/casings/casing/variants/pipe/variant_3", 0xFFE5B94C).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("brass_sifting_mesh", "Brass Sifting Mesh", "block/casings/casing/variants/unique/variant_1", 0xFFE5B94C).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("ebonite_lined_brass_casing", "Ebonite-Lined Brass Casing", "block/casings/casing/variants/casings/variant_5", 0xFFD6A93F).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("ebonite_lined_brass_pipe_casing", "Ebonite-Lined Brass Pipe Casing", "block/casings/casing/variants/pipe/variant_3", 0xFFD6A93F).mineableWith(MiningTool.PICKAXE).iron(),

                    //Steel
                    blockColor("riveted_steel_casing", "Riveted Steel Casing", "block/casings/casing/variants/casings/variant_13", 0xFF7D8588).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("heat_resistant_steel_casing", "Heat-Resistant Steel Casing", "block/casings/casing/variants/casings/variant_2", 0xFF555B5D).mineableWith(MiningTool.PICKAXE).iron(),

                    //Bronze
                    blockColor("bronze_machine_casing", "Bronze Machine Casing", "block/casings/casing/variants/casings/variant_5", 0xFFCD7F32).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("reinforced_bronze_casing", "Reinforced Bronze Casing", "block/casings/casing/variants/casings/variant_31", 0xFFCD7F32).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("pressure_sealed_bronze_casing", "Pressure-Sealed Bronze Casing", "block/casings/casing/variants/casings/variant_32", 0xFFCD7F32).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("pressure_sealed_bronze_pipe_casing", "Pressure-Sealed Bronze Pipe Casing", "block/casings/casing/variants/pipe/variant_3", 0xFFCD7F32).mineableWith(MiningTool.PICKAXE).iron(),
                    blockColor("heat_resistant_bronze_casing", "Heat-Resistant Bronze Casing", "block/casings/casing/variants/casings/variant_6", 0xFFCD7F32).mineableWith(MiningTool.PICKAXE).iron(),

                    blockCustom("bricked_copper_casing", "Bricked Copper Casing", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_top", "block/casings/casing/bricked_copper_casing_bottom").mineableWith(MiningTool.PICKAXE).iron(),
                    blockCustom("bricked_bronze_casing", "Bricked Bronze Casing", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_top", "block/casings/casing/bricked_bronze_casing_bottom").mineableWith(MiningTool.PICKAXE).iron()

            );

    public static final List<ActiveBlockDefinition> ACTIVE = List.of(
            activeBlock("firebrick_firebox", "Firebrick Firebox", "block/casings/firebox/firebrick_firebox_off", "block/casings/firebox/firebrick_firebox_on").iron().mineableWith(MiningTool.PICKAXE)
    );

    private SimpleBlocks() {
    }

    private static SimpleBlockDefinition block(String id, String displayName) { return new SimpleBlockDefinition(id, displayName); }

    private static SimpleBlockDefinition blockColor(String id, String displayName, String texture, int color) { return new SimpleBlockDefinition(id, displayName, texture, color); }

    private static SimpleBlockDefinition blockCustom(String id, String displayName, String frontTexture, String backTexture, String leftTexture, String rightTexture, String topTexture, String bottomTexture) { return new SimpleBlockDefinition(id, displayName, frontTexture, backTexture, leftTexture, rightTexture, topTexture, bottomTexture); }

    private static ActiveBlockDefinition activeBlock(String id, String displayName, String idleTexture, String... activeTextures) { return new ActiveBlockDefinition(id, displayName, idleTexture, activeTextures); }
}
