package net.mads.createexpansion.recipe.recipes.forge_hammer;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVForgeHammerRecipes {

    private ULVForgeHammerRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe(output, "minecraft:iron_ingot", "create:iron_sheet");
        recipe(output, "minecraft:copper_ingot", "create:copper_sheet");
        recipe(output, "create:brass_ingot", "create:brass_sheet");
        recipe(output, "create:zinc_ingot", "create_expansion:zinc_plate");
        recipe(output, "create_expansion:tin_ingot", "create_expansion:tin_plate");
        recipe(output, "create_expansion:lead_ingot", "create_expansion:lead_plate");
        recipe(output, "create_expansion:wrought_iron_ingot", "create_expansion:wrought_iron_plate");
        recipe(output, "create_expansion:stainless_bronze_ingot", "create_expansion:stainless_bronze_plate");
        recipe(output, "create:andesite_alloy", "create_expansion:andesite_alloy_plate");

        recipe("copper_plate_to_copper_heat_exchanger_plate")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create:copper_sheet", 2))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:copper_heat_exchanger_plate", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("stainless_bronze_plate_to_stainless_bronze_heat_exchanger_plate")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:stainless_bronze_plate", 2))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:stainless_bronze_heat_exchanger_plate", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
    }

    private static void recipe(
            RecipeOutput recipeOutput,
            String input,
            String result
    ) {
        String inputName = path(input);
        String resultName = path(result);
        String id = inputName + "_to_" + resultName;

        RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("forge_hammer/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.FORGE_HAMMER))
                .recipeDefinition(RecipeDefinition.Option.inputItem(input, 2))
                .recipeDefinition(RecipeDefinition.Option.chancedOutputItem(result, 1, 5000, 5000))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(recipeOutput);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("forge_hammer/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.FORGE_HAMMER));
    }

    private static String path(String itemId) {
        int separatorIndex = itemId.indexOf(':');

        if (separatorIndex >= 0) {
            return itemId.substring(separatorIndex + 1);
        }

        return itemId;
    }
}