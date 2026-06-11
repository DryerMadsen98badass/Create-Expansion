package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MaterialBlockTagProvider extends BlockTagsProvider {
    private static final TagKey<Block> C_ORES = cTag("ores");
    private static final TagKey<Block> C_ORE_RATES_SINGULAR = cTag("ore_rates/singular");

    private static final Map<MaterialPart, TagKey<Block>> ORE_GROUND_TAGS = Map.of(
            MaterialPart.ORE, cTag("ores_in_ground/stone"),
            MaterialPart.DEEPSLATE_ORE, cTag("ores_in_ground/deepslate"),
            MaterialPart.DIORITE_ORE, cTag("ores_in_ground/diorite"),
            MaterialPart.ANDESITE_ORE, cTag("ores_in_ground/andesite"),
            MaterialPart.GRANITE_ORE, cTag("ores_in_ground/granite"),
            MaterialPart.TUFF_ORE, cTag("ores_in_ground/tuff"),
            MaterialPart.NETHERRACK_ORE, cTag("ores_in_ground/netherrack"),
            MaterialPart.BLACKSTONE_ORE, cTag("ores_in_ground/blackstone"),
            MaterialPart.END_STONE_ORE, cTag("ores_in_ground/end_stone")
    );

    public MaterialBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (Map.Entry<MaterialPart, TagKey<Block>> oreGroundTag : ORE_GROUND_TAGS.entrySet()) {
                MaterialPart part = oreGroundTag.getKey();
                if (!material.has(part)) {
                    continue;
                }

                DeferredHolder<Block, ? extends Block> block = BlockRegistry.MATERIAL_BLOCKS.get(material.id()).get(part);

                if (block != null) {
                    tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
                    tag(BlockTags.NEEDS_STONE_TOOL).add(block.get());
                    tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).add(block.get());
                    tag(BlockTags.INCORRECT_FOR_GOLD_TOOL).add(block.get());
                    tag(C_ORES).add(block.get());
                    tag(C_ORE_RATES_SINGULAR).add(block.get());
                    tag(cTag("ores/" + material.id())).add(block.get());
                    tag(oreGroundTag.getValue()).add(block.get());
                }

                if (material.hasExistingPart(part)) {
                    ResourceLocation existingBlockId = material.existingPart(part);
                    tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(existingBlockId);
                    tag(BlockTags.NEEDS_STONE_TOOL).addOptional(existingBlockId);
                    tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).addOptional(existingBlockId);
                    tag(BlockTags.INCORRECT_FOR_GOLD_TOOL).addOptional(existingBlockId);
                    tag(C_ORES).addOptional(existingBlockId);
                    tag(C_ORE_RATES_SINGULAR).addOptional(existingBlockId);
                    tag(cTag("ores/" + material.id())).addOptional(existingBlockId);
                    tag(oreGroundTag.getValue()).addOptional(existingBlockId);
                }
            }
        }
    }

    private static TagKey<Block> cTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
