package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class AlloySmelterRecipeType {
    public static final RecipeTypeDefinition ALLOY_SMELTER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("alloy_smelter"))
            .recipeTypeDefinition(Option.displayName("Alloy Smelter"))
            .recipeTypeDefinition(Option.maxIO(2, 1, 0, 0))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();


}
