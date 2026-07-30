package net.mads.createexpansion.client;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlockVariant;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.item.SimpleItemDefinition;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(
        modid = CreateExpansion.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ClientColorHandlers {

    private ClientColorHandlers() {
    }

    /**
     * Registrerer farge for blokker plassert i verden.
     */
    @SubscribeEvent
    public static void registerBlockColors(
            RegisterColorHandlersEvent.Block event
    ) {
        for (SimpleBlockDefinition definition : SimpleBlocks.ALL) {
            if (!definition.hasColor()) {
                continue;
            }

            int color = definition.blockColor();

            event.register(
                    (state, level, position, tintIndex) ->
                            tintIndex == 0
                                    ? color
                                    : 0xFFFFFFFF,
                    BlockRegistry
                            .getSimpleBlock(definition.id())
                            .get()
            );

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                Block variantBlock = BlockRegistry
                        .getSimpleBlockVariant(
                                definition.id(),
                                variant
                        )
                        .get();

                event.register(
                        (state, level, position, tintIndex) ->
                                tintIndex == 0
                                        ? color
                                        : 0xFFFFFFFF,
                        variantBlock
                );
            }
        }
    }

    /**
     * Registrerer farge for items i inventory, JEI,
     * creative tab og når spilleren holder dem.
     */
    @SubscribeEvent
    public static void registerItemColors(
            RegisterColorHandlersEvent.Item event
    ) {
        registerSimpleItemColors(event);
        registerSimpleBlockItemColors(event);
    }

    private static void registerSimpleItemColors(
            RegisterColorHandlersEvent.Item event
    ) {
        for (SimpleItemDefinition definition : SimpleItems.ALL) {
            if (!definition.hasColor()) {
                continue;
            }

            int color = definition.itemColor();

            event.register(
                    (stack, tintIndex) ->
                            tintIndex == 0
                                    ? color
                                    : 0xFFFFFFFF,
                    ItemRegistry
                            .getSimpleItem(definition.id())
                            .get()
            );
        }
    }

    private static void registerSimpleBlockItemColors(
            RegisterColorHandlersEvent.Item event
    ) {
        for (SimpleBlockDefinition definition : SimpleBlocks.ALL) {
            if (!definition.hasColor()) {
                continue;
            }

            int color = definition.blockColor();

            event.register(
                    (stack, tintIndex) ->
                            tintIndex == 0
                                    ? color
                                    : 0xFFFFFFFF,
                    ItemRegistry
                            .getSimpleBlockItem(definition.id())
                            .get()
            );

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                event.register(
                        (stack, tintIndex) ->
                                tintIndex == 0
                                        ? color
                                        : 0xFFFFFFFF,
                        ItemRegistry
                                .getSimpleBlockVariantItem(
                                        definition.id(),
                                        variant
                                )
                                .get()
                );
            }
        }
    }
}