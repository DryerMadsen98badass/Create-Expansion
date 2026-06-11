package net.mads.createexpansion.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.mads.createexpansion.recipe.recipes.TestFoundryRecipes;

import java.util.concurrent.CompletableFuture;

public class CERecipeProvider extends RecipeProvider {
    public CERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
        TestFoundryRecipes.build(output, holderLookup);
    }
}
