package net.mads.createexpansion.recipe.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CreateRecipeJson {

    private CreateRecipeJson() {
    }

    public static void save(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId,
            JsonObject json
    ) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, recipeId);
        futures.add(DataProvider.saveStable(output, json, recipes.json(id)));
    }

    public static JsonObject item(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("item", id);
        return json;
    }

    public static JsonObject fluid(String id, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "neoforge:single");
        json.addProperty("fluid", id);
        json.addProperty("amount", amount);
        return json;
    }

    public static JsonObject fluidResult(String id, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("amount", amount);
        json.addProperty("id", id);
        return json;
    }

    public static JsonArray ingredients(JsonObject... ingredients) {
        JsonArray array = new JsonArray();

        for (JsonObject ingredient : ingredients) {
            array.add(ingredient);
        }

        return array;
    }

    public static JsonArray results(JsonObject... results) {
        JsonArray array = new JsonArray();

        for (JsonObject result : results) {
            array.add(result);
        }

        return array;
    }

    public static JsonObject result(String id) {
        return result(id, 1);
    }

    public static JsonObject result(String id, int count) {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);

        if (count > 1) {
            json.addProperty("count", count);
        }

        return json;
    }
}