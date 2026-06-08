package net.mads.createexpansion.registry;

import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.multiblock.MultiblockControllerBlockEntity;
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

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MACHINE_PORT.get(), (port, side) -> port.itemCapability());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, MACHINE_PORT.get(), (port, side) -> port.fluidCapability());
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
}
