package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialLookup;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipe;
import net.mads.createexpansion.registry.FluidRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Collections;

public final class FoundryMeltingRecipes {
    private static final Map<IndustrialMaterial, FluidRegistry.RegisteredFluid> MATERIAL_FLUID_CACHE = Collections.synchronizedMap(new IdentityHashMap<>());

    private FoundryMeltingRecipes() {
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (!material.has(MaterialPart.MOLTEN_FLUID)) {
                continue;
            }

            FluidRegistry.RegisteredFluid fluid = materialFluid(material);
            if (fluid == null) {
                continue;
            }

            for (Map.Entry<MaterialPart, Integer> entry : FoundryMeltingRecipe.meltingAmounts().entrySet()) {
                savePart(output, material, fluid, entry.getKey(), entry.getValue());
            }
        }
    }

    public static List<RecipeHolder<FoundryMeltingRecipe>> syntheticRecipes() {
        List<RecipeHolder<FoundryMeltingRecipe>> recipes = new ArrayList<>();
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (!material.has(MaterialPart.MOLTEN_FLUID)) {
                continue;
            }

            FluidRegistry.RegisteredFluid fluid = materialFluid(material);
            if (fluid == null) {
                continue;
            }

            for (Map.Entry<MaterialPart, Integer> entry : FoundryMeltingRecipe.meltingAmounts().entrySet()) {
                addSynthetic(recipes, material, fluid, entry.getKey(), entry.getValue());
            }
        }
        return recipes;
    }

    public static boolean canMelt(ItemStack stack) {
        return syntheticRecipeFor(stack) != null;
    }

    public static FoundryMeltingRecipe syntheticRecipeFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
        if (target == null) {
            return null;
        }

        IndustrialMaterial material = target.material();
        MaterialPart part = target.part();
        FluidRegistry.RegisteredFluid fluid = materialFluid(material);
        if (fluid == null || !material.has(MaterialPart.MOLTEN_FLUID)) {
            return null;
        }

        Integer amountMb = FoundryMeltingRecipe.meltingAmounts().get(part);
        return amountMb == null ? null : createRecipe(material, fluid, part, amountMb);
    }

    public static FoundryMeltingRecipe syntheticRecipeFor(Item item) {
        if (item == Items.AIR) {
            return null;
        }
        return syntheticRecipeFor(new ItemStack(item));
    }

    private static void savePart(RecipeOutput output, IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, MaterialPart part, int amountMb) {
        FoundryMeltingRecipe recipe = createRecipe(material, fluid, part, amountMb);
        if (recipe == null) {
            return;
        }
        output.accept(id(material, part), recipe, null);
    }

    private static void addSynthetic(List<RecipeHolder<FoundryMeltingRecipe>> recipes, IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, MaterialPart part, int amountMb) {
        FoundryMeltingRecipe recipe = createRecipe(material, fluid, part, amountMb);
        if (recipe != null) {
            recipes.add(new RecipeHolder<>(id(material, part), recipe));
        }
    }

    private static FoundryMeltingRecipe createRecipe(IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, MaterialPart part, int amountMb) {
        if (!material.has(part)) {
            return null;
        }

        Item item = material.hasExistingPart(part)
                ? BuiltInRegistries.ITEM.get(material.existingPart(part))
                : ItemRegistry.getMaterialItem(material, part).get();
        if (item == Items.AIR) {
            return null;
        }

        return new FoundryMeltingRecipe(
                Ingredient.of(item),
                new FluidStack(fluid.source().get(), amountMb),
                Math.max(1, material.meltingPoint()),
                Math.max(1, Math.round(amountMb / (float) FoundryMeltingRecipe.MB_PER_NUGGET))
        );
    }

    private static ResourceLocation id(IndustrialMaterial material, MaterialPart part) {
        return ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "foundry_melting/" + material.id() + "_" + part.id());
    }

    public static FluidRegistry.RegisteredFluid materialFluid(IndustrialMaterial material) {
        FluidRegistry.RegisteredFluid cached = MATERIAL_FLUID_CACHE.get(material);
        if (cached != null) {
            return cached;
        }
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (fluid.definition().id().equals(material.id())) {
                MATERIAL_FLUID_CACHE.put(material, fluid);
                return fluid;
            }
        }
        return null;
    }
}
