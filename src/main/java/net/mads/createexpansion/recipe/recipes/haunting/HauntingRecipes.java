package net.mads.createexpansion.recipe.recipes.haunting;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class HauntingRecipes {
    private HauntingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "haunting/test/sand_to_soul_sand", haunting("minecraft:sand", "minecraft:soul_sand"));
    }

    private static JsonObject haunting(String input, String output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:haunting");
        json.add("ingredients", ingredients(item(input)));
        json.add("results", results(result(output)));
        return json;
    }
}
