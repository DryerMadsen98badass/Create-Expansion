package net.mads.createexpansion.block;

import java.util.List;

public final class ActiveBlocks {

    public static final List<ActiveBlockDefinition> ALL =
            List.of(
                    activeBlock("firebrick_firebox", "Firebrick Firebox", "block/casings/firebox/firebrick_firebox_off", "block/casings/firebox/firebrick_firebox_on").iron().mineableWith(MiningTool.PICKAXE)
            );

    private ActiveBlocks() {
    }

    private static ActiveBlockDefinition activeBlock(
            String id,
            String displayName,
            String offTexture,
            String onTexture
    ) {
        return new ActiveBlockDefinition(
                id,
                displayName,
                offTexture,
                onTexture
        );
    }
}