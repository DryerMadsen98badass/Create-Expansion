package net.mads.createexpansion.recipe.recipes.compacting;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class CompactingRecipes {
    private CompactingRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "compacting/test/iron_nugget_to_ingot", compacting("create_expansion:iron_nugget", 9, "create_expansion:iron_ingot"));
    }

    private static JsonObject compacting(String input, int count, String output) {
        JsonObject ingredient = item(input);
        ingredient.addProperty("count", count);

        JsonObject json = new JsonObject();
        json.addProperty("type", "create:compacting");
        json.add("ingredients", ingredients(ingredient));
        json.add("results", results(result(output)));
        return json;
    }
}
