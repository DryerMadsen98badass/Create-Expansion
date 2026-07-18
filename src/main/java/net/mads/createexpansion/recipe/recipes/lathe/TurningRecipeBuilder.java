package net.mads.createexpansion.recipe.recipes.lathe;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TurningRecipeBuilder {
    private final String id;
    private final List<Ingredient> itemIngredients = new ArrayList<>();
    private final List<ProcessingOutput> itemResults = new ArrayList<>();
    private int duration = 100;
    private int minRpm = TurningRecipe.DEFAULT_MIN_RPM;
    private Optional<Integer> maxRpm = Optional.empty();

    public TurningRecipeBuilder(String id) {
        this.id = id;
    }

    public TurningRecipeBuilder inputItem(String itemId) {
        itemIngredients.add(Ingredient.of(item(itemId)));
        return this;
    }

    public TurningRecipeBuilder inputItem(ItemLike item) {
        itemIngredients.add(Ingredient.of(item));
        return this;
    }

    public TurningRecipeBuilder inputTag(String tagId) {
        itemIngredients.add(Ingredient.of(itemTag(tagId)));
        return this;
    }

    public TurningRecipeBuilder outputItem(String itemId) {
        return outputItem(itemId, 1);
    }

    public TurningRecipeBuilder outputItem(String itemId, int count) {
        itemResults.add(new ProcessingOutput(id(itemId), count, 1.0F));
        return this;
    }

    public TurningRecipeBuilder outputItem(ItemLike item, int count) {
        itemResults.add(new ProcessingOutput(new ItemStack(item, count), 1.0F));
        return this;
    }

    public TurningRecipeBuilder chancedOutput(String itemId, float chance) {
        return chancedOutput(itemId, 1, chance);
    }

    public TurningRecipeBuilder chancedOutput(String itemId, int count, float chance) {
        itemResults.add(new ProcessingOutput(id(itemId), count, chance));
        return this;
    }

    public TurningRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public TurningRecipeBuilder minRpm(int rpm) {
        this.minRpm = rpm;
        return this;
    }

    public TurningRecipeBuilder maxRpm(int rpm) {
        this.maxRpm = Optional.of(rpm);
        return this;
    }

    public TurningRecipe build() {
        validate();
        return new TurningRecipe(itemIngredients, itemResults, duration, minRpm, maxRpm);
    }

    public void save(RecipeOutput output) {
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "turning/" + id), build(), null);
    }

    private void validate() {
        if (id.isBlank()) {
            throw new IllegalStateException("Turning recipe id cannot be blank");
        }
        if (itemIngredients.size() != 1) {
            throw new IllegalStateException("Turning recipe " + id + " needs exactly one item input");
        }
        if (itemResults.size() != 1) {
            throw new IllegalStateException("Turning recipe " + id + " needs exactly one item output");
        }
        if (duration <= 0) {
            throw new IllegalStateException("Turning recipe " + id + " must have a positive duration");
        }
        if (minRpm < 0) {
            throw new IllegalStateException("Turning recipe " + id + " has negative minimum RPM");
        }
        if (maxRpm.isPresent() && maxRpm.get() < minRpm) {
            throw new IllegalStateException("Turning recipe " + id + " has max RPM lower than min RPM");
        }
    }

    private static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    private static Item item(String itemId) {
        return BuiltInRegistries.ITEM.get(id(itemId));
    }

    private static TagKey<Item> itemTag(String tagId) {
        return ItemTags.create(id(tagId));
    }
}
