package net.mads.createexpansion.recipe.recipes.rolling;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class RollingRecipes {
    private RollingRecipes() {
    }

    public static RollingRecipeBuilder recipe(String id) {
        return new RollingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/copper_ingot_to_sheet")
                .inputItem("minecraft:copper_ingot")
                .outputItem("create:copper_sheet")
                .duration(100)
                .minRpm(16)
                .save(output);
    }
}
