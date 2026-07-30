package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class WireDrawingRecipes {
    private WireDrawingRecipes() {
    }

    public static WireDrawingRecipeBuilder recipe(String id) {
        return new WireDrawingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/iron_ingot_to_chain")
                .inputItem("minecraft:iron_ingot")
                .outputItem("minecraft:chain")
                .duration(100)
                .maxRpm(128)
                .save(output);

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (MaterialRecipeHelper.hasItems(material, MaterialPart.PLATE, MaterialPart.WIRE)) {
                recipe("materials/" + material.id() + "_plate_to_wire")
                        .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.PLATE))
                        .outputItem(MaterialRecipeHelper.itemId(material, MaterialPart.WIRE))
                        .duration(1280)
                        .maxRpm(128)
                        .save(output);
            }

            if (MaterialRecipeHelper.hasItems(material, MaterialPart.WIRE, MaterialPart.FINE_WIRE)) {
                recipe("materials/" + material.id() + "_wire_to_fine_wire")
                        .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.WIRE))
                        .extraInputItem(CreateExpansion.MOD_ID + ":spool")
                        .outputItem(MaterialRecipeHelper.itemId(material, MaterialPart.FINE_WIRE))
                        .duration(1280)
                        .maxRpm(128)
                        .save(output);
            }
        }
    }
}
