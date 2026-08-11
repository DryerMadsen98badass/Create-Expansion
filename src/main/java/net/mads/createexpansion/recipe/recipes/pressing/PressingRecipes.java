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
        CreateRecipeBuilder.pressing(futures, output, recipes, "pressing/natural_rubber_plate")
                .inputItem("create_expansion:natural_rubber_ingot")
                .outputItem("create_expansion:natural_rubber_plate")
                .save();
        CreateRecipeBuilder.pressing(futures, output, recipes, "pressing/treated_wood_plate")
                .inputItem("create_expansion:treated_wood_slab")
                .outputItem("create_expansion:treated_wood_plate")
                .save();
    }
}
