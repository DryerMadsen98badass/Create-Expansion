package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class CompressorRecipeType {
    public static final RecipeTypeDefinition COMPRESSOR = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("compressor"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Compressor"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 0, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.HAMMER_BRONZE))
            .build();
}
