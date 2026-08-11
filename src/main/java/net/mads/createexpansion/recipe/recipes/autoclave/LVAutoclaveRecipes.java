package net.mads.createexpansion.recipe.recipes.autoclave;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class LVAutoclaveRecipes {
    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("nether_quartz_from_nether_quartz_dust")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("minecraft:water", 250))
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:quartz_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.chancedOutputItem("minecraft:quartz", 1, 1000, 500))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("autoclave/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.LV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.AUTOCLAVE));
    }
}