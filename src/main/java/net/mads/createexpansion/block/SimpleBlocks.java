package net.mads.createexpansion.block;

import java.util.List;

public final class SimpleBlocks {

    public static final List<SimpleBlockDefinition> ALL =
            List.of(
                    block("firebricks", "Firebricks").mineableWith(MiningTool.PICKAXE).iron(),
                    block("coal_coke_block", "Coal Coke Block").mineableWith(MiningTool.PICKAXE).wood(),
                    block("silica_bricks", "Silica Bricks").mineableWith(MiningTool.PICKAXE).iron(),

                    blockColor("treated_wood", "Treated Wood", "minecraft:block/oak_planks", 0xFF6B4526).mineableWith(MiningTool.AXE).stone().all(),

                    blockCustom("bricked_copper_casing", "Bricked Copper Casing", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_side", "block/casings/casing/bricked_copper_casing_top", "block/casings/casing/bricked_copper_casing_bottom").mineableWith(MiningTool.PICKAXE).iron(),
                    blockCustom("bricked_bronze_casing", "Bricked Bronze Casing", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_side", "block/casings/casing/bricked_bronze_casing_top", "block/casings/casing/bricked_bronze_casing_bottom").mineableWith(MiningTool.PICKAXE).iron()

            );

    private SimpleBlocks() {
    }

    private static SimpleBlockDefinition block(
            String id,
            String displayName
    ) {
        return new SimpleBlockDefinition(
                id,
                displayName
        );
    }

    private static SimpleBlockDefinition blockColor(
            String id,
            String displayName,
            String texture,
            int color
    ) {
        return new SimpleBlockDefinition(
                id,
                displayName,
                texture,
                color
        );
    }

    private static SimpleBlockDefinition blockCustom(
            String id,
            String displayName,
            String northTexture,
            String eastTexture,
            String southTexture,
            String westTexture,
            String topTexture,
            String bottomTexture
    ) {
        return new SimpleBlockDefinition(
                id,
                displayName,
                northTexture,
                eastTexture,
                southTexture,
                westTexture,
                topTexture,
                bottomTexture
        );
    }
}
