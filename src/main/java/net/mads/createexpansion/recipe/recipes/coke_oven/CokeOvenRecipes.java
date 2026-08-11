package net.mads.createexpansion.recipe.recipes.coke_oven;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class CokeOvenRecipes {

    private CokeOvenRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        recipe("coal_coke_from_coal")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:coal", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:coal_coke", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 200))
                .recipeDefinition(RecipeDefinition.Option.duration(1200))
                .save(output);

        recipe("charcoal_from_logs")
                .recipeDefinition(RecipeDefinition.Option.inputTag("minecraft:logs", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("minecraft:charcoal", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 100))
                .recipeDefinition(RecipeDefinition.Option.duration(600))
                .save(output);

        recipe("bio_char_dust_from_moss")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:moss_block", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 4))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 250))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);

        recipe("bio_char_dust_from_cactus")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:cactus", 4))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 20))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);

        recipe("bio_char_dust_from_bamboo")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:bamboo", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 30))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);

        recipe("bio_char_dust_from_bamboo_block")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:bamboo_block", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 2))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 80))
                .recipeDefinition(RecipeDefinition.Option.duration(450))
                .save(output);

        recipe("bio_char_dust_from_dried_kelp")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:dried_kelp", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 20))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);

        recipe("bio_char_dust_from_dried_kelp_block")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:dried_kelp_block", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 2))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 70))
                .recipeDefinition(RecipeDefinition.Option.duration(450))
                .save(output);

        recipe("bio_char_dust_from_sugar_cane")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:sugar_cane", 6))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 20))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);

        recipe("bio_char_dust_from_wheat")
                .recipeDefinition(RecipeDefinition.Option.inputItem("minecraft:wheat", 8))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:bio_char_dust", 1))
                .recipeDefinition(RecipeDefinition.Option.outputFluid("create_expansion:creosote_oil", 20))
                .recipeDefinition(RecipeDefinition.Option.duration(300))
                .save(output);


    }

    private static RecipeDefinition recipe(String id) {
        return RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("coke_oven/" + id))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.COKE_OVEN));
    }
}
