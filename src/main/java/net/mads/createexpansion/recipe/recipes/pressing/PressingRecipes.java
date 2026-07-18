package net.mads.createexpansion.recipe.recipes.pressing;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class PressingRecipes {
    private PressingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "pressing/test/iron_bolt_to_screw", pressing("create_expansion:iron_bolt", "create_expansion:iron_screw"));
    }

    private static JsonObject pressing(String input, String output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:pressing");
        json.add("ingredients", ingredients(item(input)));
        json.add("results", results(result(output)));
        return json;
    }
}
