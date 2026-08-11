package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class FluidSolidifierRecipeType {
    public static final RecipeTypeDefinition FLUID_SOLIDIFIER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("fluid_solidifier"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Fluid Solidifier"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 1, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.EXTRACT))
            .build();
}
