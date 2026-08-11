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
                .outputFluid("create_expansion:biofuel", 250)
                .duration(500)
                .save();
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/creosote_fuel")
                .inputFluid("create_expansion:creosote_oil", 750)
                .inputFluid("create_expansion:wood_tar", 250)
                .outputFluid("create_expansion:creosote_fuel", 1000)
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
        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/molten_rubber")
                .inputItem("create_expansion:natural_rubber_ingot")
                .inputItem("create_expansion:sulfur_tiny_dust")
                .outputFluid("create_expansion:molten_rubber", 144)
                .heated()
                .duration(400)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/glue")
                .inputFluid("create_expansion:pine_resin", 250)
                .inputFluid("minecraft:water", 100)
                .outputFluid("create_expansion:glue", 250)
                .duration(600)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/fermentation_mash_from_birch_syrup")
                .inputFluid("create_expansion:birch_syrup", 250)
                .inputFluid("minecraft:water", 250)
                .outputFluid("create_expansion:fermentation_mash", 500)
                .duration(400)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/organic_binder_from_gum_arabic")
                .inputFluid("create_expansion:gum_arabic", 250)
                .inputFluid("minecraft:water", 100)
                .outputFluid("create_expansion:organic_binder", 250)
                .duration(400)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/corrosion_resistant_solution")
                .inputFluid("create_expansion:pine_resin", 100)
                .inputFluid("create_expansion:mangrove_tannin", 100)
                .outputFluid("create_expansion:corrosion_resistant_solution", 200)
                .duration(600)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/wet_biomass")
                .inputFluid("minecraft:water", 10)
                .inputItem("create_expansion:biomass", 1)
                .outputItem("create_expansion:wet_biomass", 2)
                .duration(200)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/fermentation_mash")
                .inputFluid("minecraft:water", 250)
                .inputItem("create_expansion:wet_biomass", 1)
                .outputFluid("create_expansion:fermentation_mash", 250)
                .duration(200)
                .save();

        CreateRecipeBuilder.mixing(futures, output, recipes, "mixing/liquid_fertilizer")
                .inputFluid("minecraft:water", 250)
                .inputItem("create_expansion:fertilizer_dust", 1)
                .outputFluid("create_expansion:liquid_fertilizer", 250)
                .duration(100)
                .save();
    }
}
