package net.mads.createexpansion.recipe.recipes.rolling;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record RollingRecipeInput(ItemStack item, int rpm) implements RecipeInput {
    public RollingRecipeInput { item = item.copy(); }

    @Override
    public ItemStack getItem(int index) {
        if (index != 0) throw new IndexOutOfBoundsException(index);
        return item;
    }

    @Override public int size() { return 1; }
    @Override public boolean isEmpty() { return item.isEmpty(); }
}
