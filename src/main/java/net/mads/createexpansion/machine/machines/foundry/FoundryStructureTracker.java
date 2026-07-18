package net.mads.createexpansion.machine.machines.foundry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

final class FoundryStructureTracker {
    private static final Map<Level, LevelIndex> INDEXES = new WeakHashMap<>();

    private FoundryStructureTracker() {
    }

    static synchronized void watch(FoundryControllerBlockEntity controller, BlockPos min, BlockPos max) {
        Level level = controller.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        LevelIndex index = INDEXES.computeIfAbsent(level, ignored -> new LevelIndex());
        long controllerPos = controller.getBlockPos().asLong();
        index.remove(controllerPos);

        Set<Long> watched = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                watched.add(BlockPos.asLong(x, min.getY(), z));
            }
        }

        for (int y = min.getY() + 1; y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    watched.add(BlockPos.asLong(x, y, z));
                }
            }
        }

        int nextLayerY = max.getY() + 1;
        {
            for (int x = min.getX(); x <= max.getX(); x++) {
                watched.add(BlockPos.asLong(x, nextLayerY, min.getZ()));
                watched.add(BlockPos.asLong(x, nextLayerY, max.getZ()));
            }
            for (int z = min.getZ() + 1; z < max.getZ(); z++) {
                watched.add(BlockPos.asLong(min.getX(), nextLayerY, z));
                watched.add(BlockPos.asLong(max.getX(), nextLayerY, z));
            }
        }

        index.byController.put(controllerPos, watched);
        for (long watchedPos : watched) {
            index.byPosition.computeIfAbsent(watchedPos, ignored -> new HashSet<>()).add(controllerPos);
        }
    }

    static synchronized void blockChanged(Level level, BlockPos pos) {
        LevelIndex index = INDEXES.get(level);
        if (index == null) {
            return;
        }
        Set<Long> controllers = index.byPosition.get(pos.asLong());
        if (controllers == null) {
            return;
        }
        for (long controllerPos : Set.copyOf(controllers)) {
            if (level.getBlockEntity(BlockPos.of(controllerPos)) instanceof FoundryControllerBlockEntity controller) {
                controller.markStructureDirty();
            }
        }
    }

    static synchronized void remove(FoundryControllerBlockEntity controller) {
        Level level = controller.getLevel();
        LevelIndex index = level == null ? null : INDEXES.get(level);
        if (index != null) {
            index.remove(controller.getBlockPos().asLong());
        }
    }

    private static final class LevelIndex {
        private final Map<Long, Set<Long>> byPosition = new HashMap<>();
        private final Map<Long, Set<Long>> byController = new HashMap<>();

        private void remove(long controllerPos) {
            Set<Long> watched = byController.remove(controllerPos);
            if (watched == null) {
                return;
            }
            for (long watchedPos : watched) {
                Set<Long> controllers = byPosition.get(watchedPos);
                if (controllers != null) {
                    controllers.remove(controllerPos);
                    if (controllers.isEmpty()) {
                        byPosition.remove(watchedPos);
                    }
                }
            }
        }
    }
}
