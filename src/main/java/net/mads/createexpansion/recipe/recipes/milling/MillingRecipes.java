package net.mads.createexpansion.recipe.recipes.milling;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class MillingRecipes {
    private MillingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.milling(futures, output, recipes, "milling/material/nether_brick_dust")
                .inputItem("minecraft:nether_brick")
                .outputItem("create_expansion:nether_brick_dust")
                .duration(100)
                .save();

        CreateRecipeBuilder.milling(futures, output, recipes, "milling/material/clay_dust")
                .inputItem("minecraft:clay_ball")
                .outputItem("create_expansion:clay_dust")
                .duration(100)
                .save();
    }
}
