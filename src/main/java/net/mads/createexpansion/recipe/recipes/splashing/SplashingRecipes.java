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
        CreateRecipeBuilder.splashing(futures, output, recipes, "splashing/tempered_glass")
                .inputItem("create_expansion:hot_tempered_glass", 1)
                .outputItem("create_expansion:tempered_glass", 1)
                .duration(1200)
                .save();


    }
}
