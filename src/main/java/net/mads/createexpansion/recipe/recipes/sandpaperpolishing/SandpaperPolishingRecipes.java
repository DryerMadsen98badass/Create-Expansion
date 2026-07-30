package net.mads.createexpansion.recipe.recipes.sandpaperpolishing;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SandpaperPolishingRecipes {
    private SandpaperPolishingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/wood_gear")
                .inputItem("create_expansion:wood_plate")
                .outputItem("create_expansion:wood_gear")
                .save();

        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/wood_small_gear")
                .inputItem("create_expansion:wood_gear")
                .outputItem("create_expansion:wood_small_gear")
                .save();

        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/wood_screw")
                .inputItem("minecraft:stick")
                .outputItem("create_expansion:wood_screw")
                .save();

        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/treated_wood_gear")
                .inputItem("create_expansion:treated_wood_plate")
                .outputItem("create_expansion:treated_wood_gear")
                .save();

        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/treated_wood_small_gear")
                .inputItem("create_expansion:treated_wood_gear")
                .outputItem("create_expansion:treated_wood_small_gear")
                .save();

        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/andesite_alloy_tool_head_buzz_saw")
                .inputItem("create_expansion:andesite_alloy_gear")
                .outputItem("create_expansion:andesite_alloy_tool_head_buzz_saw")
                .save();

        CreateRecipeBuilder.sandpaperPolishing(futures, output, recipes, "sandpaper_polishing/treated_wood_screw")
                .inputItem("create_expansion:treated_wood_rod")
                .outputItem("create_expansion:treated_wood_screw")
                .save();


    }
}
