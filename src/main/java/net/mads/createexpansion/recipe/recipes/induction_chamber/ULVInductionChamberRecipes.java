package net.mads.createexpansion.recipe.recipes.induction_chamber;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVInductionChamberRecipes {

    private ULVInductionChamberRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("activated_carbon")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:bio_char_dust", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:activated_carbon_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(600))
                .save(output);
        recipe("mineral_ash_from_biomass")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:biomass", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:mineral_ash_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
        recipe("tempered_glass_from_hot_tempered_glass")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:hot_tempered_glass", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:tempered_glass", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(400))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("induction_chamber/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.INDUCTION_CHAMBER));
    }
}