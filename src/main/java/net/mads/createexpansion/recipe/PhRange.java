package net.mads.createexpansion.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Inclusive pH range stored in hundredths so recipe checks and machine-control
 * comparisons remain deterministic.
 */
public record PhRange(int minHundredths, int maxHundredths) {
    public static final int SCALE = 100;
    public static final int MIN_HUNDREDTHS = 0;
    public static final int MAX_HUNDREDTHS = 14 * SCALE;
    public static final int NEUTRAL_HUNDREDTHS = 7 * SCALE;

    public static final Codec<PhRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("min").forGetter(PhRange::min),
            Codec.DOUBLE.fieldOf("max").forGetter(PhRange::max)
    ).apply(instance, PhRange::of));

    public PhRange {
        if (minHundredths < MIN_HUNDREDTHS || minHundredths > MAX_HUNDREDTHS) {
            throw new IllegalArgumentException("Minimum pH must be between 0 and 14");
        }
        if (maxHundredths < MIN_HUNDREDTHS || maxHundredths > MAX_HUNDREDTHS) {
            throw new IllegalArgumentException("Maximum pH must be between 0 and 14");
        }
        if (maxHundredths < minHundredths) {
            throw new IllegalArgumentException("Maximum pH cannot be lower than minimum pH");
        }
    }

    public static PhRange of(double min, double max) {
        return new PhRange(toHundredths(min), toHundredths(max));
    }

    public static int toHundredths(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("pH must be a finite number");
        }
        if (value < 0.0D || value > 14.0D) {
            throw new IllegalArgumentException("pH must be between 0 and 14");
        }
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(SCALE))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }

    public static double fromHundredths(int value) {
        return value / (double) SCALE;
    }

    public double min() {
        return fromHundredths(minHundredths);
    }

    public double max() {
        return fromHundredths(maxHundredths);
    }

    public boolean containsHundredths(int value) {
        return value >= minHundredths && value <= maxHundredths;
    }

    public static String formatHundredths(int value) {
        return String.format(java.util.Locale.ROOT, "%.2f", fromHundredths(value));
    }
}
