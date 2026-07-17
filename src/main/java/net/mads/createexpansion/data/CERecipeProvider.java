package net.mads.createexpansion.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipes;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipes;
import net.mads.createexpansion.recipe.recipes.lathe.KineticLatheRecipes;
import net.mads.createexpansion.recipe.recipes.sifter.SifterRecipes;
import net.mads.createexpansion.recipe.recipes.rolling.RollingRecipes;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipes;
import net.mads.createexpansion.recipe.recipes.hydraulicpress.HydraulicPressingRecipes;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipes;
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
    }
}
