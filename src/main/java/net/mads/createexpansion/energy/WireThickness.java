package net.mads.createexpansion.energy;

import java.util.List;

public enum WireThickness {
    X1("1x", "1x", 4, 1),
    X2("2x", "2x", 6, 2),
    X4("4x", "4x", 8, 4),
    X8("8x", "8x", 10, 8),
    X16("16x", "16x", 12, 16);

    public static final List<WireThickness> ALL = List.of(values());

    private final String id;
    private final String displayName;
    private final int pixels;
    private final int baseAmps;

    WireThickness(String id, String displayName, int pixels, int baseAmps) {
        this.id = id;
        this.displayName = displayName;
        this.pixels = pixels;
        this.baseAmps = baseAmps;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public int pixels() {
        return pixels;
    }

    public int baseAmps() {
        return baseAmps;
    }
}
