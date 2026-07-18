package net.mads.createexpansion.recipe.recipes.splashing;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class SplashingRecipes {
    private SplashingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "splashing/test/cool_bronze_bolt_mold", splashing("create_expansion:bronze_hot_cast_bolt_mold", "create_expansion:bronze_cast_bolt_mold", 200));
    }

    private static JsonObject splashing(String input, String output, int processingTime) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:splashing");
        json.add("ingredients", ingredients(item(input)));
        json.add("results", results(result(output)));
        json.addProperty("processingTime", processingTime);
        return json;
    }
}
