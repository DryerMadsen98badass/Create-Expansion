package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

/** Recipes that keep a sprinkler active for a duration using one item or fluid input. */
public final class SprinklingRecipeType {
    public static final RecipeTypeDefinition SPRINKLING = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("sprinkling"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Sprinkling"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 0, 1, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();

    private SprinklingRecipeType() {
    }
}
