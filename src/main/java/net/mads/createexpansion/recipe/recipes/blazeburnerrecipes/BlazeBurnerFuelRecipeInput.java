package net.mads.createexpansion.recipe.recipes.blazeburnerrecipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record BlazeBurnerFuelRecipeInput(ItemStack item, FluidStack fluid) implements RecipeInput {
    public static BlazeBurnerFuelRecipeInput item(ItemStack item) {
        return new BlazeBurnerFuelRecipeInput(item, FluidStack.EMPTY);
    }

    public static BlazeBurnerFuelRecipeInput fluid(FluidStack fluid) {
        return new BlazeBurnerFuelRecipeInput(ItemStack.EMPTY, fluid);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index);
        }
        return item;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty() && fluid.isEmpty();
    }
}
