package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.item.SimpleItemDefinition;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.material.MaterialTextures;
import net.mads.createexpansion.registry.FluidRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Optional;

public class MaterialItemModelProvider extends ItemModelProvider {

    private static final ResourceLocation FIRED_BUCKET_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    CreateExpansion.MOD_ID,
                    "item/standalone/bucket_brick"
            );

    private final ExistingFileHelper existingFileHelper;

    public MaterialItemModelProvider(
            PackOutput output,
            ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                CreateExpansion.MOD_ID,
                existingFileHelper
        );

        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void registerModels() {
        registerMaterialItemModels();
        registerFluidBucketModels();
        registerSimpleItemModels();
    }

    private void registerMaterialItemModels() {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (!part.isItem()) {
                    continue;
                }

                String name = part.registryName(material);

                if (material.hasCustomPartTexture(part)) {
                    singleTexture(
                            name,
                            ResourceLocation.withDefaultNamespace(
                                    "item/generated"
                            ),
                            "layer0",
                            material.customPartTexture(part)
                    );

                    continue;
                }

                Optional<ResourceLocation> model =
                        MaterialTextures.itemModel(
                                material,
                                part
                        );

                if (model.isEmpty()) {
                    continue;
                }

                getBuilder(name).parent(
                        new ModelFile.UncheckedModelFile(
                                model.get()
                        )
                );
            }
        }
    }

    private void registerFluidBucketModels() {
        singleTexture(
                "fired_bucket",
                ResourceLocation.withDefaultNamespace(
                        "item/generated"
                ),
                "layer0",
                FIRED_BUCKET_TEXTURE
        );

        firedVanillaBucketModel(
                "fired_water_bucket",
                Fluids.WATER
        );

        firedVanillaBucketModel(
                "fired_lava_bucket",
                Fluids.LAVA
        );

        for (FluidRegistry.RegisteredFluid fluid
                : FluidRegistry.allFluids()) {

            normalFluidBucketModel(fluid);
            firedFluidBucketModel(fluid);
        }
    }

    private void normalFluidBucketModel(
            FluidRegistry.RegisteredFluid fluid
    ) {
        getBuilder(
                fluid.definition().bucketName()
        )
                .parent(
                        new ModelFile.UncheckedModelFile(
                                ResourceLocation.fromNamespaceAndPath(
                                        "neoforge",
                                        "item/bucket_drip"
                                )
                        )
                )
                .customLoader(
                        DynamicFluidContainerModelBuilder::begin
                )
                .fluid(
                        fluid.source().get()
                )
                .flipGas(
                        fluid.definition().isGas()
                )
                .applyFluidLuminosity(
                        fluid.definition().lightLevel() > 0
                );
    }

    private void firedFluidBucketModel(
            FluidRegistry.RegisteredFluid fluid
    ) {
        getBuilder(
                fluid.firedBucket().getId().getPath()
        )
                .parent(
                        new ModelFile.UncheckedModelFile(
                                ResourceLocation.fromNamespaceAndPath(
                                        "neoforge",
                                        "item/bucket_drip"
                                )
                        )
                )
                .texture(
                        "base",
                        FIRED_BUCKET_TEXTURE
                )
                .customLoader(
                        DynamicFluidContainerModelBuilder::begin
                )
                .fluid(
                        fluid.source().get()
                )
                .flipGas(
                        fluid.definition().isGas()
                )
                .applyFluidLuminosity(
                        fluid.definition().lightLevel() > 0
                );
    }

    private void firedVanillaBucketModel(
            String id,
            Fluid fluid
    ) {
        getBuilder(id)
                .parent(
                        new ModelFile.UncheckedModelFile(
                                ResourceLocation.fromNamespaceAndPath(
                                        "neoforge",
                                        "item/bucket_drip"
                                )
                        )
                )
                .texture(
                        "base",
                        FIRED_BUCKET_TEXTURE
                )
                .customLoader(
                        DynamicFluidContainerModelBuilder::begin
                )
                .fluid(fluid);
    }

    private void registerSimpleItemModels() {
        for (SimpleItemDefinition definition : SimpleItems.ALL) {
            ResourceLocation texture =
                    resolveSimpleItemTexture(definition);

            singleTexture(
                    definition.id(),
                    ResourceLocation.withDefaultNamespace(
                            "item/generated"
                    ),
                    "layer0",
                    texture
            );
        }
    }

    private ResourceLocation resolveSimpleItemTexture(
            SimpleItemDefinition definition
    ) {
        String texturePath = definition.texture();

        if (texturePath.equals(definition.id())) {
            return findSimpleItemTexture(
                    definition.id()
            );
        }

        if (texturePath.contains(":")) {
            ResourceLocation texture =
                    ResourceLocation.tryParse(texturePath);

            if (texture == null) {
                throw new IllegalStateException(
                        "Invalid texture for simple item '"
                                + definition.id()
                                + "': "
                                + texturePath
                );
            }

            return texture;
        }

        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                texturePath
        );
    }

    private ResourceLocation findSimpleItemTexture(
            String itemId
    ) {
        for (String folder : SIMPLE_ITEM_TEXTURE_FOLDERS) {
            ResourceLocation texture =
                    ResourceLocation.fromNamespaceAndPath(
                            CreateExpansion.MOD_ID,
                            folder + "/" + itemId
                    );

            boolean exists = existingFileHelper.exists(
                    texture,
                    PackType.CLIENT_RESOURCES,
                    ".png",
                    "textures"
            );

            if (exists) {
                return texture;
            }
        }

        throw new IllegalStateException(
                "Could not find texture for simple item '"
                        + itemId
                        + "'. Expected a file named '"
                        + itemId
                        + ".png' in one of these folders: "
                        + SIMPLE_ITEM_TEXTURE_FOLDERS
        );
    }

    private static final List<String>
            SIMPLE_ITEM_TEXTURE_FOLDERS = List.of(
            "item/material_sets/mold",
            "item/standalone/materials",
            "item/material_sets/dull",
            "block/machines/machines/kinetic/sifter",
            "item/standalone"
    );
}