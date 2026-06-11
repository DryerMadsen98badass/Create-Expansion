package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MachineCasingModelProvider implements DataProvider {
    private static final String CASING_TEXTURE = CreateExpansion.MOD_ID + ":block/casings/universal_textures/casing";

    private final PackOutput output;

    public MachineCasingModelProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Path assets = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(CreateExpansion.MOD_ID);
        Path blockstates = assets.resolve("blockstates");
        Path blockModels = assets.resolve("models").resolve("block");
        Path itemModels = assets.resolve("models").resolve("item");

        for (MachineTier tier : MachineTier.ALL) {
            String casingName = tier.casingRegistryName();
            futures.add(DataProvider.saveStable(cache, blockstate(casingName), blockstates.resolve(casingName + ".json")));
            futures.add(DataProvider.saveStable(cache, blockModel(), blockModels.resolve(casingName + ".json")));
            futures.add(DataProvider.saveStable(cache, itemModel(casingName), itemModels.resolve(casingName + ".json")));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Expansion Machine Casing Models";
    }

    private static JsonObject blockstate(String casingName) {
        JsonObject model = new JsonObject();
        model.addProperty("model", CreateExpansion.MOD_ID + ":block/" + casingName);

        JsonObject variants = new JsonObject();
        variants.add("", model);

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    private static JsonObject blockModel() {
        JsonObject json = new JsonObject();
        json.addProperty("loader", "fusion:model");
        json.addProperty("type", "connecting");
        json.addProperty("parent", "minecraft:block/block");

        JsonObject textures = new JsonObject();
        textures.addProperty("all", CASING_TEXTURE);
        textures.addProperty("particle", CASING_TEXTURE);
        json.add("textures", textures);

        JsonObject connections = new JsonObject();
        connections.addProperty("type", "is_same_block");
        json.add("connections", connections);
        json.add("elements", fullCubeElements());
        return json;
    }

    private static JsonArray fullCubeElements() {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        from.add(0);
        from.add(0);
        from.add(0);
        JsonArray to = new JsonArray();
        to.add(16);
        to.add(16);
        to.add(16);
        element.add("from", from);
        element.add("to", to);

        JsonObject faces = new JsonObject();
        addFace(faces, "north");
        addFace(faces, "south");
        addFace(faces, "east");
        addFace(faces, "west");
        addFace(faces, "up");
        addFace(faces, "down");
        element.add("faces", faces);

        JsonArray elements = new JsonArray();
        elements.add(element);
        return elements;
    }

    private static void addFace(JsonObject faces, String direction) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", "#all");
        face.addProperty("cullface", direction);
        face.addProperty("tintindex", 0);
        faces.add(direction, face);
    }

    private static JsonObject itemModel(String casingName) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", CreateExpansion.MOD_ID + ":block/" + casingName);
        return json;
    }
}
