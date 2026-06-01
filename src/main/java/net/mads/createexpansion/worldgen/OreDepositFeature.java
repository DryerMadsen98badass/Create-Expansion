package net.mads.createexpansion.worldgen;

import com.mojang.serialization.Codec;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OreDepositFeature extends Feature<NoneFeatureConfiguration> {
    private static final int CHUNK_SIZE = 16;
    private static final int HORIZONTAL_RADIUS_BLOCKS = 24;
    private static final double ORE_DENSITY_MULTIPLIER = 2.35D;
    private static final double HALO_DENSITY = 0.34D;
    private static final double HALO_EDGE_FADE_START = 0.70D;
    private static final double SURFACE_INDICATOR_CHANCE = 0.82D;

    public OreDepositFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        int chunkX = Math.floorDiv(origin.getX(), CHUNK_SIZE);
        int chunkZ = Math.floorDiv(origin.getZ(), CHUNK_SIZE);
        boolean placed = generateVillageBonusDeposits(level, chunkX, chunkZ);
        GridDeposit gridDeposit = resolveGridDeposit(level, chunkX, chunkZ);

        if (gridDeposit == null || !gridDeposit.contains(chunkX, chunkZ)) {
            return placed;
        }

        return generateChunkDeposit(level, chunkX, chunkZ, gridDeposit) || placed;
    }

    private static GridDeposit resolveGridDeposit(WorldGenLevel level, int chunkX, int chunkZ) {
        int gridX = Math.floorDiv(chunkX, OreDepositDefinitions.GRID_SIZE_CHUNKS);
        int gridZ = Math.floorDiv(chunkZ, OreDepositDefinitions.GRID_SIZE_CHUNKS);
        long gridSeed = mixedSeed(level.getSeed(), gridX, gridZ, 0x5349A85D);
        RandomSource layoutRandom = RandomSource.create(gridSeed);
        int maxOffset = OreDepositDefinitions.GRID_SIZE_CHUNKS - OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS;
        int startChunkX = gridX * OreDepositDefinitions.GRID_SIZE_CHUNKS + layoutRandom.nextInt(maxOffset + 1);
        int startChunkZ = gridZ * OreDepositDefinitions.GRID_SIZE_CHUNKS + layoutRandom.nextInt(maxOffset + 1);
        int centerChunkX = startChunkX + OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS / 2;
        int centerChunkZ = startChunkZ + OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS / 2;
        int centerX = centerChunkX * CHUNK_SIZE + 8;
        int centerZ = centerChunkZ * CHUNK_SIZE + 8;

        // Important: do not query biomes in chunks that are not part of this deposit.
        // During world generation, asking WorldGenRegion for a chunk outside the active
        // generation area can crash with: Requested chunk unavailable during world generation.
        GridDeposit layoutOnly = new GridDeposit(null, startChunkX, startChunkZ, centerX, 0, centerZ, gridSeed, false);
        if (!layoutOnly.contains(chunkX, chunkZ)) {
            return null;
        }

        List<Holder<Biome>> sampledBiomes = sampleBiomes(level, centerX, centerZ);
        List<OreDeposit> candidates = OreDepositDefinitions.ALL.stream()
                .filter(deposit -> deposit.matchesAny(sampledBiomes))
                .toList();

        if (candidates.isEmpty()) {
            return null;
        }

        int totalWeight = candidates.stream().mapToInt(OreDeposit::weight).sum();
        int spawnChance = Math.min(totalWeight, OreDepositDefinitions.CHANCE_SCALE);
        RandomSource chanceRandom = RandomSource.create(mixedSeed(level.getSeed(), gridX, gridZ, 0x29C9D35F));
        if (chanceRandom.nextInt(OreDepositDefinitions.CHANCE_SCALE) >= spawnChance) {
            return null;
        }

        RandomSource pickRandom = RandomSource.create(mixedSeed(level.getSeed(), gridX, gridZ, 0x713F4A7B));
        OreDeposit deposit = pickDeposit(candidates, pickRandom.nextInt(totalWeight));
        RandomSource heightRandom = RandomSource.create(mixedSeed(level.getSeed(), gridX, gridZ, deposit.id().hashCode()));
        int centerY = OreDepositPlacement.chooseCenterY(level, deposit, centerX, centerZ, heightRandom);

        return new GridDeposit(deposit, startChunkX, startChunkZ, centerX, centerY, centerZ, gridSeed, true);
    }

    private static List<Holder<Biome>> sampleBiomes(WorldGenLevel level, int centerX, int centerZ) {
        List<Holder<Biome>> biomes = new ArrayList<>();
        addBiomeSample(level, biomes, centerX, centerZ, 64);
        addBiomeSample(level, biomes, centerX, centerZ, 0);
        addBiomeSample(level, biomes, centerX, centerZ, -32);
        return biomes;
    }

    private static void addBiomeSample(WorldGenLevel level, List<Holder<Biome>> biomes, int x, int z, int y) {
        int sampleY = Mth.clamp(y, level.getMinBuildHeight(), level.getMaxBuildHeight() - 1);
        Holder<Biome> biome = level.getBiome(new BlockPos(x, sampleY, z));
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

    private static boolean generateChunkDeposit(WorldGenLevel level, int chunkX, int chunkZ, GridDeposit gridDeposit) {
        OreDeposit deposit = gridDeposit.deposit();
        int minX = chunkX * CHUNK_SIZE;
        int minZ = chunkZ * CHUNK_SIZE;
        int minY = Math.max(level.getMinBuildHeight(), gridDeposit.centerY() - deposit.verticalRadius());
        int maxY = Math.min(level.getMaxBuildHeight() - 1, gridDeposit.centerY() + deposit.verticalRadius());
        boolean placed = false;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        if (gridDeposit.allowSurfaceIndicator() && isCenterChunk(chunkX, chunkZ, gridDeposit)) {
            placed |= generateSurfaceIndicator(level, gridDeposit);
        }

        for (int x = minX; x < minX + CHUNK_SIZE; x++) {
            for (int z = minZ; z < minZ + CHUNK_SIZE; z++) {
                double dx = (x + 0.5D - gridDeposit.centerX()) / HORIZONTAL_RADIUS_BLOCKS;
                double dz = (z + 0.5D - gridDeposit.centerZ()) / HORIZONTAL_RADIUS_BLOCKS;

                for (int y = minY; y <= maxY; y++) {
                    double dy = (y + 0.5D - gridDeposit.centerY()) / deposit.verticalRadius();
                    double distance = dx * dx + dy * dy + dz * dz;
                    if (distance > 1.25D) {
                        continue;
                    }

                    double shapeNoise = randomUnit(mixedSeed(gridDeposit.seed(), x, z, y)) * 0.35D - 0.12D;
                    if (distance + shapeNoise > 1.0D) {
                        continue;
                    }

                    mutablePos.set(x, y, z);
                    BlockState currentState = level.getBlockState(mutablePos);
                    if (tryPlaceIndicatorRock(level, mutablePos, currentState, gridDeposit.seed(), x, y, z, distance)) {
                        currentState = level.getBlockState(mutablePos);
                        placed = true;
                    }

                    double density = Math.min(0.92D, deposit.density() * ORE_DENSITY_MULTIPLIER) * Math.max(0.38D, 1.25D - distance);
                    if (randomUnit(mixedSeed(gridDeposit.seed(), x, y, z + 0x51D)) > density) {
                        continue;
                    }

                    Optional<MaterialPart> part = orePartFor(currentState);
                    if (part.isEmpty()) {
                        continue;
                    }

                    OreDepositLayer layer = pickLayer(deposit, x, y, z, gridDeposit.seed());
                    Optional<BlockState> replacement = layer.stateFor(part.get());
                    if (replacement.isEmpty() || !level.ensureCanWrite(mutablePos)) {
                        continue;
                    }

                    level.setBlock(mutablePos, replacement.get(), 2);
                    placed = true;
                }
            }
        }

        return placed;
    }

    private static boolean isCenterChunk(int chunkX, int chunkZ, GridDeposit gridDeposit) {
        return chunkX == Math.floorDiv(gridDeposit.centerX(), CHUNK_SIZE)
                && chunkZ == Math.floorDiv(gridDeposit.centerZ(), CHUNK_SIZE);
    }

    private static boolean generateSurfaceIndicator(WorldGenLevel level, GridDeposit gridDeposit) {
        OreDeposit deposit = gridDeposit.deposit();
        if (deposit.surfaceIndicators().isEmpty()) {
            return false;
        }

        if (randomUnit(mixedSeed(gridDeposit.seed(), gridDeposit.centerX(), gridDeposit.centerZ(), 0x5F2A19C7)) > SURFACE_INDICATOR_CHANCE) {
            return false;
        }

        SurfaceIndicator indicator = pickSurfaceIndicator(deposit, gridDeposit.seed());
        return switch (indicator) {
            case LAVA_POOL -> placeLavaPool(level, gridDeposit);
            case STONE_SPOT -> placeStoneSpot(level, gridDeposit);
            case DEAD_SOIL -> placeDeadSoil(level, gridDeposit);
            case GRAVEL_PATCH -> placeGravelPatch(level, gridDeposit);
            case CRACKED_GROUND -> placeCrackedGround(level, gridDeposit);
            case CRYSTAL_SPOT -> placeCrystalSpot(level, gridDeposit);
            case DEAD_PLANTS -> placeDeadPlants(level, gridDeposit);
            case BOULDER_CLUSTER -> placeBoulderCluster(level, gridDeposit);
        };
    }

    private static SurfaceIndicator pickSurfaceIndicator(OreDeposit deposit, long seed) {
        int totalWeight = deposit.surfaceIndicators().stream().mapToInt(SurfaceIndicator::weight).sum();
        int roll = (int) (randomUnit(mixedSeed(seed, deposit.id().hashCode(), totalWeight, 0x43B7E21D)) * totalWeight);
        int cursor = roll;

        for (SurfaceIndicator indicator : deposit.surfaceIndicators()) {
            cursor -= indicator.weight();
            if (cursor < 0) {
                return indicator;
            }
        }

        return deposit.surfaceIndicators().get(deposit.surfaceIndicators().size() - 1);
    }

    private static boolean placeStoneSpot(WorldGenLevel level, GridDeposit gridDeposit) {
        return placeGroundPatch(level, gridDeposit, 5, 0x14A9D, (seed, x, z) -> stoneSpotBlock(seed, x, z));
    }

    private static boolean placeDeadSoil(WorldGenLevel level, GridDeposit gridDeposit) {
        return placeGroundPatch(level, gridDeposit, 5, 0x51DEAD, (seed, x, z) -> deadSoilBlock(seed, x, z));
    }

    private static boolean placeGravelPatch(WorldGenLevel level, GridDeposit gridDeposit) {
        return placeGroundPatch(level, gridDeposit, 5, 0x611A7E, (seed, x, z) -> gravelPatchBlock(seed, x, z));
    }

    private static boolean placeCrackedGround(WorldGenLevel level, GridDeposit gridDeposit) {
        return placeGroundPatch(level, gridDeposit, 4, 0xC2A55, (seed, x, z) -> crackedGroundBlock(seed, x, z));
    }

    private static boolean placeLavaPool(WorldGenLevel level, GridDeposit gridDeposit) {
        boolean placed = false;
        int radius = 4;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz) / radius;
                double noise = randomUnit(mixedSeed(gridDeposit.seed(), dx, dz, 0x1A7A)) * 0.35D - 0.12D;
                if (distance + noise > 1.0D) {
                    continue;
                }

                int x = gridDeposit.centerX() + dx;
                int z = gridDeposit.centerZ() + dz;
                BlockPos ground = findSurfaceGround(level, x, z);
                if (ground == null || !prepareSurface(level, ground)) {
                    continue;
                }

                double rawDistance = Math.sqrt(dx * dx + dz * dz);
                if (rawDistance <= 1.45D) {
                    BlockPos below = ground.below();
                    if (level.ensureCanWrite(below)) {
                        level.setBlock(below, Blocks.STONE.defaultBlockState(), 2);
                    }

                    level.setBlock(ground, Blocks.LAVA.defaultBlockState(), 2);
                } else {
                    level.setBlock(ground, lavaRimBlock(gridDeposit.seed(), x, z), 2);
                }

                placed = true;
            }
        }

        return placed;
    }

    private static boolean placeCrystalSpot(WorldGenLevel level, GridDeposit gridDeposit) {
        boolean placed = placeGroundPatch(level, gridDeposit, 4, 0xC257A1, (seed, x, z) -> crystalGroundBlock(seed, x, z));
        int crystals = 2 + (int) (randomUnit(mixedSeed(gridDeposit.seed(), 0xAC, 0xDC, 0x11)) * 3.0D);

        for (int i = 0; i < crystals; i++) {
            int dx = (int) Math.floor(randomUnit(mixedSeed(gridDeposit.seed(), i, 0xCA1, 0xA)) * 7.0D) - 3;
            int dz = (int) Math.floor(randomUnit(mixedSeed(gridDeposit.seed(), i, 0xCA1, 0xB)) * 7.0D) - 3;
            BlockPos ground = findSurfaceGround(level, gridDeposit.centerX() + dx, gridDeposit.centerZ() + dz);
            if (ground == null) {
                continue;
            }

            BlockPos crystal = ground.above();
            if (!level.getBlockState(crystal).isAir() || !level.ensureCanWrite(crystal)) {
                continue;
            }

            level.setBlock(crystal, Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
            placed = true;
        }

        return placed;
    }

    private static boolean placeDeadPlants(WorldGenLevel level, GridDeposit gridDeposit) {
        boolean placed = placeDeadSoil(level, gridDeposit);
        int radius = 5;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (!insideSurfacePatch(gridDeposit.seed(), dx, dz, radius, 0xDEADB)) {
                    continue;
                }

                if (randomUnit(mixedSeed(gridDeposit.seed(), dx, dz, 0xB05)) > 0.28D) {
                    continue;
                }

                BlockPos ground = findSurfaceGround(level, gridDeposit.centerX() + dx, gridDeposit.centerZ() + dz);
                if (ground == null) {
                    continue;
                }

                BlockPos plant = ground.above();
                if (level.getBlockState(plant).isAir() && level.ensureCanWrite(plant)) {
                    level.setBlock(plant, Blocks.DEAD_BUSH.defaultBlockState(), 2);
                    placed = true;
                }
            }
        }

        return placed;
    }

    private static boolean placeBoulderCluster(WorldGenLevel level, GridDeposit gridDeposit) {
        boolean placed = false;
        int boulders = 5 + (int) (randomUnit(mixedSeed(gridDeposit.seed(), 0xB0, 0x1D, 0xE2)) * 5.0D);

        for (int i = 0; i < boulders; i++) {
            int dx = (int) Math.floor(randomUnit(mixedSeed(gridDeposit.seed(), i, 0xB01, 0xA)) * 9.0D) - 4;
            int dz = (int) Math.floor(randomUnit(mixedSeed(gridDeposit.seed(), i, 0xB01, 0xB)) * 9.0D) - 4;
            BlockPos ground = findSurfaceGround(level, gridDeposit.centerX() + dx, gridDeposit.centerZ() + dz);
            if (ground == null) {
                continue;
            }

            int height = 1 + (int) (randomUnit(mixedSeed(gridDeposit.seed(), i, dx, dz)) * 3.0D);
            for (int y = 1; y <= height; y++) {
                BlockPos pos = ground.above(y);
                if (!level.getBlockState(pos).isAir() || !level.ensureCanWrite(pos)) {
                    break;
                }

                level.setBlock(pos, boulderBlock(gridDeposit.seed(), i, y), 2);
                placed = true;
            }
        }

        return placed;
    }

    private static boolean placeGroundPatch(WorldGenLevel level, GridDeposit gridDeposit, int radius, int salt, SurfaceBlockPicker blockPicker) {
        boolean placed = false;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (!insideSurfacePatch(gridDeposit.seed(), dx, dz, radius, salt)) {
                    continue;
                }

                int x = gridDeposit.centerX() + dx;
                int z = gridDeposit.centerZ() + dz;
                BlockPos ground = findSurfaceGround(level, x, z);
                if (ground == null || !prepareSurface(level, ground)) {
                    continue;
                }

                level.setBlock(ground, blockPicker.pick(gridDeposit.seed(), x, z), 2);
                placed = true;
            }
        }

        return placed;
    }

    private static boolean insideSurfacePatch(long seed, int dx, int dz, int radius, int salt) {
        double distance = Math.sqrt(dx * dx + dz * dz) / radius;
        double noise = randomUnit(mixedSeed(seed, dx, dz, salt)) * 0.45D - 0.16D;
        return distance + noise <= 1.0D;
    }

    private static BlockPos findSurfaceGround(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
        if (y <= level.getMinBuildHeight() || y >= level.getMaxBuildHeight() - 2) {
            return null;
        }

        BlockPos ground = new BlockPos(x, y, z);
        BlockState groundState = level.getBlockState(ground);
        BlockState aboveState = level.getBlockState(ground.above());
        if (!groundState.getFluidState().isEmpty() || !aboveState.getFluidState().isEmpty()) {
            return null;
        }

        if (!isSurfaceGround(groundState)) {
            return null;
        }

        return ground;
    }

    private static boolean prepareSurface(WorldGenLevel level, BlockPos ground) {
        if (!level.ensureCanWrite(ground)) {
            return false;
        }

        BlockPos above = ground.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.isAir()) {
            return true;
        }

        if (!aboveState.getFluidState().isEmpty() || !aboveState.canBeReplaced() || !level.ensureCanWrite(above)) {
            return false;
        }

        level.setBlock(above, Blocks.AIR.defaultBlockState(), 2);
        return true;
    }

    private static boolean isSurfaceGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.CALCITE)
                || state.is(Blocks.NETHERRACK)
                || state.is(Blocks.BLACKSTONE)
                || state.is(Blocks.BASALT)
                || state.is(Blocks.END_STONE);
    }

    private static BlockState stoneSpotBlock(long seed, int x, int z) {
        double roll = randomUnit(mixedSeed(seed, x, z, 0x570));
        if (roll < 0.28D) {
            return Blocks.TUFF.defaultBlockState();
        }

        if (roll < 0.48D) {
            return Blocks.ANDESITE.defaultBlockState();
        }

        if (roll < 0.68D) {
            return Blocks.GRANITE.defaultBlockState();
        }

        if (roll < 0.86D) {
            return Blocks.DIORITE.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    private static BlockState deadSoilBlock(long seed, int x, int z) {
        double roll = randomUnit(mixedSeed(seed, x, z, 0xD1E7));
        if (roll < 0.45D) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        }

        if (roll < 0.78D) {
            return Blocks.ROOTED_DIRT.defaultBlockState();
        }

        return Blocks.GRAVEL.defaultBlockState();
    }

    private static BlockState gravelPatchBlock(long seed, int x, int z) {
        double roll = randomUnit(mixedSeed(seed, x, z, 0x67A));
        if (roll < 0.65D) {
            return Blocks.GRAVEL.defaultBlockState();
        }

        if (roll < 0.85D) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    private static BlockState crackedGroundBlock(long seed, int x, int z) {
        double roll = randomUnit(mixedSeed(seed, x, z, 0xC2A));
        if (roll < 0.12D) {
            return Blocks.MAGMA_BLOCK.defaultBlockState();
        }

        if (roll < 0.45D) {
            return Blocks.BLACKSTONE.defaultBlockState();
        }

        if (roll < 0.75D) {
            return Blocks.BASALT.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    private static BlockState crystalGroundBlock(long seed, int x, int z) {
        double roll = randomUnit(mixedSeed(seed, x, z, 0xC457A1));
        if (roll < 0.55D) {
            return Blocks.CALCITE.defaultBlockState();
        }

        if (roll < 0.82D) {
            return Blocks.SMOOTH_BASALT.defaultBlockState();
        }

        return Blocks.AMETHYST_BLOCK.defaultBlockState();
    }

    private static BlockState lavaRimBlock(long seed, int x, int z) {
        double roll = randomUnit(mixedSeed(seed, x, z, 0x1A7A));
        if (roll < 0.12D) {
            return Blocks.MAGMA_BLOCK.defaultBlockState();
        }

        if (roll < 0.36D) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }

        if (roll < 0.65D) {
            return Blocks.BLACKSTONE.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    private static BlockState boulderBlock(long seed, int index, int y) {
        double roll = randomUnit(mixedSeed(seed, index, y, 0xB01D));
        if (roll < 0.35D) {
            return Blocks.TUFF.defaultBlockState();
        }

        if (roll < 0.58D) {
            return Blocks.ANDESITE.defaultBlockState();
        }

        if (roll < 0.80D) {
            return Blocks.GRANITE.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    private static boolean tryPlaceIndicatorRock(
            WorldGenLevel level,
            BlockPos.MutableBlockPos pos,
            BlockState currentState,
            long seed,
            int x,
            int y,
            int z,
            double distance
    ) {
        if (!isOverworldNaturalStone(currentState)) {
            return false;
        }

        double edgeFade = distance > HALO_EDGE_FADE_START
                ? Math.max(0.0D, 1.0D - (distance - HALO_EDGE_FADE_START) / (1.0D - HALO_EDGE_FADE_START))
                : 1.0D;
        double chance = HALO_DENSITY * Math.max(0.35D, edgeFade);
        if (randomUnit(mixedSeed(seed, x, z, y + 0x42B)) > chance) {
            return false;
        }

        if (!level.ensureCanWrite(pos)) {
            return false;
        }

        level.setBlock(pos, indicatorRockFor(seed, x, y, z), 2);
        return true;
    }

    private static boolean isOverworldNaturalStone(BlockState state) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    }

    private static BlockState indicatorRockFor(long seed, int x, int y, int z) {
        double roll = randomUnit(mixedSeed(seed, x + 0x2D7, y, z));
        if (roll < 0.34D) {
            return Blocks.TUFF.defaultBlockState();
        }

        if (roll < 0.56D) {
            return Blocks.GRANITE.defaultBlockState();
        }

        if (roll < 0.78D) {
            return Blocks.ANDESITE.defaultBlockState();
        }

        return Blocks.DIORITE.defaultBlockState();
    }

    private static boolean generateVillageBonusDeposits(WorldGenLevel level, int chunkX, int chunkZ) {
        StructureManager structureManager = level.getLevel().structureManager();
        if (level instanceof WorldGenRegion region) {
            structureManager = structureManager.forWorldGenRegion(region);
        }

        Registry<Structure> structures = structureManager.registryAccess().registryOrThrow(Registries.STRUCTURE);
        List<StructureStart> villageStarts = structureManager.startsForStructure(new ChunkPos(chunkX, chunkZ), structure -> {
            ResourceLocation id = structures.getKey(structure);
            return id != null && id.getPath().startsWith("village_");
        });

        boolean placed = false;
        for (StructureStart start : villageStarts) {
            if (!start.isValid()) {
                continue;
            }

            GridDeposit villageDeposit = villageDeposit(level, start);
            if (villageDeposit.contains(chunkX, chunkZ)) {
                placed |= generateChunkDeposit(level, chunkX, chunkZ, villageDeposit);
            }
        }

        return placed;
    }

    private static GridDeposit villageDeposit(WorldGenLevel level, StructureStart start) {
        BoundingBox box = start.getBoundingBox();
        BlockPos center = box.getCenter();
        ChunkPos centerChunk = new ChunkPos(center);
        long seed = mixedSeed(level.getSeed(), start.getChunkPos().x, start.getChunkPos().z, 0x612E3A1D);
        RandomSource random = RandomSource.create(seed);
        OreDeposit deposit = OreDepositDefinitions.VILLAGE_BONUS.get(random.nextInt(OreDepositDefinitions.VILLAGE_BONUS.size()));
        int minY = Math.max(level.getMinBuildHeight(), deposit.minY());
        int maxY = Math.min(level.getMaxBuildHeight() - 1, deposit.maxY());
        int centerY = Mth.nextInt(random, minY, maxY);

        return new GridDeposit(
                deposit,
                centerChunk.x - 1,
                centerChunk.z - 1,
                center.getX(),
                centerY,
                center.getZ(),
                seed,
                false
        );
    }

    private static Optional<MaterialPart> orePartFor(BlockState state) {
        if (state.is(Blocks.STONE)) {
            return Optional.of(MaterialPart.ORE);
        }

        if (state.is(Blocks.DEEPSLATE)) {
            return Optional.of(MaterialPart.DEEPSLATE_ORE);
        }

        if (state.is(Blocks.DIORITE)) {
            return Optional.of(MaterialPart.DIORITE_ORE);
        }

        if (state.is(Blocks.ANDESITE)) {
            return Optional.of(MaterialPart.ANDESITE_ORE);
        }

        if (state.is(Blocks.GRANITE)) {
            return Optional.of(MaterialPart.GRANITE_ORE);
        }

        if (state.is(Blocks.TUFF)) {
            return Optional.of(MaterialPart.TUFF_ORE);
        }

        if (state.is(Blocks.NETHERRACK)) {
            return Optional.of(MaterialPart.NETHERRACK_ORE);
        }

        if (state.is(Blocks.BLACKSTONE)) {
            return Optional.of(MaterialPart.BLACKSTONE_ORE);
        }

        if (state.is(Blocks.END_STONE)) {
            return Optional.of(MaterialPart.END_STONE_ORE);
        }

        if (state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)) {
            return Optional.of(MaterialPart.DEEPSLATE_ORE);
        }

        if (state.is(BlockTags.STONE_ORE_REPLACEABLES)) {
            return Optional.of(MaterialPart.ORE);
        }

        return Optional.empty();
    }

    private static OreDepositLayer pickLayer(OreDeposit deposit, int x, int y, int z, long seed) {
        int roll = (int) (randomUnit(mixedSeed(seed, x, y, z)) * deposit.layerWeight());
        int cursor = roll;

        for (OreDepositLayer layer : deposit.layers()) {
            cursor -= layer.weight();
            if (cursor < 0) {
                return layer;
            }
        }

        return deposit.layers().get(deposit.layers().size() - 1);
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

    private static double randomUnit(long seed) {
        return (double) (mix(seed) >>> 11) * 0x1.0p-53;
    }

    private interface SurfaceBlockPicker {
        BlockState pick(long seed, int x, int z);
    }

    private record GridDeposit(OreDeposit deposit, int startChunkX, int startChunkZ, int centerX, int centerY, int centerZ, long seed, boolean allowSurfaceIndicator) {
        boolean contains(int chunkX, int chunkZ) {
            return chunkX >= startChunkX
                    && chunkX < startChunkX + OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS
                    && chunkZ >= startChunkZ
                    && chunkZ < startChunkZ + OreDepositDefinitions.DEPOSIT_SIZE_CHUNKS;
        }
    }
}
