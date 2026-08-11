package net.mads.createexpansion.recipe.recipes.autoclave;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class PhTestAutoclaveRecipes {
    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("autoclave/ph_test_dirt_to_diamond"))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.LV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.AUTOCLAVE))
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:dirt", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("minecraft:diamond", 1))
                .recipeDefinition(RecipeDefinition.Option.phRange(8.0, 8.5))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
    }

    private PhTestAutoclaveRecipes() {
    }
}
