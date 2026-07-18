package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.foundry.CasterTransformationRecipe;
import net.mads.createexpansion.recipe.recipes.foundry.CasterTransformationRecipeInput;
import net.mads.createexpansion.registry.FluidRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class CasterTransformationRecipes {
    public static final int MOLD_AMOUNT_MB = 576;
    private static volatile List<RecipeHolder<CasterTransformationRecipe>> syntheticCache;

    private CasterTransformationRecipes() {
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        for (RecipeHolder<CasterTransformationRecipe> holder : syntheticRecipes()) {
            output.accept(holder.id(), holder.value(), null);
        }
    }

    public static List<RecipeHolder<CasterTransformationRecipe>> syntheticRecipes() {
        List<RecipeHolder<CasterTransformationRecipe>> cached = syntheticCache;
        if (cached != null) {
            return cached;
        }

        List<RecipeHolder<CasterTransformationRecipe>> recipes = new ArrayList<>();
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (!material.has(MaterialPart.MOLTEN_FLUID)) {
                continue;
            }

            FluidRegistry.RegisteredFluid fluid = FoundryMeltingRecipes.materialFluid(material);
            if (fluid == null) {
                continue;
            }

            for (FoundryCastingRecipes.CastShape shape : FoundryCastingRecipes.shapes().values()) {
                RecipeHolder<CasterTransformationRecipe> holder = createHolder(material, fluid, shape);
                if (holder != null) {
                    recipes.add(holder);
                }
            }
        }
        syntheticCache = List.copyOf(recipes);
        return syntheticCache;
    }

    public static CasterTransformationRecipe recipe(Level level, ItemStack template, FluidStack fluid) {
        if (template.isEmpty() || fluid.isEmpty()) {
            return null;
        }

        if (level != null) {
            CasterTransformationRecipeInput input = new CasterTransformationRecipeInput(template, fluid);
            Optional<RecipeHolder<CasterTransformationRecipe>> managedRecipe =
                    level.getRecipeManager().getRecipeFor(RecipeRegistry.CASTER_TRANSFORMATION_RECIPE_TYPE.get(), input, level);
            if (managedRecipe.isPresent()) {
                return managedRecipe.get().value();
            }
        }

        for (RecipeHolder<CasterTransformationRecipe> holder : syntheticRecipes()) {
            if (holder.value().matches(new CasterTransformationRecipeInput(template, fluid), level)) {
                return holder.value();
            }
        }
        return null;
    }

    public static boolean isTemplate(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (FoundryCastingRecipes.CastShape shape : FoundryCastingRecipes.shapes().values()) {
            if (template(shape).test(stack)) {
                return true;
            }
        }
        return false;
    }

    public static Ingredient template(FoundryCastingRecipes.CastShape shape) {
        return Ingredient.of(ItemRegistry.getSimpleItem(templateId(shape)).get());
    }

    public static String templateId(FoundryCastingRecipes.CastShape shape) {
        String moldId = shape.moldPart().id();
        return "terracotta_" + moldId.substring("cast_".length(), moldId.length() - "_mold".length());
    }

    private static RecipeHolder<CasterTransformationRecipe> createHolder(IndustrialMaterial material, FluidRegistry.RegisteredFluid fluid, FoundryCastingRecipes.CastShape shape) {
        if (!material.has(shape.moldPart())) {
            return null;
        }

        Item moldItem = material.hasExistingPart(shape.moldPart())
                ? BuiltInRegistries.ITEM.get(material.existingPart(shape.moldPart()))
                : ItemRegistry.getMaterialItem(material, shape.moldPart()).get();
        if (moldItem == Items.AIR) {
            return null;
        }

        CasterTransformationRecipe recipe = new CasterTransformationRecipe(
                template(shape),
                new FluidStack(fluid.source().get(), MOLD_AMOUNT_MB),
                new ItemStack(moldItem)
        );
        return new RecipeHolder<>(id(material, shape), recipe);
    }

    private static ResourceLocation id(IndustrialMaterial material, FoundryCastingRecipes.CastShape shape) {
        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                "caster_transformation/" + material.id() + "_" + shape.moldPart().id().toLowerCase(Locale.ROOT)
        );
    }
}
