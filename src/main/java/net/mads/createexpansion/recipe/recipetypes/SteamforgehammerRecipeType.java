package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class SteamforgehammerRecipeType {
    public static final RecipeTypeDefinition STEAM_FORGE_HAMMER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("forge_hammer"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Forge Hammer"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 0, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.HAMMER_BRONZE))
            .build();
    }
