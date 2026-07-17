package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlockEntity;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipe;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipeInput;
import net.mads.createexpansion.recipe.recipetypes.WireDrawingRecipeType;
import net.mads.createexpansion.registry.RecipeRegistry;
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
}
