package net.mads.createexpansion.recipe.recipes.centrifuge;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.Optional;

public class CentrifugingRecipe implements Recipe<CentrifugingRecipeInput> {
    public static final int DEFAULT_MIN_RPM = 0;

    public static final MapCodec<CentrifugingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.FLAT_CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(CentrifugingRecipe::itemIngredients),
            SizedFluidIngredient.FLAT_CODEC.listOf().optionalFieldOf("fluid_ingredients", List.of()).forGetter(CentrifugingRecipe::fluidIngredients),
            ProcessingOutput.CODEC.listOf().optionalFieldOf("results", List.of()).forGetter(CentrifugingRecipe::itemResults),
            FluidStack.CODEC.listOf().optionalFieldOf("fluid_results", List.of()).forGetter(CentrifugingRecipe::fluidResults),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("processing_time", 100).forGetter(CentrifugingRecipe::processingDuration),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm", DEFAULT_MIN_RPM).forGetter(CentrifugingRecipe::minRpm),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_rpm").forGetter(CentrifugingRecipe::maxRpm)
    ).apply(instance, CentrifugingRecipe::new));

    private final List<SizedIngredient> itemIngredients;
    private final List<SizedFluidIngredient> fluidIngredients;
    private final List<ProcessingOutput> itemResults;
    private final List<FluidStack> fluidResults;
    private final int processingDuration;
    private final int minRpm;
    private final Optional<Integer> maxRpm;

    public CentrifugingRecipe(
            List<SizedIngredient> itemIngredients,
            List<SizedFluidIngredient> fluidIngredients,
            List<ProcessingOutput> itemResults,
            List<FluidStack> fluidResults,
            int processingDuration,
            int minRpm,
            Optional<Integer> maxRpm
    ) {
        if (itemIngredients.size() > 1) {
            throw new IllegalArgumentException("Centrifuging recipes support at most one item input");
        }
        if (fluidIngredients.size() > 1) {
            throw new IllegalArgumentException("Centrifuging recipes support at most one fluid input");
        }
        if (itemResults.size() > 4) {
            throw new IllegalArgumentException("Centrifuging recipes support at most four item outputs");
        }
        if (fluidResults.size() > 2) {
            throw new IllegalArgumentException("Centrifuging recipes support at most two fluid outputs");
        }
        if (itemIngredients.isEmpty() && fluidIngredients.isEmpty()) {
            throw new IllegalArgumentException("Centrifuging recipes need an item or fluid input");
        }
        if (itemResults.isEmpty() && fluidResults.isEmpty()) {
            throw new IllegalArgumentException("Centrifuging recipes need an item or fluid output");
        }
        if (processingDuration <= 0) {
            throw new IllegalArgumentException("Centrifuging recipe duration must be positive");
        }
        if (minRpm < 0 || minRpm > 256) {
            throw new IllegalArgumentException("Centrifuging minimum RPM must be between 0 and 256");
        }
        if (maxRpm.isPresent() && (maxRpm.get() < minRpm || maxRpm.get() > 256)) {
            throw new IllegalArgumentException("Centrifuging maximum RPM must be between minimum RPM and 256");
        }

        this.itemIngredients = List.copyOf(itemIngredients);
        this.fluidIngredients = List.copyOf(fluidIngredients);
        this.itemResults = List.copyOf(itemResults);
        this.fluidResults = fluidResults.stream().map(FluidStack::copy).toList();
        this.processingDuration = processingDuration;
        this.minRpm = minRpm;
        this.maxRpm = maxRpm;
    }

    @Override
    public boolean matches(CentrifugingRecipeInput input, Level level) {
        if (input.isEmpty()) {
            return false;
        }
        return matchesItem(input.item()) && matchesFluid(input.fluid()) && canProcessAtRpm(input.rpm());
    }

    public boolean matchesItem(ItemStack stack) {
        if (itemIngredients.isEmpty()) {
            return true;
        }
        if (stack.isEmpty()) {
            return false;
        }
        SizedIngredient ingredient = itemIngredients.getFirst();
        return stack.getCount() >= ingredient.count() && ingredient.ingredient().test(stack);
    }

    public boolean matchesFluid(FluidStack stack) {
        if (fluidIngredients.isEmpty()) {
            return true;
        }
        if (stack.isEmpty()) {
            return false;
        }
        SizedFluidIngredient ingredient = fluidIngredients.getFirst();
        return stack.getAmount() >= ingredient.amount() && ingredient.ingredient().test(stack);
    }

    public boolean acceptsFluid(FluidStack stack) {
        return !fluidIngredients.isEmpty() && fluidIngredients.getFirst().ingredient().test(stack);
    }

    public boolean canProcessAtRpm(float rpm) {
        float speed = Math.abs(rpm);
        return speed >= minRpm && maxRpm.map(max -> speed <= max).orElse(true);
    }

    public List<SizedIngredient> itemIngredients() {
        return itemIngredients;
    }

    public List<SizedFluidIngredient> fluidIngredients() {
        return fluidIngredients;
    }

    public List<ProcessingOutput> itemResults() {
        return itemResults;
    }

    public List<ItemStack> rollItemResults(RandomSource random) {
        return itemResults.stream()
                .map(output -> output.rollOutput(random))
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    public List<ItemStack> possibleItemResults() {
        return itemResults.stream()
                .map(ProcessingOutput::getStack)
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    public List<FluidStack> fluidResults() {
        return fluidResults.stream().map(FluidStack::copy).toList();
    }

    public int processingDuration() {
        return processingDuration;
    }

    public int minRpm() {
        return minRpm;
    }

    public Optional<Integer> maxRpm() {
        return maxRpm;
    }

    public int consumedFluidAmount() {
        return fluidIngredients.isEmpty() ? 0 : fluidIngredients.getFirst().amount();
    }

    public boolean consumesItem() {
        return !itemIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(CentrifugingRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return itemResults.stream()
                .findFirst()
                .map(ProcessingOutput::getStack)
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        itemIngredients.forEach(input -> ingredients.add(input.ingredient()));
        return ingredients;
    }

    public int consumedItemCount() {
        return itemIngredients.isEmpty() ? 0 : itemIngredients.getFirst().count();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BlockRegistry.KINETIC_CENTRIFUGE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CENTRIFUGING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CENTRIFUGING_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CentrifugingRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, CentrifugingRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<CentrifugingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CentrifugingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
