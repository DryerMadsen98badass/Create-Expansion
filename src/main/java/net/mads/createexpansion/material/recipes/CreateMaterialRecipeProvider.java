package net.mads.createexpansion.material.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CreateMaterialRecipeProvider implements DataProvider {
    private final PackOutput.PathProvider recipes;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public CreateMaterialRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                saveCutting(futures, output, material, MaterialPart.PLATE, MaterialPart.LONG_ROD, 1, "plate_to_long_rod");
                saveCutting(futures, output, material, MaterialPart.LONG_ROD, MaterialPart.ROD, 2, "long_rod_to_rod");
                saveCutting(futures, output, material, MaterialPart.GEAR, MaterialPart.TOOL_HEAD_BUZZ_SAW, 1, "gear_to_tool_head_buzz_saw");
                savePressing(futures, output, material, MaterialPart.BOLT, MaterialPart.SCREW, "bolt_to_screw");
                saveSequencedDoublePlate(futures, output, material);
                saveSequencedBearing(futures, output, material);
                saveSequencedFrame(futures, output, material);
            }
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    private void saveCutting(List<CompletableFuture<?>> futures, CachedOutput output, IndustrialMaterial material,
                             MaterialPart inputPart, MaterialPart resultPart, int count, String name) {
        if (!MaterialRecipeHelper.hasItems(material, inputPart, resultPart)) {
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", "create:cutting");
        json.add("ingredients", ingredients(item(MaterialRecipeHelper.itemId(material, inputPart))));
        json.addProperty("processing_time", 100);
        json.add("results", results(MaterialRecipeHelper.itemId(material, resultPart), count));
        save(futures, output, "cutting/materials/" + material.id() + "_" + name, json);
    }

    private void savePressing(List<CompletableFuture<?>> futures, CachedOutput output, IndustrialMaterial material,
                              MaterialPart inputPart, MaterialPart resultPart, String name) {
        if (!MaterialRecipeHelper.hasItems(material, inputPart, resultPart)) {
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", "create:pressing");
        json.add("ingredients", ingredients(item(MaterialRecipeHelper.itemId(material, inputPart))));
        json.add("results", results(MaterialRecipeHelper.itemId(material, resultPart), 1));
        save(futures, output, "pressing/materials/" + material.id() + "_" + name, json);
    }

    private void saveSequencedDoublePlate(List<CompletableFuture<?>> futures, CachedOutput output, IndustrialMaterial material) {
        if (!MaterialRecipeHelper.hasItems(material, MaterialPart.PLATE, MaterialPart.DOUBLE_PLATE)) {
            return;
        }

        String plate = MaterialRecipeHelper.itemId(material, MaterialPart.PLATE);
        String doublePlate = MaterialRecipeHelper.itemId(material, MaterialPart.DOUBLE_PLATE);
        JsonArray sequence = new JsonArray();
        sequence.add(deploying(doublePlate, plate));
        sequence.add(pressingStep(doublePlate));
        saveSequenced(futures, output, material, "double_plate", plate, doublePlate, 1, sequence);
    }

    private void saveSequencedBearing(List<CompletableFuture<?>> futures, CachedOutput output, IndustrialMaterial material) {
        if (!MaterialRecipeHelper.hasItems(material, MaterialPart.LARGE_RING, MaterialPart.BEARING_BALL, MaterialPart.BEARING)) {
            return;
        }

        String largeRing = MaterialRecipeHelper.itemId(material, MaterialPart.LARGE_RING);
        String bearingBall = MaterialRecipeHelper.itemId(material, MaterialPart.BEARING_BALL);
        String bearing = MaterialRecipeHelper.itemId(material, MaterialPart.BEARING);
        JsonArray sequence = new JsonArray();
        sequence.add(deploying(bearing, bearingBall));
        saveSequenced(futures, output, material, "bearing", largeRing, bearing, 8, sequence);
    }

    private void saveSequencedFrame(List<CompletableFuture<?>> futures, CachedOutput output, IndustrialMaterial material) {
        if (!MaterialRecipeHelper.hasItems(material, MaterialPart.ROD, MaterialPart.FRAME)) {
            return;
        }

        String rod = MaterialRecipeHelper.itemId(material, MaterialPart.ROD);
        String frame = MaterialRecipeHelper.itemId(material, MaterialPart.FRAME);
        JsonArray sequence = new JsonArray();
        sequence.add(deploying(frame, rod));
        saveSequenced(futures, output, material, "frame", rod, frame, 11, sequence);
    }

    private void saveSequenced(List<CompletableFuture<?>> futures, CachedOutput output, IndustrialMaterial material,
                               String name, String ingredient, String result, int loops, JsonArray sequence) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:sequenced_assembly");
        json.add("ingredient", item(ingredient));
        json.addProperty("loops", loops);
        json.add("results", results(result, 1));
        json.add("sequence", sequence);
        JsonObject transitional = new JsonObject();
        transitional.addProperty("id", result);
        json.add("transitional_item", transitional);
        save(futures, output, "sequenced_assembly/materials/" + material.id() + "_" + name, json);
    }

    private JsonObject deploying(String transitionalItem, String heldItem) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:deploying");
        json.add("ingredients", ingredients(item(transitionalItem), item(heldItem)));
        json.add("results", results(transitionalItem, 1));
        return json;
    }

    private JsonObject pressingStep(String transitionalItem) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:pressing");
        json.add("ingredients", ingredients(item(transitionalItem)));
        json.add("results", results(transitionalItem, 1));
        return json;
    }

    private JsonObject item(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("item", id);
        return json;
    }

    private JsonArray ingredients(JsonObject... ingredients) {
        JsonArray array = new JsonArray();
        for (JsonObject ingredient : ingredients) {
            array.add(ingredient);
        }
        return array;
    }

    private JsonArray results(String id, int count) {
        JsonArray array = new JsonArray();
        JsonObject result = new JsonObject();
        result.addProperty("id", id);
        if (count > 1) {
            result.addProperty("count", count);
        }
        array.add(result);
        return array;
    }

    private void save(List<CompletableFuture<?>> futures, CachedOutput output, String recipeId, JsonObject json) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, recipeId);
        futures.add(DataProvider.saveStable(output, json, path(id)));
    }

    private Path path(ResourceLocation id) {
        return recipes.json(id);
    }

    @Override
    public String getName() {
        return "Create Material Recipes";
    }
}
