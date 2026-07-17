package net.mads.createexpansion.recipe.recipes.rolling;

import net.mads.createexpansion.CreateExpansion;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public final class RollingRecipeBuilder {
    private final String id;
    private Ingredient ingredient;
    private ItemStack result = ItemStack.EMPTY;
    private int duration = 100;
    private int minRpm = 0;

    RollingRecipeBuilder(String id) {
        this.id = id;
    }

    public RollingRecipeBuilder inputItem(String itemId) {
        ingredient = Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public RollingRecipeBuilder outputItem(String itemId) {
        result = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public RollingRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public RollingRecipeBuilder minRpm(int minRpm) {
        this.minRpm = minRpm;
        return this;
    }

    public void save(RecipeOutput output) {
        if (id.isBlank() || ingredient == null || result.isEmpty() || duration <= 0 || minRpm < 0) {
            throw new IllegalStateException("Invalid rolling recipe: " + id);
        }
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "rolling/" + id),
                new RollingRecipe(List.of(ingredient), List.of(new ProcessingOutput(result, 1.0F)),
                        duration, minRpm, Optional.empty()), null);
    }
}
