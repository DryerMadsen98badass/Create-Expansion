package net.mads.createexpansion.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipes;
import net.mads.createexpansion.material.recipes.CasterTransformationRecipes;
import net.mads.createexpansion.material.recipes.FoundryMeltingRecipes;
import net.mads.createexpansion.material.recipes.KineticLatheRecipes;
import net.mads.createexpansion.recipe.recipes.sifter.SifterRecipes;
import net.mads.createexpansion.material.recipes.RollingRecipes;
import net.mads.createexpansion.material.recipes.WireDrawingRecipes;
import net.mads.createexpansion.material.recipes.HydraulicPressingRecipes;
import net.mads.createexpansion.material.recipes.CoilingRecipes;
import net.mads.createexpansion.recipe.recipes.test_foundry.TestFoundryRecipes;

import java.util.concurrent.CompletableFuture;

public class CERecipeProvider extends RecipeProvider {
    public CERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
        TestFoundryRecipes.build(output, holderLookup);
        SifterRecipes.build(output, holderLookup);
        CentrifugeRecipes.build(output, holderLookup);
        KineticLatheRecipes.build(output, holderLookup);
        RollingRecipes.build(output, holderLookup);
        WireDrawingRecipes.build(output, holderLookup);
        HydraulicPressingRecipes.build(output, holderLookup);
        CoilingRecipes.build(output, holderLookup);
        FoundryMeltingRecipes.build(output, holderLookup);
        CasterTransformationRecipes.build(output, holderLookup);

        net.mads.createexpansion.recipe.recipes.shaped.crafting.build(output);
        net.mads.createexpansion.recipe.recipes.shapless.crafting.build(output);
    }
}