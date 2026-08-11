package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class DistilleryRecipeType {
    public static final RecipeTypeDefinition DISTILLERY = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("distillery"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Distillery"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 1, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
            .build();
}
