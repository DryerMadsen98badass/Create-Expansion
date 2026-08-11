package net.mads.createexpansion.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.model.ModelSwapper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.mads.createexpansion.client.model.FluidTransportPipeAttachmentModel;
import net.mads.createexpansion.client.model.FluidTransportTankModel;
import net.mads.createexpansion.client.screen.FoundryControllerScreen;
import net.mads.createexpansion.client.screen.MachinePortScreen;
import net.mads.createexpansion.client.screen.MachineControlScheduleScreen;
import net.mads.createexpansion.client.screen.MultiblockControllerScreen;
import net.mads.createexpansion.client.screen.SingleBlockMachineScreen;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.material.MaterialBlock;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.machine.MachineModelTintResolver;
import net.mads.createexpansion.machine.MachineCasingBlock;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.machine.SingleBlockDefinition;
import net.mads.createexpansion.machine.SingleBlockMachineBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.foundry.FoundryMoldCasterRenderer;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRenderer;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRenderer;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRenderer;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRenderer;
import net.mads.createexpansion.machine.machines.kinetic.singleblock.KineticSingleBlockMachineRenderer;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRenderer;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRenderer;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRenderer;
import net.mads.createexpansion.registry.FluidRegistry;
import net.mads.createexpansion.registry.MenuRegistry;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.transport.FluidTransportGlassPipeRenderer;
import net.mads.createexpansion.transport.FluidTransportPumpRenderer;
import net.mads.createexpansion.transport.FluidTransportRegistrations;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
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

