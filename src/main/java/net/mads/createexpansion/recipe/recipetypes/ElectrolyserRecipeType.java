package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class ElectrolyserRecipeType {
    public static final RecipeTypeDefinition ELECTROLYSER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("electrolyser"))
            .recipeTypeDefinition(Option.displayName("Electrolyser"))
            .recipeTypeDefinition(Option.maxIO(2, 9, 1, 3))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.EXTRACT))
            .build();

    private ElectrolyserRecipeType() {
    }
}
