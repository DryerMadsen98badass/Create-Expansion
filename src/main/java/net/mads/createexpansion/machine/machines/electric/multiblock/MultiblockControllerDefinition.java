package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

public record MultiblockControllerDefinition(
        String registryName,
        String displayName,
        String casingTexture,
        String offOverlayTexture,
        String onOverlayTexture,
        boolean tinted,
        int tintColor
) {
    public ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, registryName);
    }

    public static MultiblockControllerDefinition of(String registryName, String displayName, String casingTexture, String offOverlayTexture, String onOverlayTexture) {
        return new MultiblockControllerDefinition(registryName, displayName, casingTexture, offOverlayTexture, onOverlayTexture, false, -1);
    }

    public static MultiblockControllerDefinition tinted(String registryName, String displayName, String casingTexture, String offOverlayTexture, String onOverlayTexture, int tintColor) {
        return new MultiblockControllerDefinition(registryName, displayName, casingTexture, offOverlayTexture, onOverlayTexture, true, tintColor);
    }
}
