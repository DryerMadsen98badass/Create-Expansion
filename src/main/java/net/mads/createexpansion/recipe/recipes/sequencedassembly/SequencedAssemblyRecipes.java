package net.mads.createexpansion.recipe.recipes.sequencedassembly;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.recipe.recipes.CreateRecipeBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.mads.createexpansion.recipe.recipes.CreateRecipeJson.*;

public final class SequencedAssemblyRecipes {
    private SequencedAssemblyRecipes() {
    }

    public static void build(List<CompletableFuture<?>> futures, CachedOutput output, PackOutput.PathProvider recipes) {
        CreateRecipeBuilder.sequencedAssembly(futures, output, recipes, "sequenced_assembly/test/reinforced_apple")
                .inputItem("minecraft:apple")
                .loops(1)
                .transitionalItem("minecraft:apple")
                .sequence(sequence("minecraft:apple", "minecraft:gold_ingot"))
                .outputItem("minecraft:golden_apple")
                .save();
    }

    private static JsonArray sequence(String transitionalItem, String heldItem) {
        JsonArray sequence = new JsonArray();
        sequence.add(deployingStep(transitionalItem, heldItem));
        sequence.add(pressingStep(transitionalItem));
        return sequence;
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
