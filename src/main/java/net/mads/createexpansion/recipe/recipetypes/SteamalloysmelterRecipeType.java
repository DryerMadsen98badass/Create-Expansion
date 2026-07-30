package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class SteamalloysmelterRecipeType {
    public static final RecipeTypeDefinition STEAM_ALLOY_SMELTER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("steam_alloy_smelter"))
            .recipeTypeDefinition(Option.displayName("Steam Alloy Smelter"))
            .recipeTypeDefinition(Option.maxIO(2, 1, 0, 0))
            .recipeTypeDefinition(Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();


}
