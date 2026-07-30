package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.recipe.remove.RemovedCreateRecipes;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeRemovalProvider implements DataProvider {
    private final PackOutput.PathProvider recipes;

    public RecipeRemovalProvider(PackOutput output) {
        this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (ResourceLocation id : RemovedCreateRecipes.ALL) {
            futures.add(DataProvider.saveStable(output, disabledRecipe(), path(id)));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static JsonObject disabledRecipe() {
        JsonObject json = new JsonObject();

        JsonArray conditions = new JsonArray();
        JsonObject falseCondition = new JsonObject();
        falseCondition.addProperty("type", "neoforge:false");
        conditions.add(falseCondition);
        json.add("neoforge:conditions", conditions);

        return json;
    }

    private Path path(ResourceLocation id) {
        return recipes.json(id);
    }

    @Override
    public String getName() {
        return "Recipe Removals";
    }
}
