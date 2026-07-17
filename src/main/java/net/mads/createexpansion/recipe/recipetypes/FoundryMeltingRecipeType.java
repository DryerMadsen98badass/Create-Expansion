package net.mads.createexpansion.recipe.recipetypes;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipe;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipeInput;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class FoundryMeltingRecipeType implements IRecipeTypeInfo {
    public static final FoundryMeltingRecipeType INSTANCE = new FoundryMeltingRecipeType();
    public static final String NAME = "foundry_melting";
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, NAME);

    private FoundryMeltingRecipeType() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) RecipeRegistry.FOUNDRY_MELTING_RECIPE_SERIALIZER.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get();
    }

    public Optional<RecipeHolder<FoundryMeltingRecipe>> find(FoundryMeltingRecipeInput input, Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get(), input, level);
    }
}
