package net.mads.createexpansion.recipe.recipes.assembly;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AssemblyRecipeInput(ItemStack base) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(index);
        }
        return base;
    }

    @Override
    public int size() {
        return 1;
    }
}
