package net.mads.createexpansion.recipe.recipes.alloy_smelter;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVAlloySmelterRecipes {
    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("alloy_smelter/rose_quartz")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:redstone", 8))
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create:rose_quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);
        recipe("alloy_smelter/hot_tempered_glass")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:glass", 1))
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:hot_tempered_glass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(600))
                .save(output);

    }
    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("alloy_smelter/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.ALLOY_SMELTER));
    }
}
