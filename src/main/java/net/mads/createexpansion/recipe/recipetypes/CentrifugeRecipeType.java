package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class CentrifugeRecipeType {
    public static final RecipeTypeDefinition CENTRIFUGE = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("centrifuge"))
            .recipeTypeDefinition(Option.displayName("Centrifuge"))
            .recipeTypeDefinition(Option.maxIO(2, 9, 1, 3))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.EXTRACT))
            .build();

    private CentrifugeRecipeType() {
    }
}
