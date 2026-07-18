package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.rolling.RollingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class RollingRecipes {
    private RollingRecipes() {
    }

    public static RollingRecipeBuilder recipe(String id) {
        return new RollingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (MaterialRecipeHelper.hasItems(material, MaterialPart.PLATE, MaterialPart.FOIL)) {
                recipe("materials/" + material.id() + "_plate_to_foil")
                        .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.PLATE))
                        .outputItem(MaterialRecipeHelper.itemId(material, MaterialPart.FOIL))
                        .duration(80)
                        .maxRpm(32)
                        .save(output);
            }
        }
    }
}
