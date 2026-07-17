package net.mads.createexpansion.registry;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.item.SimpleItemDefinition;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.energy.WireThickness;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.machine.coil.CoilDefinition;
import net.mads.createexpansion.machine.coil.CoilDefinitions;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRegistration;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockRegistrations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.mads.createexpansion.CreateExpansion;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, CreateExpansion.MOD_ID);
    public static final Map<String, DeferredHolder<Item, BlockItem>> MACHINE_CASINGS = new LinkedHashMap<>();
    public static final Map<String, Map<MachinePortType, DeferredHolder<Item, BlockItem>>> MACHINE_PORTS = new LinkedHashMap<>();
    public static final Map<StaticMachinePortType, DeferredHolder<Item, BlockItem>> STATIC_MACHINE_PORTS = new LinkedHashMap<>();
    public static final Map<String, DeferredHolder<Item, BlockItem>> MULTIBLOCK_CONTROLLERS = new LinkedHashMap<>();
    public static final Map<String, DeferredHolder<Item, BlockItem>> COILS = new LinkedHashMap<>();
    public static final Map<String, Map<MaterialPart, DeferredHolder<Item, ? extends Item>>> MATERIAL_ITEMS = new LinkedHashMap<>();
    public static final Map<String, Map<WireThickness, DeferredHolder<Item, BlockItem>>> ENERGY_WIRES = new LinkedHashMap<>();
    public static final Map<String, Map<WireThickness, DeferredHolder<Item, BlockItem>>> INSULATED_ENERGY_WIRES = new LinkedHashMap<>();
    public static final Map<String, DeferredHolder<Item, Item>> SIMPLE_ITEMS = new LinkedHashMap<>();
    public static final DeferredHolder<Item, BlockItem> CREATIVE_ENERGY_PROVIDER = ITEMS.register("creative_energy_provider", () ->
            new BlockItem(BlockRegistry.CREATIVE_ENERGY_PROVIDER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CREATIVE_ENERGY_CONSUMER = ITEMS.register("creative_energy_consumer", () ->
            new BlockItem(BlockRegistry.CREATIVE_ENERGY_CONSUMER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> KINETIC_SIFTER = KineticSifterRegistration.registerItem(ITEMS, BlockRegistry.KINETIC_SIFTER::get);
    public static final DeferredHolder<Item, BlockItem> KINETIC_CENTRIFUGE = KineticCentrifugeRegistration.registerItem(ITEMS, BlockRegistry.KINETIC_CENTRIFUGE::get);
    public static final DeferredHolder<Item, BlockItem> KINETIC_LATHE = KineticLatheRegistration.registerItem(ITEMS, BlockRegistry.KINETIC_LATHE::get);
    public static final DeferredHolder<Item, BlockItem> KINETIC_ROLLING_MILL = KineticRollingMillRegistration.registerItem(ITEMS, BlockRegistry.KINETIC_ROLLING_MILL::get);
    public static final DeferredHolder<Item, BlockItem> KINETIC_WIRE_DRAWER = KineticWireDrawerRegistration.registerItem(ITEMS, BlockRegistry.KINETIC_WIRE_DRAWER::get);
    public static final DeferredHolder<Item, BlockItem> HYDRAULIC_PRESS = HydraulicPressRegistration.registerItem(ITEMS, BlockRegistry.HYDRAULIC_PRESS::get);
    public static final DeferredHolder<Item, BlockItem> SPRING_COILING_MACHINE = KineticCoilingMachineRegistration.registerItem(ITEMS, BlockRegistry.SPRING_COILING_MACHINE::get);
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_CASING = ITEMS.register("foundry_casing", () ->
            new BlockItem(BlockRegistry.FOUNDRY_CASING.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_CONTROLLER = ITEMS.register("foundry_controller", () ->
            new BlockItem(BlockRegistry.FOUNDRY_CONTROLLER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CREATIVE_FOUNDRY_CONTROLLER = ITEMS.register("creative_foundry_controller", () ->
            new BlockItem(BlockRegistry.CREATIVE_FOUNDRY_CONTROLLER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_INPUT_HATCH = ITEMS.register("foundry_input_hatch", () ->
            new BlockItem(BlockRegistry.FOUNDRY_INPUT_HATCH.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_OUTPUT_HATCH = ITEMS.register("foundry_output_hatch", () ->
            new BlockItem(BlockRegistry.FOUNDRY_OUTPUT_HATCH.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_INPUT_BUS = ITEMS.register("foundry_input_bus", () ->
            new BlockItem(BlockRegistry.FOUNDRY_INPUT_BUS.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_DRAIN = ITEMS.register("foundry_drain", () ->
            new BlockItem(BlockRegistry.FOUNDRY_DRAIN.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOUNDRY_MOLD_CASTER = ITEMS.register("foundry_mold_caster", () ->
            new BlockItem(BlockRegistry.FOUNDRY_MOLD_CASTER.get(), new Item.Properties()));
    static {
        for (SimpleItemDefinition definition : SimpleItems.ALL) {
            SIMPLE_ITEMS.put(definition.id(), ITEMS.register(definition.id(), () -> new Item(new Item.Properties())));
        }

        MultiblockRegistrations.registerControllerItems(ITEMS, MULTIBLOCK_CONTROLLERS, BlockRegistry.MULTIBLOCK_CONTROLLERS);

        for (CoilDefinition coil : CoilDefinitions.ALL) {
            COILS.put(coil.id(), ITEMS.register(coil.itemId(), () ->
                    new BlockItem(BlockRegistry.COILS.get(coil.id()).get(), new Item.Properties())));
        }

        for (MachineTier tier : MachineTier.ALL) {
            MACHINE_CASINGS.put(tier.id(), ITEMS.register(tier.casingRegistryName(), () ->
                    new BlockItem(BlockRegistry.MACHINE_CASINGS.get(tier.id()).get(), new Item.Properties())));

            Map<WireThickness, DeferredHolder<Item, BlockItem>> wires = new LinkedHashMap<>();
            Map<WireThickness, DeferredHolder<Item, BlockItem>> insulatedWires = new LinkedHashMap<>();
            for (WireThickness thickness : WireThickness.ALL) {
                wires.put(thickness, ITEMS.register(EnergyWireBlock.registryName(tier, thickness, false), () ->
                        new BlockItem(BlockRegistry.ENERGY_WIRES.get(tier.id()).get(thickness).get(), new Item.Properties())));
                insulatedWires.put(thickness, ITEMS.register(EnergyWireBlock.registryName(tier, thickness, true), () ->
                        new BlockItem(BlockRegistry.INSULATED_ENERGY_WIRES.get(tier.id()).get(thickness).get(), new Item.Properties())));
            }
            ENERGY_WIRES.put(tier.id(), wires);
            INSULATED_ENERGY_WIRES.put(tier.id(), insulatedWires);

            Map<MachinePortType, DeferredHolder<Item, BlockItem>> ports = new LinkedHashMap<>();
            for (MachinePortType portType : MachinePortType.ALL) {
                ports.put(portType, ITEMS.register(portType.registryName(tier), () ->
                        new BlockItem(BlockRegistry.MACHINE_PORTS.get(tier.id()).get(portType).get(), new Item.Properties())));
            }
            MACHINE_PORTS.put(tier.id(), ports);
        }

        for (StaticMachinePortType portType : StaticMachinePortType.ALL) {
            STATIC_MACHINE_PORTS.put(portType, ITEMS.register(portType.id(), () ->
                    new BlockItem(BlockRegistry.STATIC_MACHINE_PORTS.get(portType).get(), new Item.Properties())));
        }

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            Map<MaterialPart, DeferredHolder<Item, ? extends Item>> items = new LinkedHashMap<>();

            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (part.isFluid()) {
                    continue;
                }

                DeferredHolder<Item, ? extends Item> item;

                if (part.isBlock()) {
                    item = ITEMS.register(part.registryName(material), () ->
                            new BlockItem(BlockRegistry.getMaterialBlock(material, part).get(), new Item.Properties()));
                } else {
                    item = ITEMS.register(part.registryName(material), () -> new MaterialItem(material, part));
                }

                items.put(part, item);
            }

            MATERIAL_ITEMS.put(material.id(), items);
        }
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static DeferredHolder<Item, ? extends Item> getMaterialItem(IndustrialMaterial material, MaterialPart part) {
        return MATERIAL_ITEMS.get(material.id()).get(part);
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getAllMaterialItems() {
        return MATERIAL_ITEMS.values().stream()
                .flatMap(items -> items.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllMachineCasingItems() {
        return MACHINE_CASINGS.values();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllMachinePortItems() {
        return MACHINE_PORTS.values().stream()
                .flatMap(ports -> ports.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllStaticMachinePortItems() {
        return STATIC_MACHINE_PORTS.values();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllMultiblockControllerItems() {
        return MULTIBLOCK_CONTROLLERS.values();
    }

    public static DeferredHolder<Item, Item> getSimpleItem(String id) {
        DeferredHolder<Item, Item> item = SIMPLE_ITEMS.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown simple item: " + id);
        }
        return item;
    }

    public static Collection<DeferredHolder<Item, Item>> getAllSimpleItems() {
        return SIMPLE_ITEMS.values();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllCoilItems() {
        return COILS.values();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllEnergyWireItems() {
        return ENERGY_WIRES.values().stream()
                .flatMap(wires -> wires.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllInsulatedEnergyWireItems() {
        return INSULATED_ENERGY_WIRES.values().stream()
                .flatMap(wires -> wires.values().stream())
                .toList();
    }
}
