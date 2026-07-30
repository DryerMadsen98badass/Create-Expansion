package net.mads.createexpansion.machine;

import java.util.List;

public record MachineTier(String id, String displayName, int color) {
    public enum Family {
        ELECTRIC,
        STEAM
    }

    public static final MachineTier ULV = new MachineTier("ulv", "ULV", 0x6D6D6D);
    public static final MachineTier LV = new MachineTier("lv", "LV", 0x4E8FDC);
    public static final MachineTier MV = new MachineTier("mv", "MV", 0xE0A83A);
    public static final MachineTier HV = new MachineTier("hv", "HV", 0xE85B5B);
    public static final MachineTier EV = new MachineTier("ev", "EV", 0x9B5DE5);
    public static final MachineTier IV = new MachineTier("iv", "IV", 0x5DD9C1);

    public static final MachineTier STEAM_COPPER =
            new MachineTier("steam_copper", "Copper", 0xC87533);

    public static final MachineTier STEAM_BRONZE =
            new MachineTier("steam_bronze", "Bronze", 0xB08D57);

    public static final List<MachineTier> ALL = List.of(
            ULV,
            LV,
            MV,
            HV,
            EV,
            IV
    );

    public static final List<MachineTier> ELECTRIC_TIERS = ALL;

    public static final List<MachineTier> STEAM_SINGLEBLOCK_TIERS = List.of(
            STEAM_COPPER,
            STEAM_BRONZE
    );

    public String casingRegistryName() {
        return id + "_machine_casing";
    }

    public String casingDisplayName() {
        return displayName + " Machine Casing";
    }

    public String singleBlockMachineCasingSideTexture() {
        if (this == STEAM_COPPER) {
            return "create_expansion:block/casings/casing/bricked_copper_casing_side";
        }

        if (this == STEAM_BRONZE) {
            return "create_expansion:block/casings/casing/bricked_bronze_casing_side";
        }

        return "create_expansion:block/casings/universal_textures/casing";
    }

    public String singleBlockMachineCasingBottomTexture() {
        if (this == STEAM_COPPER) {
            return "create_expansion:block/casings/casing/bricked_copper_casing_bottom";
        }

        if (this == STEAM_BRONZE) {
            return "create_expansion:block/casings/casing/bricked_bronze_casing_bottom";
        }

        return singleBlockMachineCasingSideTexture();
    }

    public String singleBlockMachineCasingTopTexture() {
        if (this == STEAM_COPPER) {
            return "create_expansion:block/casings/casing/bricked_copper_casing_top";
        }

        if (this == STEAM_BRONZE) {
            return "create_expansion:block/casings/casing/bricked_bronze_casing_top";
        }

        return singleBlockMachineCasingSideTexture();
    }

    public Family family() {
        return isSteam()
                ? Family.STEAM
                : Family.ELECTRIC;
    }

    public boolean isSteam() {
        return STEAM_SINGLEBLOCK_TIERS.contains(this);
    }

    public boolean isElectric() {
        return ELECTRIC_TIERS.contains(this);
    }

    public int steamDurationMultiplier() {
        if (this == STEAM_COPPER) {
            return 8;
        }

        if (this == STEAM_BRONZE) {
            return 4;
        }

        return 1;
    }

    public int steamUsageMultiplier() {
        if (this == STEAM_BRONZE) {
            return 2;
        }

        return 1;
    }

    public int steamCapacityMultiplier() {
        if (this == STEAM_BRONZE) {
            return 2;
        }

        return 1;
    }

    public float steamExplosionPower() {
        if (this == STEAM_BRONZE) {
            return 8.0F;
        }

        return 4.0F;
    }

    public static List<MachineTier> expandSingleBlockTiers(
            MachineTier startTier
    ) {
        List<MachineTier> family = startTier.isSteam()
                ? STEAM_SINGLEBLOCK_TIERS
                : ELECTRIC_TIERS;

        int startIndex = family.indexOf(startTier);
        if (startIndex < 0) {
            throw new IllegalArgumentException(
                    "Unknown singleblock machine tier: "
                            + startTier
            );
        }

        return family.subList(startIndex, family.size());
    }
}
