package net.mads.createexpansion.recipe.recipes.sequencedassembly;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class SequencedAssemblyRecipes {
    private SequencedAssemblyRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "sequenced_assembly/test/iron_double_plate", doublePlate("create_expansion:iron_plate", "create_expansion:iron_double_plate"));
    }

    private static JsonObject doublePlate(String plate, String doublePlate) {
        JsonArray sequence = new JsonArray();
        sequence.add(deployingStep(doublePlate, plate));
        sequence.add(pressingStep(doublePlate));

        JsonObject transitional = new JsonObject();
        transitional.addProperty("id", doublePlate);

        JsonObject json = new JsonObject();
        json.addProperty("type", "create:sequenced_assembly");
        json.add("ingredient", item(plate));
        json.addProperty("loops", 1);
        json.add("transitional_item", transitional);
        json.add("sequence", sequence);
        json.add("results", results(result(doublePlate)));
        return json;
    }

    private static JsonObject deployingStep(String transitionalItem, String heldItem) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:deploying");
        json.add("ingredients", ingredients(item(transitionalItem), item(heldItem)));
        json.add("results", results(result(transitionalItem)));
        return json;
    }

    private static JsonObject pressingStep(String transitionalItem) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:pressing");
        json.add("ingredients", ingredients(item(transitionalItem)));
        json.add("results", results(result(transitionalItem)));
        return json;
    }
}
