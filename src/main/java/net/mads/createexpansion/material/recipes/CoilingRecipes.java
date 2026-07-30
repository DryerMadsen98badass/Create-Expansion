package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class CoilingRecipes {
    private CoilingRecipes() {
    }

    public static CoilingRecipeBuilder recipe(String id) {
        return new CoilingRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (MaterialRecipeHelper.hasItems(material, MaterialPart.WIRE, MaterialPart.SPRING)) {
                recipe("materials/" + material.id() + "_wire_to_spring")
                        .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.WIRE))
                        .outputItem(MaterialRecipeHelper.itemId(material, MaterialPart.SPRING))
                        .duration(320)
                        .maxRpm(64)
                        .save(output);
            }
        }
    }
}
