package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class SteamautoclaveRecipeType {
    public static final RecipeTypeDefinition STEAM_AUTOCLAVE = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("steam_autoclave"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Steam Autoclave"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(3, 3, 1, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();
}
