package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.material.MaterialTextures;
import net.minecraft.resources.ResourceLocation;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;

public class MaterialBlockStateProvider extends BlockStateProvider {
    public MaterialBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (!part.isBlock()) {
                    continue;
                }

                Block block = BlockRegistry.getMaterialBlock(material, part).get();
                Optional<ResourceLocation> model = MaterialTextures.blockModel(material, part);
                if (model.isEmpty()) {
                    continue;
                }

                ModelFile materialBlockModel = switch (part) {
                    case ORE,
                         DEEPSLATE_ORE,
                         DIORITE_ORE,
                         ANDESITE_ORE,
                         GRANITE_ORE,
                         TUFF_ORE,
                         NETHERRACK_ORE,
                         BLACKSTONE_ORE,
                         END_STONE_ORE -> oreBlockModel(material, part);
                    default -> models().withExistingParent(part.registryName(material), model.get());
                };
                simpleBlockWithItem(block, materialBlockModel);
            }
        }
    }

    private BlockModelBuilder oreBlockModel(IndustrialMaterial material, MaterialPart part) {
        String name = part.registryName(material);
        ResourceLocation oreTexture = MaterialTextures.blockTexture(material, part).orElseThrow();
        ResourceLocation oreOverlayTexture = MaterialTextures.blockOverlayTexture(material, part).orElse(oreTexture);
        ResourceLocation baseTexture = baseStoneTexture(part);

        BlockModelBuilder baseStone = models().nested()
                .parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("block/cube_all")))
                .texture("all", baseTexture)
                .renderType("minecraft:solid");

        BlockModelBuilder oreTextureModel = models().nested()
                .parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("layer0", oreTexture)
                .texture("layer1", oreOverlayTexture)
                .texture("particle", oreTexture)
                .renderType("minecraft:cutout");
        fullCube(oreTextureModel, "#layer0", 0);
        fullCube(oreTextureModel, "#layer1", 1);

        BlockModelBuilder model = models().getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("block/block")))
                .texture("particle", baseTexture);
        model.customLoader(CompositeModelBuilder::begin)
                .child("base_stone", baseStone)
                .child("ore_texture", oreTextureModel)
                .itemRenderOrder("base_stone", "ore_texture");
        return model;
    }

    private static void fullCube(BlockModelBuilder model, String texture, int tintIndex) {
        model.element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .allFaces((direction, face) -> face.texture(texture).cullface(direction).tintindex(tintIndex));
    }

    private static ResourceLocation baseStoneTexture(MaterialPart part) {
        return switch (part) {
            case ORE -> ResourceLocation.withDefaultNamespace("block/stone");
            case DEEPSLATE_ORE -> ResourceLocation.withDefaultNamespace("block/deepslate");
            case DIORITE_ORE -> ResourceLocation.withDefaultNamespace("block/diorite");
            case ANDESITE_ORE -> ResourceLocation.withDefaultNamespace("block/andesite");
            case GRANITE_ORE -> ResourceLocation.withDefaultNamespace("block/granite");
            case TUFF_ORE -> ResourceLocation.withDefaultNamespace("block/tuff");
            case NETHERRACK_ORE -> ResourceLocation.withDefaultNamespace("block/netherrack");
            case BLACKSTONE_ORE -> ResourceLocation.withDefaultNamespace("block/blackstone");
            case END_STONE_ORE -> ResourceLocation.withDefaultNamespace("block/end_stone");
            default -> throw new IllegalArgumentException("Expected an ore block part, got " + part);
        };
    }
}
