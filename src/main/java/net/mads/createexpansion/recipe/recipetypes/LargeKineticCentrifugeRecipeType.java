package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class LargeKineticCentrifugeRecipeType {
    public static final RecipeTypeDefinition LARGE_KINETIC_CENTRIFUGE = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("large_kinetic_centrifuge"))
            .recipeTypeDefinition(Option.displayName("Large Kinetic Centrifuge"))
            .recipeTypeDefinition(Option.maxIO(2, 9, 1, 3))
            .recipeTypeDefinition(Option.kineticMode(RecipeTypeDefinition.KineticMode.CONSUMES))
            .recipeTypeDefinition(Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.EXTRACT))
            .build();

    private LargeKineticCentrifugeRecipeType() {
    }
}
