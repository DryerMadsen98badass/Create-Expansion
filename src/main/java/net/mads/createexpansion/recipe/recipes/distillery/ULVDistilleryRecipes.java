package net.mads.createexpansion.recipe.recipes.distillery;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class ULVDistilleryRecipes {

    private ULVDistilleryRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {

        recipe("pine_resin_from_spruce_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:spruce_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:pine_resin", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("tannin_extract_from_oak_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:oak_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:tannin_extract", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("plant_wax_from_plant_oil")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:plant_oil", 250))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:plant_wax", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("mangrove_tannin_from_mangrove_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:mangrove_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:mangrove_tannin", 150))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("latex_from_jungle_sap")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:jungle_sap", 250))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:latex", 200))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);

    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("distillery/" + id))
                .recipeDefinition(RecipeDefinition.Option.tier(MachineTier.ULV))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.DISTILLERY));
    }
}