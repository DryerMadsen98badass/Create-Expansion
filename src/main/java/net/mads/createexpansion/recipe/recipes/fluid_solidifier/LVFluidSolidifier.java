package net.mads.createexpansion.recipe.recipes.fluid_solidifier;

import net.mads.createexpansion.fluid.IndustrialFluidLookup;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.material.recipes.FoundryCastingRecipes;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.Map;

public final class LVFluidSolidifier {

    private static final int MAX_MELTING_POINT = 1000;
    private static final int MB_PER_INGOT = 144;
    private static final int TICKS_PER_INGOT = 100;

    private LVFluidSolidifier() {
    }

    public static void build(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {



        recipe("rubber_ingot_from_rubber_solution")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:rubber_solution", 144))
                .recipeDefinition(RecipeDefinition.Option.notConsumableInputItemTag("cold_molds/ingot", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:rubber_ingot", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("rubber_plate_from_rubber_solution")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:rubber_solution", 144))
                .recipeDefinition(RecipeDefinition.Option.notConsumableInputItemTag("cold_molds/plate", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:rubber_plate", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("rubber_ingot_from_molten_rubber")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:molten_rubber", 144))
                .recipeDefinition(RecipeDefinition.Option.notConsumableInputItemTag("cold_molds/ingot", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:rubber_ingot", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);
        recipe("rubber_plate_from_molten_rubber")
                .recipeDefinition(RecipeDefinition.Option.inputFluid("create_expansion:molten_rubber", 144))
                .recipeDefinition(RecipeDefinition.Option.notConsumableInputItemTag("cold_molds/plate", 1))
                .recipeDefinition(RecipeDefinition.Option.outputItem("create_expansion:rubber_plate", 1))
                .recipeDefinition(RecipeDefinition.Option.duration(100))
                .save(output);


        Map<MaterialPart, FoundryCastingRecipes.CastShape> shapes =
                FoundryCastingRecipes.shapes();

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (!canSolidify(material)) {
                continue;
            }

            String moltenFluidId =
                    IndustrialFluidLookup.fluidId(material).toString();

            for (MaterialPart moldPart : MaterialPart.values()) {
                FoundryCastingRecipes.CastShape shape =
                        shapes.get(moldPart);

                if (shape == null) {
                    continue;
                }

                MaterialPart outputPart = shape.cooledPart();

                if (!material.has(outputPart)) {
                    continue;
                }

                RecipeDefinition definition = recipe(
                        material.id() + "/" + outputPart.id()
                )
                        .recipeDefinition(
                                RecipeDefinition.Option.inputFluid(
                                        moltenFluidId,
                                        shape.amountMb()
                                )
                        )
                        .recipeDefinition(
                                RecipeDefinition.Option.notConsumableInputItemTag(
                                        coldMoldTag(shape),
                                        1
                                )
                        )
                        .recipeDefinition(
                                RecipeDefinition.Option.duration(
                                        durationFor(shape.amountMb())
                                )
                        )
                        .recipeDefinition(
                                RecipeDefinition.Option.tier(
                                        MachineTier.LV
                                )
                        );

                if (material.hasExistingPart(outputPart)) {
                    definition.recipeDefinition(
                            RecipeDefinition.Option.outputItem(
                                    material.existingPart(outputPart).toString(),
                                    1
                            )
                    );
                } else {
                    var item = ItemRegistry.getMaterialItem(
                            material,
                            outputPart
                    );

                    if (item == null) {
                        continue;
                    }

                    definition.recipeDefinition(
                            RecipeDefinition.Option.outputItem(
                                    item.get(),
                                    1
                            )
                    );
                }

                definition.save(output);
            }
        }

    }

    private static boolean canSolidify(
            IndustrialMaterial material
    ) {
        return material.has(MaterialPart.MOLTEN_FLUID)
                && material.meltingPoint() <= MAX_MELTING_POINT;
    }

    private static String coldMoldTag(
            FoundryCastingRecipes.CastShape shape
    ) {
        String moldId = shape.moldPart().id();
        String prefix = "cast_";
        String suffix = "_mold";

        if (moldId.startsWith(prefix)) {
            moldId = moldId.substring(prefix.length());
        }

        if (moldId.endsWith(suffix)) {
            moldId = moldId.substring(
                    0,
                    moldId.length() - suffix.length()
            );
        }

        return "create_expansion:cold_molds/" + moldId;
    }

    private static int durationFor(
            int fluidAmount
    ) {
        return Math.max(
                1,
                Math.ceilDiv(
                        fluidAmount * TICKS_PER_INGOT,
                        MB_PER_INGOT
                )
        );
    }

    private static RecipeDefinition recipe(
            String id
    ) {
        return RecipeDefinition.recipe()
                .recipeDefinition(
                        RecipeDefinition.Option.id(
                                "fluid_solidifier/" + id
                        )
                )
                .recipeDefinition(
                        RecipeDefinition.Option.recipeType(
                                CERecipeTypes.FLUID_SOLIDIFIER
                        )
                )
                .recipeDefinition(
                        RecipeDefinition.Option.duration(
                                TICKS_PER_INGOT
                        )
                )
                .recipeDefinition(
                        RecipeDefinition.Option.tier(
                                MachineTier.LV
                        )
                );
    }
}
