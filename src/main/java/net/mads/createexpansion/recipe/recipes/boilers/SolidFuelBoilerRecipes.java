package net.mads.createexpansion.recipe.recipes.boilers;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class SolidFuelBoilerRecipes {

    private SolidFuelBoilerRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("furnace_fuels")
                .recipeDefinition(RecipeDefinition.Option.furnaceFuel())
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("solid_fuel_boiler/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.SOLID_FUEL_BOILER));
    }
}