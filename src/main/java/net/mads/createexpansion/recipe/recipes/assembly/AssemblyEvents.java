package net.mads.createexpansion.recipe.recipes.assembly;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.recipetypes.AssemblyRecipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID)
public final class AssemblyEvents {
    private AssemblyEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event
    ) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        BlockPos pos = event.getPos();

        if (!matchesAssembly(level, pos, held)) {
            return;
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);

        if (!level.isClientSide()) {
            handleServerAssembly(
                    level,
                    pos,
                    player,
                    held
            );
        }
    }

    private static boolean matchesAssembly(
            Level level,
            BlockPos pos,
            ItemStack held
    ) {
        AssemblyWorldProgress.Entry progress =
                AssemblyWorldProgress.get(level, pos);

        if (progress != null) {
            if (held.isEmpty()) {
                return progress.canFinalizeCompletedRecipe();
            }

            return resolveRecipes(
                    level,
                    progress.candidateRecipeIds()
            )
                    .stream()
                    .anyMatch(holder ->
                            holder.value().matchesAction(
                                    progress.action(),
                                    held
                            )
                    );
        }

        if (held.isEmpty()) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        ItemStack base = new ItemStack(
                state.getBlock().asItem()
        );

        if (base.isEmpty()) {
            return false;
        }

        return !AssemblyRecipeType.INSTANCE
                .findAllForStart(
                        new AssemblyRecipeInput(base),
                        held,
                        level
                )
                .isEmpty();
    }

    private static void handleServerAssembly(
            Level level,
            BlockPos pos,
            Player player,
            ItemStack held
    ) {
        AssemblyWorldProgress.Entry progress =
                AssemblyWorldProgress.get(level, pos);

        if (progress != null) {
            if (held.isEmpty()) {
                finalizeCompletedFallback(
                        level,
                        pos,
                        progress
                );
                return;
            }

            List<RecipeHolder<AssemblyRecipe>> matching =
                    resolveRecipes(
                            level,
                            progress.candidateRecipeIds()
                    )
                            .stream()
                            .filter(holder ->
                                    holder.value().matchesAction(
                                            progress.action(),
                                            held
                                    )
                            )
                            .toList();

            if (matching.isEmpty()) {
                return;
            }

            consumeAction(
                    level,
                    pos,
                    player,
                    held,
                    matching,
                    progress.action()
            );
            return;
        }

        if (held.isEmpty()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        ItemStack base = new ItemStack(
                state.getBlock().asItem()
        );

        if (base.isEmpty()) {
            return;
        }

        List<RecipeHolder<AssemblyRecipe>> candidates =
                AssemblyRecipeType.INSTANCE.findAllForStart(
                        new AssemblyRecipeInput(base),
                        held,
                        level
                );

        if (candidates.isEmpty()) {
            return;
        }

        consumeAction(
                level,
                pos,
                player,
                held,
                candidates,
                0
        );
    }

    private static void consumeAction(
            Level level,
            BlockPos pos,
            Player player,
            ItemStack held,
            List<RecipeHolder<AssemblyRecipe>> candidates,
            int action
    ) {
        SizedIngredient input = candidates
                .getFirst()
                .value()
                .inputForAction(action);

        if (input == null) {
            AssemblyWorldProgress.remove(level, pos);
            return;
        }

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        spawnStepParticles(level, pos);

        List<RecipeHolder<AssemblyRecipe>> completed =
                new ArrayList<>();
        List<RecipeHolder<AssemblyRecipe>> continuing =
                new ArrayList<>();

        for (RecipeHolder<AssemblyRecipe> candidate : candidates) {
            if (candidate.value().completeAfterAction(action)) {
                completed.add(candidate);
            } else {
                continuing.add(candidate);
            }
        }

        if (continuing.isEmpty()) {
            if (completed.isEmpty()) {
                AssemblyWorldProgress.remove(level, pos);
                return;
            }

            finishAssembly(
                    level,
                    pos,
                    completed.getFirst().value().result()
            );
            AssemblyWorldProgress.remove(level, pos);
            spawnCompleteParticles(level, pos);
            return;
        }

        AssemblyWorldProgress.set(
                level,
                pos,
                continuing.stream()
                        .map(RecipeHolder::id)
                        .toList(),
                action + 1,
                completed.stream()
                        .map(RecipeHolder::id)
                        .toList()
        );
    }

    /**
     * Hvis en kort oppskrift er ferdig samtidig som en lengre oppskrift har
     * samme prefiks, kan spilleren høyreklikke med tom hånd for å velge den
     * ferdige, korte oppskriften. Bruker spilleren neste item i stedet,
     * fortsetter bare oppskriftene som matcher det itemet.
     */
    private static void finalizeCompletedFallback(
            Level level,
            BlockPos pos,
            AssemblyWorldProgress.Entry progress
    ) {
        List<RecipeHolder<AssemblyRecipe>> completed =
                resolveRecipes(
                        level,
                        progress.completedFallbackRecipeIds()
                );

        if (completed.isEmpty()) {
            return;
        }

        finishAssembly(
                level,
                pos,
                completed.getFirst().value().result()
        );
        AssemblyWorldProgress.remove(level, pos);
        spawnCompleteParticles(level, pos);
    }

    private static List<RecipeHolder<AssemblyRecipe>> resolveRecipes(
            Level level,
            List<ResourceLocation> recipeIds
    ) {
        List<RecipeHolder<AssemblyRecipe>> recipes =
                new ArrayList<>();

        for (ResourceLocation recipeId : recipeIds) {
            level.getRecipeManager()
                    .byKey(recipeId)
                    .filter(holder ->
                            holder.value() instanceof AssemblyRecipe
                    )
                    .ifPresent(holder ->
                            recipes.add(
                                    new RecipeHolder<>(
                                            holder.id(),
                                            (AssemblyRecipe) holder.value()
                                    )
                            )
                    );
        }

        return recipes;
    }

    private static void finishAssembly(
            Level level,
            BlockPos pos,
            ItemStack result
    ) {
        if (result.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            level.setBlock(
                    pos,
                    block.defaultBlockState(),
                    Block.UPDATE_ALL
            );
            return;
        }

        level.destroyBlock(pos, false);

        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                result.copy()
        );

        level.addFreshEntity(entity);
    }

    private static void spawnStepParticles(
            Level level,
            BlockPos pos
    ) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5,
                    pos.getY() + 1.05,
                    pos.getZ() + 0.5,
                    6,
                    0.25,
                    0.12,
                    0.25,
                    0.02
            );
        }
    }

    private static void spawnCompleteParticles(
            Level level,
            BlockPos pos
    ) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    pos.getX() + 0.5,
                    pos.getY() + 0.8,
                    pos.getZ() + 0.5,
                    18,
                    0.35,
                    0.25,
                    0.35,
                    0.03
            );
        }
    }
}
