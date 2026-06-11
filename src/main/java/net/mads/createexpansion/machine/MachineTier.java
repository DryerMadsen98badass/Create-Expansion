package net.mads.createexpansion.machine;

import java.util.List;

public record MachineTier(String id, String displayName, int color) {
    public static final MachineTier ULV = new MachineTier("ulv", "ULV", 0x6D6D6D);
    public static final MachineTier LV = new MachineTier("lv", "LV", 0x4E8FDC);
    public static final MachineTier MV = new MachineTier("mv", "MV", 0xE0A83A);
    public static final MachineTier HV = new MachineTier("hv", "HV", 0xE85B5B);
    public static final MachineTier EV = new MachineTier("ev", "EV", 0x9B5DE5);
    public static final MachineTier IV = new MachineTier("iv", "IV", 0x5DD9C1);

    public static final List<MachineTier> ALL = List.of(
            ULV,
            LV,
            MV,
            HV,
            EV,
            IV
    );

    public String casingRegistryName() {
        return id + "_machine_casing";
    }

    public String casingDisplayName() {
        return displayName + " Machine Casing";
    }
}
