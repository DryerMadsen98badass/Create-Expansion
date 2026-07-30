package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public class CokeOvenRecipeType {
    public static final RecipeTypeDefinition COKE_OVEN = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("coke_oven"))
            .recipeTypeDefinition(Option.displayName("Coke Oven"))
            .recipeTypeDefinition(Option.maxIO(1, 1, 0, 1))
            .recipeTypeDefinition(Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.COKE_OVEN))
            .build();

    private CokeOvenRecipeType() {
    }
}

