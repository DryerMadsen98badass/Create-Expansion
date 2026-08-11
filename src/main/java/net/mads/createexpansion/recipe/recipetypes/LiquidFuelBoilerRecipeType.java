package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class LiquidFuelBoilerRecipeType {
    public static final RecipeTypeDefinition LIQUID_FUEL_BOILER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("liquid_fuel_boiler"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Liquid Fuel Boiler"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(0, 0, 1, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();
}
