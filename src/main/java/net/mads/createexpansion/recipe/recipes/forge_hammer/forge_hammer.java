package net.mads.createexpansion.recipe.recipes.forge_hammer;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class forge_hammer {

    private forge_hammer() {
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
                .recipeDefinition(RecipeDefinition.Option.id(id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.STEAM_FORGE_HAMMER))
                .recipeDefinition(RecipeDefinition.Option.inputItem(input, 4))
                .recipeDefinition(RecipeDefinition.Option.chancedOutput(result, 1, 5000))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(recipeOutput);
    }

    private static String path(String itemId) {
        int separatorIndex = itemId.indexOf(':');

        if (separatorIndex >= 0) {
            return itemId.substring(separatorIndex + 1);
        }

        return itemId;
    }
}