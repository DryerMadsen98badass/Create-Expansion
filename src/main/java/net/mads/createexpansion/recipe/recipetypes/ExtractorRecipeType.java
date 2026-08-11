package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class ExtractorRecipeType {
    public static final RecipeTypeDefinition EXTRACTOR = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("extractor"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Extractor"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 0, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
            .build();
}
