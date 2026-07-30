package net.mads.createexpansion.fluid;

import net.mads.createexpansion.material.IndustrialMaterials;

import java.util.List;

public final class IndustrialFluidRegistrationPolicy {
    private IndustrialFluidRegistrationPolicy() {
    }

    /** Only these definitions must create a FluidType, source, flowing fluid and bucket. */
    public static List<IndustrialFluid> generatedFluids() {
        return IndustrialMaterials.FLUIDS.stream()
                .filter(IndustrialFluidLookup::shouldRegister)
                .toList();
    }

    /** These definitions point at fluids and buckets already provided by Minecraft or another mod. */
    public static List<IndustrialFluid> existingFluids() {
        return IndustrialMaterials.FLUIDS.stream()
                .filter(IndustrialFluid::hasExistingFluid)
                .toList();
    }
}
