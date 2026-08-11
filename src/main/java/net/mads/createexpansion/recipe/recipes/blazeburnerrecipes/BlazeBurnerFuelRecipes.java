package net.mads.createexpansion.recipe.recipes.blazeburnerrecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class BlazeBurnerFuelRecipes {
    private BlazeBurnerFuelRecipes() {
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        heated(output, "ethanol", 2000);
        heated(output, "plant_oil", 2400);
        heated(output, "creosote_oil", 2600);
        heated(output, "naphtha", 3000);

        BlazeBurnerFuelRecipe.recipe()
                .id("biofuel")
                .inputFluid("create_expansion:biofuel", 100)
                .superheated(1700)
                .heated(1700)
                .save(output);

        heated(output, "creosote_fuel", 3600);
        heated(output, "gasoline", 3800);
        heated(output, "kerosene", 4000);
        heated(output, "diesel", 4400);


    }

    private static void heated(RecipeOutput output, String fluidId, int ticks) {
        BlazeBurnerFuelRecipe.recipe()
                .id(fluidId)
                .inputFluid("create_expansion:" + fluidId, 100)
                .heated(ticks)
                .save(output);
    }
}
