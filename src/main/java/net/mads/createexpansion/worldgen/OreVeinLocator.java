package net.mads.createexpansion.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class OreVeinLocator {
    private static final int CHUNK_SIZE = 16;
    private static final int DEFAULT_SEARCH_RADIUS_GRIDS = 48;

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
        int originChunkX = Math.floorDiv(origin.getX(), CHUNK_SIZE);
        int originChunkZ = Math.floorDiv(origin.getZ(), CHUNK_SIZE);
        int originGridX = Math.floorDiv(originChunkX, OreDepositDefinitions.GRID_SIZE_CHUNKS);
        int originGridZ = Math.floorDiv(originChunkZ, OreDepositDefinitions.GRID_SIZE_CHUNKS);
        Result closest = null;

        for (int gridX = originGridX - radiusGrids; gridX <= originGridX + radiusGrids; gridX++) {
            for (int gridZ = originGridZ - radiusGrids; gridZ <= originGridZ + radiusGrids; gridZ++) {
                Optional<GridResult> resolved = resolveGrid(level, gridX, gridZ);
                if (resolved.isEmpty() || !matches(resolved.get().deposit(), normalizedQuery)) {
                    continue;
                }

                GridResult gridResult = resolved.get();
                int dx = gridResult.centerX() - origin.getX();
                int dz = gridResult.centerZ() - origin.getZ();
                int distance = Mth.floor(Math.sqrt(dx * dx + dz * dz));
                Result candidate = new Result(
                        gridResult.deposit().id(),
                        new BlockPos(gridResult.centerX(), gridResult.centerY(), gridResult.centerZ()),
                        distance,
                        gridResult.deposit().layers().stream().map(OreDepositLayer::id).toList(),
                        gridResult.deposit().surfaceIndicators().stream().map(indicator -> indicator.name().toLowerCase(Locale.ROOT)).toList()
                );

                if (closest == null || candidate.distanceBlocks() < closest.distanceBlocks()) {
                    closest = candidate;
                }
            }
        }

        return Optional.ofNullable(closest);
    }

    private static Optional<GridResult> resolveGrid(ServerLevel level, int gridX, int gridZ) {
        long gridSeed = OreDepositUtils.mixedSeed(level.getSeed(), gridX, gridZ, 0x5349A85D);
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
        RandomSource chanceRandom = RandomSource.create(OreDepositUtils.mixedSeed(level.getSeed(), gridX, gridZ, 0x29C9D35F));
        if (chanceRandom.nextInt(OreDepositDefinitions.CHANCE_SCALE) >= spawnChance) {
            return Optional.empty();
        }

        RandomSource pickRandom = RandomSource.create(OreDepositUtils.mixedSeed(level.getSeed(), gridX, gridZ, 0x713F4A7B));
        OreDeposit deposit = OreDepositUtils.pickDeposit(candidates, pickRandom.nextInt(totalWeight));
        RandomSource heightRandom = RandomSource.create(OreDepositUtils.mixedSeed(level.getSeed(), gridX, gridZ, deposit.id().hashCode()));
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

    private record GridResult(OreDeposit deposit, int centerX, int centerY, int centerZ) {
    }

    public record Result(
            String depositId,
            BlockPos center,
            int distanceBlocks,
            List<String> layers,
            List<String> surfaceIndicators
    ) {
    }
}
