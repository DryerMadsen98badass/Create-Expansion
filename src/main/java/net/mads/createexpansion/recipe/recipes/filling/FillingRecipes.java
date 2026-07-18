package net.mads.createexpansion.recipe.recipes.filling;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class FillingRecipes {
    private FillingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "filling/test/water_bottle", filling("minecraft:glass_bottle", "minecraft:water", 250, "minecraft:potion"));
    }

    private static JsonObject filling(String itemInput, String fluidInput, int amount, String output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:filling");
        json.add("ingredients", ingredients(item(itemInput), fluid(fluidInput, amount)));
        json.add("results", results(result(output)));
        return json;
    }
}
