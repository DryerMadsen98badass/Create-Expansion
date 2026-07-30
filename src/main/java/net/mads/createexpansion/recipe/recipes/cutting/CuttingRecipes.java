package net.mads.createexpansion.recipe.recipes.cutting;

import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;
import java.util.List;

public final class CuttingRecipes {
    private CuttingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
       //  CreateRecipeBuilder.cutting(futures, output, recipes, "cutting/materials/plate_to_long_rod")
       //          .inputItem("create_expansion:wood_plate")
       //          .outputItem("create_expansion:wood_long_rod")
       //          .duration(100)
       //          .save();

        terracotta(futures, output, recipes, "white", "terracotta_screw");
        terracotta(futures, output, recipes, "orange", "terracotta_nugget");
        terracotta(futures, output, recipes, "magenta", "terracotta_ingot");
        terracotta(futures, output, recipes, "light_blue", "terracotta_plate");
        terracotta(futures, output, recipes, "yellow", "terracotta_rod");
        terracotta(futures, output, recipes, "lime", "terracotta_long_rod");
        terracotta(futures, output, recipes, "blue", "terracotta_bolt");
        terracotta(futures, output, recipes, "pink", "terracotta_ring");
        terracotta(futures, output, recipes, "gray", "terracotta_small_ring");
        terracotta(futures, output, recipes, "light_gray", "terracotta_large_ring");
        terracotta(futures, output, recipes, "cyan", "terracotta_gear");
        terracotta(futures, output, recipes, "purple", "terracotta_small_gear");
        terracotta(futures, output, recipes, "brown", "terracotta_bearing_ball");
        terracotta(futures, output, recipes, "green", "terracotta_bearing");
        terracotta(futures, output, recipes, "red", "terracotta_rotor");
    }

    private static void terracotta(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes, String color, String part) {
        CreateRecipeBuilder.cutting(futures, output, recipes, "cutting/" + color + "_terracotta_to_" + part)
                .inputItem("minecraft:" + color + "_terracotta")
                .outputItem("create_expansion:" + part).chance(0.05F)
                .duration(100)
                .save();
    }
}
