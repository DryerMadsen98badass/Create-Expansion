package net.mads.createexpansion.recipe.recipes.crushing;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CrushingRecipes {
    private CrushingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
      //  CreateRecipeBuilder.crushing(futures, output, recipes, "crushing/clay_dust")
      //          .inputItem("minecraft:clay_ball")
      //          .outputItem("create_expansion:clay_dust")
      //          .duration(100)
      //          .save();
    }
}
