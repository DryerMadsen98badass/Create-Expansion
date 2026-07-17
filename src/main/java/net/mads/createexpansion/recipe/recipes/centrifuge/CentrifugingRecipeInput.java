package net.mads.createexpansion.recipe.recipes.centrifuge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record CentrifugingRecipeInput(ItemStack item, FluidStack fluid, int rpm) implements RecipeInput {
    public CentrifugingRecipeInput {
        item = item.copy();
        fluid = fluid.copy();
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
