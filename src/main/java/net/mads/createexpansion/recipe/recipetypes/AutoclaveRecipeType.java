package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class AutoclaveRecipeType {
    public static final RecipeTypeDefinition AUTOCLAVE = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("autoclave"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Autoclave"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(3, 3, 1, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();
}
