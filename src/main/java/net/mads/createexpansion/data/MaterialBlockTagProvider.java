package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.ActiveBlockDefinition;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.block.MiningTier;
import net.mads.createexpansion.block.MiningTool;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlockVariant;
import net.mads.createexpansion.machine.MachineDefinition;
import net.mads.createexpansion.machine.SingleBlockMachineInstance;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.transport.FluidTransportRegistrations;
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MaterialBlockTagProvider
        extends BlockTagsProvider {

    private static final TagKey<Block> C_ORES =
            cTag("ores");

    private static final TagKey<Block>
            C_ORE_RATES_SINGULAR =
            cTag("ore_rates/singular");

    private static final Map<
            MaterialPart,
            TagKey<Block>
            > ORE_GROUND_TAGS = Map.of(
            MaterialPart.ORE,
            cTag("ores_in_ground/stone"),

            MaterialPart.DEEPSLATE_ORE,
            cTag("ores_in_ground/deepslate"),

            MaterialPart.DIORITE_ORE,
            cTag("ores_in_ground/diorite"),

            MaterialPart.ANDESITE_ORE,
            cTag("ores_in_ground/andesite"),

            MaterialPart.GRANITE_ORE,
            cTag("ores_in_ground/granite"),

            MaterialPart.TUFF_ORE,
            cTag("ores_in_ground/tuff"),

            MaterialPart.NETHERRACK_ORE,
            cTag("ores_in_ground/netherrack"),

            MaterialPart.BLACKSTONE_ORE,
            cTag("ores_in_ground/blackstone"),

            MaterialPart.END_STONE_ORE,
            cTag("ores_in_ground/end_stone")
    );

    public MaterialBlockTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider>
                    lookupProvider,
            ExistingFileHelper existingFileHelper
    ) {
        super(
                output,
                lookupProvider,
                CreateExpansion.MOD_ID,
                existingFileHelper
        );
    }

    @Override
    protected void addTags(
            HolderLookup.Provider provider
    ) {
        addSimpleBlockMiningTags();
        addSimpleBlockConnectionTags();
        addActiveBlockMiningTags();
        addFoundryMiningTags();
        addMultiblockControllerMiningTags();
        addSingleBlockMachineMiningTags();
        addFluidTransportMiningTags();

        for (IndustrialMaterial material
                : IndustrialMaterials.ALL) {

            for (Map.Entry<
                    MaterialPart,
                    TagKey<Block>
                    > oreGroundTag
                    : ORE_GROUND_TAGS.entrySet()) {

                MaterialPart part =
                        oreGroundTag.getKey();

                if (!material.has(part)) {
                    continue;
                }

                DeferredHolder<
                        Block,
                        ? extends Block
                        > block =
                        BlockRegistry.MATERIAL_BLOCKS
                                .get(material.id())
                                .get(part);

                if (block != null) {
                    addMiningTags(
                            block.get(),
                            Set.of(
                                    MiningTool.PICKAXE
                            ),
                            MiningTier.STONE
                    );

                    addOreTags(
                            block.get(),
                            material,
                            oreGroundTag.getValue()
                    );
                }

                if (material.hasExistingPart(part)) {
                    ResourceLocation existingBlockId =
                            material.existingPart(part);

                    addOptionalStoneMiningTags(
                            existingBlockId
                    );

                    tag(C_ORES)
                            .addOptional(existingBlockId);

                    tag(C_ORE_RATES_SINGULAR)
                            .addOptional(existingBlockId);

                    tag(
                            cTag(
                                    "ores/"
                                            + material.id()
                            )
                    ).addOptional(existingBlockId);

                    tag(oreGroundTag.getValue())
                            .addOptional(existingBlockId);
                }
            }
        }
    }

    private void addSimpleBlockConnectionTags() {
        for (SimpleBlockDefinition definition
                : SimpleBlocks.ALL) {

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                Block block = BlockRegistry
                        .getSimpleBlockVariant(
                                definition.id(),
                                variant
                        )
                        .get();

                switch (variant) {
                    case WALL -> tag(BlockTags.WALLS)
                            .add(block);

                    case FENCE -> tag(BlockTags.FENCES)
                            .add(block);

                    case FENCE_GATE -> tag(BlockTags.FENCE_GATES)
                            .add(block);

                    default -> {
                    }
                }
            }
        }
    }


    private void addSimpleBlockMiningTags() {
        for (SimpleBlockDefinition definition
                : SimpleBlocks.ALL) {

            addMiningTags(
                    BlockRegistry
                            .getSimpleBlock(
                                    definition.id()
                            )
                            .get(),
                    definition.miningTools(),
                    definition.miningTier()
            );

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                addMiningTags(
                        BlockRegistry
                                .getSimpleBlockVariant(
                                        definition.id(),
                                        variant
                                )
                                .get(),
                        definition.miningTools(),
                        definition.miningTier()
                );
            }
        }
    }

    private void addActiveBlockMiningTags() {
        for (ActiveBlockDefinition definition
                : SimpleBlocks.ACTIVE) {

            addMiningTags(
                    BlockRegistry
                            .getActiveBlock(
                                    definition.id()
                            )
                            .get(),
                    definition.miningTools(),
                    definition.miningTier()
            );
        }
    }

    private void addFoundryMiningTags() {
        addPickaxeStoneTags(
                BlockRegistry.FOUNDRY_CASING.get(),
                BlockRegistry.FOUNDRY_CONTROLLER.get(),
                BlockRegistry
                        .CREATIVE_FOUNDRY_CONTROLLER
                        .get(),
                BlockRegistry.FOUNDRY_INPUT_HATCH.get(),
                BlockRegistry.FOUNDRY_OUTPUT_HATCH.get(),
                BlockRegistry.FOUNDRY_INPUT_BUS.get(),
                BlockRegistry.FOUNDRY_DRAIN.get(),
                BlockRegistry.FOUNDRY_MOLD_CASTER.get()
        );
    }

    private void addMultiblockControllerMiningTags() {
        addPickaxeStoneTags(
                BlockRegistry
                        .getAllMultiblockControllers()
                        .stream()
                        .map(DeferredHolder::get)
                .toList()
        );
    }

    private void addSingleBlockMachineMiningTags() {
        for (SingleBlockMachineInstance instance
                : MachineDefinition.INSTANCES) {

            addMiningTags(
                    BlockRegistry
                            .getSingleBlockMachine(
                                    instance.registryName()
                            )
                            .get(),
                    instance.definition().miningTools(),
                    instance.definition().miningTier()
            );
        }
    }

    private void addFluidTransportMiningTags() {
        FluidTransportRegistrations.allBlocks().forEach(registration -> addPickaxeStoneTags(
                registration.pipe().get(),
                registration.glassPipe().get(),
                registration.pump().get(),
                registration.tank().get()
        ));
    }

    private void addPickaxeStoneTags(
            Block... blocks
    ) {
        for (Block block : blocks) {
            addMiningTags(
                    block,
                    Set.of(
                            MiningTool.PICKAXE
                    ),
                    MiningTier.STONE
            );
        }
    }

    private void addPickaxeStoneTags(
            Collection<? extends Block> blocks
    ) {
        for (Block block : blocks) {
            addMiningTags(
                    block,
                    Set.of(
                            MiningTool.PICKAXE
                    ),
                    MiningTier.STONE
            );
        }
    }

    private void addMiningTags(
            Block block,
            Set<MiningTool> tools,
            MiningTier tier
    ) {
        for (MiningTool tool : tools) {
            tag(tool.tag()).add(block);
        }

        addMiningTierTags(
                block,
                tier
        );
    }

    private void addMiningTierTags(
            Block block,
            MiningTier tier
    ) {
        switch (tier) {
            case WOOD -> {
                tag(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                        .add(block);
            }

            case STONE -> {
                tag(BlockTags.NEEDS_STONE_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                        .add(block);
            }

            case IRON -> {
                tag(BlockTags.NEEDS_IRON_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_STONE_TOOL)
                        .add(block);
            }

            case DIAMOND -> {
                tag(BlockTags.NEEDS_DIAMOND_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_STONE_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_IRON_TOOL)
                        .add(block);
            }

            case NETHERITE -> {
                tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_STONE_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_IRON_TOOL)
                        .add(block);

                tag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                        .add(block);
            }
        }
    }

    private void addOptionalStoneMiningTags(
            ResourceLocation blockId
    ) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addOptional(blockId);

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addOptional(blockId);

        tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                .addOptional(blockId);

        tag(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                .addOptional(blockId);
    }

    private void addOreTags(
            Block block,
            IndustrialMaterial material,
            TagKey<Block> groundTag
    ) {
        tag(C_ORES)
                .add(block);

        tag(C_ORE_RATES_SINGULAR)
                .add(block);

        tag(
                cTag(
                        "ores/"
                                + material.id()
                )
        ).add(block);

        tag(groundTag)
                .add(block);
    }

    private static TagKey<Block> cTag(
            String path
    ) {
        return TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(
                        "c",
                        path
                )
        );
    }
}
