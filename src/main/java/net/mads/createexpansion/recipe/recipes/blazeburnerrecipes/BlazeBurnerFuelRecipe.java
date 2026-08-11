package net.mads.createexpansion.recipe.recipes.blazeburnerrecipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class BlazeBurnerFuelRecipe implements Recipe<BlazeBurnerFuelRecipeInput> {
    public static final MapCodec<BlazeBurnerFuelRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.optionalFieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
            FluidStack.CODEC.optionalFieldOf("fluid_ingredient").forGetter(recipe -> recipe.fluidIngredient),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("heated", 0).forGetter(BlazeBurnerFuelRecipe::heated),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("superheated", 0).forGetter(BlazeBurnerFuelRecipe::superheated)
    ).apply(instance, BlazeBurnerFuelRecipe::new));

    private final Optional<Ingredient> ingredient;
    private final Optional<FluidStack> fluidIngredient;
    private final int heated;
    private final int superheated;

    public BlazeBurnerFuelRecipe(
            Optional<Ingredient> ingredient,
            Optional<FluidStack> fluidIngredient,
            int heated,
            int superheated
    ) {
        this.ingredient = ingredient;
        this.fluidIngredient = fluidIngredient.map(FluidStack::copy);
        this.heated = heated;
        this.superheated = superheated;
        validate();
    }

    public static Builder recipe() {
        return new Builder();
    }

    @Override
    public boolean matches(BlazeBurnerFuelRecipeInput input, Level level) {
        if (ingredient.isPresent()) {
            return !input.item().isEmpty() && ingredient.get().test(input.item());
        }
        return fluidIngredient.isPresent()
                && !input.fluid().isEmpty()
                && FluidStack.isSameFluidSameComponents(input.fluid(), fluidIngredient.get())
                && input.fluid().getAmount() >= fluidIngredient.get().getAmount();
    }

    public boolean matchesItem(ItemStack stack) {
        return ingredient.isPresent() && !stack.isEmpty() && ingredient.get().test(stack);
    }

    public boolean matchesFluid(FluidStack stack) {
        return fluidIngredient.isPresent()
                && !stack.isEmpty()
                && FluidStack.isSameFluidSameComponents(stack, fluidIngredient.get())
                && stack.getAmount() >= fluidIngredient.get().getAmount();
    }

    public Optional<Ingredient> ingredient() {
        return ingredient;
    }

    public Optional<FluidStack> fluidIngredient() {
        return fluidIngredient.map(FluidStack::copy);
    }

    public int heated() {
        return heated;
    }

    public int superheated() {
        return superheated;
    }

    public int fluidAmount() {
        return fluidIngredient.map(FluidStack::getAmount).orElse(0);
    }

    @Override
    public ItemStack assemble(BlazeBurnerFuelRecipeInput input, HolderLookup.Provider registries) {
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
        ingredient.ifPresent(ingredients::add);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return com.simibubi.create.AllBlocks.BLAZE_BURNER.asStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.BLAZE_BURNER_FUEL_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.BLAZE_BURNER_FUEL_RECIPE_TYPE.get();
    }

    private void validate() {
        if (ingredient.isPresent() == fluidIngredient.isPresent()) {
            throw new IllegalStateException("Blaze Burner fuel recipes need exactly one item or fluid input");
        }
        if (heated <= 0 && superheated <= 0) {
            throw new IllegalStateException("Blaze Burner fuel recipes need heated or superheated time");
        }
        if (fluidIngredient.isPresent() && fluidIngredient.get().getAmount() <= 0) {
            throw new IllegalStateException("Blaze Burner fluid input amount must be positive");
        }
    }

    public static class Serializer implements RecipeSerializer<BlazeBurnerFuelRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, BlazeBurnerFuelRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<BlazeBurnerFuelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlazeBurnerFuelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static final class Builder {
        private String id;
        private Optional<Ingredient> ingredient = Optional.empty();
        private Optional<FluidStack> fluidIngredient = Optional.empty();
        private int heated;
        private int superheated;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder inputItem(String itemId) {
            return inputItem(item(itemId));
        }

        public Builder inputItem(ItemLike item) {
            this.ingredient = Optional.of(Ingredient.of(item));
            return this;
        }

        public Builder inputFluid(String fluidId, int amount) {
            this.fluidIngredient = Optional.of(new FluidStack(fluid(fluidId), amount));
            return this;
        }

        public Builder heated(int ticks) {
            this.heated = ticks;
            return this;
        }

        public Builder superheated(int ticks) {
            this.superheated = ticks;
            return this;
        }

        public BlazeBurnerFuelRecipe build() {
            return new BlazeBurnerFuelRecipe(ingredient, fluidIngredient, heated, superheated);
        }

        public void save(RecipeOutput output) {
            if (id == null || id.isBlank() || !ResourceLocation.isValidPath(id)) {
                throw new IllegalStateException("Blaze Burner fuel recipe has invalid id: " + id);
            }
            output.accept(
                    ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "blaze_burner_fuel/" + id),
                    build(),
                    null
            );
        }

        private static ResourceLocation resourceId(String id) {
            return id.contains(":")
                    ? ResourceLocation.parse(id)
                    : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
        }

        private static Item item(String itemId) {
            return BuiltInRegistries.ITEM.get(resourceId(itemId));
        }

        private static Fluid fluid(String fluidId) {
            return BuiltInRegistries.FLUID.get(resourceId(fluidId));
        }
    }
}
