package net.mads.createexpansion.recipe.recipes.wiredrawer;

import net.mads.createexpansion.CreateExpansion;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public final class WireDrawingRecipeBuilder {
    private final String id;
    private Ingredient ingredient;
    private Ingredient extraIngredient;
    private ItemStack result = ItemStack.EMPTY;
    private int duration = 100;
    private int minRpm = 0;
    private Optional<Integer> maxRpm = Optional.empty();

    public WireDrawingRecipeBuilder(String id) {
        this.id = id;
    }

    public WireDrawingRecipeBuilder inputItem(String itemId) {
        ingredient = Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public WireDrawingRecipeBuilder extraInputItem(String itemId) {
        extraIngredient = Ingredient.of(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public WireDrawingRecipeBuilder outputItem(String itemId) {
        result = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)));
        return this;
    }

    public WireDrawingRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public WireDrawingRecipeBuilder minRpm(int minRpm) {
        this.minRpm = minRpm;
        return this;
    }

    public WireDrawingRecipeBuilder maxRpm(int maxRpm) {
        this.maxRpm = Optional.of(maxRpm);
        return this;
    }

    public void save(RecipeOutput output) {
        if (id.isBlank() || ingredient == null || result.isEmpty() || duration <= 0 || minRpm < 0
                || maxRpm.isPresent() && maxRpm.get() < minRpm) {
            throw new IllegalStateException("Invalid wire drawing recipe: " + id);
        }
        List<Ingredient> ingredients = extraIngredient == null ? List.of(ingredient) : List.of(ingredient, extraIngredient);
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "wire_drawing/" + id),
                new WireDrawingRecipe(ingredients, List.of(new ProcessingOutput(result, 1.0F)),
                        duration, minRpm, maxRpm), null);
    }
}
