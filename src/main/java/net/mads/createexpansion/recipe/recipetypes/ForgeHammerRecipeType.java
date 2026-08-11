package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;

public final class ForgeHammerRecipeType {
    public static final RecipeTypeDefinition FORGE_HAMMER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(RecipeTypeDefinition.Option.id("forge_hammer"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.displayName("Forge Hammer"))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.maxIO(1, 1, 0, 0))
            .recipeTypeDefinition(RecipeTypeDefinition.Option.progressBar(ProgressBar.HAMMER_BRONZE))
            .build();
    }
