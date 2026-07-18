package net.mads.createexpansion.recipe.recipes.itemapplication;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class ItemApplicationRecipes {
    private ItemApplicationRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        // save(futures, output, recipes, "item_application/test/iron_casing", itemApplication("minecraft:iron_block", "create_expansion:iron_plate", "create_expansion:iron_casing"));
    }

    private static JsonObject itemApplication(String blockInput, String heldItem, String outputBlock) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:item_application");
        json.add("ingredients", ingredients(item(blockInput), item(heldItem)));
        json.add("results", results(result(outputBlock)));
        return json;
    }
}
