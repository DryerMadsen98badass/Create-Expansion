package net.mads.createexpansion.recipe.recipes.deploying;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class DeployingRecipes {
    private DeployingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "deploying/test/iron_bearing", deploying("create_expansion:iron_large_ring", "create_expansion:iron_bearing_ball", "create_expansion:iron_bearing"));
    }

    private static JsonObject deploying(String base, String held, String output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:deploying");
        json.add("ingredients", ingredients(item(base), item(held)));
        json.add("results", results(result(output)));
        return json;
    }
}
