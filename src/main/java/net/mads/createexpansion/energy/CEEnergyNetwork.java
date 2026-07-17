package net.mads.createexpansion.energy;

import net.mads.createexpansion.debug.CEPerformanceProfiler;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

public final class CEEnergyNetwork {
    private static final Map<Level, LevelRouteCache> ROUTE_CACHES = new WeakHashMap<>();
    private static final Map<Level, Map<BlockPos, FlowSample>> WIRE_FLOWS = new WeakHashMap<>();

    private CEEnergyNetwork() {
    }

    public static void invalidate(Level level) {
        if (level == null || level.isClientSide()) {
            return;
        }
        LevelRouteCache cache = ROUTE_CACHES.get(level);
        if (cache != null) {
            cache.clear();
        }
    }

    public static int acceptFromWire(Level level, BlockPos sourceWirePos, Direction sourceSide, int voltage, int amperage) {
        if (level == null || level.isClientSide() || voltage <= 0 || amperage <= 0) {
            return 0;
        }

        int usedAmps = 0;
        for (EnergyRoutePath path : cachedRoutes(level, sourceWirePos, sourceSide)) {
            if (usedAmps >= amperage) {
                break;
            }

            BlockEntity targetBlockEntity = level.getBlockEntity(path.targetPos);
            CEEnergyContainer destination = energyContainer(targetBlockEntity);
            if (destination == null || !destination.inputsEnergy(path.targetSide) || destination.getEnergyCanBeInserted() <= 0) {
                continue;
            }

            int accepted = destination.acceptEnergyFromNetwork(path.targetSide, voltage, amperage - usedAmps);
            if (accepted <= 0) {
                continue;
            }

            usedAmps += accepted;
            if (targetBlockEntity instanceof MachinePortBlockEntity port) {
                port.recordEnergyNetworkInput(accepted * voltage, voltage);
            }
            for (BlockPos wirePos : path.wires) {
                recordWireFlow(level, wirePos, accepted * voltage, voltage);
                if (level.getBlockEntity(wirePos) instanceof EnergyWireBlockEntity wire) {
                    wire.incrementAmperage(accepted, voltage);
                }
            }
        }
        return usedAmps;
    }

    public static int outputToAdjacentWires(Level level, BlockPos sourcePos, CEEnergyContainer source) {
        long profileStart = CEPerformanceProfiler.begin(level);
        try {
            return outputToAdjacentWiresInner(level, sourcePos, source);
        } finally {
            CEPerformanceProfiler.record(CEPerformanceProfiler.Metric.WIRE_NETWORK, profileStart);
        }
    }

    public static void recordPortLoad(Level level, BlockPos portPos, int cePerTick, int voltage) {
        if (level == null || level.isClientSide() || cePerTick <= 0 || voltage <= 0) {
            return;
        }

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        for (Direction direction : Direction.values()) {
            BlockPos wirePos = portPos.relative(direction);
            BlockState wireState = level.getBlockState(wirePos);
            if (wireState.getBlock() instanceof EnergyWireBlock && EnergyWireBlock.hasEnabledConnection(wireState, direction.getOpposite()) && seen.add(wirePos)) {
                queue.add(wirePos);
            }
        }

        while (!queue.isEmpty()) {
            BlockPos wirePos = queue.remove();
            BlockState state = level.getBlockState(wirePos);
            if (!(state.getBlock() instanceof EnergyWireBlock)) {
                continue;
            }

            if (level.getBlockEntity(wirePos) instanceof EnergyWireBlockEntity wire) {
                wire.incrementLoad(cePerTick, voltage);
            }
            recordWireFlow(level, wirePos, cePerTick, voltage);

            for (Direction direction : Direction.values()) {
                if (!EnergyWireBlock.hasEnabledConnection(state, direction)) {
                    continue;
                }
                BlockPos nextPos = wirePos.relative(direction);
                BlockState nextState = level.getBlockState(nextPos);
                if (nextState.getBlock() instanceof EnergyWireBlock && EnergyWireBlock.wiresConnect(state, direction, nextState) && seen.add(nextPos)) {
                    queue.add(nextPos);
                }
            }
        }
    }

    public static int currentWireCEt(Level level, BlockPos wirePos) {
        FlowSample sample = currentWireFlow(level, wirePos);
        return sample == null ? 0 : sample.cePerTick;
    }

    public static int currentWireVoltage(Level level, BlockPos wirePos) {
        FlowSample sample = currentWireFlow(level, wirePos);
        return sample == null ? 0 : sample.voltage;
    }

    private static FlowSample currentWireFlow(Level level, BlockPos wirePos) {
        if (level == null || wirePos == null) {
            return null;
        }
        Map<BlockPos, FlowSample> flows = WIRE_FLOWS.get(level);
        if (flows == null) {
            return null;
        }
        FlowSample sample = flows.get(wirePos);
        if (sample == null || level.getGameTime() - sample.tick > 40) {
            return null;
        }
        return sample;
    }

