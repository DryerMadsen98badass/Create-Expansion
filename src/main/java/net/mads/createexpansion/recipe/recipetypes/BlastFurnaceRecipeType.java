package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class BlastFurnaceRecipeType {
    public static final RecipeTypeDefinition BLAST_FURNACE = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("blast_furnace"))
            .recipeTypeDefinition(Option.displayName("Blast Furnace"))
            .recipeTypeDefinition(Option.maxIO(3, 2, 0, 0))
            .recipeTypeDefinition(Option.kineticMode(RecipeTypeDefinition.KineticMode.NONE))
            .recipeTypeDefinition(Option.energyMode(RecipeTypeDefinition.EnergyMode.NONE))
            .recipeTypeDefinition(Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();

    private BlastFurnaceRecipeType() {
    }
}
