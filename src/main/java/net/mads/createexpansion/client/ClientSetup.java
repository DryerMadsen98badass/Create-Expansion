package net.mads.createexpansion.client;

import net.mads.createexpansion.client.screen.MachinePortScreen;
import net.mads.createexpansion.client.screen.MultiblockControllerScreen;
import net.mads.createexpansion.material.MaterialBlock;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.util.ColorUtils;
import net.mads.createexpansion.machine.MachineCasingBlock;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.registry.FluidRegistry;
import net.mads.createexpansion.registry.MenuRegistry;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.Arrays;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.MACHINE_PORT.get(), MachinePortOverlayRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        Item[] materialItems = ItemRegistry.getAllMaterialItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] fluidBuckets = FluidRegistry.getAllBucketItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] machineCasings = ItemRegistry.getAllMachineCasingItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] machinePorts = ItemRegistry.getAllMachinePortItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] staticMachinePorts = ItemRegistry.getAllStaticMachinePortItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] multiblockControllers = ItemRegistry.getAllMultiblockControllerItems().stream()
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
                    color = ColorUtils.darken(color, 0.65F);
                }

                return ColorUtils.opaque(color);
            }

            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MaterialBlock materialBlock) {
                return ColorUtils.opaque(materialBlock.material().color());
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

        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MachineCasingBlock casing) {
                return ColorUtils.opaque(casing.tier().color());
            }

            return -1;
        }, machineCasings);

        event.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MachinePortBlock port) {
                if (tintIndex == 0 && port.usesTint()) {
                    return ColorUtils.opaque(port.tintColor());
                }
                if (tintIndex == 1 && port.hasTier() && portColorable(port)) {
                    return dyeColor(DyeColor.GRAY);
                }
            }

            return -1;
        }, merge(machinePorts, staticMachinePorts));

        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MultiblockControllerBlock controller && controller.usesTint()) {
                return ColorUtils.opaque(controller.tintColor());
            }

            return -1;
        }, multiblockControllers);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        Block[] materialBlocks = BlockRegistry.getAllMaterialBlocks().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] machineCasings = BlockRegistry.getAllMachineCasings().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] machinePorts = BlockRegistry.getAllMachinePorts().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] staticMachinePorts = BlockRegistry.getAllStaticMachinePorts().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] multiblockControllers = BlockRegistry.getAllMultiblockControllers().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);

        event.register((state, level, pos, tintIndex) -> {
            if (!isMaterialTintLayer(tintIndex)) {
                return -1;
            }

            if (state.getBlock() instanceof MaterialBlock materialBlock) {
                return ColorUtils.opaque(materialBlock.material().color());
            }

            return -1;
        }, materialBlocks);

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (state.getBlock() instanceof MachineCasingBlock casing) {
                return ColorUtils.opaque(casing.tier().color());
            }

            return -1;
        }, machineCasings);

        event.register((state, level, pos, tintIndex) -> {
            if (state.getBlock() instanceof MachinePortBlock port) {
                if (tintIndex == 0 && port.usesTint()) {
                    return ColorUtils.opaque(port.tintColor());
                }
                if (tintIndex == 1 && port.hasTier() && level != null && pos != null && level.getBlockEntity(pos) instanceof MachinePortBlockEntity portEntity && portEntity.supportsIoColor()) {
                    return dyeColor(portEntity.ioColor());
                }
            }

            return -1;
        }, merge(machinePorts, staticMachinePorts));

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (state.getBlock() instanceof MultiblockControllerBlock controller && controller.usesTint()) {
                return ColorUtils.opaque(controller.tintColor());
            }

            return -1;
        }, multiblockControllers);
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

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.MACHINE_PORT.get(), MachinePortScreen::new);
        event.register(MenuRegistry.MULTIBLOCK_CONTROLLER.get(), MultiblockControllerScreen::new);
    }

    private static boolean isMaterialTintLayer(int tintIndex) {
        return tintIndex == 0 || tintIndex == 1;
    }

    private static <T> T[] merge(T[] first, T[] second) {
        T[] merged = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, merged, first.length, second.length);
        return merged;
    }

    private static boolean usesDarkerCastTint(MaterialPart part) {
        if (part.isHotCast()) {
            return true;
        }

        return part.isCastPart() && !part.isCastMold() && part != MaterialPart.CAST_INGOT;
    }

    private static boolean portColorable(MachinePortBlock port) {
        return port.abilities().contains(net.mads.createexpansion.multiblock.MultiblockAbility.ITEM_INPUT)
                || port.abilities().contains(net.mads.createexpansion.multiblock.MultiblockAbility.ITEM_OUTPUT)
                || port.abilities().contains(net.mads.createexpansion.multiblock.MultiblockAbility.FLUID_INPUT)
                || port.abilities().contains(net.mads.createexpansion.multiblock.MultiblockAbility.FLUID_OUTPUT)
                || port.abilities().contains(net.mads.createexpansion.multiblock.MultiblockAbility.IO_INTERFACE);
    }

    private static int dyeColor(DyeColor color) {
        return switch (color) {
            case WHITE -> 0xFFF9FFFE;
            case ORANGE -> 0xFFF9801D;
            case MAGENTA -> 0xFFC74EBD;
            case LIGHT_BLUE -> 0xFF3AB3DA;
            case YELLOW -> 0xFFFED83D;
            case LIME -> 0xFF80C71F;
            case PINK -> 0xFFF38BAA;
            case GRAY -> 0xFF474F52;
            case LIGHT_GRAY -> 0xFF9D9D97;
            case CYAN -> 0xFF169C9C;
            case PURPLE -> 0xFF8932B8;
            case BLUE -> 0xFF3C44AA;
            case BROWN -> 0xFF835432;
            case GREEN -> 0xFF5E7C16;
            case RED -> 0xFFB02E26;
            case BLACK -> 0xFF1D1D21;
        };
    }
}
