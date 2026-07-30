package net.mads.createexpansion.mixin;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.remove.RemovedCreateRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    public abstract Iterable<RecipeHolder<?>> getRecipes();

    @Shadow
    public abstract void replaceRecipes(Iterable<RecipeHolder<?>> recipes);

    @Inject(method = "apply", at = @At("TAIL"))
    private void createExpansion$removeProgressionRecipes(
            Map<ResourceLocation, ?> recipes,
            ResourceManager resourceManager,
            ProfilerFiller profiler,
            CallbackInfo callbackInfo
    ) {
        try {
            Set<ResourceLocation> removedIds = new HashSet<>(RemovedCreateRecipes.ALL);
            List<RecipeHolder<?>> loadedRecipes = StreamSupport.stream(getRecipes().spliterator(), false).toList();
            Set<ResourceLocation> loadedIds = loadedRecipes.stream()
                    .map(RecipeHolder::id)
                    .collect(Collectors.toCollection(HashSet::new));
            Set<ResourceLocation> foundIds = removedIds.stream()
                    .filter(loadedIds::contains)
                    .collect(Collectors.toCollection(HashSet::new));
            Set<ResourceLocation> missingIds = removedIds.stream()
                    .filter(id -> !loadedIds.contains(id))
                    .collect(Collectors.toCollection(HashSet::new));
            List<RecipeHolder<?>> keptRecipes = loadedRecipes.stream()
                    .filter(holder -> !removedIds.contains(holder.id()))
                    .toList();
            int removed = loadedRecipes.size() - keptRecipes.size();
            replaceRecipes(keptRecipes);

            CreateExpansion.LOGGER.info(
                    "Create Expansion recipe removal: removed {} of {} configured recipes from {} loaded recipes",
                    removed,
                    removedIds.size(),
                    loadedIds.size()
            );
            CreateExpansion.LOGGER.info("Create Expansion removed recipe ids: {}", foundIds.stream()
                    .map(ResourceLocation::toString)
                    .sorted()
                    .collect(Collectors.joining(", ")));
            CreateExpansion.LOGGER.info("Create Expansion missing configured recipe ids: {}", missingIds.stream()
                    .map(ResourceLocation::toString)
                    .sorted()
                    .collect(Collectors.joining(", ")));
            CreateExpansion.LOGGER.info("Create Expansion matching loaded recipe ids for debugging: {}", loadedIds.stream()
                    .map(ResourceLocation::toString)
                    .filter(RecipeManagerMixin::createExpansion$isInterestingRecipeId)
                    .sorted()
                    .limit(300)
                    .collect(Collectors.joining(", ")));
        } catch (RuntimeException exception) {
            CreateExpansion.LOGGER.error("Create Expansion recipe removal failed; leaving recipes unchanged", exception);
        }
    }

    private static boolean createExpansion$isInterestingRecipeId(String id) {
        return id.contains("iron_ingot")
                || id.contains("copper_ingot")
                || id.contains("gold_ingot")
                || id.contains("zinc")
                || id.contains("brass")
                || id.contains("andesite_alloy")
                || id.contains("cogwheel")
                || id.contains("mechanical_")
                || id.contains("casing")
                || id.contains("crushed")
                || id.contains("raw_")
                || id.contains("tuff");
    }
}
