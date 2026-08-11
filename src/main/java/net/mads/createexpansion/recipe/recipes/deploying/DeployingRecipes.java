package net.mads.createexpansion.recipe.recipes.deploying;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class DeployingRecipes {
    private DeployingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.deploying(futures, output, recipes, "deploying/insulated_1x_ulv_wire_from_1x_ulv_wire")
                .inputItem("create_expansion:1x_ulv_wire")
                .inputItem("create_expansion:rubber_plate")
                .outputItem("create_expansion:insulated_1x_ulv_wire")
                .save();

        CreateRecipeBuilder.deploying(futures, output, recipes, "deploying/insulated_2x_ulv_wire_from_2x_ulv_wire")
                .inputItem("create_expansion:2x_ulv_wire")
                .inputItem("create_expansion:rubber_plate", 2)
                .outputItem("create_expansion:insulated_2x_ulv_wire")
                .save();

        CreateRecipeBuilder.deploying(futures, output, recipes, "deploying/insulated_4x_ulv_wire_from_4x_ulv_wire")
                .inputItem("create_expansion:4x_ulv_wire")
                .inputItem("create_expansion:rubber_plate", 4)
                .outputItem("create_expansion:insulated_4x_ulv_wire")
                .save();

        CreateRecipeBuilder.deploying(futures, output, recipes, "deploying/insulated_8x_ulv_wire_from_8x_ulv_wire")
                .inputItem("create_expansion:8x_ulv_wire")
                .inputItem("create_expansion:rubber_plate", 8)
                .outputItem("create_expansion:insulated_8x_ulv_wire")
                .save();

        CreateRecipeBuilder.deploying(futures, output, recipes, "deploying/insulated_16x_ulv_wire_from_16x_ulv_wire")
                .inputItem("create_expansion:16x_ulv_wire")
                .inputItem("create_expansion:rubber_plate", 16)
                .outputItem("create_expansion:insulated_16x_ulv_wire")
                .save();


    }
}
