package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class SteamextractorRecipeType {
    public static final RecipeTypeDefinition STEAM_EXTRACTOR = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("steam_extractor"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Steam Extractor"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 0, 1))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
            .build();
}
