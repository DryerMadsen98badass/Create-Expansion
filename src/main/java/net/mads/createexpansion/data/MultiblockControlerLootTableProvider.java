package net.mads.createexpansion.data;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class MultiblockControlerLootTableProvider
        extends LootTableProvider {

    public MultiblockControlerLootTableProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(
                output,
                Set.of(),
                List.of(
                        new SubProviderEntry(
                                MultiblockControllerBlockLoot::new,
                                LootContextParamSets.BLOCK
                        )
                ),
                lookupProvider
        );
    }

    private static final class MultiblockControllerBlockLoot
            extends BlockLootSubProvider {

        private MultiblockControllerBlockLoot(
                HolderLookup.Provider lookupProvider
        ) {
            super(
                    Set.of(),
                    FeatureFlags.REGISTRY.allFlags(),
                    lookupProvider
            );
        }

        @Override
        protected void generate() {
            for (MultiblockControllerDefinition controller :
                    MultiblockControllerDefinition.all()) {

                Block block = BuiltInRegistries.BLOCK.get(
                        controller.id()
                );

                if (block == Blocks.AIR) {
                    throw new IllegalStateException(
                            "No registered controller block was found for "
                                    + controller.id()
                    );
                }

                dropSelf(block);
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return MultiblockControllerDefinition.all()
                    .stream()
                    .map(controller ->
                            BuiltInRegistries.BLOCK.get(
                                    controller.id()
                            )
                    )
                    .filter(block -> block != Blocks.AIR)
                    .toList();
        }
    }
}