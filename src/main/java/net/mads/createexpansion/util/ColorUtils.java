package net.mads.createexpansion.util;

public final class ColorUtils {
    private ColorUtils() {
    }

    public static int opaque(int color) {
        return 0xFF000000 | color;
    }

    public static int darken(int color, float multiplier) {
        int red = (int) (((color >> 16) & 0xFF) * multiplier);
        int green = (int) (((color >> 8) & 0xFF) * multiplier);
        int blue = (int) ((color & 0xFF) * multiplier);
        return (red << 16) | (green << 8) | blue;
    }
}
