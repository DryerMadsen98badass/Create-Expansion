package net.mads.createexpansion.recipe.recipes.filling;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FillingRecipes {
    private FillingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_wood_from_spruce")
                .inputItem("minecraft:spruce_planks")
                .inputFluid("create_expansion:creosote_oil", 100)
                .outputItem("create_expansion:treated_wood")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_leather_from_leather")
                .inputItem("minecraft:leather")
                .inputFluid("create_expansion:tannin_extract", 10)
                .outputItem("create_expansion:treated_leather")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/belt_connector_from_treated_leather")
                .inputItem("create_expansion:treated_leather")
                .inputFluid("create_expansion:molten_rubber", 576)
                .outputItem("create:belt_connector")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/belt_connector_from_treated_leather_and_rubber_solution")
                .inputItem("create_expansion:treated_leather")
                .inputFluid("create_expansion:rubber_solution", 576)
                .outputItem("create:belt_connector")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_wood_from_oak")
                .inputItem("minecraft:oak_planks")
                .inputFluid("create_expansion:creosote_oil", 100)
                .outputItem("create_expansion:treated_wood")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/electron_tube")
                .inputItem("create:polished_rose_quartz")
                .inputFluid("create_expansion:molten_iron", 144)
                .outputItem("create:electron_tube")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_wood_from_birch")
                .inputItem("minecraft:birch_planks")
                .inputFluid("create_expansion:creosote_oil", 100)
                .outputItem("create_expansion:treated_wood")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_wood_from_oak_and_wood_tar")
                .inputItem("minecraft:oak_planks")
                .inputFluid("create_expansion:wood_tar", 10)
                .outputItem("create_expansion:treated_wood")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_wood_from_spruce_and_wood_tar")
                .inputItem("minecraft:spruce_planks")
                .inputFluid("create_expansion:wood_tar", 10)
                .outputItem("create_expansion:treated_wood")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/treated_wood_from_birch_and_wood_tar")
                .inputItem("minecraft:birch_planks")
                .inputFluid("create_expansion:wood_tar", 10)
                .outputItem("create_expansion:treated_wood")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/super_glue")
                .inputItem("create_expansion:empty_glue")
                .inputFluid("create_expansion:glue", 1000)
                .outputItem("create:super_glue")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/stainless_bronze_ingot")
                .inputItem("create_expansion:bronze_ingot")
                .inputFluid("create_expansion:corrosion_resistant_solution", 10)
                .outputItem("create_expansion:stainless_bronze_ingot")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/ebonite_lined_brass_casing")
                .inputItem("create_expansion:sturdy_brass_casing")
                .inputFluid("create_expansion:molten_rubber", 288)
                .outputItem("create_expansion:ebonite_lined_brass_casing")
                .save();

        CreateRecipeBuilder.filling(futures, output, recipes, "filling/ebonite_lined_brass_casing_from_rubber_solution")
                .inputItem("create_expansion:sturdy_brass_casing")
                .inputFluid("create_expansion:rubber_solution", 288)
                .outputItem("create_expansion:ebonite_lined_brass_casing")
                .save();

    }
}
