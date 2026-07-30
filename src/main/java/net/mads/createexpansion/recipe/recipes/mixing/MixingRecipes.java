package net.mads.createexpansion.recipe.recipes.mixing;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class MixingRecipes {
    private MixingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/seared_dust")
                .inputItem("create_expansion:tuff_dust")
                .inputItem("create_expansion:nether_brick_dust")
                .inputItem("create_expansion:clay_dust")
                .outputItem("create_expansion:seared_dust", 3)
                .duration(600)
                .save();
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/concrete")
                .inputItem("minecraft:clay_ball")
                .inputFluid("minecraft:water", 250)
                .outputFluid("create_expansion:concrete", 250)
                .duration(600)
                .save();
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/fermented_mash")
                .inputFluid("create_expansion:fermentation_mash", 250)
                .outputFluid("create_expansion:fermented_mash", 250)
                .duration(2400)
                .save();
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/biofuel")
                .inputFluid("create_expansion:plant_oil", 200)
                .inputFluid("create_expansion:ethanol", 100)
                .inputItem("create_expansion:biofuel_catalyst")
                .outputFluid("create_expansion:biofuel", 250)
                .heated()
                .duration(500)
                .save();
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/biolubricant")
                .inputFluid("create_expansion:plant_oil", 250)
                .inputItem("create_expansion:plant_wax")
                .outputFluid("create_expansion:biolubricant", 250)
                .heated()
                .duration(400)
                .save();
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/organic_binder")
                .inputFluid("create_expansion:industrial_resin", 200)
                .inputItem("create_expansion:plant_fiber", 2)
                .outputFluid("create_expansion:organic_binder", 250)
                .heated()
                .duration(400)
                .save();
    }
}
