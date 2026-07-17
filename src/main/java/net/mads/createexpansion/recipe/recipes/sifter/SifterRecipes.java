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
                .inputItem("minecraft:gravel")
                .outputItem("minecraft:flint")
                .chancedOutput("minecraft:iron_nugget", 0.25F)
                .duration(100)
                .minRpm(16)
                .save(output);
    }
}
