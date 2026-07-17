package net.mads.createexpansion.recipe.recipes.foundry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class FoundryMeltingRecipe implements Recipe<FoundryMeltingRecipeInput> {
    public static final int TICKS_PER_NUGGET = 80;

    public static final MapCodec<FoundryMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FoundryMeltingRecipe::ingredient),
            FluidStack.CODEC.fieldOf("result").forGetter(FoundryMeltingRecipe::result),
            ExtraCodecs.POSITIVE_INT.fieldOf("temperature").forGetter(FoundryMeltingRecipe::temperature),
            ExtraCodecs.POSITIVE_INT.fieldOf("nuggets").forGetter(FoundryMeltingRecipe::nuggets)
    ).apply(instance, FoundryMeltingRecipe::new));

    private final Ingredient ingredient;
    private final FluidStack result;
    private final int temperature;
    private final int nuggets;

    public FoundryMeltingRecipe(Ingredient ingredient, FluidStack result, int temperature, int nuggets) {
        this.ingredient = ingredient;
        this.result = result.copy();
        this.temperature = temperature;
        this.nuggets = nuggets;
    }

    @Override
    public boolean matches(FoundryMeltingRecipeInput input, Level level) {
        return !input.isEmpty() && input.temperature() >= temperature && ingredient.test(input.item());
    }

    public boolean matchesItem(ItemStack stack) {
        return !stack.isEmpty() && ingredient.test(stack);
    }

    public int durationAt(int foundryTemperature) {
        if (foundryTemperature < temperature) {
            return Integer.MAX_VALUE;
        }
        return Math.max(1, Math.round((float) baseDuration() * temperature / foundryTemperature));
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public FluidStack result() {
        return result.copy();
    }

    public int temperature() {
        return temperature;
    }

    public int nuggets() {
        return nuggets;
    }

    public int baseDuration() {
        return nuggets * TICKS_PER_NUGGET;
    }

    @Override
    public ItemStack assemble(FoundryMeltingRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ingredient);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BlockRegistry.FOUNDRY_CONTROLLER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FOUNDRY_MELTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FoundryMeltingRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, FoundryMeltingRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<FoundryMeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FoundryMeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
