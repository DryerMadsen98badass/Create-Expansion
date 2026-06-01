package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.FluidRegistry;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, CreateExpansion.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.create_expansion", "Create Expansion");
        add("itemGroup.create_expansion.industry", "Create Expansion: Industry");
        add("itemGroup.create_expansion.materials", "Create Expansion: Materials");

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (part.isFluid()) {
                    continue;
                }

                String type = part.isBlock() ? "block" : "item";
                add(type + "." + CreateExpansion.MOD_ID + "." + part.registryName(material), part.readableName(material));
            }
        }

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.allFluids()) {
            add("fluid_type." + CreateExpansion.MOD_ID + "." + fluid.definition().registryName(), fluid.definition().localizedName());
            add("fluid." + CreateExpansion.MOD_ID + "." + fluid.definition().registryName(), fluid.definition().localizedName());
            add("item." + CreateExpansion.MOD_ID + "." + fluid.definition().bucketName(), fluid.definition().bucketDisplayName());
        }
    }
}
