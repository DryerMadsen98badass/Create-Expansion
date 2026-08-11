package net.mads.createexpansion.recipe.recipetypes;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.recipes.blazeburnerrecipes.BlazeBurnerFuelRecipe;
import net.mads.createexpansion.recipe.recipes.blazeburnerrecipes.BlazeBurnerFuelRecipeInput;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public final class BlazeBurnerFuelRecipeType implements IRecipeTypeInfo {
    public static final BlazeBurnerFuelRecipeType INSTANCE = new BlazeBurnerFuelRecipeType();
    public static final String NAME = "blaze_burner_fuel";
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, NAME);

    private BlazeBurnerFuelRecipeType() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) RecipeRegistry.BLAZE_BURNER_FUEL_RECIPE_SERIALIZER.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) RecipeRegistry.BLAZE_BURNER_FUEL_RECIPE_TYPE.get();
    }

    public Optional<RecipeHolder<BlazeBurnerFuelRecipe>> findItem(net.minecraft.world.item.ItemStack stack, Level level) {
        return level.getRecipeManager().getRecipeFor(
                RecipeRegistry.BLAZE_BURNER_FUEL_RECIPE_TYPE.get(),
                BlazeBurnerFuelRecipeInput.item(stack),
                level
        );
    }

    public Optional<RecipeHolder<BlazeBurnerFuelRecipe>> findFluid(FluidStack stack, Level level) {
        return level.getRecipeManager().getRecipeFor(
                RecipeRegistry.BLAZE_BURNER_FUEL_RECIPE_TYPE.get(),
                BlazeBurnerFuelRecipeInput.fluid(stack),
                level
        );
    }
}
