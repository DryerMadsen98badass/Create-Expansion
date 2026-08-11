package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class InductionChamberRecipeType {
    public static final RecipeTypeDefinition INDUCTION_CHAMBER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("induction_chamber"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Induction Chamber"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(2, 2, 0, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();
}
