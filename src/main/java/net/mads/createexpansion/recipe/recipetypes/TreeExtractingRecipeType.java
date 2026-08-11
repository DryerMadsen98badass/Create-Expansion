package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class TreeExtractingRecipeType {
    public static final RecipeTypeDefinition TREE_EXTRACTING = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("tree_extracting"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Tree Extracting"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(0, 1, 0, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
            .build();

    private TreeExtractingRecipeType() {
    }
}
