package net.mads.createexpansion.recipe.recipes.hydraulicpress;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class HydraulicPressingRecipes {
    private HydraulicPressingRecipes() {}

    public static HydraulicPressingRecipeBuilder recipe(String id) {
        return new HydraulicPressingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/gold_ingot_to_sheet")
                .inputItem("minecraft:gold_ingot")
                .outputItem("create:golden_sheet")
                .blows(2)
                .save(output);
    }
}
