package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.resources.ResourceLocation;

public final class MaterialRecipeHelper {
    private MaterialRecipeHelper() {
    }

    public static boolean hasItems(IndustrialMaterial material, MaterialPart... parts) {
        for (MaterialPart part : parts) {
            if (!part.isItem() && !part.isBlock()) {
                return false;
            }
            if (!material.has(part)) {
                return false;
            }
        }
        return true;
    }

    public static String itemId(IndustrialMaterial material, MaterialPart part) {
        ResourceLocation location = material.hasExistingPart(part)
                ? material.existingPart(part)
                : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, part.registryName(material));
        return location.toString();
    }
}
