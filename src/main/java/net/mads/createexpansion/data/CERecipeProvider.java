package net.mads.createexpansion.data;

import net.mads.createexpansion.material.recipes.*;
import net.mads.createexpansion.recipe.recipes.alloy_smelter.SteamAlloySmelterRecipes;
import net.mads.createexpansion.recipe.recipes.autoclave.SteamAutoclaveRecipes;
import net.mads.createexpansion.recipe.recipes.coke_oven.CokeOvenRecipes;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyRecipes;
import net.mads.createexpansion.recipe.recipes.coke_oven.steamcompressor;
import net.mads.createexpansion.recipe.recipes.coke_oven.steamextractor;
import net.mads.createexpansion.recipe.recipes.distillery.steam_distillery_recipes;
import net.mads.createexpansion.recipe.recipes.forge_hammer.forge_hammer;
import net.mads.createexpansion.recipe.recipes.induction_chamber.SteamInductionChamberRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipes;
import net.mads.createexpansion.recipe.recipes.sifter.SifterRecipes;
import net.mads.createexpansion.recipe.recipes.blast_furnace.BlastFurnaceRecipes;

import java.util.concurrent.CompletableFuture;

public class CERecipeProvider extends RecipeProvider {
    public CERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
        BlastFurnaceRecipes.build(output, holderLookup);
        steamextractor.build(output, holderLookup);
        steamcompressor.build(output, holderLookup);
        steam_distillery_recipes.build(output, holderLookup);
        IndustrialMaterialBlastFurnaceRecipes.build(output, holderLookup);
        CokeOvenRecipes.build(output, holderLookup);
        forge_hammer.build(output, holderLookup);
        SteamAlloySmelterRecipes.build(output, holderLookup);
        SteamAutoclaveRecipes.build(output, holderLookup);
        SteamInductionChamberRecipes.build(output, holderLookup);
        SifterRecipes.build(output, holderLookup);
        CreateMaterialRecipeProvider.buildSifterRecipes(output, holderLookup);
        CentrifugeRecipes.build(output, holderLookup);
        KineticLatheRecipes.build(output, holderLookup);
        RollingRecipes.build(output, holderLookup);
        WireDrawingRecipes.build(output, holderLookup);
        HydraulicPressingRecipes.build(output, holderLookup);
        CoilingRecipes.build(output, holderLookup);
        AssemblyRecipes.build(output, holderLookup);
        FoundryMeltingRecipes.build(output, holderLookup);
        CasterTransformationRecipes.build(output, holderLookup);
        MaterialSeparationRecipes.build(output, holderLookup);
        MaterialGemCookingRecipes.build(output);
        net.mads.createexpansion.recipe.recipes.shaped.crafting.build(output);
        net.mads.createexpansion.recipe.recipes.shapless.crafting.build(output);
        net.mads.createexpansion.recipe.recipes.campfire.CampfireRecipes.build(output);
    }
}
