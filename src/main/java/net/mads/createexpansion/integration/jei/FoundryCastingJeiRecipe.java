package net.mads.createexpansion.integration.jei;

import net.mads.createexpansion.machine.machines.foundry.FoundryCastingRecipes;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.registry.FluidRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public record FoundryCastingJeiRecipe(
        FluidStack fluid,
        List<ItemStack> molds,
        ItemStack output,
        FoundryCastingRecipes.CastShape shape
) {
    private static final int MAX_VISIBLE_MOLDS = 64;

    public static List<FoundryCastingJeiRecipe> all() {
        List<FoundryCastingJeiRecipe> recipes = new ArrayList<>();
        for (IndustrialMaterial castMaterial : IndustrialMaterials.ALL) {
            FluidRegistry.RegisteredFluid fluid = materialFluid(castMaterial);
            if (fluid == null) {
                continue;
            }

            for (FoundryCastingRecipes.CastShape shape : FoundryCastingRecipes.shapes().values()) {
                if (!castMaterial.has(shape.castPart())) {
                    continue;
                }

                DeferredHolder<Item, ? extends Item> outputItem = ItemRegistry.getMaterialItem(castMaterial, shape.castPart());
                if (outputItem == null) {
                    continue;
                }

                List<ItemStack> molds = moldsFor(shape);
                if (molds.isEmpty()) {
                    continue;
                }

                recipes.add(new FoundryCastingJeiRecipe(
                        new FluidStack(fluid.source().get(), shape.amountMb()),
                        molds,
                        new ItemStack(outputItem.get()),
                        shape
                ));
            }
        }
        return recipes;
    }

    private static List<ItemStack> moldsFor(FoundryCastingRecipes.CastShape shape) {
        List<ItemStack> molds = new ArrayList<>();
        for (IndustrialMaterial moldMaterial : IndustrialMaterials.ALL) {
            if (!moldMaterial.has(shape.moldPart())) {
                continue;
            }

            DeferredHolder<Item, ? extends Item> moldItem = ItemRegistry.getMaterialItem(moldMaterial, shape.moldPart());
            if (moldItem != null) {
                molds.add(new ItemStack(moldItem.get()));
                if (molds.size() >= MAX_VISIBLE_MOLDS) {
                    break;
                }
            }
        }
        return molds;
    }

    private static FluidRegistry.RegisteredFluid materialFluid(IndustrialMaterial material) {
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (fluid.definition().id().equals(material.id())) {
                return fluid;
            }
        }
        return null;
    }
}
