package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.recipe.RecipeTypeDefinition;

/** @deprecated Use the generic {@link CentrifugeRecipeType#CENTRIFUGE} process type. */
@Deprecated(forRemoval = false)
public final class LargeKineticCentrifugeRecipeType {
    public static final RecipeTypeDefinition LARGE_KINETIC_CENTRIFUGE = CentrifugeRecipeType.CENTRIFUGE;

    private LargeKineticCentrifugeRecipeType() {
    }
}
