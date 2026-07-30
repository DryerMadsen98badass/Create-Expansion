package net.mads.createexpansion.worldgen;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class OreVeinSavedData extends SavedData {
    private static final String NAME = CreateExpansion.MOD_ID + "_ore_veins";

    private final List<OreVeinLocator.SavedVein> veins = new ArrayList<>();
    private Path textExportPath;

    private OreVeinSavedData() {
        setDirty();
    }

    static OreVeinSavedData get(ServerLevel level) {
        OreVeinSavedData data = level.getDataStorage().computeIfAbsent(
                new Factory<>(
                        OreVeinSavedData::new,
                        OreVeinSavedData::load
                ),
                NAME
        );
        data.textExportPath = textExportPath(level);
        return data;
    }

    private static OreVeinSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        OreVeinSavedData data = new OreVeinSavedData();
        ListTag entries = tag.getList("Veins", Tag.TAG_COMPOUND);
        for (Tag entryTag : entries) {
            if (!(entryTag instanceof CompoundTag entry)) {
                continue;
            }

            String depositId = entry.getString("Deposit");
            Optional<OreDeposit> deposit = OreDepositDefinitions.ALL.stream()
                    .filter(candidate -> candidate.id().equals(depositId))
                    .findFirst();
            if (deposit.isEmpty()) {
                continue;
            }

            data.veins.add(new OreVeinLocator.SavedVein(
                    deposit.get(),
                    readPos(entry.getCompound("Center")),
                    readPos(entry.getCompound("SurfaceIndicator")),
                    entry.getString("Dimension")
            ));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        for (OreVeinLocator.SavedVein vein : veins) {
            CompoundTag entry = new CompoundTag();
            entry.putString("Dimension", vein.dimension());
            entry.putString("Deposit", vein.deposit().id());
            entry.put("Center", writePos(vein.center()));
            entry.put("SurfaceIndicator", writePos(vein.surfaceIndicator()));
            entry.put("Layers", writeStrings(vein.deposit().layers().stream()
                    .map(OreDepositLayer::id)
                    .toList()));
            entry.put("SurfaceIndicators", writeStrings(vein.deposit().surfaceIndicators().stream()
                    .map(indicator -> indicator.name().toLowerCase(java.util.Locale.ROOT))
                    .toList()));
            entries.add(entry);
        }

        tag.put("Veins", entries);
        writeTextExport();
        return tag;
    }

    List<OreVeinLocator.SavedVein> veins() {
        return List.copyOf(veins);
    }

    void addIfMissing(OreVeinLocator.SavedVein vein) {
        boolean exists = veins.stream().anyMatch(existing -> sameCenter(existing, vein));
        if (exists) {
            return;
        }

        veins.add(vein);
        setDirty();
    }

    private void writeTextExport() {
        if (textExportPath == null) {
            return;
        }

        try {
            Files.createDirectories(textExportPath.getParent());
            Files.write(textExportPath, textLines());
        } catch (IOException exception) {
            CreateExpansion.LOGGER.error("Could not save readable ore vein list {}", textExportPath, exception);
        }
    }

    private List<String> textLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Create Expansion Ore Veins");
        lines.add("==========================");
        lines.add("");
        lines.add("Entries: " + veins.size());
        lines.add("");

        for (OreVeinLocator.SavedVein vein : veins) {
            lines.add("Deposit: " + vein.deposit().id());
            lines.add("Dimension: " + vein.dimension());
            lines.add("Center: " + formatPos(vein.center()));
            lines.add("Surface indicator: " + formatPos(vein.surfaceIndicator()));
            lines.add("Layers: " + String.join(", ", vein.deposit().layers().stream()
                    .map(OreDepositLayer::id)
                    .toList()));
            lines.add("Surface indicator types: " + String.join(", ", vein.deposit().surfaceIndicators().stream()
                    .map(indicator -> indicator.name().toLowerCase(java.util.Locale.ROOT))
                    .toList()));
            lines.add("");
        }

        return lines;
    }

    private static boolean sameCenter(OreVeinLocator.SavedVein left, OreVeinLocator.SavedVein right) {
        return left.dimension().equals(right.dimension())
                && left.center().equals(right.center());
    }

    private static CompoundTag writePos(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    private static Path textExportPath(ServerLevel level) {
        Path dataFolder = level.getServer().getWorldPath(LevelResource.ROOT).resolve("data");
        String suffix = level.dimension() == Level.OVERWORLD
                ? ""
                : "_" + sanitize(level.dimension().location().toString());
        return dataFolder.resolve(NAME + suffix + ".txt");
    }

    private static String sanitize(String value) {
        return value.replace(':', '_').replace('/', '_');
    }

    private static BlockPos readPos(CompoundTag tag) {
        return new BlockPos(
                tag.getInt("X"),
                tag.getInt("Y"),
                tag.getInt("Z")
        );
    }

    private static ListTag writeStrings(List<String> values) {
        ListTag tag = new ListTag();
        for (String value : values) {
            tag.add(StringTag.valueOf(value));
        }

        return tag;
    }
}
