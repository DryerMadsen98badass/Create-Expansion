package net.mads.createexpansion.fluid;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.util.ColorUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class MaterialFluidType extends FluidType {
    private final ResourceLocation texture;
    private final int color;

    public MaterialFluidType(IndustrialFluid fluid) {
        super(FluidType.Properties.create()
                .descriptionId("fluid_type." + CreateExpansion.MOD_ID + "." + fluid.registryName())
                .temperature(fluid.temperature())
                .density(fluid.density())
                .viscosity(fluid.viscosity())
                .lightLevel(fluid.lightLevel())
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
        this.texture = ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                "block/material_sets/dull/" + fluid.textureName()
        );
        this.color = ColorUtils.opaque(fluid.color());
    }

    public ResourceLocation texture() {
        return texture;
    }

    public int color() {
        return color;
    }
}
