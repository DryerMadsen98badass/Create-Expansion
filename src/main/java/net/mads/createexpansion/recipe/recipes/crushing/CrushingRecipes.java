package net.mads.createexpansion.recipe.recipes.crushing;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class CrushingRecipes {
    private CrushingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "crushing/test/iron_ore", crushing("create_expansion:iron_ore", "create_expansion:iron_crushed_ore", 1, 250));
    }

    private static JsonObject crushing(String input, String output, int count, int processingTime) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:crushing");
        json.add("ingredients", ingredients(item(input)));
        json.add("results", results(result(output, count)));
        json.addProperty("processing_time", processingTime);
        return json;
    }
}
