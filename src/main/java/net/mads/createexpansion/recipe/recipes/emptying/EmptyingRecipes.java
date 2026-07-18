package net.mads.createexpansion.recipe.recipes.emptying;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class EmptyingRecipes {
    private EmptyingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "emptying/test/water_bucket", emptying("minecraft:water_bucket", "minecraft:bucket", "minecraft:water", 1000));
    }

    private static JsonObject emptying(String input, String itemOutput, String fluidOutput, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:emptying");
        json.add("ingredients", ingredients(item(input)));
        json.add("results", results(result(itemOutput), fluid(fluidOutput, amount)));
        return json;
    }
}
