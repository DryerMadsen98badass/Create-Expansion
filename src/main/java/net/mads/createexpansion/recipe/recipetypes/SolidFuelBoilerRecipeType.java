package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class SolidFuelBoilerRecipeType {
    public static final RecipeTypeDefinition SOLID_FUEL_BOILER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("solid_fuel_boiler"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Solid Fuel Boiler"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 0, 1, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
            .build();

    private SolidFuelBoilerRecipeType() {
    }
}
