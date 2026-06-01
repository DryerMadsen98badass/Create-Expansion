package net.mads.createexpansion.fluid;

public record IndustrialFluid(
        String id,
        String displayName,
        int color,
        Kind kind,
        int temperature,
        int density,
        int viscosity,
        int lightLevel
) {
    public boolean isGas() {
        return kind == Kind.GAS;
    }

    public String textureName() {
        return switch (kind) {
            case LIQUID -> "liquid";
            case GAS -> "gas";
            case MOLTEN -> "molten";
        };
    }

    public String registryName() {
        return kind == Kind.MOLTEN ? "molten_" + id : id;
    }

    public String bucketName() {
        return registryName() + "_bucket";
    }

    public String localizedName() {
        return kind == Kind.MOLTEN ? "Molten " + displayName : displayName;
    }

    public String bucketDisplayName() {
        return localizedName() + " Bucket";
    }

    public enum Kind {
        LIQUID,
        GAS,
        MOLTEN
    }
}
