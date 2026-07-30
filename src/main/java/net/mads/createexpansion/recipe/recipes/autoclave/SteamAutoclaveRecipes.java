package net.mads.createexpansion.recipe.recipes.autoclave;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class SteamAutoclaveRecipes {
    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("ethanol_from_fermentation_mash")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:fermentation_mash", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:ethanol", 100))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
        recipe("industrial_resin_from_raw_resin")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:raw_resin", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:industrial_resin", 200))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
        recipe("polished_rose_quartz_from_rose_quartz")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create:rose_quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.chancedOutput("create:polished_rose_quartz", 1, 5000))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("autoclave/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.STEAM_AUTOCLAVE));
    }
}