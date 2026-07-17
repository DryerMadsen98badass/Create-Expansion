package net.mads.createexpansion.recipe.recipes.sifter;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.mads.createexpansion.recipe.recipetypes.SiftingRecipeType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SiftingRecipe extends ProcessingRecipe<RecipeInput, SiftingRecipeParams> {
    private final int minRpm;
    private final Optional<Integer> maxRpm;

    public SiftingRecipe(SiftingRecipeParams params) {
        super(SiftingRecipeType.INSTANCE, params);
        this.minRpm = params.minRpm();
        this.maxRpm = params.maxRpm();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.isEmpty()) {
            return false;
        }
        return ingredients.getFirst().test(input.getItem(0));
    }

    public boolean canProcessAtRpm(float rpm) {
        float speed = Math.abs(rpm);
        return speed >= minRpm && maxRpm.map(max -> speed <= max).orElse(true);
    }

    public int minRpm() {
        return minRpm;
    }

    public Optional<Integer> maxRpm() {
        return maxRpm;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @FunctionalInterface
    public interface Factory<R extends SiftingRecipe> extends ProcessingRecipe.Factory<SiftingRecipeParams, R> {
        R create(SiftingRecipeParams params);
    }

    public static class Serializer<R extends SiftingRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(Factory<R> factory) {
            this.codec = ProcessingRecipe.codec(factory, SiftingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, SiftingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }
    }
}
