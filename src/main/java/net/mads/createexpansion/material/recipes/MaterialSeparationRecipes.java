package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialComponent;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipeBuilder;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class MaterialSeparationRecipes {
    private static final int DURATION_PER_COMPONENT = 200;

    private MaterialSeparationRecipes() {
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            saveCentrifugeRecipes(output, material);
            saveElectrolyserRecipe(output, material);
        }
    }

    private static void saveCentrifugeRecipes(RecipeOutput output, IndustrialMaterial material) {
        Optional<MachineTier> minimumTier = material.centrifugeTier();
        if (minimumTier.isEmpty()) return;
        SeparationRecipe recipe = separationRecipe(material);
        if (recipe == null) return;
        int inputCount = material.centrifugeInputCount() > 0 ? material.centrifugeInputCount() : recipe.inputCount();

        if (!MachineTierStats.isAtLeast(minimumTier.get(), MachineTier.LV) && recipe.outputs().size() <= 4) {
            CentrifugeRecipeBuilder kinetic = CentrifugeRecipes.recipe("materials/" + material.id() + "_dust")
                    .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.DUST), inputCount)
                    .duration(recipe.duration()).minRpm(16).maxRpm(256);
            recipe.outputs().forEach((component, count) ->
                    kinetic.outputItem(MaterialRecipeHelper.itemId(component, MaterialPart.DUST), count));
            kinetic.save(output);
        }

        if (MachineTierStats.isAtLeast(MachineTier.LV, minimumTier.get())) {
            RecipeDefinition largeKinetic = RecipeDefinition.recipe()
                    .recipeDefinition(RecipeDefinition.Option.id("materials/" + material.id() + "_dust"))
                    .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.LARGE_KINETIC_CENTRIFUGE))
                    .recipeDefinition(RecipeDefinition.Option.inputItem(
                            MaterialRecipeHelper.itemId(material, MaterialPart.DUST),
                            inputCount
                    ))
                    .recipeDefinition(RecipeDefinition.Option.duration(Math.max(1, recipe.duration() / 2)))
                    .recipeDefinition(RecipeDefinition.Option.kinetic(MachineTier.LV));
            recipe.outputs().forEach((component, count) ->
                    largeKinetic.recipeDefinition(RecipeDefinition.Option.outputItem(
                            MaterialRecipeHelper.itemId(component, MaterialPart.DUST),
                            count
                    )));
            largeKinetic.save(output);
        }

        MachineTier electricTier = MachineTierStats.max(MachineTier.ULV, minimumTier.get());
        RecipeDefinition electric = RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("materials/" + material.id() + "_dust"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.ELECTRIC_CENTRIFUGE))
                .recipeDefinition(RecipeDefinition.Option.inputItem(
                        MaterialRecipeHelper.itemId(material, MaterialPart.DUST),
                        inputCount
                ))
                .recipeDefinition(RecipeDefinition.Option.duration(recipe.duration()))
                .recipeDefinition(RecipeDefinition.Option.tier(electricTier))
                .recipeDefinition(RecipeDefinition.Option.CEt(electricTier));
        recipe.outputs().forEach((component, count) ->
                electric.recipeDefinition(RecipeDefinition.Option.outputItem(
                        MaterialRecipeHelper.itemId(component, MaterialPart.DUST),
                        count
                )));
        electric.save(output);
    }

    private static void saveElectrolyserRecipe(RecipeOutput output, IndustrialMaterial material) {
        Optional<MachineTier> minimumTier = material.electrolyserTier();
        if (minimumTier.isEmpty()) return;
        SeparationRecipe recipe = separationRecipe(material);
        if (recipe == null) return;
        MachineTier tier = MachineTierStats.max(MachineTier.ULV, minimumTier.get());
        RecipeDefinition electrolyser = RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("materials/" + material.id() + "_dust"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.ELECTROLYSER))
                .recipeDefinition(RecipeDefinition.Option.inputItem(
                        MaterialRecipeHelper.itemId(material, MaterialPart.DUST),
                        recipe.inputCount()
                ))
                .recipeDefinition(RecipeDefinition.Option.duration(recipe.duration()))
                .recipeDefinition(RecipeDefinition.Option.tier(tier))
                .recipeDefinition(RecipeDefinition.Option.CEt(tier));
        recipe.outputs().forEach((component, count) ->
                electrolyser.recipeDefinition(RecipeDefinition.Option.outputItem(
                        MaterialRecipeHelper.itemId(component, MaterialPart.DUST),
                        count
                )));
        electrolyser.save(output);
    }

    private static SeparationRecipe separationRecipe(IndustrialMaterial material) {
        if (!material.has(MaterialPart.DUST) || material.components().isEmpty()) return null;
        Map<IndustrialMaterial, Integer> outputs = new LinkedHashMap<>();
        int total = 0;
        for (MaterialComponent component : material.components()) {
            if (!(component.substance() instanceof IndustrialMaterial componentMaterial)) return null;
            if (!componentMaterial.has(MaterialPart.DUST)) return null;
            outputs.merge(componentMaterial, component.amount(), Integer::sum);
            total += component.amount();
        }
        if (outputs.isEmpty() || outputs.size() > 9 || total <= 0) return null;
        return new SeparationRecipe(Map.copyOf(outputs), total, total * DURATION_PER_COMPONENT);
    }

    private record SeparationRecipe(Map<IndustrialMaterial, Integer> outputs, int inputCount, int duration) {
    }
}