    private static void recordWireFlow(Level level, BlockPos wirePos, int cePerTick, int voltage) {
        if (level == null || level.isClientSide() || wirePos == null || cePerTick <= 0 || voltage <= 0) {
            return;
        }
        Map<BlockPos, FlowSample> flows = WIRE_FLOWS.computeIfAbsent(level, ignored -> new HashMap<>());
        long tick = level.getGameTime();
        FlowSample previous = flows.get(wirePos);
        if (previous != null && previous.tick == tick) {
            flows.put(wirePos, new FlowSample(previous.cePerTick + cePerTick, Math.max(previous.voltage, voltage), tick));
        } else {
            flows.put(wirePos, new FlowSample(cePerTick, voltage, tick));
        }
    }

    private static int outputToAdjacentWiresInner(Level level, BlockPos sourcePos, CEEnergyContainer source) {
        if (level == null || level.isClientSide() || source == null || source.getOutputVoltage() <= 0 || source.getOutputAmperage() <= 0) {
            return 0;
        }

        int remainingAmps = source.getOutputAmperage();
        int usedAmps = 0;
        int voltage = source.getOutputVoltage();
        for (Direction direction : Direction.values()) {
            if (remainingAmps <= 0) {
                break;
            }
            if (!source.outputsEnergy(direction)) {
                continue;
            }

            BlockPos wirePos = sourcePos.relative(direction);
            BlockState wireState = level.getBlockState(wirePos);
            if (!(wireState.getBlock() instanceof EnergyWireBlock) || !EnergyWireBlock.hasEnabledConnection(wireState, direction.getOpposite())) {
                continue;
            }

            int canExtract = Math.min(remainingAmps, source.extract(remainingAmps * voltage, true) / voltage);
            if (canExtract <= 0) {
                continue;
            }

            int accepted = acceptFromWire(level, wirePos, direction.getOpposite(), voltage, canExtract);
            if (accepted <= 0) {
                continue;
            }

            source.extract(accepted * voltage, false);
            usedAmps += accepted;
            remainingAmps -= accepted;
        }
        return usedAmps;
    }

    private static List<EnergyRoutePath> cachedRoutes(Level level, BlockPos start, Direction sourceSide) {
        LevelRouteCache cache = ROUTE_CACHES.computeIfAbsent(level, ignored -> new LevelRouteCache());
        RouteKey key = new RouteKey(start.asLong(), sourceSide);
        List<EnergyRoutePath> cached = cache.routes.get(key);
        if (cached != null) {
            return cached;
        }

        List<EnergyRoutePath> routes = routes(level, start, sourceSide);
        cache.routes.put(key, routes);
        return routes;
    }

    private static List<EnergyRoutePath> routes(Level level, BlockPos start, Direction sourceSide) {
        List<EnergyRoutePath> routes = new ArrayList<>();
        Queue<PathNode> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        queue.add(new PathNode(start, List.of(start)));
        seen.add(start);

        while (!queue.isEmpty()) {
            PathNode node = queue.remove();
            BlockState state = level.getBlockState(node.pos);
            if (!(state.getBlock() instanceof EnergyWireBlock)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                if (node.pos.equals(start) && direction == sourceSide) {
                    continue;
                }
                if (!EnergyWireBlock.hasEnabledConnection(state, direction)) {
                    continue;
                }

                BlockPos nextPos = node.pos.relative(direction);
                BlockState nextState = level.getBlockState(nextPos);
                if (nextState.getBlock() instanceof EnergyWireBlock && EnergyWireBlock.wiresConnect(state, direction, nextState)) {
                    if (seen.add(nextPos)) {
                        List<BlockPos> path = new ArrayList<>(node.wires);
                        path.add(nextPos);
                        queue.add(new PathNode(nextPos, path));
                    }
                    continue;
                }

                CEEnergyContainer container = energyContainer(level.getBlockEntity(nextPos));
                Direction targetSide = direction.getOpposite();
                if (container != null && container.inputsEnergy(targetSide)) {
                    routes.add(new EnergyRoutePath(nextPos, targetSide, node.wires));
                }
            }
        }

        routes.sort(java.util.Comparator.comparingInt(path -> path.wires.size()));
        return routes;
    }

    private static CEEnergyContainer energyContainer(BlockEntity blockEntity) {
        if (blockEntity instanceof MachinePortBlockEntity port) {
            return port.ceContainer();
        }
        if (blockEntity instanceof CreativeEnergyBlockEntity creative) {
            return creative.ceContainer();
        }
        return null;
    }

    private record PathNode(BlockPos pos, List<BlockPos> wires) {
    }

    private record EnergyRoutePath(BlockPos targetPos, Direction targetSide, List<BlockPos> wires) {
    }

    private record RouteKey(long startPos, Direction sourceSide) {
    }

    private record FlowSample(int cePerTick, int voltage, long tick) {
    }

    private static final class LevelRouteCache {
        private final Map<RouteKey, List<EnergyRoutePath>> routes = new HashMap<>();

        private void clear() {
            routes.clear();
        }
    }
}
