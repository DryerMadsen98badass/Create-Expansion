package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class SteaminductionchamberRecipeType {
    public static final RecipeTypeDefinition STEAM_INDUCTION_CHAMBER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("steam_induction_chamber"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Steam Induction Chamber"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(2, 2, 0, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();
}
