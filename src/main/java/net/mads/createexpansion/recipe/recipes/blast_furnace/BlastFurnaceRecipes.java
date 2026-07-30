package net.mads.createexpansion.recipe.recipes.blast_furnace;

import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.List;

public final class BlastFurnaceRecipes {

    private static final List<Fuel> FUELS = List.of(
            new Fuel("minecraft:coal", 200),
            new Fuel("create_expansion:coal_coke", 400),
            new Fuel("create_expansion:bio_char_dust", 50),
            new Fuel("minecraft:charcoal", 100)
    );

    private static final List<BlastRecipe> RECIPES = List.of(
            new BlastRecipe("minecraft:iron_ingot", 1, "create_expansion:wrought_iron_cast_ingot", 1, 1200),
            new BlastRecipe("create_expansion:wrought_iron_ingot", 1, "create_expansion:steel_cast_ingot", 1, 2400),
            new BlastRecipe("create_expansion:iron_andesite_compound_dust", 1, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:zinc_dust", 1, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:hematite_dust", 3, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:magnetite_dust", 4, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:pyrite_dust", 3, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:siderite_dust", 5, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:goethite_dust", 4, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:sphalerite_dust", 2, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:smithsonite_dust", 5, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create_expansion:andesite_dust", 8, "create_expansion:zincite_dust", 2, "create:andesite_alloy", 1, 2400),
            new BlastRecipe("create:cinder_flour", 1, "minecraft:nether_brick", 1, 600),
            new BlastRecipe("minecraft:clay_ball", 1, "minecraft:brick", 1, 600),
            new BlastRecipe("create_expansion:seared_dust", 1, "create_expansion:seared_brick", 1, 600),
            new BlastRecipe("minecraft:clay_ball", 12, "create_expansion:andesite_dust", 1, "create_expansion:firebrick", 1, 600),
            new BlastRecipe("minecraft:clay_ball", 6, "create_expansion:tuff_dust",1, "create_expansion:silica_brick", 1, 600)
    );

    private BlastFurnaceRecipes() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        for (BlastRecipe recipe : RECIPES) {
            for (Fuel fuel : FUELS) {
                int fuelAmount = fuelAmount(recipe, fuel);

                if (fuelAmount > 64) {
                    continue;
                }

                RecipeDefinition builder = RecipeDefinition.recipe()
                        .recipeDefinition(RecipeDefinition.Option.id(recipeId(recipe, fuel, fuelAmount)))
                        .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.BLAST_FURNACE))
                        .recipeDefinition(RecipeDefinition.Option.inputItem(
                                recipe.inputA(),
                                recipe.inputAAmount()
                        ));

                if (recipe.hasInputB()) {
                    builder.recipeDefinition(RecipeDefinition.Option.inputItem(
                            recipe.inputB(),
                            recipe.inputBAmount()
                    ));
                }

                builder
                        .recipeDefinition(RecipeDefinition.Option.inputItem(fuel.item(), fuelAmount))
                        .recipeDefinition(RecipeDefinition.Option.outputItem(
                                recipe.output(),
                                recipe.outputAmount()
                        ))
                        .recipeDefinition(RecipeDefinition.Option.duration(recipe.duration()))
                        .save(output);
            }
        }
    }

    private static int fuelAmount(BlastRecipe recipe, Fuel fuel) {
        long amount = Math.round(recipe.duration() / (double) fuel.burnDuration());
        return (int) Math.max(1, amount);
    }

    private static String recipeId(BlastRecipe recipe, Fuel fuel, int fuelAmount) {
        String id = path(recipe.output())
                + "_x" + recipe.outputAmount()
                + "_from_"
                + path(recipe.inputA())
                + "_x" + recipe.inputAAmount();

        if (recipe.hasInputB()) {
            id += "_and_"
                    + path(recipe.inputB())
                    + "_x" + recipe.inputBAmount();
        }

        return id
                + "_with_"
                + path(fuel.item())
                + "_x" + fuelAmount;
    }

    private static String path(String id) {
        return id.substring(id.indexOf(':') + 1);
    }

    private record Fuel(
            String item,
            int burnDuration
    ) {
        private Fuel {
            if (item == null || item.isBlank()) {
                throw new IllegalArgumentException("Fuel item cannot be empty");
            }

            if (burnDuration < 1) {
                throw new IllegalArgumentException("Fuel burn duration must be 1 or higher");
            }
        }
    }

    private record BlastRecipe(
            String inputA,
            int inputAAmount,
            String inputB,
            int inputBAmount,
            String output,
            int outputAmount,
            int duration
    ) {
        // 1 input: input, input amount, output, output amount, duration
        private BlastRecipe(
                String input,
                int inputAmount,
                String output,
                int outputAmount,
                int duration
        ) {
            this(input, inputAmount, "", 0, output, outputAmount, duration);
        }

        // 2 inputs: input A, amount A, input B, amount B, output, output amount, duration
        private BlastRecipe(
                String inputA,
                int inputAAmount,
                String inputB,
                int inputBAmount,
                String output,
                int outputAmount,
                int duration
        ) {
            this.inputA = inputA;
            this.inputAAmount = inputAAmount;
            this.inputB = inputB;
            this.inputBAmount = inputBAmount;
            this.output = output;
            this.outputAmount = outputAmount;
            this.duration = duration;

            if (inputA == null || inputA.isBlank()) {
                throw new IllegalArgumentException("Input A cannot be empty");
            }

            if (inputAAmount < 1) {
                throw new IllegalArgumentException("Input A amount must be 1 or higher");
            }

            if (hasInputB() && inputBAmount < 1) {
                throw new IllegalArgumentException("Input B amount must be 1 or higher");
            }

            if (!hasInputB() && inputBAmount != 0) {
                throw new IllegalArgumentException("Input B amount must be 0 when input B is empty");
            }

            if (output == null || output.isBlank()) {
                throw new IllegalArgumentException("Output cannot be empty");
            }

            if (outputAmount < 1) {
                throw new IllegalArgumentException("Output amount must be 1 or higher");
            }

            if (duration < 1) {
                throw new IllegalArgumentException("Duration must be 1 or higher");
            }
        }

        private boolean hasInputB() {
            return inputB != null && !inputB.isBlank();
        }
    }
}
