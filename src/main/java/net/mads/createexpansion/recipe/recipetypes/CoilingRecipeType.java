package net.mads.createexpansion.recipe.recipetypes;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipe;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipeInput;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class CoilingRecipeType implements IRecipeTypeInfo {
    public static final CoilingRecipeType INSTANCE = new CoilingRecipeType();
    public static final String NAME = "coiling";
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, NAME);

    private CoilingRecipeType() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) RecipeRegistry.COILING_RECIPE_SERIALIZER.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) RecipeRegistry.COILING_RECIPE_TYPE.get();
    }

    public Optional<RecipeHolder<CoilingRecipe>> find(CoilingRecipeInput input, Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeRegistry.COILING_RECIPE_TYPE.get(), input, level);
    }
}
