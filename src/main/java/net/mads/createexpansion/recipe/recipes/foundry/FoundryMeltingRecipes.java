package net.mads.createexpansion.recipe.recipes.foundry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialLookup;
import net.mads.createexpansion.material.MaterialPart;
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

public final class FoundryMeltingRecipes {
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

            savePart(output, material, fluid, MaterialPart.NUGGET, 1, 16);
            savePart(output, material, fluid, MaterialPart.INGOT, 9, 144);
            savePart(output, material, fluid, MaterialPart.BLOCK, 81, 1296);
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

            addSynthetic(recipes, material, fluid, MaterialPart.NUGGET, 1, 16);
            addSynthetic(recipes, material, fluid, MaterialPart.INGOT, 9, 144);
            addSynthetic(recipes, material, fluid, MaterialPart.BLOCK, 81, 1296);
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

        return switch (part) {
            case NUGGET -> createRecipe(material, fluid, MaterialPart.NUGGET, 1, 16);
            case INGOT -> createRecipe(material, fluid, MaterialPart.INGOT, 9, 144);
            case BLOCK -> createRecipe(material, fluid, MaterialPart.BLOCK, 81, 1296);
            default -> null;
        };
    }

    public static FoundryMeltingRecipe syntheticRecipeFor(Item item) {
        if (item == Items.AIR) {
            return null;
        }
        return syntheticRecipeFor(new ItemStack(item));
    }

    private static void savePart(RecipeOutput output, IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, MaterialPart part, int nuggets, int amount) {
        FoundryMeltingRecipe recipe = createRecipe(material, fluid, part, nuggets, amount);
        if (recipe == null) {
            return;
        }
        output.accept(id(material, part), recipe, null);
    }

    private static void addSynthetic(List<RecipeHolder<FoundryMeltingRecipe>> recipes, IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, MaterialPart part, int nuggets, int amount) {
        FoundryMeltingRecipe recipe = createRecipe(material, fluid, part, nuggets, amount);
        if (recipe != null) {
            recipes.add(new RecipeHolder<>(id(material, part), recipe));
        }
    }

    private static FoundryMeltingRecipe createRecipe(IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, MaterialPart part, int nuggets, int amount) {
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
                new FluidStack(fluid.source().get(), amount),
                Math.max(1, material.meltingPoint()),
                nuggets
        );
    }

    private static ResourceLocation id(IndustrialMaterial material, MaterialPart part) {
        return ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "foundry_melting/" + material.id() + "_" + part.id());
    }

    public static FluidRegistry.RegisteredFluid materialFluid(IndustrialMaterial material) {
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.MATERIAL_FLUIDS.values()) {
            if (fluid.definition().id().equals(material.id())) {
                return fluid;
            }
        }
        return null;
    }
}
