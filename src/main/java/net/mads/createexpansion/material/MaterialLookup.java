package net.mads.createexpansion.material;

import net.mads.createexpansion.registry.FluidRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public final class MaterialLookup {
    private MaterialLookup() {
    }

    public static Optional<MaterialTarget> find(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof MaterialItem materialItem) {
            return Optional.of(new MaterialTarget(materialItem.material(), materialItem.part()));
        }

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MaterialBlock materialBlock) {
            return Optional.of(new MaterialTarget(materialBlock.material(), materialBlock.part()));
        }

        Optional<MaterialTarget> existingTarget = findExistingMaterialPart(item);
        if (existingTarget.isPresent()) {
            return existingTarget;
        }

        return findMoltenBucket(stack);
    }

    public static Optional<MaterialTarget> find(FluidStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (!stack.is(fluid.source().get()) && !stack.is(fluid.flowing().get())) {
                continue;
            }

            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                if (material.id().equals(fluid.definition().id())) {
                    return Optional.of(new MaterialTarget(material, MaterialPart.MOLTEN_FLUID));
                }
            }
        }

        return Optional.empty();
    }

    private static Optional<MaterialTarget> findExistingMaterialPart(Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (itemId.equals(material.existingParts().get(part))) {
                    return Optional.of(new MaterialTarget(material, part));
                }
            }
        }

        return Optional.empty();
    }

    private static Optional<MaterialTarget> findMoltenBucket(ItemStack stack) {
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (!stack.is(fluid.bucket().get())) {
                continue;
            }

            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                if (material.id().equals(fluid.definition().id())) {
                    return Optional.of(new MaterialTarget(material, MaterialPart.MOLTEN_FLUID));
                }
            }
        }

        return Optional.empty();
    }

    public record MaterialTarget(IndustrialMaterial material, MaterialPart part) {
    }
}
