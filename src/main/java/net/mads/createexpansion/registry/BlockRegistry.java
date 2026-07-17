package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.energy.CreativeEnergyBlock;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.WireThickness;
import net.mads.createexpansion.machine.MachineCasingBlock;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.machine.coil.CoilBlock;
import net.mads.createexpansion.machine.coil.CoilDefinition;
import net.mads.createexpansion.machine.coil.CoilDefinitions;
import net.mads.createexpansion.machine.machines.foundry.FoundryCasingBlock;
import net.mads.createexpansion.machine.machines.foundry.FoundryControllerBlock;
import net.mads.createexpansion.machine.machines.foundry.FoundryDrainBlock;
import net.mads.createexpansion.machine.machines.foundry.FoundryHatchBlock;
import net.mads.createexpansion.machine.machines.foundry.FoundryHatchType;
import net.mads.createexpansion.machine.machines.foundry.FoundryMoldCasterBlock;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeBlock;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugePartBlock;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineBlock;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressBlock;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRegistration;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheBlock;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillBlock;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterBlock;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerBlock;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerPartBlock;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialBlock;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockRegistrations;
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
    public static final Map<String, DeferredHolder<Block, CoilBlock>> COILS = new LinkedHashMap<>();
    public static final Map<String, Map<MaterialPart, DeferredHolder<Block, ? extends Block>>> MATERIAL_BLOCKS = new LinkedHashMap<>();
    public static final Map<String, Map<WireThickness, DeferredHolder<Block, EnergyWireBlock>>> ENERGY_WIRES = new LinkedHashMap<>();
    public static final Map<String, Map<WireThickness, DeferredHolder<Block, EnergyWireBlock>>> INSULATED_ENERGY_WIRES = new LinkedHashMap<>();
    public static final DeferredHolder<Block, CreativeEnergyBlock> CREATIVE_ENERGY_PROVIDER = BLOCKS.register("creative_energy_provider", () -> new CreativeEnergyBlock(true));
    public static final DeferredHolder<Block, CreativeEnergyBlock> CREATIVE_ENERGY_CONSUMER = BLOCKS.register("creative_energy_consumer", () -> new CreativeEnergyBlock(false));
    public static final DeferredHolder<Block, KineticSifterBlock> KINETIC_SIFTER = KineticSifterRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticCentrifugeBlock> KINETIC_CENTRIFUGE = KineticCentrifugeRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticCentrifugePartBlock> KINETIC_CENTRIFUGE_PART = KineticCentrifugeRegistration.registerPartBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticLatheBlock> KINETIC_LATHE = KineticLatheRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticRollingMillBlock> KINETIC_ROLLING_MILL = KineticRollingMillRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticWireDrawerBlock> KINETIC_WIRE_DRAWER = KineticWireDrawerRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticWireDrawerPartBlock> KINETIC_WIRE_DRAWER_PART = KineticWireDrawerRegistration.registerPartBlock(BLOCKS);
    public static final DeferredHolder<Block, HydraulicPressBlock> HYDRAULIC_PRESS = HydraulicPressRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, KineticCoilingMachineBlock> SPRING_COILING_MACHINE = KineticCoilingMachineRegistration.registerBlock(BLOCKS);
    public static final DeferredHolder<Block, FoundryCasingBlock> FOUNDRY_CASING = BLOCKS.register("foundry_casing", FoundryCasingBlock::new);
    public static final DeferredHolder<Block, FoundryControllerBlock> FOUNDRY_CONTROLLER = BLOCKS.register("foundry_controller", () -> new FoundryControllerBlock());
    public static final DeferredHolder<Block, FoundryControllerBlock> CREATIVE_FOUNDRY_CONTROLLER = BLOCKS.register("creative_foundry_controller", () -> new FoundryControllerBlock());
    public static final DeferredHolder<Block, FoundryHatchBlock> FOUNDRY_INPUT_HATCH = BLOCKS.register(FoundryHatchType.INPUT.id(), () -> new FoundryHatchBlock(FoundryHatchType.INPUT));
    public static final DeferredHolder<Block, FoundryHatchBlock> FOUNDRY_OUTPUT_HATCH = BLOCKS.register(FoundryHatchType.OUTPUT.id(), () -> new FoundryHatchBlock(FoundryHatchType.OUTPUT));
    public static final DeferredHolder<Block, FoundryHatchBlock> FOUNDRY_INPUT_BUS = BLOCKS.register(FoundryHatchType.INPUT_BUS.id(), () -> new FoundryHatchBlock(FoundryHatchType.INPUT_BUS));
    public static final DeferredHolder<Block, FoundryDrainBlock> FOUNDRY_DRAIN = BLOCKS.register("foundry_drain", () -> new FoundryDrainBlock());
    public static final DeferredHolder<Block, FoundryMoldCasterBlock> FOUNDRY_MOLD_CASTER = BLOCKS.register("foundry_mold_caster", () -> new FoundryMoldCasterBlock());

    static {
        MultiblockRegistrations.registerControllerBlocks(BLOCKS, MULTIBLOCK_CONTROLLERS);

        for (CoilDefinition coil : CoilDefinitions.ALL) {
            COILS.put(coil.id(), BLOCKS.register(coil.blockId(), () -> new CoilBlock(coil)));
        }

        for (MachineTier tier : MachineTier.ALL) {
            MACHINE_CASINGS.put(tier.id(), BLOCKS.register(tier.casingRegistryName(), () -> new MachineCasingBlock(tier)));

            Map<WireThickness, DeferredHolder<Block, EnergyWireBlock>> wires = new LinkedHashMap<>();
            Map<WireThickness, DeferredHolder<Block, EnergyWireBlock>> insulatedWires = new LinkedHashMap<>();
            for (WireThickness thickness : WireThickness.ALL) {
                wires.put(thickness, BLOCKS.register(EnergyWireBlock.registryName(tier, thickness, false), () -> new EnergyWireBlock(tier, thickness, false)));
                insulatedWires.put(thickness, BLOCKS.register(EnergyWireBlock.registryName(tier, thickness, true), () -> new EnergyWireBlock(tier, thickness, true)));
            }
            ENERGY_WIRES.put(tier.id(), wires);
            INSULATED_ENERGY_WIRES.put(tier.id(), insulatedWires);

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

    public static Collection<DeferredHolder<Block, CoilBlock>> getAllCoils() {
        return COILS.values();
    }

    public static Collection<DeferredHolder<Block, EnergyWireBlock>> getAllEnergyWires() {
        return ENERGY_WIRES.values().stream()
                .flatMap(wires -> wires.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Block, EnergyWireBlock>> getAllInsulatedEnergyWires() {
        return INSULATED_ENERGY_WIRES.values().stream()
                .flatMap(wires -> wires.values().stream())
                .toList();
    }
}
