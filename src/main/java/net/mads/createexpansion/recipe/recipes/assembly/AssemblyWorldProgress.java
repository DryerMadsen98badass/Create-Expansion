package net.mads.createexpansion.recipe.recipes.assembly;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AssemblyWorldProgress {
    private static final Map<Key, Entry> ACTIVE = new HashMap<>();

    private AssemblyWorldProgress() {
    }

    public static Entry get(Level level, BlockPos pos) {
        return ACTIVE.get(new Key(level.dimension(), pos.immutable()));
    }

    public static void set(
            Level level,
            BlockPos pos,
            List<ResourceLocation> candidateRecipeIds,
            int action,
            List<ResourceLocation> completedFallbackRecipeIds
    ) {
        if (candidateRecipeIds.isEmpty() && completedFallbackRecipeIds.isEmpty()) {
            remove(level, pos);
            return;
        }

        ACTIVE.put(
                new Key(level.dimension(), pos.immutable()),
                new Entry(
                        List.copyOf(candidateRecipeIds),
                        action,
                        List.copyOf(completedFallbackRecipeIds)
                )
        );
    }

    public static void remove(Level level, BlockPos pos) {
        ACTIVE.remove(new Key(level.dimension(), pos.immutable()));
    }

    private record Key(ResourceKey<Level> dimension, BlockPos pos) {
    }

    public record Entry(
            List<ResourceLocation> candidateRecipeIds,
            int action,
            List<ResourceLocation> completedFallbackRecipeIds
    ) {
        public Entry {
            candidateRecipeIds = List.copyOf(candidateRecipeIds);
            completedFallbackRecipeIds = List.copyOf(completedFallbackRecipeIds);
        }

        /**
         * Backwards-compatible recipe id for integrations such as Jade that
         * display one representative active assembly recipe.
         *
         * The actual assembly logic still keeps every candidate recipe in
         * candidateRecipeIds and does not lock the block to this recipe.
         */
        public ResourceLocation recipeId() {
            if (!candidateRecipeIds.isEmpty()) {
                return candidateRecipeIds.getFirst();
            }

            if (!completedFallbackRecipeIds.isEmpty()) {
                return completedFallbackRecipeIds.getFirst();
            }

            throw new IllegalStateException(
                    "Assembly progress has no candidate or completed recipe ids"
            );
        }

        public boolean canFinalizeCompletedRecipe() {
            return !completedFallbackRecipeIds.isEmpty();
        }
    }
}
