package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.material.MaterialTextures;
import net.mads.createexpansion.registry.FluidRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;

public class MaterialItemModelProvider extends ItemModelProvider {
    public MaterialItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (!part.isItem()) {
                    continue;
                }

                String name = part.registryName(material);
                Optional<ResourceLocation> model = MaterialTextures.itemModel(material, part);
                if (model.isEmpty()) {
                    continue;
                }

                getBuilder(name).parent(new ModelFile.UncheckedModelFile(model.get()));
            }
        }

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.allFluids()) {
            getBuilder(fluid.definition().bucketName())
                    .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket_drip")))
                    .customLoader(DynamicFluidContainerModelBuilder::begin)
                    .fluid(fluid.source().get())
                    .flipGas(fluid.definition().isGas())
                    .applyFluidLuminosity(fluid.definition().lightLevel() > 0);
        }
    }
}
