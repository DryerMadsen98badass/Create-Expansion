package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class ElectricCentrifugeRecipeType {
    public static final RecipeTypeDefinition ELECTRIC_CENTRIFUGE = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("electric_centrifuge"))
            .recipeTypeDefinition(Option.displayName("Electric Centrifuge"))
            .recipeTypeDefinition(Option.maxIO(2, 9, 1, 3))
            .recipeTypeDefinition(Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(Option.energyMode(RecipeTypeDefinition.EnergyMode.CONSUMES))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.EXTRACT))
            .build();

    private ElectricCentrifugeRecipeType() {
    }
}
