package net.mads.createexpansion.machine.machines.kinetic.coiling;

import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlockEntity;
import net.mads.createexpansion.recipe.recipetypes.CoilingRecipeType;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipe;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipeInput;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class KineticCoilingMachineBlockEntity extends AbstractSimpleKineticMachineBlockEntity {
    public KineticCoilingMachineBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistry.SPRING_COILING_MACHINE.get(), pos, state);
    }

    public KineticCoilingMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected Optional<CoilingRecipe> findRecipe(ItemStack stack, int rpm) {
        if (level == null) return Optional.empty();
        return CoilingRecipeType.INSTANCE.find(new CoilingRecipeInput(stack, rpm), level).map(RecipeHolder::value);
    }

    @Override
    protected boolean hasRecipeFor(ItemStack stack) {
        if (level == null) return false;
        return level.getRecipeManager().getAllRecipesFor(RecipeRegistry.COILING_RECIPE_TYPE.get()).stream()
                .map(RecipeHolder::value).anyMatch(recipe -> recipe.matchesItem(stack));
    }
}
