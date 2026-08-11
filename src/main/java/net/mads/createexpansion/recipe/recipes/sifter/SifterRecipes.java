package net.mads.createexpansion.recipe.recipes.sifter;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class SifterRecipes {
    private SifterRecipes() {
    }

    public static SifterRecipeBuilder recipe(String id) {
        return new SifterRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("ore_processing/gravel")
                .inputItem("create_expansion:stone_dust")
                .chancedOutput("minecraft:flint", 0.15F)
                .chancedOutput("minecraft:iron_nugget", 0.05F)
                .chancedOutput("create_expansion:tin_nugget", 0.05F)
                .chancedOutput("create:copper_nugget", 0.05F)
                .duration(200)
                .minRpm(16)
                .save(output);
    }
}
