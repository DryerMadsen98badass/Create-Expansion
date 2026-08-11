package net.mads.createexpansion.recipe.recipes.extractor;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVExtractorRecipes {

    private ULVExtractorRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        /*
         * BIOMASS DEWATERING
         */

        recipe("biomass_from_wet_biomass")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:wet_biomass", 2))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("minecraft:water", 10))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);


        /*
         * WET BIOMASS
         */

        recipe("wet_biomass_from_carrots")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:carrot", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_cactus")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:cactus", 2))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_kelp")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:kelp", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_cocoa_beans")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:cocoa_beans", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_bamboo")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:bamboo", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_vines")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:vine", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_lily_pads")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:lily_pad", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_brown_mushrooms")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:brown_mushroom", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_red_mushrooms")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:red_mushroom", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_azalea")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:azalea", 3))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("wet_biomass_from_flowering_azalea")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:flowering_azalea", 3))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);


        /*
         * FERMENTATION MASH
         */

        recipe("fermentation_mash_from_sugar_cane")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:sugar_cane", 8))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 100))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(40))
                .save(output);

        recipe("fermentation_mash_from_beetroots")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:beetroot", 8))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 75))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(40))
                .save(output);

        recipe("fermentation_mash_from_potatoes")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:potato", 8))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 50))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(45))
                .save(output);

        recipe("fermentation_mash_from_wheat")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:wheat", 8))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 50))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(45))
                .save(output);

        recipe("fermentation_mash_from_melon")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:melon", 2))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 50))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(35))
                .save(output);

        recipe("fermentation_mash_from_melon_slices")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:melon_slice", 18))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 50))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(35))
                .save(output);

        recipe("fermentation_mash_from_pumpkin")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:pumpkin", 2))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 35))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(40))
                .save(output);

        recipe("fermentation_mash_from_sweet_berries")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:sweet_berries", 12))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 35))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(35))
                .save(output);

        recipe("fermentation_mash_from_glow_berries")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:glow_berries", 12))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:fermentation_mash", 35))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(35))
                .save(output);


        /*
         * PLANT OIL
         */

        recipe("plant_oil_from_wheat_seeds")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:wheat_seeds", 8))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 20))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_oil_from_beetroot_seeds")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:beetroot_seeds", 8))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 20))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_oil_from_melon_seeds")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:melon_seeds", 4))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 30))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_oil_from_pumpkin_seeds")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:pumpkin_seeds", 4))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 30))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_oil_from_torchflower_seeds")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:torchflower_seeds", 4))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 40))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_oil_from_pitcher_pods")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:pitcher_pod", 4))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 40))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_oak_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:oak_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_spruce_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:spruce_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_birch_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:birch_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_jungle_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:jungle_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_acacia_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:acacia_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_dark_oak_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:dark_oak_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_mangrove_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:mangrove_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_cherry_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:cherry_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_azalea_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:azalea_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);

        recipe("plant_fiber_from_flowering_azalea_leaves")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:flowering_azalea_leaves", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_fiber", 2))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);


    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("extractor/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.EXTRACTOR));
    }
}