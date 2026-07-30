package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record MultiblockControllerDefinition(
        String registryName,
        String displayName,
        String casingTexture,
        String offOverlayTexture,
        String onOverlayTexture,
        boolean tinted,
        int tintColor
) {
    private static final List<MultiblockControllerDefinition> ALL = new ArrayList<>();

    public static MultiblockControllerDefinition controller(
            MultiblockControllerDefinition controller
    ) {
        return controller;
    }

    public ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                registryName
        );
    }

    public static MultiblockControllerDefinition of(
            String registryName,
            String displayName,
            String casingTexture,
            String offOverlayTexture,
            String onOverlayTexture
    ) {
        MultiblockControllerDefinition definition =
                new MultiblockControllerDefinition(
                        registryName,
                        displayName,
                        casingTexture,
                        offOverlayTexture,
                        onOverlayTexture,
                        false,
                        -1
                );

        register(definition);
        return definition;
    }

    public static MultiblockControllerDefinition tinted(
            String registryName,
            String displayName,
            String casingTexture,
            String offOverlayTexture,
            String onOverlayTexture,
            int tintColor
    ) {
        MultiblockControllerDefinition definition =
                new MultiblockControllerDefinition(
                        registryName,
                        displayName,
                        casingTexture,
                        offOverlayTexture,
                        onOverlayTexture,
                        true,
                        tintColor
                );

        register(definition);
        return definition;
    }

    public static List<MultiblockControllerDefinition> all() {
        return Collections.unmodifiableList(ALL);
    }

    private static void register(
            MultiblockControllerDefinition definition
    ) {
        boolean alreadyRegistered = ALL.stream()
                .anyMatch(existing ->
                        existing.registryName().equals(definition.registryName())
                );

        if (!alreadyRegistered) {
            ALL.add(definition);
        }
    }
}