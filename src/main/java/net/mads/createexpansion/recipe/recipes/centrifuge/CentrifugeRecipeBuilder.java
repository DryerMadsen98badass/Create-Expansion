package net.mads.createexpansion.recipe.recipes.centrifuge;

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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CentrifugeRecipeBuilder {
    private final String id;
    private final List<SizedIngredient> itemIngredients = new ArrayList<>();
    private final List<SizedFluidIngredient> fluidIngredients = new ArrayList<>();
    private final List<ProcessingOutput> itemResults = new ArrayList<>();
    private final List<FluidStack> fluidResults = new ArrayList<>();
    private int duration = 100;
    private int minRpm = CentrifugingRecipe.DEFAULT_MIN_RPM;
    private Optional<Integer> maxRpm = Optional.empty();

    CentrifugeRecipeBuilder(String id) {
        this.id = id;
    }

    public CentrifugeRecipeBuilder inputItem(String itemId) {
        return inputItem(itemId, 1);
    }

    public CentrifugeRecipeBuilder inputItem(String itemId, int count) {
        itemIngredients.add(SizedIngredient.of(item(itemId), count));
        return this;
    }

    public CentrifugeRecipeBuilder inputItem(ItemLike item) {
        return inputItem(item, 1);
    }

    public CentrifugeRecipeBuilder inputItem(ItemLike item, int count) {
        itemIngredients.add(SizedIngredient.of(item, count));
        return this;
    }

    public CentrifugeRecipeBuilder inputTag(String tagId) {
        return inputTag(tagId, 1);
    }

    public CentrifugeRecipeBuilder inputTag(String tagId, int count) {
        itemIngredients.add(SizedIngredient.of(itemTag(tagId), count));
        return this;
    }

    public CentrifugeRecipeBuilder inputFluid(String fluidId, int amount) {
        fluidIngredients.add(SizedFluidIngredient.of(fluid(fluidId), amount));
        return this;
    }

    public CentrifugeRecipeBuilder outputItem(String itemId) {
        return outputItem(itemId, 1);
    }

    public CentrifugeRecipeBuilder outputItem(String itemId, int count) {
        itemResults.add(new ProcessingOutput(id(itemId), count, 1.0F));
        return this;
    }

    public CentrifugeRecipeBuilder outputItem(ItemLike item, int count) {
        itemResults.add(new ProcessingOutput(new ItemStack(item, count), 1.0F));
        return this;
    }

    public CentrifugeRecipeBuilder chancedOutput(String itemId, float chance) {
        return chancedOutput(itemId, 1, chance);
    }

    public CentrifugeRecipeBuilder chancedOutput(String itemId, int count, float chance) {
        itemResults.add(new ProcessingOutput(id(itemId), count, chance));
        return this;
    }

    public CentrifugeRecipeBuilder outputFluid(String fluidId, int amount) {
        fluidResults.add(new FluidStack(fluid(fluidId), amount));
        return this;
    }

    public CentrifugeRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public CentrifugeRecipeBuilder minRpm(int rpm) {
        this.minRpm = rpm;
        return this;
    }

    public CentrifugeRecipeBuilder maxRpm(int rpm) {
        this.maxRpm = Optional.of(rpm);
        return this;
    }

    public CentrifugingRecipe build() {
        validate();
        return new CentrifugingRecipe(itemIngredients, fluidIngredients, itemResults, fluidResults, duration, minRpm, maxRpm);
    }

    public void save(RecipeOutput output) {
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "centrifuging/" + id), build(), null);
    }

    private void validate() {
        if (id.isBlank()) {
            throw new IllegalStateException("Centrifuging recipe id cannot be blank");
        }
        if (itemIngredients.size() > 1) {
            throw new IllegalStateException("Centrifuging recipe " + id + " has more than one item input");
        }
        if (fluidIngredients.size() > 1) {
            throw new IllegalStateException("Centrifuging recipe " + id + " has more than one fluid input");
        }
        if (itemResults.size() > 4) {
            throw new IllegalStateException("Centrifuging recipe " + id + " has more than four item outputs");
        }
        if (fluidResults.size() > 2) {
            throw new IllegalStateException("Centrifuging recipe " + id + " has more than two fluid outputs");
        }
        if (itemIngredients.isEmpty() && fluidIngredients.isEmpty()) {
            throw new IllegalStateException("Centrifuging recipe " + id + " needs an item or fluid input");
        }
        if (itemResults.isEmpty() && fluidResults.isEmpty()) {
            throw new IllegalStateException("Centrifuging recipe " + id + " needs an item or fluid output");
        }
        if (duration <= 0) {
            throw new IllegalStateException("Centrifuging recipe " + id + " must have a positive duration");
        }
        if (minRpm < 0) {
            throw new IllegalStateException("Centrifuging recipe " + id + " has negative minimum RPM");
        }
        if (maxRpm.isPresent() && maxRpm.get() < minRpm) {
            throw new IllegalStateException("Centrifuging recipe " + id + " has max RPM lower than min RPM");
        }
    }

    private static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    private static Item item(String itemId) {
        return BuiltInRegistries.ITEM.get(id(itemId));
    }

    private static Fluid fluid(String fluidId) {
        return BuiltInRegistries.FLUID.get(id(fluidId));
    }

    private static TagKey<Item> itemTag(String tagId) {
        return ItemTags.create(id(tagId));
    }
}
