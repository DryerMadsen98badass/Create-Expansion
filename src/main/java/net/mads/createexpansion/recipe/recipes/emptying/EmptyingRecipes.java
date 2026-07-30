package net.mads.createexpansion.recipe.recipes.emptying;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class EmptyingRecipes {
    private EmptyingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.emptying(futures, output, recipes, "emptying/test/water_bucket")
                .inputItem("minecraft:water_bucket")
                .outputItem("minecraft:bucket")
                .outputFluid("minecraft:water", 1000)
                .save();
    }
}
