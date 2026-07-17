package net.mads.createexpansion.recipe.recipes.wiredrawer;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class WireDrawingRecipes {
    private WireDrawingRecipes() {
    }

    public static WireDrawingRecipeBuilder recipe(String id) {
        return new WireDrawingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/copper_ingot_to_wire")
                .inputItem("minecraft:copper_ingot")
                .outputItem("create_expansion:copper_wire")
                .duration(100)
                .minRpm(16)
                .save(output);
    }
}
