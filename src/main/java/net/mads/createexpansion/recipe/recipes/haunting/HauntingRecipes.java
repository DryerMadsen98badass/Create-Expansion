package net.mads.createexpansion.recipe.recipes.haunting;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class HauntingRecipes {
    private HauntingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.haunting(futures, output, recipes, "haunting/test/sand_to_soul_sand")
                .inputItem("minecraft:sand")
                .outputItem("minecraft:soul_sand")
                .save();
    }
}
