package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlockEntity;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipe;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipeInput;
import net.mads.createexpansion.recipe.recipetypes.WireDrawingRecipeType;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.mads.createexpansion.recipe.SingleItemKineticRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import net.minecraft.world.item.crafting.RecipeHolder;

public class KineticWireDrawerBlockEntity extends AbstractSimpleKineticMachineBlockEntity {
    public KineticWireDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public KineticWireDrawerBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistry.KINETIC_WIRE_DRAWER.get(), pos, state);
    }

    @Override
    protected Optional<WireDrawingRecipe> findRecipe(ItemStack stack, int rpm) {
        if (level == null) return Optional.empty();
        return WireDrawingRecipeType.INSTANCE.find(new WireDrawingRecipeInput(stack, rpm), level)
                .map(holder -> holder.value());
    }

    @Override
    protected boolean hasRecipeFor(ItemStack stack) {
        return level != null && level.getRecipeManager().getAllRecipesFor(RecipeRegistry.WIRE_DRAWING_RECIPE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.matchesItem(stack));
    }

    @Override
    protected int inputSlotCount() {
        return 2;
    }

    @Override
    protected boolean canAcceptItemInSlot(int slot, ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }

        return switch (slot) {
            case 0 -> hasRecipeFor(stack);
            case 1 -> level.getRecipeManager().getAllRecipesFor(RecipeRegistry.WIRE_DRAWING_RECIPE_TYPE.get()).stream()
                    .map(RecipeHolder::value)
                    .anyMatch(recipe -> recipe.hasExtraInput() && recipe.matchesExtraInput(stack));
            default -> false;
        };
    }

    @Override
    protected boolean canStartRecipe(SingleItemKineticRecipe recipe) {
        if (recipe instanceof WireDrawingRecipe wireDrawingRecipe && wireDrawingRecipe.hasExtraInput()) {
            return wireDrawingRecipe.matchesExtraInput(inputInv.getStackInSlot(1));
        }
        return true;
    }

    @Override
    protected void consumeRecipeInputs(SingleItemKineticRecipe recipe) {
        inputInv.extractItem(0, 1, false);
        if (recipe instanceof WireDrawingRecipe wireDrawingRecipe && wireDrawingRecipe.hasExtraInput()) {
            inputInv.extractItem(1, 1, false);
        }
    }
}
