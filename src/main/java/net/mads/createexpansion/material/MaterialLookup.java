package net.mads.createexpansion.material;

import net.mads.createexpansion.registry.FluidRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public final class MaterialLookup {
    private MaterialLookup() {
    }

    public static MaterialTarget find(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof MaterialItem materialItem) {
            return new MaterialTarget(materialItem.material(), materialItem.part());
        }

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MaterialBlock materialBlock) {
            return new MaterialTarget(materialBlock.material(), materialBlock.part());
        }

        MaterialTarget existingTarget = findExistingMaterialPart(item);
        if (existingTarget != null) {
            return existingTarget;
        }

        return findMoltenBucket(stack);
    }

    public static MaterialTarget find(FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (!stack.is(fluid.source().get()) && !stack.is(fluid.flowing().get())) {
                continue;
            }

            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                if (material.id().equals(fluid.definition().id())) {
                    return new MaterialTarget(material, MaterialPart.MOLTEN_FLUID);
                }
            }
        }

        return null;
    }

    private static MaterialTarget findExistingMaterialPart(Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (itemId.equals(material.existingParts().get(part))) {
                    return new MaterialTarget(material, part);
                }
            }
        }

        return null;
    }

    private static MaterialTarget findMoltenBucket(ItemStack stack) {
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (!stack.is(fluid.bucket().get())) {
                continue;
            }

            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                if (material.id().equals(fluid.definition().id())) {
                    return new MaterialTarget(material, MaterialPart.MOLTEN_FLUID);
                }
            }
        }

        return null;
    }

    public record MaterialTarget(IndustrialMaterial material, MaterialPart part) {
    }
}