@EventBusSubscriber(modid = CreateExpansion.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        CreateExpansionSpriteShifts.init();
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                ItemRegistry.KINETIC_SIFTER.get(),
                new KineticStats(BlockRegistry.KINETIC_SIFTER.get())
        ));
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                ItemRegistry.KINETIC_CENTRIFUGE.get(),
                new KineticStats(BlockRegistry.KINETIC_CENTRIFUGE.get())
        ));
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                ItemRegistry.KINETIC_LATHE.get(),
                new KineticStats(BlockRegistry.KINETIC_LATHE.get())
        ));
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                ItemRegistry.KINETIC_ROLLING_MILL.get(),
                new KineticStats(BlockRegistry.KINETIC_ROLLING_MILL.get())
        ));
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                ItemRegistry.KINETIC_WIRE_DRAWER.get(),
                new KineticStats(BlockRegistry.KINETIC_WIRE_DRAWER.get())
        ));
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                ItemRegistry.SPRING_COILING_MACHINE.get(),
                new KineticStats(BlockRegistry.SPRING_COILING_MACHINE.get())
        ));
        FluidTransportRegistrations.allItems().forEach(registration ->
                event.enqueueWork(() -> TooltipModifier.REGISTRY.register(
                        registration.pump().get(),
                        new KineticStats(FluidTransportRegistrations.blocks(registration.tier()).pump().get())
                ))
        );
        FluidTransportRegistrations.allBlockEntities().forEach(registration ->
                event.enqueueWork(() -> SimpleBlockEntityVisualizer.builder(registration.pump().get())
                        .factory(SingleAxisRotatingVisual.ofZ(AllPartialModels.MECHANICAL_PUMP_COG))
                        .skipVanillaRender(blockEntity -> false)
                        .apply())
        );
    }

    @SubscribeEvent
    public static void modifyBakedModels(ModelEvent.ModifyBakingResult event) {
        FluidTransportRegistrations.allBlocks().forEach(registration -> {
            ModelSwapper.swapModels(
                    event.getModels(),
                    ModelSwapper.getAllBlockStateModelLocations(registration.pipe().get()),
                    model -> new FluidTransportPipeAttachmentModel(model, registration.tier())
            );
            ModelSwapper.swapModels(
                    event.getModels(),
                    ModelSwapper.getAllBlockStateModelLocations(registration.glassPipe().get()),
                    model -> new FluidTransportPipeAttachmentModel(model, registration.tier())
            );
            ModelSwapper.swapModels(
                    event.getModels(),
                    ModelSwapper.getAllBlockStateModelLocations(registration.pump().get()),
                    model -> new FluidTransportPipeAttachmentModel(model, registration.tier())
            );
            ModelSwapper.swapModels(
                    event.getModels(),
                    ModelSwapper.getAllBlockStateModelLocations(registration.tank().get()),
                    model -> new FluidTransportTankModel(model, registration.tier())
            );
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.MACHINE_PORT.get(), MachinePortOverlayRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.SINGLE_BLOCK_MACHINE.get(), KineticSingleBlockMachineRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.KINETIC_SIFTER.get(), KineticSifterRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.KINETIC_CENTRIFUGE.get(), KineticCentrifugeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.KINETIC_LATHE.get(), KineticLatheRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.KINETIC_ROLLING_MILL.get(), KineticRollingMillRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.KINETIC_WIRE_DRAWER.get(), KineticWireDrawerRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.HYDRAULIC_PRESS.get(), HydraulicPressRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.SPRING_COILING_MACHINE.get(), KineticCoilingMachineRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.FOUNDRY_MOLD_CASTER.get(), FoundryMoldCasterRenderer::new);
        FluidTransportRegistrations.allBlockEntities().forEach(registration -> {
            event.registerBlockEntityRenderer(registration.glassPipe().get(), FluidTransportGlassPipeRenderer::new);
            event.registerBlockEntityRenderer(registration.pump().get(), FluidTransportPumpRenderer::new);
            event.registerBlockEntityRenderer(registration.tank().get(), FluidTankRenderer::new);
        });
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
        Item[] energyWires = ItemRegistry.getAllEnergyWireItems().stream()
                .map(item -> item.get())
                .toArray(Item[]::new);
        Item[] insulatedEnergyWires = ItemRegistry.getAllInsulatedEnergyWireItems().stream()
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
        Item[] singleBlockMachines = ItemRegistry.getAllSingleBlockMachineItems().stream()
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

        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MachineCasingBlock casing) {
                return opaque(casing.tier().color());
            }

            return -1;
        }, machineCasings);

        event.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof EnergyWireBlock wire) {
                return opaque(wire.tier().color());
            }

            return -1;
        }, merge(energyWires, insulatedEnergyWires));

        event.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MachinePortBlock port) {
                if (tintIndex == 0 && port.usesTint()) {
                    return opaque(port.tintColor());
                }
                if (tintIndex == 1 && port.hasTier() && portColorable(port)) {
                    return dyeColor(DyeColor.GRAY);
                }
            }

            return -1;
        }, merge(machinePorts, staticMachinePorts));

        event.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof MultiblockControllerBlock controller
                    && controller.usesTint(tintIndex)) {
                return opaque(controller.tintColor(tintIndex));
            }

            return -1;
        }, multiblockControllers);

        event.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof SingleBlockMachineBlock machine) {
                return singleBlockMachineColor(machine, tintIndex);
            }

            return -1;
        }, singleBlockMachines);


        for (SimpleBlockDefinition definition : SimpleBlocks.ALL) {
            if (!definition.hasColor()) {
                continue;
            }

            Item[] simpleBlockItems = merge(
                    new Item[]{
                            ItemRegistry
                                    .getSimpleBlockItem(
                                            definition.id()
                                    )
                                    .get()
                    },
                    definition.variants()
                            .stream()
                            .map(variant ->
                                    ItemRegistry
                                            .getSimpleBlockVariantItem(
                                                    definition.id(),
                                                    variant
                                            )
                                            .get()
                            )
                            .toArray(Item[]::new)
            );

            event.register(
                    (stack, tintIndex) ->
                            tintIndex == 0
                                    ? definition.blockColor()
                                    : -1,
                    simpleBlockItems
            );
        }
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        Block[] materialBlocks = BlockRegistry.getAllMaterialBlocks().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] machineCasings = BlockRegistry.getAllMachineCasings().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] energyWires = BlockRegistry.getAllEnergyWires().stream()
                .map(block -> block.get())
                .toArray(Block[]::new);
        Block[] insulatedEnergyWires = BlockRegistry.getAllInsulatedEnergyWires().stream()
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
        Block[] singleBlockMachines = BlockRegistry.getAllSingleBlockMachines().stream()
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

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (state.getBlock() instanceof MachineCasingBlock casing) {
                return opaque(casing.tier().color());
            }

            return -1;
        }, machineCasings);

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (state.getBlock() instanceof EnergyWireBlock wire) {
                if (wire.insulated()) {
                    return -1;
                }
                return opaque(wire.tier().color());
            }

            return -1;
        }, merge(energyWires, insulatedEnergyWires));

        event.register((state, level, pos, tintIndex) -> {
            if (state.getBlock() instanceof MachinePortBlock port) {
                if (tintIndex == 0 && port.usesTint()) {
                    return opaque(port.tintColor());
                }
                if (tintIndex == 1 && port.hasTier() && level != null && pos != null && level.getBlockEntity(pos) instanceof MachinePortBlockEntity portEntity && portEntity.supportsIoColor()) {
                    return dyeColor(portEntity.ioColor());
                }
            }

            return -1;
        }, merge(machinePorts, staticMachinePorts));

        event.register((state, level, pos, tintIndex) -> {
            if (state.getBlock() instanceof MultiblockControllerBlock controller && controller.usesTint(tintIndex)) {
                return opaque(controller.tintColor(tintIndex));
            }

            return -1;
        }, multiblockControllers);

        event.register((state, level, pos, tintIndex) -> {
            if (state.getBlock() instanceof SingleBlockMachineBlock machine) {
                return singleBlockMachineColor(machine, tintIndex);
            }

            return -1;
        }, singleBlockMachines);


        for (SimpleBlockDefinition definition : SimpleBlocks.ALL) {
            if (!definition.hasColor()) {
                continue;
            }

            Block[] simpleBlocks = merge(
                    new Block[]{
                            BlockRegistry
                                    .getSimpleBlock(
                                            definition.id()
                                    )
                                    .get()
                    },
                    definition.variants()
                            .stream()
                            .map(variant ->
                                    BlockRegistry
                                            .getSimpleBlockVariant(
                                                    definition.id(),
                                                    variant
                                            )
                                            .get()
                            )
                            .toArray(Block[]::new)
            );

            event.register(
                    (state, level, pos, tintIndex) ->
                            tintIndex == 0
                                    ? definition.blockColor()
                                    : -1,
                    simpleBlocks
            );
        }
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
        event.register(MenuRegistry.FOUNDRY_CONTROLLER.get(), FoundryControllerScreen::new);
        event.register(MenuRegistry.SINGLE_BLOCK_MACHINE.get(), SingleBlockMachineScreen::new);
        event.register(MenuRegistry.MACHINE_CONTROL_SCHEDULE.get(), MachineControlScheduleScreen::new);
    }

    private static boolean isMaterialTintLayer(int tintIndex) {
        return tintIndex == 0 || tintIndex == 1;
    }

    private static int singleBlockMachineColor(SingleBlockMachineBlock machine, int tintIndex) {
        SingleBlockDefinition.MachineSide side = SingleBlockDefinition.MachineSide.fromTintIndex(tintIndex);
        if (side == null) {
            return -1;
        }

        Integer customColor = machine.instance().definition().sideTextureColor(side);
        if (customColor != null) {
            return opaque(customColor);
        }

        if (tintIndex == 0) {
            Integer modelColor = MachineModelTintResolver.resolve(machine.instance().definition().model());
            if (modelColor != null) {
                return opaque(modelColor);
            }
        }

        if (machine.instance().definition().sideTexture(side) == null && machine.instance().tier().isElectric()) {
            return opaque(machine.instance().tier().color());
        }

        return -1;
    }

    private static int opaque(int color) {
        return 0xFF000000 | color;
    }

    private static Item[] merge(Item[] first, Item[] second) {
        Item[] merged = new Item[first.length + second.length];
        System.arraycopy(first, 0, merged, 0, first.length);
        System.arraycopy(second, 0, merged, first.length, second.length);
        return merged;
    }

    private static Block[] merge(Block[] first, Block[] second) {
        Block[] merged = new Block[first.length + second.length];
        System.arraycopy(first, 0, merged, 0, first.length);
        System.arraycopy(second, 0, merged, first.length, second.length);
        return merged;
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

    private static boolean portColorable(MachinePortBlock port) {
        return port.abilities().contains(net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility.ITEM_INPUT)
                || port.abilities().contains(net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility.ITEM_OUTPUT)
                || port.abilities().contains(net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility.FLUID_INPUT)
                || port.abilities().contains(net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility.FLUID_OUTPUT)
                || port.abilities().contains(net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility.IO_INTERFACE);
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
