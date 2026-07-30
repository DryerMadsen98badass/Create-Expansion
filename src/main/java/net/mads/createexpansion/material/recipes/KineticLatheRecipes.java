package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.lathe.TurningRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

public final class KineticLatheRecipes {
    private KineticLatheRecipes() {
    }

    public static TurningRecipeBuilder recipe(String id) {
        return new TurningRecipeBuilder(id);
    }

    public static void build(RecipeOutput output, HolderLookup.Provider holderLookup) {
        recipe("test/stick_to_torch")
                .inputItem("minecraft:stick")
                .outputItem("minecraft:torch")
                .duration(100)
                .minRpm(32)
                .save(output);

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (MaterialRecipeHelper.hasItems(material, MaterialPart.ROD, MaterialPart.BOLT)) {
                recipe("materials/" + material.id() + "_rod_to_bolt")
                        .inputItem(MaterialRecipeHelper.itemId(material, MaterialPart.ROD))
                        .outputItem(MaterialRecipeHelper.itemId(material, MaterialPart.BOLT), 2)
                        .duration(80)
                        .minRpm(64)
                        .save(output);
            }
        }
    }
}
