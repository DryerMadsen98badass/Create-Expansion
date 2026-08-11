package net.mads.createexpansion.recipe.recipes.autoclave;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVAutoclaveRecipes {
    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("ethanol_from_fermentation_mash")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:fermentation_mash", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:ethanol", 10))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:wet_biomass", 5))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
        recipe("polished_rose_quartz_from_rose_quartz")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create:rose_quartz", 1))
                .recipeDefinition(RecipeDefinition.Option.chancedOutputItem("create:polished_rose_quartz", 1, 5000))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
        recipe("natural_rubber_from_latex")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:latex", 250))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:natural_rubber_ingot", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
        recipe("fertilizer_from_mineral_ash_and_bone_meal")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:mineral_ash_dust", 2))
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:bone_meal", 2))
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:organic_binder", 100))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:fertilizer_dust", 3))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("autoclave/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.AUTOCLAVE));
    }
}