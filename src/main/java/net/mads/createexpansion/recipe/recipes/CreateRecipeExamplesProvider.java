package net.mads.createexpansion.recipe.recipes;

import net.mads.createexpansion.recipe.recipes.compacting.CompactingRecipes;
import net.mads.createexpansion.recipe.recipes.crushing.CrushingRecipes;
import net.mads.createexpansion.recipe.recipes.cutting.CuttingRecipes;
import net.mads.createexpansion.recipe.recipes.deploying.DeployingRecipes;
import net.mads.createexpansion.recipe.recipes.emptying.EmptyingRecipes;
import net.mads.createexpansion.recipe.recipes.filling.FillingRecipes;
import net.mads.createexpansion.recipe.recipes.haunting.HauntingRecipes;
import net.mads.createexpansion.recipe.recipes.itemapplication.ItemApplicationRecipes;
import net.mads.createexpansion.recipe.recipes.milling.MillingRecipes;
import net.mads.createexpansion.recipe.recipes.mixing.MixingRecipes;
import net.mads.createexpansion.recipe.recipes.pressing.PressingRecipes;
import net.mads.createexpansion.recipe.recipes.sandpaperpolishing.SandpaperPolishingRecipes;
import net.mads.createexpansion.recipe.recipes.sequencedassembly.SequencedAssemblyRecipes;
import net.mads.createexpansion.recipe.recipes.splashing.SplashingRecipes;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CreateRecipeExamplesProvider implements DataProvider {
    private final PackOutput.PathProvider recipes;

    public CreateRecipeExamplesProvider(PackOutput output) {
        this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        CompactingRecipes.build(futures, output, recipes);
        CrushingRecipes.build(futures, output, recipes);
        CuttingRecipes.build(futures, output, recipes);
        DeployingRecipes.build(futures, output, recipes);
        EmptyingRecipes.build(futures, output, recipes);
        FillingRecipes.build(futures, output, recipes);
        HauntingRecipes.build(futures, output, recipes);
        ItemApplicationRecipes.build(futures, output, recipes);
        MillingRecipes.build(futures, output, recipes);
        MixingRecipes.build(futures, output, recipes);
        PressingRecipes.build(futures, output, recipes);
        SandpaperPolishingRecipes.build(futures, output, recipes);
        SequencedAssemblyRecipes.build(futures, output, recipes);
        SplashingRecipes.build(futures, output, recipes);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Recipe Examples";
    }
}
