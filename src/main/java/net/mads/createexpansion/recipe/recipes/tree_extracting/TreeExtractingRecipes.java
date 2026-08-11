package net.mads.createexpansion.recipe.recipes.tree_extracting;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class TreeExtractingRecipes {

    private TreeExtractingRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("jungle_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:jungle_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:jungle_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("oak_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:oak_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:oak_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("dark_oak_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:dark_oak_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:dark_oak_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("spruce_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:spruce_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:spruce_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("birch_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:birch_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:birch_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("acacia_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:acacia_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:acacia_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("cherry_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:cherry_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:cherry_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
        recipe("mangrove_sap")
                .recipeDefinition(RecipeDefinition.Option.treeSource("minecraft:mangrove_log"))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:mangrove_sap", 50))
                .recipeDefinition(RecipeDefinition.Option.duration(200))
                .save(output);
    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("tree_extracting/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.TREE_EXTRACTING));
    }
}
