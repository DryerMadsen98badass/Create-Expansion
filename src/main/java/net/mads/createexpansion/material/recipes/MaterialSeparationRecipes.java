package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.fluid.IndustrialFluid;
import net.mads.createexpansion.fluid.IndustrialFluidLookup;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.IndustrialSubstance;
import net.mads.createexpansion.material.MaterialComponent;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipeBuilder;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugeRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MaterialSeparationRecipes {
    private static final int DURATION_PER_COMPONENT = 200;
    private static final int MILLIBUCKETS_PER_COMPONENT = 1000;
    private static final int CREATE_CENTRIFUGE_MAX_ITEM_OUTPUTS = 4;
    private static final int CREATE_CENTRIFUGE_MAX_FLUID_OUTPUTS = 2;
    private static final int CE_CENTRIFUGE_MAX_ITEM_OUTPUTS = 9;
    private static final int CE_CENTRIFUGE_MAX_FLUID_OUTPUTS = 3;
    private static final int ELECTROLYSER_MAX_ITEM_OUTPUTS = 9;
    private static final int ELECTROLYSER_MAX_FLUID_OUTPUTS = 3;

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
        if (minimumTier.isEmpty()) {
            return;
        }

        SeparationRecipe recipe = separationRecipe(material);
        if (recipe == null) {
            return;
        }
        validateOutputCapacity(
                material,
                "Centrifuge",
                recipe,
                CE_CENTRIFUGE_MAX_ITEM_OUTPUTS,
                CE_CENTRIFUGE_MAX_FLUID_OUTPUTS
        );

        int inputCount = explicitOrDefault(material.centrifugeInputCount(), recipe.defaultInputCount());
        if (!MachineTierStats.isAtLeast(minimumTier.get(), MachineTier.LV)
                && fitsOutputCapacity(
                        recipe,
                        CREATE_CENTRIFUGE_MAX_ITEM_OUTPUTS,
                        CREATE_CENTRIFUGE_MAX_FLUID_OUTPUTS
                )) {
            saveCreateCentrifugeRecipe(output, material, recipe, inputCount);
        }

        RecipeDefinition centrifuge = RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("materials/" + material.id() + "_dust"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.CENTRIFUGE))
                .recipeDefinition(RecipeDefinition.Option.inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.DUST), inputCount))
                .recipeDefinition(RecipeDefinition.Option.duration(recipe.duration()))
                .recipeDefinition(RecipeDefinition.Option.tier(minimumTier.get()));
        addOutputs(centrifuge, recipe);
        centrifuge.save(output);
    }

    private static void saveCreateCentrifugeRecipe(
            RecipeOutput output,
            IndustrialMaterial material,
            SeparationRecipe recipe,
            int inputCount
    ) {
        CentrifugeRecipeBuilder centrifuge = CentrifugeRecipes.recipe("materials/" + material.id() + "_dust")
                .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.DUST), inputCount)
                .duration(recipe.duration())
                .minRpm(16)
                .maxRpm(256);
        recipe.itemOutputs().forEach(item -> centrifuge.outputItem(item.id(), item.count()));
        recipe.fluidOutputs().forEach(fluid -> centrifuge.outputFluid(fluid.id(), fluid.amount()));
        centrifuge.save(output);
    }

    private static void saveElectrolyserRecipe(RecipeOutput output, IndustrialMaterial material) {
        Optional<MachineTier> minimumTier = material.electrolyserTier();
        if (minimumTier.isEmpty()) {
            return;
        }

        SeparationRecipe recipe = separationRecipe(material);
        if (recipe == null) {
            return;
        }
        validateOutputCapacity(
                material,
                "Electrolyser",
                recipe,
                ELECTROLYSER_MAX_ITEM_OUTPUTS,
                ELECTROLYSER_MAX_FLUID_OUTPUTS
        );

        int inputCount = explicitOrDefault(material.electrolyserInputCount(), recipe.defaultInputCount());
        RecipeDefinition electrolyser = RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id("materials/" + material.id() + "_dust"))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.ELECTROLYSER))
                .recipeDefinition(RecipeDefinition.Option.inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.DUST), inputCount))
                .recipeDefinition(RecipeDefinition.Option.duration(recipe.duration()))
                .recipeDefinition(RecipeDefinition.Option.tier(minimumTier.get()));
        addOutputs(electrolyser, recipe);
        electrolyser.save(output);
    }

    private static void addOutputs(RecipeDefinition definition, SeparationRecipe recipe) {
        recipe.itemOutputs().forEach(item -> definition.recipeDefinition(
                RecipeDefinition.Option.outputItem(item.id(), item.count())
        ));
        recipe.fluidOutputs().forEach(fluid -> definition.recipeDefinition(
                RecipeDefinition.Option.outputFluid(fluid.id(), fluid.amount())
        ));
    }

    private static SeparationRecipe separationRecipe(IndustrialMaterial material) {
        if (!material.has(MaterialPart.DUST) || material.components().isEmpty()) {
            return null;
        }

        Map<String, Integer> itemOutputs = new LinkedHashMap<>();
        Map<String, Integer> fluidOutputs = new LinkedHashMap<>();
        int totalComponents = 0;

        for (MaterialComponent component : material.components()) {
            totalComponents = Math.addExact(totalComponents, component.amount());
            addComponentOutput(material, component.substance(), component.amount(), itemOutputs, fluidOutputs);
        }

        if (itemOutputs.isEmpty() && fluidOutputs.isEmpty()) {
            throw new IllegalStateException("Material separation has no outputs: " + material.id());
        }

        List<ItemOutput> items = itemOutputs.entrySet().stream()
                .map(entry -> new ItemOutput(entry.getKey(), entry.getValue()))
                .toList();
        List<FluidOutput> fluids = fluidOutputs.entrySet().stream()
                .map(entry -> new FluidOutput(entry.getKey(), entry.getValue()))
                .toList();
        return new SeparationRecipe(
                items,
                fluids,
                totalComponents,
                Math.multiplyExact(totalComponents, DURATION_PER_COMPONENT)
        );
    }

    private static void addComponentOutput(
            IndustrialMaterial source,
            IndustrialSubstance substance,
            int amount,
            Map<String, Integer> itemOutputs,
            Map<String, Integer> fluidOutputs
    ) {
        if (substance instanceof IndustrialMaterial material) {
            if (material.has(MaterialPart.DUST)) {
                itemOutputs.merge(MaterialRecipeHelper.itemId(material, MaterialPart.DUST), amount, Math::addExact);
                return;
            }
            if (material.has(MaterialPart.MOLTEN_FLUID)) {
                ResourceLocation fluidId = IndustrialFluidLookup.fluidId(material);
                fluidOutputs.merge(
                        fluidId.toString(),
                        Math.multiplyExact(amount, MILLIBUCKETS_PER_COMPONENT),
                        Math::addExact
                );
                return;
            }
        } else if (substance instanceof IndustrialFluid fluid) {
            fluidOutputs.merge(
                    IndustrialFluidLookup.fluidId(fluid).toString(),
                    Math.multiplyExact(amount, MILLIBUCKETS_PER_COMPONENT),
                    Math::addExact
            );
            return;
        }

        throw new IllegalStateException(
                "Material " + source.id() + " cannot separate component " + substance.id()
                        + ": it has neither a dust nor a registered fluid output"
        );
    }

    private static int explicitOrDefault(int explicitInputCount, int defaultInputCount) {
        return explicitInputCount > 0 ? explicitInputCount : defaultInputCount;
    }

    private static boolean fitsOutputCapacity(
            SeparationRecipe recipe,
            int maxItemOutputs,
            int maxFluidOutputs
    ) {
        return recipe.itemOutputs().size() <= maxItemOutputs
                && recipe.fluidOutputs().size() <= maxFluidOutputs;
    }

    private static void validateOutputCapacity(
            IndustrialMaterial material,
            String machine,
            SeparationRecipe recipe,
            int maxItemOutputs,
            int maxFluidOutputs
    ) {
        if (recipe.itemOutputs().size() > maxItemOutputs || recipe.fluidOutputs().size() > maxFluidOutputs) {
            throw new IllegalStateException(
                    machine + " recipe for " + material.id() + " has "
                            + recipe.itemOutputs().size() + " item outputs and "
                            + recipe.fluidOutputs().size() + " fluid outputs; maximum is "
                            + maxItemOutputs + " item outputs and " + maxFluidOutputs + " fluid outputs"
            );
        }
    }

    private record SeparationRecipe(
            List<ItemOutput> itemOutputs,
            List<FluidOutput> fluidOutputs,
            int defaultInputCount,
            int duration
    ) {
    }

    private record ItemOutput(String id, int count) {
    }

    private record FluidOutput(String id, int amount) {
    }
}
