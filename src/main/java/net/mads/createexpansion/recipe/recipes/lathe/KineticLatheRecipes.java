package net.mads.createexpansion.recipe.recipes.lathe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class KineticLatheRecipes {
    private KineticLatheRecipes() {
    }

    public static TurningRecipeBuilder recipe(String id) {
        return new TurningRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/oak_log_to_stick")
                .inputItem("minecraft:oak_log")
                .outputItem("minecraft:stick", 4)
                .duration(120)
                .minRpm(32)
                .maxRpm(256)
                .save(output);
    }
}
