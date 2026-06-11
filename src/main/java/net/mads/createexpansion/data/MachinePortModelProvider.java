package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.multiblock.MultiblockDefinitions;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MachinePortModelProvider implements DataProvider {
    private static final String CASING_TEXTURE = CreateExpansion.MOD_ID + ":block/machines/ino/casing";
    private static final String COLOR_OVERLAY_TEXTURE = CreateExpansion.MOD_ID + ":block/machines/ino/frame_coler_overlay";
    private static final String KINETIC_MODEL_NAME = "machine_port/kinetic_box";
    private static final String KINETIC_CASING_TEXTURE = CreateExpansion.MOD_ID + ":block/casings/universal_textures/casing";
    private static final String KINETIC_CASING_SINGLE_TEXTURE = CreateExpansion.MOD_ID + ":block/machines/ino/kinetic_casing_single";
    private static final String KINETIC_INSIDE_FRAME_TEXTURE = CreateExpansion.MOD_ID + ":block/machines/ino/kinetic_inside_frame";
    private static final String KINETIC_HOLE_TEXTURE = CreateExpansion.MOD_ID + ":block/machines/ino/kinetic_hole";

    private final PackOutput output;

    public MachinePortModelProvider(PackOutput output) {
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

        futures.add(DataProvider.saveStable(cache, kineticBoxModel(), blockModels.resolve(KINETIC_MODEL_NAME + ".json")));

        for (MultiblockControllerDefinition controller : MultiblockDefinitions.controllers()) {
            String offModel = controllerModelName(controller, false);
            String onModel = controllerModelName(controller, true);
            futures.add(DataProvider.saveStable(cache, controllerModel(controller, false), blockModels.resolve(offModel + ".json")));
            futures.add(DataProvider.saveStable(cache, controllerModel(controller, true), blockModels.resolve(onModel + ".json")));
            futures.add(DataProvider.saveStable(cache, controllerBlockstate(controller), blockstates.resolve(controller.registryName() + ".json")));
            futures.add(DataProvider.saveStable(cache, itemModel(offModel), itemModels.resolve(controller.registryName() + ".json")));
        }

        for (MachinePortType portType : MachinePortType.ALL) {
            String modelName = portType.isKinetic() ? KINETIC_MODEL_NAME : "machine_port/" + portType.id();
            if (!portType.isKinetic()) {
                futures.add(DataProvider.saveStable(cache, machinePortModel(portType), blockModels.resolve(modelName + ".json")));
            }

            for (MachineTier tier : MachineTier.ALL) {
                String registryName = portType.registryName(tier);
                futures.add(DataProvider.saveStable(cache, facingBlockstate(modelName), blockstates.resolve(registryName + ".json")));
                futures.add(DataProvider.saveStable(cache, itemModel(modelName), itemModels.resolve(registryName + ".json")));
            }
        }

        for (StaticMachinePortType portType : StaticMachinePortType.ALL) {
            String modelName = "machine_port/" + portType.id();
            futures.add(DataProvider.saveStable(cache, staticMachinePortModel(portType), blockModels.resolve(modelName + ".json")));
            futures.add(DataProvider.saveStable(cache, facingBlockstate(modelName), blockstates.resolve(portType.id() + ".json")));
            futures.add(DataProvider.saveStable(cache, itemModel(modelName), itemModels.resolve(portType.id() + ".json")));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Expansion Machine Port Models";
    }

    private static JsonObject machinePortModel(MachinePortType portType) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/block");
        json.addProperty("render_type", "minecraft:cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("casing", CASING_TEXTURE);
        textures.addProperty("overlay", CreateExpansion.MOD_ID + ":" + portType.texturePath());
        textures.addProperty("color_overlay", COLOR_OVERLAY_TEXTURE);
        textures.addProperty("particle", CASING_TEXTURE);
        json.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(casingElement());
        if (colorable(portType.abilities())) {
            elements.add(colorOverlayElement());
        }
        elements.add(overlayElement());
        json.add("elements", elements);
        return json;
    }

    private static JsonObject staticMachinePortModel(StaticMachinePortType portType) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/block");
        json.addProperty("render_type", "minecraft:cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("casing", textureReference(portType.casingTexture()));
        textures.addProperty("front", textureReference(portType.frontTexture()));
        textures.addProperty("particle", textureReference(portType.casingTexture()));
        json.add("textures", textures);

        JsonArray elements = new JsonArray();
        if (portType.modelKind() == StaticMachinePortType.ModelKind.KINETIC) {
            elements.add(staticKineticBodyElement(portType.tinted()));
            elements.add(staticKineticOuterTopElement(portType.tinted()));
            elements.add(staticKineticOuterBottomElement(portType.tinted()));
            elements.add(staticKineticOuterLeftElement(portType.tinted()));
            elements.add(staticKineticOuterRightElement(portType.tinted()));
            elements.add(staticKineticFrontTopRingElement());
            elements.add(staticKineticFrontBottomRingElement());
            elements.add(staticKineticFrontLeftRingElement());
            elements.add(staticKineticFrontRightRingElement());
            elements.add(staticKineticInsetElement());
        } else {
            elements.add(staticCasingElement(portType.tinted(), true));
            elements.add(staticFrontOverlayElement());
        }

        json.add("elements", elements);
        return json;
    }

    private static JsonObject controllerModel(MultiblockControllerDefinition controller, boolean active) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/block");
        json.addProperty("render_type", "minecraft:cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("casing", textureReference(controller.casingTexture()));
        textures.addProperty("front", textureReference(active ? controller.onOverlayTexture() : controller.offOverlayTexture()));
        textures.addProperty("particle", textureReference(controller.casingTexture()));
        json.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(staticCasingElement(controller.tinted(), true));
        elements.add(staticFrontOverlayElement());
        json.add("elements", elements);
        return json;
    }

    private static JsonObject casingElement() {
        JsonObject element = element(0, 0, 0, 16, 16, 16);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "down", "#casing", "down");
        addTintedFace(faces, "up", "#casing", "up");
        addTintedFace(faces, "north", "#casing", "north");
        addTintedFace(faces, "south", "#casing", "south");
        addTintedFace(faces, "west", "#casing", "west");
        addTintedFace(faces, "east", "#casing", "east");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject overlayElement() {
        JsonObject element = element(0, 0, -0.02, 16, 16, -0.01);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#overlay");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject colorOverlayElement() {
        JsonObject element = element(0, 0, -0.01, 16, 16, 0);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#color_overlay", 1);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticCasingElement(boolean tinted, boolean includeFront) {
        JsonObject element = element(0, 0, 0, 16, 16, 16);
        JsonObject faces = new JsonObject();
        addFace(faces, "down", "#casing", "down", tinted);
        addFace(faces, "up", "#casing", "up", tinted);
        addFace(faces, "south", "#casing", "south", tinted);
        addFace(faces, "west", "#casing", "west", tinted);
        addFace(faces, "east", "#casing", "east", tinted);
        if (includeFront) {
            addFace(faces, "north", "#casing", "north", tinted);
        }
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticFrontElement() {
        JsonObject element = element(0, 0, -0.02, 16, 16, -0.01);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#front");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticFrontOverlayElement() {
        return staticFrontElement();
    }

    private static JsonObject staticKineticBodyElement(boolean tinted) {
        JsonObject element = element(0, 0, 1, 16, 16, 16);
        JsonObject faces = new JsonObject();
        addFace(faces, "east", "#casing", "east", tinted);
        addFace(faces, "west", "#casing", "west", tinted);
        addFace(faces, "south", "#casing", "south", tinted);
        addFace(faces, "up", "#casing", "up", tinted);
        addFace(faces, "down", "#casing", "down", tinted);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticOuterTopElement(boolean tinted) {
        JsonObject element = element(0, 15, 0, 16, 16, 1);
        JsonObject faces = new JsonObject();
        addFace(faces, "north", "#casing", "north", tinted);
        addFace(faces, "up", "#casing", "up", tinted);
        addFace(faces, "east", "#casing", "east", tinted);
        addFace(faces, "west", "#casing", "west", tinted);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticOuterBottomElement(boolean tinted) {
        JsonObject element = element(0, 0, 0, 16, 1, 1);
        JsonObject faces = new JsonObject();
        addFace(faces, "north", "#casing", "north", tinted);
        addFace(faces, "down", "#casing", "down", tinted);
        addFace(faces, "east", "#casing", "east", tinted);
        addFace(faces, "west", "#casing", "west", tinted);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticOuterLeftElement(boolean tinted) {
        JsonObject element = element(0, 1, 0, 1, 15, 1);
        JsonObject faces = new JsonObject();
        addFace(faces, "north", "#casing", "north", tinted);
        addFace(faces, "west", "#casing", "west", tinted);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticOuterRightElement(boolean tinted) {
        JsonObject element = element(15, 1, 0, 16, 15, 1);
        JsonObject faces = new JsonObject();
        addFace(faces, "north", "#casing", "north", tinted);
        addFace(faces, "east", "#casing", "east", tinted);
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticFrontTopRingElement() {
        JsonObject element = element(1, 14, 0, 15, 15, 1);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#front");
        addUntintedFace(faces, "down", "#front");
        addUntintedFace(faces, "east", "#front");
        addUntintedFace(faces, "west", "#front");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticFrontBottomRingElement() {
        JsonObject element = element(1, 1, 0, 15, 2, 1);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#front");
        addUntintedFace(faces, "up", "#front");
        addUntintedFace(faces, "east", "#front");
        addUntintedFace(faces, "west", "#front");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticFrontLeftRingElement() {
        JsonObject element = element(1, 2, 0, 2, 14, 1);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#front");
        addUntintedFace(faces, "east", "#front");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticFrontRightRingElement() {
        JsonObject element = element(14, 2, 0, 15, 14, 1);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#front");
        addUntintedFace(faces, "west", "#front");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject staticKineticInsetElement() {
        JsonObject element = element(2, 2, 0.5, 14, 14, 1);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#front");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticBoxModel() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/block");
        json.addProperty("render_type", "minecraft:cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("casing", KINETIC_CASING_TEXTURE);
        textures.addProperty("kinetic_casing_single", KINETIC_CASING_SINGLE_TEXTURE);
        textures.addProperty("kinetic_inside_frame", KINETIC_INSIDE_FRAME_TEXTURE);
        textures.addProperty("kinetic_hole", KINETIC_HOLE_TEXTURE);
        textures.addProperty("particle", KINETIC_CASING_TEXTURE);
        json.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(kineticBodyElement());
        elements.add(kineticOuterTopCasingElement());
        elements.add(kineticOuterBottomCasingElement());
        elements.add(kineticOuterLeftCasingElement());
        elements.add(kineticOuterRightCasingElement());
        elements.add(kineticFrontTopRingElement());
        elements.add(kineticFrontBottomRingElement());
        elements.add(kineticFrontLeftRingElement());
        elements.add(kineticFrontRightRingElement());
        elements.add(kineticInsideFrameElement());
        elements.add(kineticHoleElement());
        json.add("elements", elements);
        return json;
    }

    private static JsonObject kineticBodyElement() {
        JsonObject element = element(0, 0, 1, 16, 16, 16);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "east", "#casing", "east");
        addTintedFace(faces, "west", "#casing", "west");
        addTintedFace(faces, "south", "#casing", "south");
        addTintedFace(faces, "up", "#casing", "up");
        addTintedFace(faces, "down", "#casing", "down");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticOuterTopCasingElement() {
        JsonObject element = element(0, 15, 0, 16, 16, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#casing", "north");
        addTintedFace(faces, "up", "#casing", "up");
        addUntintedFace(faces, "down", "#casing");
        addTintedFace(faces, "east", "#casing", "east");
        addTintedFace(faces, "west", "#casing", "west");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticOuterBottomCasingElement() {
        JsonObject element = element(0, 0, 0, 16, 1, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#casing", "north");
        addUntintedFace(faces, "up", "#casing");
        addTintedFace(faces, "down", "#casing", "down");
        addTintedFace(faces, "east", "#casing", "east");
        addTintedFace(faces, "west", "#casing", "west");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticOuterLeftCasingElement() {
        JsonObject element = element(0, 1, 0, 1, 15, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#casing", "north");
        addUntintedFace(faces, "east", "#casing");
        addTintedFace(faces, "west", "#casing", "west");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticOuterRightCasingElement() {
        JsonObject element = element(15, 1, 0, 16, 15, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#casing", "north");
        addTintedFace(faces, "east", "#casing", "east");
        addUntintedFace(faces, "west", "#casing");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticFrontTopRingElement() {
        JsonObject element = element(1, 14, 0, 15, 15, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#kinetic_casing_single", "north");
        addTintedFace(faces, "down", "#kinetic_casing_single");
        addTintedFace(faces, "east", "#kinetic_casing_single");
        addTintedFace(faces, "west", "#kinetic_casing_single");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticFrontBottomRingElement() {
        JsonObject element = element(1, 1, 0, 15, 2, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#kinetic_casing_single", "north");
        addTintedFace(faces, "up", "#kinetic_casing_single");
        addTintedFace(faces, "east", "#kinetic_casing_single");
        addTintedFace(faces, "west", "#kinetic_casing_single");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticFrontLeftRingElement() {
        JsonObject element = element(1, 2, 0, 2, 14, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#kinetic_casing_single", "north");
        addTintedFace(faces, "east", "#kinetic_casing_single");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticFrontRightRingElement() {
        JsonObject element = element(14, 2, 0, 15, 14, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#kinetic_casing_single", "north");
        addTintedFace(faces, "west", "#kinetic_casing_single");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticInsideFrameElement() {
        JsonObject element = element(2, 2, 0.5, 14, 14, 1);
        JsonObject faces = new JsonObject();
        addTintedFace(faces, "north", "#kinetic_inside_frame", "north");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject kineticHoleElement() {
        JsonObject element = element(3, 3, 0.49, 13, 13, 0.5);
        JsonObject faces = new JsonObject();
        addUntintedFace(faces, "north", "#kinetic_hole");
        element.add("faces", faces);
        return element;
    }

    private static JsonObject facingBlockstate(String modelName) {
        JsonObject variants = new JsonObject();
        variants.add("facing=down", variant(modelName, 90, 0));
        variants.add("facing=north", variant(modelName, 0));
        variants.add("facing=east", variant(modelName, 90));
        variants.add("facing=south", variant(modelName, 180));
        variants.add("facing=west", variant(modelName, 270));
        variants.add("facing=up", variant(modelName, 270, 0));

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    private static JsonObject horizontalFacingBlockstate(String modelName) {
        JsonObject variants = new JsonObject();
        variants.add("facing=north", variant(modelName, 0));
        variants.add("facing=east", variant(modelName, 90));
        variants.add("facing=south", variant(modelName, 180));
        variants.add("facing=west", variant(modelName, 270));

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    private static JsonObject controllerBlockstate(MultiblockControllerDefinition controller) {
        JsonObject variants = new JsonObject();
        addControllerVariants(variants, controller, false, false);
        addControllerVariants(variants, controller, true, false);
        addControllerVariants(variants, controller, false, true);
        addControllerVariants(variants, controller, true, true);

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    private static void addControllerVariants(JsonObject variants, MultiblockControllerDefinition controller, boolean formed, boolean active) {
        addControllerVariant(variants, controller, formed, active, "north", 0);
        addControllerVariant(variants, controller, formed, active, "east", 90);
        addControllerVariant(variants, controller, formed, active, "south", 180);
        addControllerVariant(variants, controller, formed, active, "west", 270);
    }

    private static void addControllerVariant(JsonObject variants, MultiblockControllerDefinition controller, boolean formed, boolean active, String facing, int yRotation) {
        variants.add("facing=" + facing + ",formed=" + formed + ",active=" + active, variant(controllerModelName(controller, formed && active), yRotation));
    }

    private static String controllerModelName(MultiblockControllerDefinition controller, boolean active) {
        return active ? controller.registryName() + "_formed" : controller.registryName();
    }

    private static JsonObject variant(String modelName, int yRotation) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", CreateExpansion.MOD_ID + ":block/" + modelName);
        if (yRotation != 0) {
            variant.addProperty("y", yRotation);
        }
        return variant;
    }

    private static JsonObject variant(String modelName, int xRotation, int yRotation) {
        JsonObject variant = variant(modelName, yRotation);
        if (xRotation != 0) {
            variant.addProperty("x", xRotation);
        }
        return variant;
    }

    private static JsonObject itemModel(String modelName) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", CreateExpansion.MOD_ID + ":block/" + modelName);
        return json;
    }

    private static JsonObject element(double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
        JsonObject element = new JsonObject();
        element.add("from", vec(fromX, fromY, fromZ));
        element.add("to", vec(toX, toY, toZ));
        return element;
    }

    private static JsonArray vec(double x, double y, double z) {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private static String textureReference(String texture) {
        if (texture.contains(":")) {
            return texture;
        }

        return CreateExpansion.MOD_ID + ":" + texture;
    }

    private static void addTintedFace(JsonObject faces, String direction, String texture, String cullface) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        face.addProperty("cullface", cullface);
        face.addProperty("tintindex", 0);
        faces.add(direction, face);
    }

    private static void addTintedFace(JsonObject faces, String direction, String texture) {
        addTintedFace(faces, direction, texture, 0);
    }

    private static void addTintedFace(JsonObject faces, String direction, String texture, int tintIndex) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        face.addProperty("tintindex", tintIndex);
        faces.add(direction, face);
    }

    private static void addUntintedFace(JsonObject faces, String direction, String texture) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        faces.add(direction, face);
    }

    private static void addFace(JsonObject faces, String direction, String texture, String cullface, boolean tinted) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        face.addProperty("cullface", cullface);
        if (tinted) {
            face.addProperty("tintindex", 0);
        }
        faces.add(direction, face);
    }

    private static boolean colorable(java.util.Set<net.mads.createexpansion.multiblock.MultiblockAbility> abilities) {
        return abilities.contains(net.mads.createexpansion.multiblock.MultiblockAbility.ITEM_INPUT)
                || abilities.contains(net.mads.createexpansion.multiblock.MultiblockAbility.ITEM_OUTPUT)
                || abilities.contains(net.mads.createexpansion.multiblock.MultiblockAbility.FLUID_INPUT)
                || abilities.contains(net.mads.createexpansion.multiblock.MultiblockAbility.FLUID_OUTPUT)
                || abilities.contains(net.mads.createexpansion.multiblock.MultiblockAbility.IO_INTERFACE);
    }
}
