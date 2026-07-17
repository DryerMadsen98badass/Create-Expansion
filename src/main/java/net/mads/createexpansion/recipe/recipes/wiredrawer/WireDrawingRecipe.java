package net.mads.createexpansion.recipe.recipes.wiredrawer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.recipe.SingleItemKineticRecipe;
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

import java.util.List;
import java.util.Optional;

public class WireDrawingRecipe implements Recipe<WireDrawingRecipeInput>, SingleItemKineticRecipe {
    public static final MapCodec<WireDrawingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(WireDrawingRecipe::ingredients),
            ProcessingOutput.CODEC.listOf().fieldOf("results").forGetter(WireDrawingRecipe::results),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("processing_time", 100).forGetter(WireDrawingRecipe::processingDuration),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm", 0).forGetter(WireDrawingRecipe::minRpm),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_rpm").forGetter(WireDrawingRecipe::maxRpm)
    ).apply(instance, WireDrawingRecipe::new));

    private final List<Ingredient> ingredients;
    private final List<ProcessingOutput> results;
    private final int processingDuration;
    private final int minRpm;
    private final Optional<Integer> maxRpm;

    public WireDrawingRecipe(List<Ingredient> ingredients, List<ProcessingOutput> results, int processingDuration,
                             int minRpm, Optional<Integer> maxRpm) {
        this.ingredients = List.copyOf(ingredients);
        this.results = List.copyOf(results);
        this.processingDuration = processingDuration;
        this.minRpm = minRpm;
        this.maxRpm = maxRpm;
    }

    @Override public boolean matches(WireDrawingRecipeInput input, Level level) { return matchesItem(input.item()) && canProcessAtRpm(input.rpm()); }
    @Override public boolean matchesItem(ItemStack stack) { return !stack.isEmpty() && !ingredients.isEmpty() && ingredients.getFirst().test(stack); }
    @Override public boolean canProcessAtRpm(float rpm) { float speed = Math.abs(rpm); return speed >= minRpm && maxRpm.map(max -> speed <= max).orElse(true); }
    @Override public ItemStack result() { return results.isEmpty() ? ItemStack.EMPTY : results.getFirst().getStack().copy(); }
    @Override public int processingDuration() { return processingDuration; }
    public int minRpm() { return minRpm; }
    public Optional<Integer> maxRpm() { return maxRpm; }
    public List<Ingredient> ingredients() { return ingredients; }
    public List<ProcessingOutput> results() { return results; }
    @Override public ItemStack assemble(WireDrawingRecipeInput input, HolderLookup.Provider registries) { return result(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result(); }
    @Override public NonNullList<Ingredient> getIngredients() { NonNullList<Ingredient> list = NonNullList.create(); list.addAll(ingredients); return list; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(BlockRegistry.KINETIC_WIRE_DRAWER.get()); }
    @Override public RecipeSerializer<?> getSerializer() { return RecipeRegistry.WIRE_DRAWING_RECIPE_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return RecipeRegistry.WIRE_DRAWING_RECIPE_TYPE.get(); }

    public static class Serializer implements RecipeSerializer<WireDrawingRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, WireDrawingRecipe> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
        @Override public MapCodec<WireDrawingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, WireDrawingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
