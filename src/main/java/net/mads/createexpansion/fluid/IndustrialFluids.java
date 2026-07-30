package net.mads.createexpansion.fluid;

import net.mads.createexpansion.material.IndustrialMaterials;

import java.util.List;

public final class IndustrialFluids {
    public static final IndustrialFluid WATER = IndustrialMaterials.WATER;
    public static final IndustrialFluid CRUDE_OIL = IndustrialMaterials.CRUDE_OIL;
    public static final IndustrialFluid STEAM = IndustrialMaterials.STEAM;
    public static final IndustrialFluid CONCRETE = IndustrialMaterials.CONCRETE;
    public static final IndustrialFluid CREOSOTE_OIL = IndustrialMaterials.CREOSOTE_OIL;

    public static final List<IndustrialFluid> ALL = IndustrialMaterials.FLUIDS;

    private IndustrialFluids() {
    }

    public static IndustrialMaterials.FluidBuilder fluid(String id, String displayName, int color) {
        return IndustrialMaterials.fluid(id, displayName, color);
    }

    public static IndustrialMaterials.FluidBuilder gas(String id, String displayName, int color) {
        return IndustrialMaterials.gas(id, displayName, color);
    }

    public static IndustrialMaterials.FluidBuilder molten(String id, String displayName, int color, int temperature) {
        return IndustrialMaterials.molten(id, displayName, color, temperature);
    }
}
