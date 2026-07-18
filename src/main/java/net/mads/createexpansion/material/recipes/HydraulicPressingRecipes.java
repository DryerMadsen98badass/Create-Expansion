package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.hydraulicpress.HydraulicPressingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class HydraulicPressingRecipes {
    private HydraulicPressingRecipes() {}

    public static HydraulicPressingRecipeBuilder recipe(String id) {
        return new HydraulicPressingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (MaterialRecipeHelper.hasItems(material, MaterialPart.INGOT, MaterialPart.PLATE)) {
                recipe("materials/" + material.id() + "_ingot_to_plate")
                        .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.INGOT))
                        .outputItem(MaterialRecipeHelper.itemId(material, MaterialPart.PLATE))
                        .blows(3)
                        .save(output);
            }
        }
    }
}
