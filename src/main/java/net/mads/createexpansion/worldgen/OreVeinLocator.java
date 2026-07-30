package net.mads.createexpansion.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class OreVeinLocator {
    private static final int CHUNK_SIZE = 16;
    private static final int DEFAULT_SEARCH_RADIUS_GRIDS = 48;
    private static final Map<ResourceKey<Level>, Map<Long, SavedVein>> GENERATED_VEINS = new ConcurrentHashMap<>();

    private OreVeinLocator() {
    }

    public static List<String> searchableIds() {
        Set<String> ids = new LinkedHashSet<>();
        for (OreDeposit deposit : OreDepositDefinitions.ALL) {
            ids.add(deposit.id());
            for (OreDepositLayer layer : deposit.layers()) {
                ids.add(layer.id());
            }
        }

        return ids.stream()
                .sorted()
                .toList();
    }

    public static Optional<Result> locate(ServerLevel level, BlockPos origin, String query) {
        return locate(level, origin, query, DEFAULT_SEARCH_RADIUS_GRIDS);
    }

    public static Optional<Result> locate(ServerLevel level, BlockPos origin, String query, int radiusGrids) {
        String normalizedQuery = normalize(query);
        int maxDistanceBlocks = radiusGrids * OreDepositDefinitions.GRID_SIZE_CHUNKS * CHUNK_SIZE;
        return locateGenerated(level, origin, normalizedQuery, maxDistanceBlocks);
    }

    public static List<Result> listNearby(ServerLevel level, BlockPos origin, int radiusBlocks) {
        loadSaved(level);
        Map<Long, SavedVein> veins = GENERATED_VEINS.getOrDefault(level.dimension(), Map.of());
        return veins.values().stream()
                .map(vein -> toResult(vein, origin))
                .filter(result -> result.distanceBlocks() <= radiusBlocks)
                .sorted(Comparator.comparingInt(Result::distanceBlocks))
                .toList();
    }

    public static void ensureSavedData(ServerLevel level) {
        loadSaved(level);
    }

    static void registerGenerated(WorldGenLevel level, OreDeposit deposit, BlockPos center, BlockPos surfaceIndicator) {
        ResourceKey<Level> dimension = level.getLevel().dimension();
        SavedVein vein = new SavedVein(
                deposit,
                center,
                surfaceIndicator,
                dimension.location().toString()
        );
        GENERATED_VEINS
                .computeIfAbsent(dimension, ignored -> new ConcurrentHashMap<>())
                .putIfAbsent(centerKey(center), vein);
        OreVeinSavedData.get(level.getLevel()).addIfMissing(vein);
    }

    private static Optional<Result> locateGenerated(ServerLevel level, BlockPos origin, String normalizedQuery, int maxDistanceBlocks) {
        loadSaved(level);
        Map<Long, SavedVein> veins = GENERATED_VEINS.getOrDefault(level.dimension(), Map.of());
        return veins.values().stream()
                .filter(vein -> matches(vein.deposit(), normalizedQuery))
                .map(vein -> toResult(vein, origin))
                .filter(result -> result.distanceBlocks() <= maxDistanceBlocks)
                .min(Comparator.comparingInt(Result::distanceBlocks));
    }

    private static void loadSaved(ServerLevel level) {
        Map<Long, SavedVein> veins = GENERATED_VEINS.computeIfAbsent(level.dimension(), ignored -> new ConcurrentHashMap<>());
        for (SavedVein vein : OreVeinSavedData.get(level).veins()) {
            veins.putIfAbsent(centerKey(vein.center()), vein);
        }
    }

    private static Result toResult(SavedVein vein, BlockPos origin) {
        BlockPos center = vein.center();
        int dx = center.getX() - origin.getX();
        int dz = center.getZ() - origin.getZ();
        int distance = Mth.floor(Math.sqrt(dx * dx + dz * dz));
        return new Result(
                vein.deposit().id(),
                center,
                vein.surfaceIndicator(),
                vein.dimension(),
                distance,
                vein.deposit().layers().stream().map(OreDepositLayer::id).toList(),
                vein.deposit().surfaceIndicators().stream().map(indicator -> indicator.name().toLowerCase(Locale.ROOT)).toList()
        );
    }

    private static Optional<GridResult> resolveGrid(ServerLevel level, int gridX, int gridZ) {
        long gridSeed = mixedSeed(level.getSeed(), gridX, gridZ, 0x5349A85D);
        RandomSource layoutRandom = RandomSource.create(gridSeed);
        int maxOffset = OreDepositDefinitions.GRID_SIZE_CHUNKS - OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS;
        int startChunkX = gridX * OreDepositDefinitions.GRID_SIZE_CHUNKS + layoutRandom.nextInt(maxOffset + 1);
        int startChunkZ = gridZ * OreDepositDefinitions.GRID_SIZE_CHUNKS + layoutRandom.nextInt(maxOffset + 1);
        int centerChunkX = startChunkX + OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS / 2;
        int centerChunkZ = startChunkZ + OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS / 2;
        int centerX = centerChunkX * CHUNK_SIZE + 8;
        int centerZ = centerChunkZ * CHUNK_SIZE + 8;

        List<Holder<Biome>> sampledBiomes = sampleBiomes(level, centerX, centerZ);
        List<OreDeposit> candidates = OreDepositDefinitions.ALL.stream()
                .filter(deposit -> deposit.matchesAny(sampledBiomes))
                .toList();

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        int totalWeight = candidates.stream().mapToInt(OreDeposit::weight).sum();
        int spawnChance = Math.min(totalWeight, OreDepositDefinitions.CHANCE_SCALE);
        RandomSource chanceRandom = RandomSource.create(mixedSeed(level.getSeed(), gridX, gridZ, 0x29C9D35F));
        if (chanceRandom.nextInt(OreDepositDefinitions.CHANCE_SCALE) >= spawnChance) {
            return Optional.empty();
        }

        RandomSource pickRandom = RandomSource.create(mixedSeed(level.getSeed(), gridX, gridZ, 0x713F4A7B));
        OreDeposit deposit = pickDeposit(candidates, pickRandom.nextInt(totalWeight));
        RandomSource heightRandom = RandomSource.create(mixedSeed(level.getSeed(), gridX, gridZ, deposit.id().hashCode()));
        int centerY = OreDepositPlacement.chooseCenterY(level, deposit, centerX, centerZ, heightRandom);

        return Optional.of(new GridResult(deposit, centerX, centerY, centerZ));
    }

    private static List<Holder<Biome>> sampleBiomes(ServerLevel level, int centerX, int centerZ) {
        List<Holder<Biome>> biomes = new ArrayList<>();
        addBiomeSample(level, biomes, centerX, centerZ, 64);
        addBiomeSample(level, biomes, centerX, centerZ, 0);
        addBiomeSample(level, biomes, centerX, centerZ, -32);
        return biomes;
    }

    private static void addBiomeSample(ServerLevel level, List<Holder<Biome>> biomes, int x, int z, int y) {
        int sampleY = Mth.clamp(y, level.getMinBuildHeight(), level.getMaxBuildHeight() - 1);
        Holder<Biome> biome = level.getUncachedNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(sampleY), QuartPos.fromBlock(z));
        if (!biomes.contains(biome)) {
            biomes.add(biome);
        }
    }

    private static OreDeposit pickDeposit(List<OreDeposit> candidates, int roll) {
        int cursor = roll;
        for (OreDeposit deposit : candidates) {
            cursor -= deposit.weight();
            if (cursor < 0) {
                return deposit;
            }
        }

        return candidates.get(candidates.size() - 1);
    }

    private static boolean matches(OreDeposit deposit, String normalizedQuery) {
        if (normalize(deposit.id()).equals(normalizedQuery) || normalize(deposit.id()).contains(normalizedQuery)) {
            return true;
        }

        return deposit.layers().stream()
                .map(OreDepositLayer::id)
                .map(OreVeinLocator::normalize)
                .anyMatch(id -> id.equals(normalizedQuery) || id.contains(normalizedQuery));
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    private static long centerKey(BlockPos center) {
        int chunkX = Math.floorDiv(center.getX(), CHUNK_SIZE);
        int chunkZ = Math.floorDiv(center.getZ(), CHUNK_SIZE);
        return (((long) chunkX) << 32) ^ (chunkZ & 0xffffffffL);
    }

    private static long mixedSeed(long seed, int a, int b, int c) {
        long value = seed;
        value ^= (long) a * 0x9E3779B97F4A7C15L;
        value ^= (long) b * 0xC2B2AE3D27D4EB4FL;
        value ^= (long) c * 0x165667B19E3779F9L;
        return mix(value);
    }

    private static long mix(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return value;
    }

    private record GridResult(OreDeposit deposit, int centerX, int centerY, int centerZ) {
    }

    public record Result(
            String depositId,
            BlockPos center,
            BlockPos surfaceIndicator,
            String dimension,
            int distanceBlocks,
            List<String> layers,
            List<String> surfaceIndicators
    ) {
    }

    record SavedVein(
            OreDeposit deposit,
            BlockPos center,
            BlockPos surfaceIndicator,
            String dimension
    ) {
    }
}
