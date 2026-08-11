package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.recipe.RecipeTypeDefinition;

/** @deprecated Use the generic {@link CentrifugeRecipeType#CENTRIFUGE} process type. */
@Deprecated(forRemoval = false)
public final class ElectricCentrifugeRecipeType {
    public static final RecipeTypeDefinition ELECTRIC_CENTRIFUGE = CentrifugeRecipeType.CENTRIFUGE;

    private ElectricCentrifugeRecipeType() {
    }
}
