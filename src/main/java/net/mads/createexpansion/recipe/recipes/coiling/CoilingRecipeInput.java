package net.mads.createexpansion.recipe.recipes.coiling;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CoilingRecipeInput(ItemStack item, int rpm) implements RecipeInput {
    public CoilingRecipeInput { item = item.copy(); }

    @Override
    public ItemStack getItem(int index) {
        if (index != 0) throw new IndexOutOfBoundsException(index);
        return item;
    }

    @Override public int size() { return 1; }
    @Override public boolean isEmpty() { return item.isEmpty(); }
}
