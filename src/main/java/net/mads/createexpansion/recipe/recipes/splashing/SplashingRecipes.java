package net.mads.createexpansion.recipe.recipes.splashing;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SplashingRecipes {
    private SplashingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.splashing(futures, output, recipes, "splashing/test/red_sand_to_sand")
                .inputItem("minecraft:red_sand")
                .outputItem("minecraft:sand")
                .duration(100)
                .save();
    }
}
