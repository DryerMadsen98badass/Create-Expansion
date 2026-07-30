package net.mads.createexpansion.recipe.recipes.alloy_smelter;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class SteamAlloySmelterRecipes {
    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("steam_alloy_smelter/rose_quartz")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:redstone", 8))
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create:rose_quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);
        recipe("fertilizer_from_mineral_ash_and_bone_meal")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:mineral_ash_dust", 2))
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:bone_meal", 2))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:fertilizer_dust", 3))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);
    }
    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id(id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.STEAM_ALLOY_SMELTER));
    }
}
