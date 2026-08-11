package net.mads.createexpansion.recipe.recipes.compressor;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVCompressorRecipes {

    private ULVCompressorRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("biomass_briquette_from_biomass")
                .recipeDefinition(RecipeDefinition.Option.inputItem("create_expansion:biomass", 9))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:biomass_briquette", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);



    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("compressor/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.COMPRESSOR));
    }
}
