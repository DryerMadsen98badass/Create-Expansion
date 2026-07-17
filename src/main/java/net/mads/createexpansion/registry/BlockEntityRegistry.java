package net.mads.createexpansion.registry;

import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.energy.CreativeEnergyBlockEntity;
import net.mads.createexpansion.energy.EnergyWireBlockEntity;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.mads.createexpansion.machine.machines.foundry.FoundryControllerBlockEntity;
import net.mads.createexpansion.machine.machines.foundry.FoundryDrainBlockEntity;
import net.mads.createexpansion.machine.machines.foundry.FoundryHatchBlockEntity;
import net.mads.createexpansion.machine.machines.foundry.FoundryMoldCasterBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugePartBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRegistration;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerPartBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.mads.createexpansion.CreateExpansion;

import java.util.stream.Stream;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateExpansion.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MachinePortBlockEntity>> MACHINE_PORT = BLOCK_ENTITIES.register("machine_port", () ->
            BlockEntityType.Builder.of(MachinePortBlockEntity::new, allMachinePortBlocks()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiblockControllerBlockEntity>> MULTIBLOCK_CONTROLLER = BLOCK_ENTITIES.register("multiblock_controller", () ->
            BlockEntityType.Builder.of(MultiblockControllerBlockEntity::new, allControllerBlocks()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FoundryControllerBlockEntity>> FOUNDRY_CONTROLLER = BLOCK_ENTITIES.register("foundry_controller", () ->
            BlockEntityType.Builder.of(FoundryControllerBlockEntity::new, BlockRegistry.FOUNDRY_CONTROLLER.get(), BlockRegistry.CREATIVE_FOUNDRY_CONTROLLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FoundryHatchBlockEntity>> FOUNDRY_HATCH = BLOCK_ENTITIES.register("foundry_hatch", () ->
            BlockEntityType.Builder.of(FoundryHatchBlockEntity::new, BlockRegistry.FOUNDRY_INPUT_HATCH.get(), BlockRegistry.FOUNDRY_OUTPUT_HATCH.get(), BlockRegistry.FOUNDRY_INPUT_BUS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FoundryDrainBlockEntity>> FOUNDRY_DRAIN = BLOCK_ENTITIES.register("foundry_drain", () ->
            BlockEntityType.Builder.of(FoundryDrainBlockEntity::new, BlockRegistry.FOUNDRY_DRAIN.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FoundryMoldCasterBlockEntity>> FOUNDRY_MOLD_CASTER = BLOCK_ENTITIES.register("foundry_mold_caster", () ->
            BlockEntityType.Builder.of(FoundryMoldCasterBlockEntity::new, BlockRegistry.FOUNDRY_MOLD_CASTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeEnergyBlockEntity>> CREATIVE_ENERGY = BLOCK_ENTITIES.register("creative_energy", () ->
            BlockEntityType.Builder.of(CreativeEnergyBlockEntity::new, BlockRegistry.CREATIVE_ENERGY_PROVIDER.get(), BlockRegistry.CREATIVE_ENERGY_CONSUMER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyWireBlockEntity>> ENERGY_WIRE = BLOCK_ENTITIES.register("energy_wire", () ->
            BlockEntityType.Builder.of(EnergyWireBlockEntity::new, allEnergyWireBlocks()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticSifterBlockEntity>> KINETIC_SIFTER =
            KineticSifterRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_SIFTER::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCentrifugeBlockEntity>> KINETIC_CENTRIFUGE =
            KineticCentrifugeRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_CENTRIFUGE::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCentrifugePartBlockEntity>> KINETIC_CENTRIFUGE_PART =
            KineticCentrifugeRegistration.registerPartBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_CENTRIFUGE_PART::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticLatheBlockEntity>> KINETIC_LATHE =
            KineticLatheRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_LATHE::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticRollingMillBlockEntity>> KINETIC_ROLLING_MILL =
            KineticRollingMillRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_ROLLING_MILL::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticWireDrawerBlockEntity>> KINETIC_WIRE_DRAWER =
            KineticWireDrawerRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_WIRE_DRAWER::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticWireDrawerPartBlockEntity>> KINETIC_WIRE_DRAWER_PART =
            KineticWireDrawerRegistration.registerPartBlockEntity(BLOCK_ENTITIES, BlockRegistry.KINETIC_WIRE_DRAWER_PART::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HydraulicPressBlockEntity>> HYDRAULIC_PRESS =
            HydraulicPressRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.HYDRAULIC_PRESS::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCoilingMachineBlockEntity>> SPRING_COILING_MACHINE =
            KineticCoilingMachineRegistration.registerBlockEntity(BLOCK_ENTITIES, BlockRegistry.SPRING_COILING_MACHINE::get);

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MACHINE_PORT.get(), (port, side) -> port.itemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, MACHINE_PORT.get(), (port, side) -> port.fluidCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_SIFTER.get(), (sifter, side) -> sifter.itemCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_CENTRIFUGE.get(), (centrifuge, side) -> centrifuge.itemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, KINETIC_CENTRIFUGE.get(), (centrifuge, side) -> centrifuge.fluidCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_CENTRIFUGE_PART.get(), (part, side) -> part.itemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, KINETIC_CENTRIFUGE_PART.get(), (part, side) -> part.fluidCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_LATHE.get(), (lathe, side) -> lathe.itemCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_ROLLING_MILL.get(), (rollingMill, side) -> rollingMill.itemCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_WIRE_DRAWER.get(), (wireDrawer, side) -> wireDrawer.itemCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, KINETIC_WIRE_DRAWER_PART.get(), (part, side) -> part.itemCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, HYDRAULIC_PRESS.get(), (press, side) -> press.itemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, HYDRAULIC_PRESS.get(), (press, side) -> press.fluidCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SPRING_COILING_MACHINE.get(), (machine, side) -> machine.itemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, FOUNDRY_HATCH.get(), (hatch, side) -> hatch.fluidCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FOUNDRY_HATCH.get(), (hatch, side) -> hatch.itemCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FOUNDRY_MOLD_CASTER.get(), (caster, side) -> caster.itemCapability());
    }

    private static Block[] allMachinePortBlocks() {
        return Stream.concat(BlockRegistry.getAllMachinePorts().stream(), BlockRegistry.getAllStaticMachinePorts().stream())
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }

    private static Block[] allControllerBlocks() {
        return BlockRegistry.getAllMultiblockControllers().stream()
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }

    private static Block[] allEnergyWireBlocks() {
        return Stream.concat(BlockRegistry.getAllEnergyWires().stream(), BlockRegistry.getAllInsulatedEnergyWires().stream())
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }
}
