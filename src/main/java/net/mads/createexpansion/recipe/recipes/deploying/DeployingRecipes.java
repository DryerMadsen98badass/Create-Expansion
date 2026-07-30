package net.mads.createexpansion.recipe.recipes.deploying;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class DeployingRecipes {
    private DeployingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.deploying(futures, output, recipes, "deploying/test/apple_to_golden_apple")
                .inputItem("minecraft:apple")
                .inputItem("minecraft:gold_ingot")
                .outputItem("minecraft:golden_apple")
                .save();
    }
}
