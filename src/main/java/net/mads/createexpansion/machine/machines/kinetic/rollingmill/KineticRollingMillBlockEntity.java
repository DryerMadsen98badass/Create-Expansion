package net.mads.createexpansion.machine.machines.kinetic.rollingmill;

import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlockEntity;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.recipe.recipes.rolling.RollingRecipe;
import net.mads.createexpansion.recipe.recipes.rolling.RollingRecipeInput;
import net.mads.createexpansion.recipe.recipetypes.RollingRecipeType;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import net.minecraft.world.item.crafting.RecipeHolder;

public class KineticRollingMillBlockEntity extends AbstractSimpleKineticMachineBlockEntity {
    public KineticRollingMillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public KineticRollingMillBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistry.KINETIC_ROLLING_MILL.get(), pos, state);
    }

    @Override
    protected Optional<RollingRecipe> findRecipe(ItemStack stack, int rpm) {
        if (level == null) return Optional.empty();
        return RollingRecipeType.INSTANCE.find(new RollingRecipeInput(stack, rpm), level)
                .map(holder -> holder.value());
    }

    @Override
    protected boolean hasRecipeFor(ItemStack stack) {
        return level != null && level.getRecipeManager().getAllRecipesFor(RecipeRegistry.ROLLING_RECIPE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.matchesItem(stack));
    }
}
