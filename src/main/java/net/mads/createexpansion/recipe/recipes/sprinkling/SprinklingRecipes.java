package net.mads.createexpansion.recipe.recipes.sprinkling;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

/** Test recipes for the Steam Sprinkler. */
public final class SprinklingRecipes {
    private SprinklingRecipes() {
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("sprinkling/fertilizer"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.SPRINKLING))
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:fertilizer_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(50))
                .save(output);

        RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("sprinkling/liquid_fertilizer"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.SPRINKLING))
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:liquid_fertilizer", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(60))
                .save(output);

        RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("sprinkling/water"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.SPRINKLING))
                .recipeDefinition(RecipeDefinition.Option.inputFluid("minecraft:water", 16000))
                .recipeDefinition(RecipeDefinition.Option.duration(1))
                .save(output);
    }
}
