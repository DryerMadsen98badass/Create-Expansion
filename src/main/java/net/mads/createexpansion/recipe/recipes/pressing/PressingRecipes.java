package net.mads.createexpansion.recipe.recipes.pressing;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class PressingRecipes {
    private PressingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.pressing(futures, output, recipes, "pressing/test/iron_ingot_to_sheet")
                .inputItem("minecraft:iron_ingot")
                .outputItem("create:iron_sheet")
                .duration(100)
                .save();
    }
}
