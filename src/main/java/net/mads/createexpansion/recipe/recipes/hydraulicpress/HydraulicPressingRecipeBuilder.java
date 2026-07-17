package net.mads.createexpansion.recipe.recipes.hydraulicpress;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public final class HydraulicPressingRecipeBuilder {
    private final String id;
    private Ingredient ingredient;
    private ItemStack result = ItemStack.EMPTY;
    private int blows = 1;

    HydraulicPressingRecipeBuilder(String id) { this.id = id; }

    public HydraulicPressingRecipeBuilder inputItem(String itemId) {
        ingredient = Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public HydraulicPressingRecipeBuilder outputItem(String itemId) {
        result = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public HydraulicPressingRecipeBuilder blows(int blows) {
        this.blows = blows;
        return this;
    }

    public void save(RecipeOutput output) {
        if (id.isBlank() || ingredient == null || result.isEmpty() || blows <= 0) {
            throw new IllegalStateException("Invalid hydraulic pressing recipe: " + id);
        }
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "hydraulic_pressing/" + id),
                new HydraulicPressingRecipe(List.of(ingredient),
                        List.of(new ProcessingOutput(result, 1.0F)), blows), null);
    }
}
