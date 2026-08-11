package net.mads.createexpansion.recipe.recipes.distillery;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class LVDistilleryRecipes {

    private LVDistilleryRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("ethanol_from_fermented_mash")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:fermented_mash", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:ethanol", 100))
                .recipeDefinition(RecipeDefinition.Option.chancedOutputItem("create_expansion:wet_biomass", 1, 1000))
                .recipeDefinition(RecipeDefinition.Option.duration(50))
                .recipeDefinition(RecipeDefinition.Option.circuit(1))
                .save(output);
        recipe("plant_oil_from_fermented_mash")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:fermented_mash", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:plant_oil", 10))
                .recipeDefinition(RecipeDefinition.Option.chancedOutputItem("create_expansion:wet_biomass", 1, 1000))
                .recipeDefinition(RecipeDefinition.Option.duration(50))
                .recipeDefinition(RecipeDefinition.Option.circuit(2))
                .save(output);
        recipe("wood_tar_from_dark_oak_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:dark_oak_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:wood_tar", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(50))
                .save(output);
        recipe("birch_syrup_from_birch_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:birch_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:birch_syrup", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("gum_arabic_from_acacia_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:acacia_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:gum_arabic", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("aromatic_extract_from_cherry_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:cherry_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:aromatic_extract", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("biolubricant_from_creosote_oil")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:creosote_oil", 12))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:biolubricant", 6))
                .recipeDefinition(RecipeDefinition.Option.duration(20))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("distillery/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.LV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.DISTILLERY));
    }
}