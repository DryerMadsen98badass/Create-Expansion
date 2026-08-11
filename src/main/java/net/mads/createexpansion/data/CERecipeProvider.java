package net.mads.createexpansion.data;

import net.mads.createexpansion.material.recipes.*;
import net.mads.createexpansion.recipe.recipes.alloy_smelter.ULVAlloySmelterRecipes;
import net.mads.createexpansion.recipe.recipes.autoclave.LVAutoclaveRecipes;
import net.mads.createexpansion.recipe.recipes.autoclave.PhTestAutoclaveRecipes;
import net.mads.createexpansion.recipe.recipes.autoclave.ULVAutoclaveRecipes;
import net.mads.createexpansion.recipe.recipes.boilers.LiquidFuelBoilerRecipes;
import net.mads.createexpansion.recipe.recipes.blazeburnerrecipes.BlazeBurnerFuelRecipes;
import net.mads.createexpansion.recipe.recipes.coke_oven.CokeOvenRecipes;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyRecipes;
import net.mads.createexpansion.recipe.recipes.boilers.SolidFuelBoilerRecipes;
import net.mads.createexpansion.recipe.recipes.compressor.ULVCompressorRecipes;
import net.mads.createexpansion.recipe.recipes.extractor.ULVExtractorRecipes;
import net.mads.createexpansion.recipe.recipes.distillery.LVDistilleryRecipes;
import net.mads.createexpansion.recipe.recipes.distillery.ULVDistilleryRecipes;
import net.mads.createexpansion.recipe.recipes.fluid_solidifier.LVFluidSolidifier;
import net.mads.createexpansion.recipe.recipes.forge_hammer.ULVForgeHammerRecipes;
import net.mads.createexpansion.recipe.recipes.induction_chamber.ULVInductionChamberRecipes;
import net.mads.createexpansion.recipe.recipes.tree_extracting.TreeExtractingRecipes;
import net.mads.createexpansion.recipe.recipes.sprinkling.SprinklingRecipes;
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
        ULVExtractorRecipes.build(output, holderLookup);
        ULVCompressorRecipes.build(output, holderLookup);
        ULVDistilleryRecipes.build(output, holderLookup);
        LVAutoclaveRecipes.build(output, holderLookup);
        PhTestAutoclaveRecipes.build(output, holderLookup);
        LVDistilleryRecipes.build(output, holderLookup);
        IndustrialMaterialBlastFurnaceRecipes.build(output, holderLookup);
        CokeOvenRecipes.build(output, holderLookup);
        LiquidFuelBoilerRecipes.build(output, holderLookup);
        ULVForgeHammerRecipes.build(output, holderLookup);
        ULVAlloySmelterRecipes.build(output, holderLookup);
        SolidFuelBoilerRecipes.build(output, holderLookup);
        ULVAutoclaveRecipes.build(output, holderLookup);
        ULVInductionChamberRecipes.build(output, holderLookup);
        TreeExtractingRecipes.build(output, holderLookup);
        SprinklingRecipes.build(output, holderLookup);
        BlazeBurnerFuelRecipes.build(output, holderLookup);
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
        LVFluidSolidifier.build(output, holderLookup);
        net.mads.createexpansion.recipe.recipes.shaped.crafting.build(output);
        net.mads.createexpansion.recipe.recipes.shapless.crafting.build(output);
        net.mads.createexpansion.recipe.recipes.campfire.CampfireRecipes.build(output);
    }
}
