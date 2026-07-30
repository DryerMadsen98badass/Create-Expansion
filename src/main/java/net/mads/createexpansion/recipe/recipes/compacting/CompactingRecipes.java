package net.mads.createexpansion.recipe.recipes.compacting;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CompactingRecipes {
    private CompactingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.compacting(futures, output, recipes, "compacting/test/apple_to_golden_apple")
                .inputItem("minecraft:apple", 5)
                .outputItem("minecraft:golden_apple", 2).chance(0.1F)
                .duration(100)
                .save();
    }
}
