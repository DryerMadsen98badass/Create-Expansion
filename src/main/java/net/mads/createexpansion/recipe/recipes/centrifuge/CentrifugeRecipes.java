package net.mads.createexpansion.recipe.recipes.centrifuge;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class CentrifugeRecipes {
    private CentrifugeRecipes() {
    }

    public static CentrifugeRecipeBuilder recipe(String id) {
        return new CentrifugeRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/dirt_washing")
                .inputItem("minecraft:dirt")
                .inputFluid("minecraft:water", 1000)
                .outputItem("minecraft:clay_ball")
                .outputFluid("minecraft:water", 1000)
                .duration(100)
                .minRpm(16)
                .maxRpm(256)
                .save(output);
    }
}
