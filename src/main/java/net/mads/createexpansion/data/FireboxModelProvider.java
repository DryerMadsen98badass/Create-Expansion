package net.mads.createexpansion.data;

import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.ActiveBlockDefinition;
import net.mads.createexpansion.block.ActiveBlocks;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FireboxModelProvider implements DataProvider {
    private final PackOutput output;

    public FireboxModelProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Path assets = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(CreateExpansion.MOD_ID);
        Path blockstates = assets.resolve("blockstates");
        Path blockModels = assets.resolve("models").resolve("block").resolve("casings").resolve("active");
        Path itemModels = assets.resolve("models").resolve("item");

        for (ActiveBlockDefinition definition : ActiveBlocks.ALL) {
            futures.add(DataProvider.saveStable(cache, blockstate(definition), blockstates.resolve(definition.id() + ".json")));
            futures.add(DataProvider.saveStable(cache, blockModel(definition, false), blockModels.resolve(definition.id() + "_off.json")));
            futures.add(DataProvider.saveStable(cache, blockModel(definition, true), blockModels.resolve(definition.id() + "_on.json")));
            futures.add(DataProvider.saveStable(cache, itemModel(definition), itemModels.resolve(definition.id() + ".json")));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Expansion Active Block Models";
    }

    private static JsonObject blockstate(ActiveBlockDefinition definition) {
        JsonObject variants = new JsonObject();
        variants.add("active=false", variant(definition, false));
        variants.add("active=true", variant(definition, true));

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    private static JsonObject variant(ActiveBlockDefinition definition, boolean active) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", CreateExpansion.MOD_ID + ":" + definition.modelPath(active));
        return variant;
    }

    private static JsonObject blockModel(ActiveBlockDefinition definition, boolean active) {
        String texture = definition.texture(active).toString();
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");

        JsonObject textures = new JsonObject();
        textures.addProperty("all", texture);
        textures.addProperty("particle", texture);
        json.add("textures", textures);
        return json;
    }

    private static JsonObject itemModel(ActiveBlockDefinition definition) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", CreateExpansion.MOD_ID + ":" + definition.modelPath(false));
        return json;
    }
}
