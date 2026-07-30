package net.mads.createexpansion.recipe.remove;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.fired_bucket.FiredBucketRecipeInjector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RecipeRemovalEvents {
    private RecipeRemovalEvents() {
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();

        removeRecipes(
                server,
                server.getRecipeManager(),
                RemovedCreateRecipes.ALL
        );
    }

    private static void removeRecipes(
            MinecraftServer server,
            RecipeManager recipeManager,
            Set<ResourceLocation> recipesToRemove
    ) {
        Collection<RecipeHolder<?>> loadedRecipes = recipeManager.getRecipes();
        List<RecipeHolder<?>> filteredRecipes = new ArrayList<>(loadedRecipes.size());
        Set<ResourceLocation> loadedRecipeIds = new HashSet<>();
        Set<ResourceLocation> removedRecipes = new HashSet<>();

        for (RecipeHolder<?> recipeHolder : loadedRecipes) {
            ResourceLocation recipeId = recipeHolder.id();
            loadedRecipeIds.add(recipeId);

            if (recipesToRemove.contains(recipeId)) {
                removedRecipes.add(recipeId);
                continue;
            }

            filteredRecipes.add(recipeHolder);
        }

        List<RecipeHolder<?>> finalRecipes = FiredBucketRecipeInjector.withFiredBucketRecipes(
                filteredRecipes,
                server.registryAccess()
        );

        recipeManager.replaceRecipes(finalRecipes);

        CreateExpansion.LOGGER.info(
                "Create Expansion recipe removal: removed {} of {} configured recipes from {} loaded recipes",
                removedRecipes.size(),
                recipesToRemove.size(),
                loadedRecipes.size()
        );

        if (!removedRecipes.isEmpty()) {
            CreateExpansion.LOGGER.info(
                    "Create Expansion removed recipe ids: {}",
                    removedRecipes.stream()
                            .map(ResourceLocation::toString)
                            .sorted()
                            .collect(Collectors.joining(", "))
            );
        }

        Set<ResourceLocation> missingRecipes = recipesToRemove.stream()
                .filter(id -> !loadedRecipeIds.contains(id))
                .collect(Collectors.toCollection(HashSet::new));

        if (!missingRecipes.isEmpty()) {
            CreateExpansion.LOGGER.warn(
                    "Create Expansion missing configured recipe ids: {}",
                    missingRecipes.stream()
                            .map(ResourceLocation::toString)
                            .sorted()
                            .collect(Collectors.joining(", "))
            );
        }
    }
}