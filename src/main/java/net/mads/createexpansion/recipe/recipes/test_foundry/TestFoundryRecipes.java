package net.mads.createexpansion.recipe.recipes.test_foundry;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.coil.CoilLogic;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class TestFoundryRecipes {
    private TestFoundryRecipes() {
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        CERecipeTypes.TEST_FOUNDRY.recipe("dirt_foundry_test")
                .inputItem("minecraft:diamond", 1)
                .outputItem("minecraft:dirt", 1)
                .duration(20)
                .CEt(MachineTier.LV)
                .logic(CoilLogic.TEMP)
                .temp(1000)
                .save(output);
    }
}
