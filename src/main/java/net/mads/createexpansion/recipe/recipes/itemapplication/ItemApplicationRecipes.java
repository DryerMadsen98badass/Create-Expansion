package net.mads.createexpansion.recipe.recipes.itemapplication;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ItemApplicationRecipes {
    private ItemApplicationRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.itemApplication(futures, output, recipes, "item_application/test/apple_to_golden_apple")
                .inputItem("minecraft:apple")
                .inputItem("minecraft:gold_ingot")
                .outputItem("minecraft:golden_apple")
                .save();
    }
}
