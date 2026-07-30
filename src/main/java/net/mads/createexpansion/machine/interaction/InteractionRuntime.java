package net.mads.createexpansion.machine.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

/** Shared evaluator for planned machine and recipe interactions. */
public final class InteractionRuntime {
    private InteractionRuntime() {
    }

    public static boolean conditionsMatch(List<MachineCondition> conditions, InteractionContext context, InteractionPhase phase) {
        return conditions.stream()
                .filter(condition -> condition.check() == phase || condition.check() == InteractionPhase.WHILE_PROCESSING && phase == InteractionPhase.ON_START)
                .allMatch(condition -> condition.matches(context.level(), context.origin(), context.facing()));
    }

    public static ConditionFailure failedConditionBehavior(List<MachineCondition> conditions, InteractionContext context, InteractionPhase phase) {
        for (MachineCondition condition : conditions) {
            if (condition.check() == phase && !condition.matches(context.level(), context.origin(), context.facing())) {
                return condition.onFailure();
            }
        }
        return null;
    }

    public static boolean interactionsMatch(List<BlockInteraction> interactions, InteractionContext context, InteractionPhase phase) {
        return interactions.stream()
                .filter(interaction -> interaction.when() == phase)
                .allMatch(interaction -> matchingRequirement(interaction, context).isPresent());
    }

    public static void applyInteractions(List<BlockInteraction> interactions, InteractionContext context, InteractionPhase phase) {
        for (BlockInteraction interaction : interactions) {
            if (interaction.when() == phase) {
                applyInteraction(interaction, context);
            }
        }
    }

    public static Optional<MachineModifier> firstMatchingModifier(List<MachineModifier> modifiers, InteractionContext context) {
        return modifiers.stream()
                .filter(modifier -> modifierMatches(modifier, context))
                .findFirst();
    }

    public static int adjustedDuration(int duration, Optional<MachineModifier> machineModifier, Optional<MachineModifier> recipeModifier) {
        float speedBonus = machineModifier.map(MachineModifier::speedBonus).orElse(0.0F)
                + recipeModifier.map(MachineModifier::speedBonus).orElse(0.0F);
        return Math.max(1, Math.round(duration / Math.max(0.01F, 1.0F + speedBonus)));
    }

    public static int adjustedResource(int resourcePerTick, boolean steam, Optional<MachineModifier> machineModifier, Optional<MachineModifier> recipeModifier) {
        if (resourcePerTick <= 0) {
            return resourcePerTick;
        }
        float bonus = (steam ? machineModifier.map(MachineModifier::steamUsageBonus) : machineModifier.map(MachineModifier::energyUsageBonus)).orElse(0.0F)
                + (steam ? recipeModifier.map(MachineModifier::steamUsageBonus) : recipeModifier.map(MachineModifier::energyUsageBonus)).orElse(0.0F);
        return Math.max(0, Math.round(resourcePerTick * Math.max(0.0F, 1.0F + bonus)));
    }

    private static boolean modifierMatches(MachineModifier modifier, InteractionContext context) {
        for (ModifierRequirement requirement : modifier.requirements()) {
            boolean matches = switch (requirement.type()) {
                case ITEM -> requirement.matchesItems(context.itemInputs());
                case FLUID -> requirement.matchesFluids(context.fluidInputs());
                case BLOCK -> requirement.blockInteraction().flatMap(interaction -> matchingRequirement(interaction, context)).isPresent();
                case CONDITION -> requirement.condition().map(condition -> condition.matches(context.level(), context.origin(), context.facing())).orElse(false);
            };
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    private static Optional<BlockRequirement> matchingRequirement(BlockInteraction interaction, InteractionContext context) {
        Level level = context.level();
        BlockPos pos = interaction.pos().rotate(context.origin(), context.facing());
        return interaction.requirement().matchingLeaf(level, level.getBlockState(pos));
    }

    private static void applyInteraction(BlockInteraction interaction, InteractionContext context) {
        Level level = context.level();
        BlockPos pos = interaction.pos().rotate(context.origin(), context.facing());
        BlockState state = level.getBlockState(pos);
        Optional<BlockRequirement> requirement = interaction.requirement().matchingLeaf(level, state);
        if (requirement.isEmpty()) {
            return;
        }
        BlockRequirement matched = requirement.get();
        if (interaction.type() != BlockInteraction.Type.DAMAGE && level.random.nextFloat() >= matched.actionChance()) {
            return;
        }

        switch (interaction.type()) {
            case REQUIRE -> {
            }
            case CONSUME -> removeWithLoot(level, pos, state, matched);
            case CONVERT -> {
                removeDrops(level, pos, state, matched);
                level.setBlock(pos, interaction.targetBlock().defaultBlockState(), Block.UPDATE_ALL);
                context.wearStore().clear(pos);
            }
            case PLACE -> {
                level.setBlock(pos, interaction.targetBlock().defaultBlockState(), Block.UPDATE_ALL);
                context.wearStore().clear(pos);
            }
            case DAMAGE -> damage(level, pos, state, matched, context.wearStore());
        }
    }

    private static void damage(Level level, BlockPos pos, BlockState state, BlockRequirement requirement, InteractionWearStore wearStore) {
        if (requirement.damage() <= 0 || requirement.breakAfter() <= 0 || level.random.nextFloat() >= requirement.damageChance()) {
            return;
        }
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        int wear = wearStore.addWear(pos, blockId, requirement.damage());
        if (wear >= requirement.breakAfter()) {
            removeWithLoot(level, pos, state, requirement);
            wearStore.clear(pos);
        }
    }

    private static void removeWithLoot(Level level, BlockPos pos, BlockState state, BlockRequirement requirement) {
        removeDrops(level, pos, state, requirement);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void removeDrops(Level level, BlockPos pos, BlockState state, BlockRequirement requirement) {
        for (ItemStack stack : requirement.lootTable().roll(state, level.random)) {
            if (!stack.isEmpty()) {
                Block.popResource(level, pos, stack);
            }
        }
    }
}
