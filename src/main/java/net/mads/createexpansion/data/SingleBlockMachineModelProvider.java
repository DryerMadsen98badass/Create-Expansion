package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachineDefinition;
import net.mads.createexpansion.machine.SingleBlockMachinePower;
import net.mads.createexpansion.machine.SingleBlockMachineInstance;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SingleBlockMachineModelProvider implements DataProvider {
    private static final String MUFFLER_TEXTURE =
            CreateExpansion.MOD_ID
                    + ":block/machines/ino/muffler";

    private final PackOutput output;

    public SingleBlockMachineModelProvider(PackOutput output) {
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

        for (SingleBlockMachineInstance instance
                : MachineDefinition.INSTANCES) {
            String name = instance.registryName();
            futures.add(DataProvider.saveStable(
                    cache,
                    blockstate(name),
                    blockstates.resolve(name + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    blockModel(instance, false, 0),
                    blockModels.resolve(name + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    blockModel(instance, true, 0),
                    blockModels.resolve(name + "_active.json")
            ));
            for (int frame = 0; frame <= 9; frame++) {
                futures.add(DataProvider.saveStable(
                        cache,
                        blockModel(instance, true, frame),
                        blockModels.resolve(name + "_active_" + frame + ".json")
                ));
            }
            futures.add(DataProvider.saveStable(
                    cache,
                    itemModel(name),
                    itemModels.resolve(name + ".json")
            ));
        }

        return CompletableFuture.allOf(
                futures.toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public String getName() {
        return "Create Expansion Singleblock Machine Models";
    }

    private static JsonObject blockstate(String name) {
        JsonObject variants = new JsonObject();
        addFacingVariants(variants, name, "north", 0);
        addFacingVariants(variants, name, "east", 90);
        addFacingVariants(variants, name, "south", 180);
        addFacingVariants(variants, name, "west", 270);

        JsonObject root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    private static void addFacingVariants(
            JsonObject variants,
            String name,
            String facing,
            int rotation
    ) {
        for (int frame = 0; frame <= 9; frame++) {
            addVariant(variants, "facing=" + facing + ",active=false,overlay_frame=" + frame, name, false, frame, rotation);
            addVariant(variants, "facing=" + facing + ",active=true,overlay_frame=" + frame, name, true, frame, rotation);
        }
    }

    private static void addVariant(
            JsonObject variants,
            String key,
            String name,
            boolean active,
            int frame,
            int rotation
    ) {
        JsonObject variant = new JsonObject();
        variant.addProperty(
                "model",
                CreateExpansion.MOD_ID + ":block/" + name + activeModelSuffix(active, frame)
        );
        if (rotation != 0) {
            variant.addProperty("y", rotation);
        }
        variants.add(key, variant);
    }

    private static String activeModelSuffix(boolean active, int frame) {
        if (!active) {
            return "";
        }

        return frame == 0 ? "_active" : "_active_" + frame;
    }

    private static JsonObject blockModel(
            SingleBlockMachineInstance instance,
            boolean active,
            int frame
    ) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/block");
        root.addProperty("render_type", "minecraft:cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("side", instance.tier().singleBlockMachineCasingSideTexture());
        textures.addProperty("bottom", instance.tier().singleBlockMachineCasingBottomTexture());
        textures.addProperty("top", instance.tier().singleBlockMachineCasingTopTexture());
        textures.addProperty("front", CreateExpansion.MOD_ID + ":" + overlay(instance, active, frame));
        textures.addProperty("muffler", MUFFLER_TEXTURE);
        textures.addProperty("particle", instance.tier().singleBlockMachineCasingSideTexture());
        root.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(fullCube(instance));
        elements.add(frontOverlay());
        if (instance.definition().power() == SingleBlockMachinePower.STEAM) {
            elements.add(topMufflerOverlay());
        }
        root.add("elements", elements);
        return root;
    }

    private static String overlay(
            SingleBlockMachineInstance instance,
            boolean active,
            int frame
    ) {
        if (!active || instance.definition().activeOverlays().isEmpty()) {
            return instance.definition().idleOverlay();
        }

        List<String> activeOverlays = instance.definition().activeOverlays();
        return activeOverlays.get(Math.floorMod(frame, activeOverlays.size()));
    }

    private static JsonObject fullCube(SingleBlockMachineInstance instance) {
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
        int casingTint = instance.tier().isElectric() ? 0 : -1;
        addFace(faces, "north", "#side", casingTint);
        addFace(faces, "south", "#side", casingTint);
        addFace(faces, "east", "#side", casingTint);
        addFace(faces, "west", "#side", casingTint);
        addFace(faces, "up", "#top", casingTint);
        addFace(faces, "down", "#bottom", casingTint);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject frontOverlay() {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        from.add(0);
        from.add(0);
        from.add(-0.01F);
        JsonArray to = new JsonArray();
        to.add(16);
        to.add(16);
        to.add(0);
        element.add("from", from);
        element.add("to", to);

        JsonObject faces = new JsonObject();
        addFace(faces, "north", "#front", -1);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject topMufflerOverlay() {
        JsonObject element = new JsonObject();
        JsonArray from = new JsonArray();
        from.add(0);
        from.add(16);
        from.add(0);
        JsonArray to = new JsonArray();
        to.add(16);
        to.add(16.01F);
        to.add(16);
        element.add("from", from);
        element.add("to", to);

        JsonObject faces = new JsonObject();
        addFace(faces, "up", "#muffler", -1);
        element.add("faces", faces);
        return element;
    }

    private static void addFace(
            JsonObject faces,
            String direction,
            String texture,
            int tintIndex
    ) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        face.addProperty("cullface", direction);
        if (tintIndex >= 0) {
            face.addProperty("tintindex", tintIndex);
        }
        faces.add(direction, face);
    }

    private static JsonObject itemModel(String name) {
        JsonObject root = new JsonObject();
        root.addProperty(
                "parent",
                CreateExpansion.MOD_ID + ":block/" + name
        );
        return root;
    }
}
