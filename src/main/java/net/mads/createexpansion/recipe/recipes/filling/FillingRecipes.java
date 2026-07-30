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
    }
}
