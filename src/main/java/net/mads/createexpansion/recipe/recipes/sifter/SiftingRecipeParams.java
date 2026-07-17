package net.mads.createexpansion.recipe.recipes.sifter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;
import java.util.function.Function;

public class SiftingRecipeParams extends ProcessingRecipeParams {
    public static final int DEFAULT_MIN_RPM = 0;

    public static final MapCodec<SiftingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(SiftingRecipeParams::new).forGetter(Function.identity()),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm").forGetter(params -> optionalNonDefault(params.minRpm, DEFAULT_MIN_RPM)),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_rpm").forGetter(SiftingRecipeParams::maxRpm),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("minimal_rpm").forGetter(params -> Optional.<Integer>empty()),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("maximal_rpm").forGetter(params -> Optional.<Integer>empty())
    ).apply(instance, (params, minRpm, maxRpm, legacyMinRpm, legacyMaxRpm) -> {
        params.minRpm = minRpm.or(() -> legacyMinRpm).orElse(DEFAULT_MIN_RPM);
        params.maxRpm = maxRpm.or(() -> legacyMaxRpm);
        return params;
    }));

    public static final StreamCodec<RegistryFriendlyByteBuf, SiftingRecipeParams> STREAM_CODEC =
            streamCodec(SiftingRecipeParams::new);

    private int minRpm = DEFAULT_MIN_RPM;
    private Optional<Integer> maxRpm = Optional.empty();

    public int minRpm() {
        return minRpm;
    }

    public Optional<Integer> maxRpm() {
        return maxRpm;
    }

    void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    void addResult(ProcessingOutput result) {
        results.add(result);
    }

    void processingDuration(int processingDuration) {
        this.processingDuration = processingDuration;
    }

    void minRpm(int minRpm) {
        this.minRpm = minRpm;
    }

    void maxRpm(int maxRpm) {
        this.maxRpm = Optional.of(maxRpm);
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.VAR_INT.encode(buffer, minRpm);
        buffer.writeBoolean(maxRpm.isPresent());
        maxRpm.ifPresent(max -> ByteBufCodecs.VAR_INT.encode(buffer, max));
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        minRpm = ByteBufCodecs.VAR_INT.decode(buffer);
        maxRpm = buffer.readBoolean() ? Optional.of(ByteBufCodecs.VAR_INT.decode(buffer)) : Optional.empty();
    }

    private static Optional<Integer> optionalNonDefault(int value, int defaultValue) {
        return value == defaultValue ? Optional.empty() : Optional.of(value);
    }
}
