package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.machine.coil.CoilLogic;
import net.mads.createexpansion.recipe.CERecipeTypeDefinition;

public final class TestFoundryRecipeType {
    public static final CERecipeTypeDefinition TEST_FOUNDRY = CERecipeTypeDefinition.builder("test_foundry")
            .displayName("Test Foundry")
            .maxIO(4, 4, 0, 0, CERecipeTypeDefinition.KineticMode.NONE, CERecipeTypeDefinition.EnergyMode.CONSUMES)
            .logic(CoilLogic.TEMP)
            .build();

    private TestFoundryRecipeType() {
    }
}
