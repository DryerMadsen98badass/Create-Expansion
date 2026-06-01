package net.mads.createexpansion.client;

import net.mads.createexpansion.material.MaterialBlock;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.FluidRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Client-side setup goes here
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        Item[] materialItems = ItemRegistry.getAllMaterialItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] fluidBuckets = FluidRegistry.getAllBucketItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);

        event.register((stack, tintIndex) -> {
            if (!isMaterialTintLayer(tintIndex)) {
                return -1;
            }

            Item item = stack.getItem();

            if (item instanceof MaterialItem materialItem) {
                int color = materialItem.material().color();
                if (usesDarkerCastTint(materialItem.part())) {
                    color = darken(color, 0.65F);
                }

                return opaque(color);
            }

            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MaterialBlock materialBlock) {
                return opaque(materialBlock.material().color());
            }

            return -1;
        }, materialItems);

        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) {
                return -1;
            }

            for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.allFluids()) {
                if (stack.is(fluid.bucket().get())) {
                    return fluid.type().get().color();
                }
            }

            return new DynamicFluidContainerModel.Colors().getColor(stack, tintIndex);
        }, fluidBuckets);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        Block[] materialBlocks = BlockRegistry.getAllMaterialBlocks().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);

        event.register((state, level, pos, tintIndex) -> {
            if (!isMaterialTintLayer(tintIndex)) {
                return -1;
            }

            if (state.getBlock() instanceof MaterialBlock materialBlock) {
                return opaque(materialBlock.material().color());
            }

            return -1;
        }, materialBlocks);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.allFluids()) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public int getTintColor() {
                    return fluid.type().get().color();
                }

                @Override
                public net.minecraft.resources.ResourceLocation getStillTexture() {
                    return fluid.type().get().texture();
                }

                @Override
                public net.minecraft.resources.ResourceLocation getFlowingTexture() {
                    return fluid.type().get().texture();
                }
            }, fluid.type());
        }
    }

    private static boolean isMaterialTintLayer(int tintIndex) {
        return tintIndex == 0 || tintIndex == 1;
    }

    private static int opaque(int color) {
        return 0xFF000000 | color;
    }

    private static boolean usesDarkerCastTint(MaterialPart part) {
        return switch (part) {
            case CAST_NUGGET,
                 CAST_BLOCK,
                 CAST_PLATE,
                 CAST_ROD,
                 CAST_LONG_ROD,
                 CAST_BOLT,
                 CAST_SCREW,
                 CAST_RING,
                 CAST_SMALL_RING,
                 CAST_LARGE_RING,
                 CAST_GEAR,
                 CAST_SMALL_GEAR,
                 CAST_BEARING_BALL,
                 CAST_BEARING,
                 CAST_ROTOR,
                 HOT_CAST_NUGGET_MOLD,
                 HOT_CAST_BLOCK_MOLD,
                 HOT_CAST_BEARING_BALL_MOLD,
                 HOT_CAST_ROTOR_MOLD,
                 HOT_CAST_INGOT_MOLD,
                 HOT_CAST_PLATE_MOLD,
                 HOT_CAST_ROD_MOLD,
                 HOT_CAST_LONG_ROD_MOLD,
                 HOT_CAST_BOLT_MOLD,
                 HOT_CAST_RING_MOLD,
                 HOT_CAST_SMALL_RING_MOLD,
                 HOT_CAST_LARGE_RING_MOLD,
                 HOT_CAST_GEAR_MOLD,
                 HOT_CAST_SMALL_GEAR_MOLD,
                 HOT_CAST_BEARING_MOLD,
                 HOT_CAST_SCREW_MOLD -> true;
            default -> false;
        };
    }

    private static int darken(int color, float multiplier) {
        int red = (int) (((color >> 16) & 0xFF) * multiplier);
        int green = (int) (((color >> 8) & 0xFF) * multiplier);
        int blue = (int) ((color & 0xFF) * multiplier);
        return (red << 16) | (green << 8) | blue;
    }
}
