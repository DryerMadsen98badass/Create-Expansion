package net.mads.createexpansion.recipe.recipes.assembly;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public final class AssemblyRecipeBuilder {
    private final String id;
    private Ingredient base;
    private final List<SizedIngredient> inputs = new ArrayList<>();
    private ItemStack result = ItemStack.EMPTY;
    private int loops = 1;

    public AssemblyRecipeBuilder(String id) {
        this.id = id;
    }

    public AssemblyRecipeBuilder baseBlock(String itemId) {
        Item item = item(itemId);
        if (!(item instanceof BlockItem)) {
            throw new IllegalArgumentException("Assembly base must be a block item: " + itemId);
        }
        base = Ingredient.of(item);
        return this;
    }

    public AssemblyRecipeBuilder inputItem(String itemId) {
        return inputItem(itemId, 1);
    }

    public AssemblyRecipeBuilder inputItem(String itemId, int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Assembly input count must be at least 1 for " + itemId);
        }
        inputs.add(SizedIngredient.of(item(itemId), count));
        return this;
    }

    public AssemblyRecipeBuilder outputBlock(String itemId) {
        return outputBlock(itemId, 1);
    }

    public AssemblyRecipeBuilder outputBlock(String itemId, int count) {
        result = new ItemStack(item(itemId), count);
        return this;
    }

    public AssemblyRecipeBuilder loops(int loops) {
        if (loops < 1) {
            throw new IllegalArgumentException("Assembly loops must be at least 1");
        }
        this.loops = loops;
        return this;
    }

    public void save(RecipeOutput output) {
        if (id.isBlank() || base == null || inputs.isEmpty() || result.isEmpty()) {
            throw new IllegalStateException("Invalid assembly recipe: " + id);
        }
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "assembly/" + id),
                new AssemblyRecipe(base, inputs, new ProcessingOutput(result, 1.0F), loops), null);
    }

    private static Item item(String itemId) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
    }
}
