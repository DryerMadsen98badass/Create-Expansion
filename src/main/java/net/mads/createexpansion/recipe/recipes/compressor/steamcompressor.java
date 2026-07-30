package net.mads.createexpansion.recipe.recipes.coke_oven;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class steamcompressor {

    private steamcompressor() {
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
                .recipeDefinition(RecipeDefinition.Option.id(id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.STEAM_COMPRESSOR));
    }
}
