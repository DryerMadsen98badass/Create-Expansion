package net.mads.createexpansion.recipe.recipes.induction_chamber;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class SteamInductionChamberRecipes {

    private SteamInductionChamberRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("activated_carbon")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:bio_char_dust", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:activated_carbon_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(600))
                .save(output);
        recipe("mineral_ash_from_biomass")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:biomass", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:mineral_ash_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("steam_induction_chamber/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.STEAM_INDUCTION_CHAMBER));
    }
}