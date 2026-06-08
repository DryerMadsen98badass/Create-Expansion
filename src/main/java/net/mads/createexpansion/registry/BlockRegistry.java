package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachineCasingBlock;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialBlock;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.multiblock.MultiblockDefinitions;
import net.mads.createexpansion.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.multiblock.MultiblockControllerDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, CreateExpansion.MOD_ID);
    public static final Map<String, DeferredHolder<Block, MachineCasingBlock>> MACHINE_CASINGS = new LinkedHashMap<>();
    public static final Map<String, Map<MachinePortType, DeferredHolder<Block, MachinePortBlock>>> MACHINE_PORTS = new LinkedHashMap<>();
    public static final Map<StaticMachinePortType, DeferredHolder<Block, MachinePortBlock>> STATIC_MACHINE_PORTS = new LinkedHashMap<>();
    public static final Map<String, DeferredHolder<Block, MultiblockControllerBlock>> MULTIBLOCK_CONTROLLERS = new LinkedHashMap<>();
    public static final Map<String, Map<MaterialPart, DeferredHolder<Block, ? extends Block>>> MATERIAL_BLOCKS = new LinkedHashMap<>();

    static {
        for (MultiblockControllerDefinition controller : MultiblockDefinitions.controllers()) {
            MULTIBLOCK_CONTROLLERS.put(controller.registryName(), BLOCKS.register(controller.registryName(), () -> new MultiblockControllerBlock(controller)));
        }

        for (MachineTier tier : MachineTier.ALL) {
            MACHINE_CASINGS.put(tier.id(), BLOCKS.register(tier.casingRegistryName(), () -> new MachineCasingBlock(tier)));

            Map<MachinePortType, DeferredHolder<Block, MachinePortBlock>> ports = new LinkedHashMap<>();
            for (MachinePortType portType : MachinePortType.ALL) {
                ports.put(portType, BLOCKS.register(portType.registryName(tier), () -> new MachinePortBlock(tier, portType)));
            }
            MACHINE_PORTS.put(tier.id(), ports);
        }

        for (StaticMachinePortType portType : StaticMachinePortType.ALL) {
            STATIC_MACHINE_PORTS.put(portType, BLOCKS.register(portType.id(), () -> new MachinePortBlock(portType)));
        }

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            Map<MaterialPart, DeferredHolder<Block, ? extends Block>> blocks = new LinkedHashMap<>();

            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (!part.isBlock()) {
                    continue;
                }

                DeferredHolder<Block, MaterialBlock> block = BLOCKS.register(part.registryName(material), () -> new MaterialBlock(material, part));
                blocks.put(part, block);
            }

            MATERIAL_BLOCKS.put(material.id(), blocks);
        }
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    public static DeferredHolder<Block, ? extends Block> getMaterialBlock(IndustrialMaterial material, MaterialPart part) {
        return MATERIAL_BLOCKS.get(material.id()).get(part);
    }

    public static Collection<DeferredHolder<Block, ? extends Block>> getAllMaterialBlocks() {
        return MATERIAL_BLOCKS.values().stream()
                .flatMap(blocks -> blocks.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Block, MachineCasingBlock>> getAllMachineCasings() {
        return MACHINE_CASINGS.values();
    }

    public static Collection<DeferredHolder<Block, MachinePortBlock>> getAllMachinePorts() {
        return MACHINE_PORTS.values().stream()
                .flatMap(ports -> ports.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Block, MachinePortBlock>> getAllStaticMachinePorts() {
        return STATIC_MACHINE_PORTS.values();
    }

    public static Collection<DeferredHolder<Block, MultiblockControllerBlock>> getAllMultiblockControllers() {
        return MULTIBLOCK_CONTROLLERS.values();
    }
}
