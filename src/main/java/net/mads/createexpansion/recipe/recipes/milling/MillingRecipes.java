package net.mads.createexpansion.recipe.recipes.milling;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class MillingRecipes {
    private MillingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "milling/test/iron_ingot_to_dust", milling("create_expansion:iron_ingot", "create_expansion:iron_dust", 1, 100));
    }

    private static JsonObject milling(String input, String output, int count, int processingTime) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:milling");
        json.add("ingredients", ingredients(item(input)));
        json.add("results", results(result(output, count)));
        json.addProperty("processing_time", processingTime);
        return json;
    }
}
