package net.mads.createexpansion.recipe.remove;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class RemovedCreateRecipes {
    public static final List<ResourceLocation> ALL = List.of(
            ResourceLocation.fromNamespaceAndPath("create", "pressing/iron_ingot"),
            ResourceLocation.fromNamespaceAndPath("create", "pressing/gold_ingot"),
            ResourceLocation.fromNamespaceAndPath("create", "pressing/copper_ingot"),
            ResourceLocation.fromNamespaceAndPath("create", "pressing/brass_ingot")
    );

    private RemovedCreateRecipes() {
    }
}
