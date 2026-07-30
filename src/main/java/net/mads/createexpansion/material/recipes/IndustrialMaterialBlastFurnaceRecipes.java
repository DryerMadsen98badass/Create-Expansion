package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.List;

public final class IndustrialMaterialBlastFurnaceRecipes {

    /*
     * Ore processing:
     * 400 ticks = 20 seconds
     */
    private static final int ORE_PROCESSING_DURATION = 400;

    private static final List<Fuel> FUELS = List.of(
            new Fuel("minecraft:coal", 200),
            new Fuel("create_expansion:coal_coke", 400),
            new Fuel("create_expansion:bio_char_dust", 50),
            new Fuel("minecraft:charcoal", 100)
    );

    private IndustrialMaterialBlastFurnaceRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        for (IndustrialMaterial inputMaterial : IndustrialMaterials.ALL) {
            /*
             * Oppskriftene krever enten:
             *
             * .smelting(resultMaterial)
             *
             * eller:
             *
             * .smeltingSelf()
             */
            IndustrialMaterial resultMaterial;

            if (inputMaterial.smeltingResult().isPresent()) {
                resultMaterial = inputMaterial.smeltingResult().get();
            } else if (inputMaterial.smeltingSelf()) {
                resultMaterial = inputMaterial;
            } else {
                continue;
            }

            /*
             * 1 raw ore -> 3 nuggets
             */
            saveOreRecipes(
                    output,
                    inputMaterial,
                    MaterialPart.RAW_ORE,
                    resultMaterial,
                    3,
                    "raw_ore"
            );

            /*
             * Alle videre prosesserte former gir 4 nuggets.
             */
            saveOreRecipes(
                    output,
                    inputMaterial,
                    MaterialPart.CRUSHED_ORE,
                    resultMaterial,
                    4,
                    "crushed_ore"
            );

            saveOreRecipes(
                    output,
                    inputMaterial,
                    MaterialPart.REFINED_ORE,
                    resultMaterial,
                    4,
                    "refined_ore"
            );

            saveOreRecipes(
                    output,
                    inputMaterial,
                    MaterialPart.PURIFIED_DUST,
                    resultMaterial,
                    4,
                    "purified_dust"
            );

            saveOreRecipes(
                    output,
                    inputMaterial,
                    MaterialPart.WASHED_CRUSHED_ORE,
                    resultMaterial,
                    4,
                    "washed_crushed_ore"
            );

            saveOreRecipes(
                    output,
                    inputMaterial,
                    MaterialPart.IMPURE_DUST,
                    resultMaterial,
                    4,
                    "impure_dust"
            );
        }
    }

    private static void saveOreRecipes(
            RecipeOutput output,
            IndustrialMaterial inputMaterial,
            MaterialPart inputPart,
            IndustrialMaterial resultMaterial,
            int resultAmount,
            String inputName
    ) {
        if (!MaterialRecipeHelper.hasItems(
                inputMaterial,
                inputPart
        )) {
            return;
        }

        if (!MaterialRecipeHelper.hasItems(
                resultMaterial,
                MaterialPart.NUGGET
        )) {
            return;
        }

        String inputItem = MaterialRecipeHelper.itemId(
                inputMaterial,
                inputPart
        );

        String resultItem = MaterialRecipeHelper.itemId(
                resultMaterial,
                MaterialPart.NUGGET
        );

        for (Fuel fuel : FUELS) {
            int fuelAmount = fuelAmount(
                    ORE_PROCESSING_DURATION,
                    fuel
            );

            if (fuelAmount > 64) {
                continue;
            }

            RecipeDefinition.recipe()
                    .recipeDefinition(RecipeDefinition.Option.id(
                            oreRecipeId(
                                    inputMaterial,
                                    inputName,
                                    resultMaterial,
                                    resultAmount,
                                    fuel,
                                    fuelAmount
                            )
                    ))
                    .recipeDefinition(RecipeDefinition.Option.recipeType(
                            CERecipeTypes.BLAST_FURNACE
                    ))
                    .recipeDefinition(RecipeDefinition.Option.inputItem(
                            inputItem,
                            1
                    ))
                    .recipeDefinition(RecipeDefinition.Option.inputItem(
                            fuel.item(),
                            fuelAmount
                    ))
                    .recipeDefinition(RecipeDefinition.Option.outputItem(
                            resultItem,
                            resultAmount
                    ))
                    .recipeDefinition(RecipeDefinition.Option.duration(
                            ORE_PROCESSING_DURATION
                    ))
                    .save(output);
        }
    }

    private static int fuelAmount(
            int duration,
            Fuel fuel
    ) {
        long amount = Math.round(
                duration / (double) fuel.burnDuration()
        );

        return (int) Math.max(1, amount);
    }

    private static String oreRecipeId(
            IndustrialMaterial inputMaterial,
            String inputName,
            IndustrialMaterial resultMaterial,
            int resultAmount,
            Fuel fuel,
            int fuelAmount
    ) {
        return inputMaterial.id()
                + "_"
                + inputName
                + "_to_"
                + resultMaterial.id()
                + "_nugget_x"
                + resultAmount
                + "_with_"
                + path(fuel.item())
                + "_x"
                + fuelAmount;
    }

    private static String path(String id) {
        int separator = id.indexOf(':');

        return separator >= 0
                ? id.substring(separator + 1)
                : id;
    }

    private record Fuel(
            String item,
            int burnDuration
    ) {
        private Fuel {
            if (item == null || item.isBlank()) {
                throw new IllegalArgumentException(
                        "Fuel item cannot be empty"
                );
            }

            if (burnDuration < 1) {
                throw new IllegalArgumentException(
                        "Fuel burn duration must be 1 or higher"
                );
            }
        }
    }
}