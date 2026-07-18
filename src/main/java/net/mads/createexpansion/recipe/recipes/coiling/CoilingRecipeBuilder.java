package net.mads.createexpansion.recipe.recipes.coiling;

import net.mads.createexpansion.CreateExpansion;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public final class CoilingRecipeBuilder {
    private final String id;
    private Ingredient ingredient;
    private ItemStack result = ItemStack.EMPTY;
    private int duration = 100;
    private int minRpm = 0;
    private Optional<Integer> maxRpm = Optional.empty();

    public CoilingRecipeBuilder(String id) {
        this.id = id;
    }

    public CoilingRecipeBuilder inputItem(String itemId) {
        ingredient = Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public CoilingRecipeBuilder outputItem(String itemId) {
        result = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public CoilingRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public CoilingRecipeBuilder minRpm(int minRpm) {
        this.minRpm = minRpm;
        return this;
    }

    public CoilingRecipeBuilder maxRpm(int maxRpm) {
        this.maxRpm = Optional.of(maxRpm);
        return this;
    }

    public void save(RecipeOutput output) {
        if (id.isBlank() || ingredient == null || result.isEmpty() || duration <= 0 || minRpm < 0
                || maxRpm.isPresent() && maxRpm.get() < minRpm) {
            throw new IllegalStateException("Invalid coiling recipe: " + id);
        }
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "coiling/" + id),
                new CoilingRecipe(List.of(ingredient), List.of(new ProcessingOutput(result, 1.0F)),
                        duration, minRpm, maxRpm), null);
    }
}
