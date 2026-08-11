package net.mads.createexpansion.recipe.recipes.boilers;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class LiquidFuelBoilerRecipes {

    private LiquidFuelBoilerRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        fuel(output, "crude_oil", 22);
        fuel(output, "ethanol", 20);
        fuel(output, "pine_resin", 22);
        fuel(output, "plant_oil", 24);
        fuel(output, "creosote_oil", 26);
        fuel(output, "naphtha", 30);
        fuel(output, "wood_tar", 30);
        fuel(output, "biofuel", 34);
        fuel(output, "creosote_fuel", 36);
        fuel(output, "gasoline", 38);
        fuel(output, "kerosene", 40);
        fuel(output, "diesel", 44);
        fuel(output, "heavy_fuel_oil", 48);
    }

    private static void fuel(RecipeOutput output, String fluidId, int duration) {
        recipe(fluidId)
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:" + fluidId, 1))
                .recipeDefinition(RecipeDefinition.Option.duration(duration))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("liquid_fuel_boiler/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.LIQUID_FUEL_BOILER));
    }
}
