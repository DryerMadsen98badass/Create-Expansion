package net.mads.createexpansion.material;

public final class MaterialProperties {
    private MaterialProperties() {
    }

    public static boolean isGasAtRoomTemperature(IndustrialMaterial material) {
        return switch (material.id()) {
            case "hydrogen",
                 "helium",
                 "nitrogen",
                 "oxygen",
                 "fluorine",
                 "neon",
                 "chlorine",
                 "argon",
                 "krypton",
                 "xenon",
                 "radon",
                 "oganesson" -> true;
            default -> false;
        };
    }

    public static boolean isFluidAtRoomTemperature(IndustrialMaterial material) {
        return material.meltingPoint() <= 20 || isGasAtRoomTemperature(material);
    }
}
