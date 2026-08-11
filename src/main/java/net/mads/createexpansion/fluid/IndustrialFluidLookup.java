package net.mads.createexpansion.fluid;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.Optional;

public final class IndustrialFluidLookup {
    private IndustrialFluidLookup() {
    }

    public static boolean shouldRegister(IndustrialFluid definition) {
        return !definition.hasExistingFluid();
    }

    public static ResourceLocation fluidId(IndustrialFluid definition) {
        if (definition.hasExistingFluid()) {
            return definition.existingFluidId();
        }
        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                definition.registryName()
        );
    }

    public static ResourceLocation fluidId(IndustrialMaterial material) {
        if (!material.has(MaterialPart.MOLTEN_FLUID)) {
            throw new IllegalArgumentException("Material does not define a fluid part: " + material.id());
        }
        if (material.hasExistingPart(MaterialPart.MOLTEN_FLUID)) {
            return material.existingPart(MaterialPart.MOLTEN_FLUID);
        }
        return fluidId(materialFluid(material));
    }

    public static IndustrialFluid materialFluid(IndustrialMaterial material) {
        if (!material.has(MaterialPart.MOLTEN_FLUID)) {
            throw new IllegalArgumentException("Material does not define a fluid part: " + material.id());
        }
        if (isGasAtRoomTemperature(material)) {
            return IndustrialFluids
                    .gas(material.id(), material.displayName(), material.color())
                    .temperature(300)
                    .build();
        }
        if (material.meltingPoint() <= 20) {
            return IndustrialFluids
                    .fluid(material.id(), material.displayName(), material.color())
                    .temperature(300)
                    .build();
        }
        return IndustrialFluids
                .molten(
                        material.id(),
                        material.displayName(),
                        material.color(),
                        material.meltingPoint()
                )
                .build();
    }

    public static Fluid fluid(IndustrialFluid definition) {
        return BuiltInRegistries.FLUID.get(fluidId(definition));
    }

    public static IndustrialFluid find(Fluid fluid) {
        if (fluid == null) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
        for (IndustrialFluid definition : IndustrialMaterials.FLUIDS) {
            if (fluidId(definition).equals(id)) {
                return definition;
            }
        }
        return null;
    }

    public static IndustrialFluid find(FluidStack stack) {
        return stack == null || stack.isEmpty() ? null : find(stack.getFluid());
    }

    public static IndustrialFluid find(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Optional<FluidStack> contained = FluidUtil.getFluidContained(stack);
        return contained.map(IndustrialFluidLookup::find).orElse(null);
    }

    private static boolean isGasAtRoomTemperature(IndustrialMaterial material) {
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
}
