package net.mads.createexpansion.recipe.recipes.distillery;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class steam_distillery_recipes {

    private steam_distillery_recipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("ethanol_from_fermented_mash")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:fermented_mash", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:ethanol", 100))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);

        recipe("plant_wax_from_plant_oil")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:plant_oil", 250))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_wax", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("steam_distillery/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.STEAM_DISTILLERY));
    }
}