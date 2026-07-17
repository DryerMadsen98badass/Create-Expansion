package net.mads.createexpansion.recipe.recipes.lathe;

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

import java.util.List;
import java.util.Optional;

public class TurningRecipe implements Recipe<TurningRecipeInput> {
    public static final int DEFAULT_MIN_RPM = 0;

    public static final MapCodec<TurningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("ingredients", List.of()).forGetter(TurningRecipe::itemIngredients),
            ProcessingOutput.CODEC.listOf().optionalFieldOf("results", List.of()).forGetter(TurningRecipe::itemResults),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("processing_time", 100).forGetter(TurningRecipe::processingDuration),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm", DEFAULT_MIN_RPM).forGetter(TurningRecipe::minRpm),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_rpm").forGetter(TurningRecipe::maxRpm)
    ).apply(instance, TurningRecipe::new));

    private final List<Ingredient> itemIngredients;
    private final List<ProcessingOutput> itemResults;
    private final int processingDuration;
    private final int minRpm;
    private final Optional<Integer> maxRpm;

    public TurningRecipe(
            List<Ingredient> itemIngredients,
            List<ProcessingOutput> itemResults,
            int processingDuration,
            int minRpm,
            Optional<Integer> maxRpm
    ) {
        this.itemIngredients = List.copyOf(itemIngredients);
        this.itemResults = List.copyOf(itemResults);
        this.processingDuration = processingDuration;
        this.minRpm = minRpm;
        this.maxRpm = maxRpm;
    }

    @Override
    public boolean matches(TurningRecipeInput input, Level level) {
        if (input.isEmpty()) {
            return false;
        }
        return matchesItem(input.item()) && canProcessAtRpm(input.rpm());
    }

    public boolean matchesItem(ItemStack stack) {
        if (itemIngredients.isEmpty() || stack.isEmpty()) {
            return false;
        }
        return itemIngredients.getFirst().test(stack);
    }

    public boolean canProcessAtRpm(float rpm) {
        float speed = Math.abs(rpm);
        return speed >= minRpm && maxRpm.map(max -> speed <= max).orElse(true);
    }

    public List<Ingredient> itemIngredients() {
        return itemIngredients;
    }

    public List<ProcessingOutput> itemResults() {
        return itemResults;
    }

    public List<ItemStack> rollResults(RandomSource random) {
        return itemResults.stream()
                .map(output -> output.rollOutput(random))
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    public List<ItemStack> possibleResults() {
        return itemResults.stream()
                .map(ProcessingOutput::getStack)
                .filter(stack -> !stack.isEmpty())
                .toList();
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

    @Override
    public ItemStack assemble(TurningRecipeInput input, HolderLookup.Provider registries) {
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
        itemIngredients.forEach(ingredients::add);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BlockRegistry.KINETIC_LATHE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.TURNING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.TURNING_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TurningRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, TurningRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<TurningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TurningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
