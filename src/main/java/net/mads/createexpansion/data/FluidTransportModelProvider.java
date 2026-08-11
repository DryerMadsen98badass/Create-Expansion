package net.mads.createexpansion.data;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.transport.FluidTransportTier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FluidTransportModelProvider implements DataProvider {
    private static final String TEMPLATE_ROOT = "/fluid_transport_templates/";

    private static final List<TextureTemplate> TEXTURES = List.of(
            new TextureTemplate("pipes.png", "_pipes.png"),
            new TextureTemplate("pipes_connected.png", "_pipes_connected.png"),
            new TextureTemplate("pump.png", "_pump.png"),
            new TextureTemplate("fluid_tank.png", "_fluid_tank.png"),
            new TextureTemplate("fluid_tank_connected.png", "_fluid_tank_connected.png"),
            new TextureTemplate("fluid_tank_top.png", "_fluid_tank_top.png"),
            new TextureTemplate("fluid_tank_top_connected.png", "_fluid_tank_top_connected.png"),
            new TextureTemplate("fluid_tank_inner.png", "_fluid_tank_inner.png"),
            new TextureTemplate("fluid_tank_inner_connected.png", "_fluid_tank_inner_connected.png")
    );

    private final PackOutput output;

    public FluidTransportModelProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Path assets = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(CreateExpansion.MOD_ID);
        Path blockstates = assets.resolve("blockstates");
        Path blockModels = assets.resolve("models/block");
        Path itemModels = assets.resolve("models/item");
        Path blockTextures = assets.resolve("textures/block");

        List<String> pipeModels = readLines("pipe_models.txt");
        List<String> pumpModels = readLines("pump_models.txt");
        List<String> tankModels = readLines("tank_models.txt");

        for (FluidTransportTier tier : FluidTransportTier.all()) {
            futures.add(DataProvider.saveStable(
                    cache,
                    templateJson("blockstates/fluid_pipe.json", tier),
                    blockstates.resolve(tier.pipeId() + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    glassPipeBlockState(tier),
                    blockstates.resolve(tier.glassPipeId() + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    templateJson("blockstates/mechanical_pump.json", tier),
                    blockstates.resolve(tier.pumpId() + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    templateJson("blockstates/fluid_tank.json", tier),
                    blockstates.resolve(tier.tankId() + ".json")
            ));

            addModels(futures, cache, tier, "fluid_pipe", tier.pipeId(), pipeModels, blockModels);
            addModels(futures, cache, tier, "mechanical_pump", tier.pumpId(), pumpModels, blockModels);
            addModels(futures, cache, tier, "fluid_tank", tier.tankId(), tankModels, blockModels);

            futures.add(DataProvider.saveStable(
                    cache,
                    parentModel(CreateExpansion.MOD_ID + ":block/" + tier.pipeId() + "/item"),
                    itemModels.resolve(tier.pipeId() + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    parentModel(CreateExpansion.MOD_ID + ":block/" + tier.pumpId() + "/item"),
                    itemModels.resolve(tier.pumpId() + ".json")
            ));
            futures.add(DataProvider.saveStable(
                    cache,
                    parentModel(CreateExpansion.MOD_ID + ":block/" + tier.tankId() + "/block_single_window"),
                    itemModels.resolve(tier.tankId() + ".json")
            ));

            writeTextures(cache, tier, blockTextures);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Expansion Fluid Transport Models and Textures";
    }

    private static void addModels(
            List<CompletableFuture<?>> futures,
            CachedOutput cache,
            FluidTransportTier tier,
            String templateFolder,
            String outputFolder,
            List<String> modelFiles,
            Path blockModels
    ) {
        for (String modelFile : modelFiles) {
            futures.add(DataProvider.saveStable(
                    cache,
                    templateJson("models/" + templateFolder + "/" + modelFile, tier),
                    blockModels.resolve(outputFolder).resolve(modelFile)
            ));
        }
    }

    private static JsonObject templateJson(String path, FluidTransportTier tier) {
        String json = readText(path)
                .replace("\uFEFF", "")
                .replace("bronze_fluid_pipe", tier.pipeId())
                .replace("bronze_mechanical_pump", tier.pumpId())
                .replace("bronze_fluid_tank", tier.tankId())
                .replace("bronze_pipes", tier.id() + "_pipes")
                .replace("bronze_pump", tier.id() + "_pump");
        return JsonParser.parseString(json).getAsJsonObject();
    }

    private static JsonObject glassPipeBlockState(FluidTransportTier tier) {
        JsonObject variants = new JsonObject();
        String model = CreateExpansion.MOD_ID + ":block/" + tier.pipeId() + "/window";

        addGlassPipeVariant(variants, false, "x", model, 90, 90);
        addGlassPipeVariant(variants, false, "y", model, 0, 0);
        addGlassPipeVariant(variants, false, "z", model, 90, 0);
        addGlassPipeVariant(variants, true, "x", model, 90, 90);
        addGlassPipeVariant(variants, true, "y", model, 0, 0);
        addGlassPipeVariant(variants, true, "z", model, 90, 0);

        JsonObject root = new JsonObject();
        root.add("variants", variants);
        return root;
    }

    private static void addGlassPipeVariant(
            JsonObject variants,
            boolean alt,
            String axis,
            String model,
            int rotationX,
            int rotationY
    ) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model);
        if (rotationX != 0) {
            variant.addProperty("x", rotationX);
        }
        if (rotationY != 0) {
            variant.addProperty("y", rotationY);
        }
        variants.add("alt=" + alt + ",axis=" + axis, variant);
    }

    private static JsonObject parentModel(String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        return json;
    }

    private static List<String> readLines(String path) {
        return readText(path).lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    private static String readText(String path) {
        try (InputStream stream = open(path)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read fluid transport template " + path, exception);
        }
    }

    private static void writeTextures(CachedOutput cache, FluidTransportTier tier, Path outputFolder) {
        try {
            Files.createDirectories(outputFolder);
            for (TextureTemplate texture : TEXTURES) {
                BufferedImage source = readImage("textures/" + texture.templateName());
                BufferedImage recolored = recolor(source, tier.color());
                Path outputPath = outputFolder.resolve(tier.id() + texture.outputSuffix());
                writePng(cache, outputPath, recolored);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not generate fluid transport textures for " + tier.id(), exception);
        }
    }

    private static BufferedImage readImage(String path) throws IOException {
        try (InputStream stream = open(path)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IOException("Unsupported image template: " + path);
            }
            return image;
        }
    }

    private static BufferedImage recolor(BufferedImage source, int color) {
        int targetRed = color >> 16 & 0xFF;
        int targetGreen = color >> 8 & 0xFF;
        int targetBlue = color & 0xFF;
        double referenceLuminance = referenceLuminance(source);

        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int alpha = argb >>> 24;
                if (alpha == 0) {
                    output.setRGB(x, y, 0);
                    continue;
                }

                double shade = luminance(argb) / referenceLuminance;
                int red = clamp((int) Math.round(targetRed * shade));
                int green = clamp((int) Math.round(targetGreen * shade));
                int blue = clamp((int) Math.round(targetBlue * shade));
                output.setRGB(x, y, alpha << 24 | red << 16 | green << 8 | blue);
            }
        }
        return output;
    }

    private static double referenceLuminance(BufferedImage source) {
        int[] histogram = new int[256];
        int count = 0;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                if ((argb >>> 24) == 0) {
                    continue;
                }
                histogram[clamp((int) Math.round(luminance(argb)))]++;
                count++;
            }
        }

        int middle = Math.max(1, count) / 2;
        int seen = 0;
        for (int value = 0; value < histogram.length; value++) {
            seen += histogram[value];
            if (seen >= middle) {
                return Math.max(1.0D, value);
            }
        }
        return 255.0D;
    }

    private static double luminance(int argb) {
        int red = argb >> 16 & 0xFF;
        int green = argb >> 8 & 0xFF;
        int blue = argb & 0xFF;
        return red * 0.2126D + green * 0.7152D + blue * 0.0722D;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    @SuppressWarnings("deprecation")
    private static void writePng(CachedOutput cache, Path outputPath, BufferedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (!ImageIO.write(image, "PNG", output)) {
            throw new IOException("No PNG writer is available");
        }

        byte[] png = output.toByteArray();
        cache.writeIfNeeded(outputPath, png, Hashing.sha1().hashBytes(png));
    }

    private static InputStream open(String path) {
        InputStream stream = FluidTransportModelProvider.class.getResourceAsStream(TEMPLATE_ROOT + path);
        if (stream == null) {
            throw new IllegalStateException("Missing fluid transport template: " + path);
        }
        return stream;
    }

    private record TextureTemplate(String templateName, String outputSuffix) {
    }
}
