package net.mads.createexpansion.recipe.recipes.coiling;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class CoilingRecipes {
    private CoilingRecipes() {
    }

    public static CoilingRecipeBuilder recipe(String id) {
        return new CoilingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/copper_wire_to_spring")
                .inputItem("create_expansion:copper_wire")
                .outputItem("create_expansion:copper_spring")
                .duration(100)
                .minRpm(16)
                .save(output);
    }
}
