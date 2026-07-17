package net.mads.createexpansion.recipe.recipes.sifter;

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

public class SifterRecipeBuilder {
    private final String id;
    private final SiftingRecipeParams params = new SiftingRecipeParams();

    SifterRecipeBuilder(String id) {
        this.id = id;
        params.processingDuration(100);
    }

    public SifterRecipeBuilder inputItem(String itemId) {
        params.addIngredient(Ingredient.of(item(itemId)));
        return this;
    }

    public SifterRecipeBuilder inputItem(ItemLike item) {
        params.addIngredient(Ingredient.of(item));
        return this;
    }

    public SifterRecipeBuilder inputTag(String tagId) {
        params.addIngredient(Ingredient.of(itemTag(tagId)));
        return this;
    }

    public SifterRecipeBuilder outputItem(String itemId) {
        return outputItem(itemId, 1);
    }

    public SifterRecipeBuilder outputItem(String itemId, int count) {
        params.addResult(new ProcessingOutput(id(itemId), count, 1.0F));
        return this;
    }

    public SifterRecipeBuilder outputItem(ItemLike item, int count) {
        params.addResult(new ProcessingOutput(new ItemStack(item, count), 1.0F));
        return this;
    }

    public SifterRecipeBuilder chancedOutput(String itemId, float chance) {
        return chancedOutput(itemId, 1, chance);
    }

    public SifterRecipeBuilder chancedOutput(String itemId, int count, float chance) {
        params.addResult(new ProcessingOutput(id(itemId), count, chance));
        return this;
    }

    public SifterRecipeBuilder duration(int duration) {
        params.processingDuration(duration);
        return this;
    }

    public SifterRecipeBuilder minRpm(int rpm) {
        params.minRpm(rpm);
        return this;
    }

    public SifterRecipeBuilder maxRpm(int rpm) {
        params.maxRpm(rpm);
        return this;
    }

    public SiftingRecipe build() {
        validate();
        return new SiftingRecipe(params);
    }

    public void save(RecipeOutput output) {
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "sifting/" + id), build(), null);
    }

    private void validate() {
        if (id.isBlank()) {
            throw new IllegalStateException("Sifting recipe id cannot be blank");
        }
        if (params.minRpm() < 0) {
            throw new IllegalStateException("Sifting recipe " + id + " has negative minimum RPM");
        }
        if (params.maxRpm().isPresent() && params.maxRpm().get() < params.minRpm()) {
            throw new IllegalStateException("Sifting recipe " + id + " has max RPM lower than min RPM");
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
