package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.ActiveBlockDefinition;
import net.mads.createexpansion.block.ActiveBlocks;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlockVariant;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.WireThickness;
import net.mads.createexpansion.item.FiredBucketItem;
import net.mads.createexpansion.item.SimpleItemDefinition;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineDefinition;
import net.mads.createexpansion.machine.SingleBlockMachineInstance;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.machine.coil.CoilDefinition;
import net.mads.createexpansion.machine.coil.CoilDefinitions;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockRegistrations;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRegistration;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(
                    BuiltInRegistries.ITEM,
                    CreateExpansion.MOD_ID
            );

    public static final DeferredHolder<Item, FiredBucketItem> FIRED_BUCKET =
            ITEMS.register(
                    "fired_bucket",
                    () -> new FiredBucketItem(
                            Fluids.EMPTY,
                            new Item.Properties().stacksTo(16)
                    )
            );

    public static final Map<
            String,
            DeferredHolder<Item, BlockItem>
            > MACHINE_CASINGS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    MachinePortType,
                    DeferredHolder<Item, BlockItem>
                    >
            > MACHINE_PORTS = new LinkedHashMap<>();

    public static final Map<
            StaticMachinePortType,
            DeferredHolder<Item, BlockItem>
            > STATIC_MACHINE_PORTS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Item, BlockItem>
            > MULTIBLOCK_CONTROLLERS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Item, BlockItem>
            > SINGLE_BLOCK_MACHINES = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Item, BlockItem>
            > COILS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Item, BlockItem>
            > ACTIVE_BLOCK_ITEMS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<String, DeferredHolder<Item, BlockItem>>
            > MATERIAL_STONE_ITEMS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    MaterialPart,
                    DeferredHolder<Item, ? extends Item>
                    >
            > MATERIAL_ITEMS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    WireThickness,
                    DeferredHolder<Item, BlockItem>
                    >
            > ENERGY_WIRES = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    WireThickness,
                    DeferredHolder<Item, BlockItem>
                    >
            > INSULATED_ENERGY_WIRES =
            new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Item, Item>
            > SIMPLE_ITEMS = new LinkedHashMap<>();

    /**
     * BlockItem for hovedblokken.
     *
     * Eksempel:
     * treated_wood
     */
    public static final Map<
            String,
            DeferredHolder<Item, BlockItem>
            > SIMPLE_BLOCK_ITEMS = new LinkedHashMap<>();

    /**
     * BlockItem for variantene.
     *
     * Eksempel:
     * treated_wood -> STAIR -> treated_wood_stairs
     */
    public static final Map<
            String,
            Map<
                    SimpleBlockVariant,
                    DeferredHolder<Item, BlockItem>
                    >
            > SIMPLE_BLOCK_VARIANT_ITEMS =
            new LinkedHashMap<>();

    public static final DeferredHolder<
            Item,
            BlockItem
            > CREATIVE_ENERGY_PROVIDER =
            ITEMS.register(
                    "creative_energy_provider",
                    () -> new BlockItem(
                            BlockRegistry
                                    .CREATIVE_ENERGY_PROVIDER
                                    .get(),
                            new Item.Properties()
                    )
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > CREATIVE_ENERGY_CONSUMER =
            ITEMS.register(
                    "creative_energy_consumer",
                    () -> new BlockItem(
                            BlockRegistry
                                    .CREATIVE_ENERGY_CONSUMER
                                    .get(),
                            new Item.Properties()
                    )
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FIREBRICK_FIREBOX =
            registerActiveBlockItem(
                    ActiveBlocks.ALL.getFirst()
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > KINETIC_SIFTER =
            KineticSifterRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.KINETIC_SIFTER::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > KINETIC_CENTRIFUGE =
            KineticCentrifugeRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.KINETIC_CENTRIFUGE::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > KINETIC_LATHE =
            KineticLatheRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.KINETIC_LATHE::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > KINETIC_ROLLING_MILL =
            KineticRollingMillRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.KINETIC_ROLLING_MILL::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > KINETIC_WIRE_DRAWER =
            KineticWireDrawerRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.KINETIC_WIRE_DRAWER::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > HYDRAULIC_PRESS =
            HydraulicPressRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.HYDRAULIC_PRESS::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > SPRING_COILING_MACHINE =
            KineticCoilingMachineRegistration.registerItem(
                    ITEMS,
                    BlockRegistry.SPRING_COILING_MACHINE::get
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_CASING =
            registerBlockItem(
                    "foundry_casing",
                    BlockRegistry.FOUNDRY_CASING
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_CONTROLLER =
            registerBlockItem(
                    "foundry_controller",
                    BlockRegistry.FOUNDRY_CONTROLLER
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > CREATIVE_FOUNDRY_CONTROLLER =
            registerBlockItem(
                    "creative_foundry_controller",
                    BlockRegistry.CREATIVE_FOUNDRY_CONTROLLER
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_INPUT_HATCH =
            registerBlockItem(
                    "foundry_input_hatch",
                    BlockRegistry.FOUNDRY_INPUT_HATCH
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_OUTPUT_HATCH =
            registerBlockItem(
                    "foundry_output_hatch",
                    BlockRegistry.FOUNDRY_OUTPUT_HATCH
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_INPUT_BUS =
            registerBlockItem(
                    "foundry_input_bus",
                    BlockRegistry.FOUNDRY_INPUT_BUS
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_DRAIN =
            registerBlockItem(
                    "foundry_drain",
                    BlockRegistry.FOUNDRY_DRAIN
            );

    public static final DeferredHolder<
            Item,
            BlockItem
            > FOUNDRY_MOLD_CASTER =
            registerBlockItem(
                    "foundry_mold_caster",
                    BlockRegistry.FOUNDRY_MOLD_CASTER
            );

    static {
        registerActiveBlockItems();
        registerSimpleBlockItems();
        registerSimpleItems();
        registerMultiblockControllerItems();
        registerSingleBlockMachineItems();
        registerCoilItems();
        registerTieredItems();
        registerStaticMachinePortItems();
        registerMaterialItems();
    }

    private ItemRegistry() {
    }

    private static DeferredHolder<
            Item,
            BlockItem
            > registerActiveBlockItem(
            ActiveBlockDefinition definition
    ) {
        DeferredHolder<Item, BlockItem> item =
                ITEMS.register(
                        definition.id(),
                        () -> new BlockItem(
                                BlockRegistry
                                        .getActiveBlock(
                                                definition.id()
                                        )
                                        .get(),
                                new Item.Properties()
                        )
                );

        ACTIVE_BLOCK_ITEMS.put(
                definition.id(),
                item
        );

        return item;
    }

    private static void registerActiveBlockItems() {
        for (ActiveBlockDefinition definition
                : ActiveBlocks.ALL) {

            if (ACTIVE_BLOCK_ITEMS.containsKey(definition.id())) {
                continue;
            }

            registerActiveBlockItem(definition);
        }
    }

    private static void registerSimpleBlockItems() {
        for (SimpleBlockDefinition definition
                : SimpleBlocks.ALL) {

            DeferredHolder<Item, BlockItem> baseItem =
                    ITEMS.register(
                            definition.id(),
                            () -> new BlockItem(
                                    BlockRegistry
                                            .getSimpleBlock(
                                                    definition.id()
                                            )
                                            .get(),
                                    new Item.Properties()
                            )
                    );

            SIMPLE_BLOCK_ITEMS.put(
                    definition.id(),
                    baseItem
            );

            Map<
                    SimpleBlockVariant,
                    DeferredHolder<Item, BlockItem>
                    > variantItems = new LinkedHashMap<>();

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                String registryName =
                        definition.variantId(variant);

                DeferredHolder<Item, BlockItem> item =
                        ITEMS.register(
                                registryName,
                                () -> new BlockItem(
                                        BlockRegistry
                                                .getSimpleBlockVariant(
                                                        definition.id(),
                                                        variant
                                                )
                                                .get(),
                                        new Item.Properties()
                                )
                        );

                variantItems.put(variant, item);
            }

            SIMPLE_BLOCK_VARIANT_ITEMS.put(
                    definition.id(),
                    variantItems
            );
        }
    }

    private static void registerSimpleItems() {
        for (SimpleItemDefinition definition
                : SimpleItems.ALL) {

            SIMPLE_ITEMS.put(
                    definition.id(),
                    ITEMS.register(
                            definition.id(),
                            () -> new Item(
                                    new Item.Properties()
                            )
                    )
            );
        }
    }

    private static void registerMultiblockControllerItems() {
        MultiblockRegistrations.registerControllerItems(
                ITEMS,
                MULTIBLOCK_CONTROLLERS,
                BlockRegistry.MULTIBLOCK_CONTROLLERS
        );
    }

    private static void registerSingleBlockMachineItems() {
        for (SingleBlockMachineInstance instance
                : MachineDefinition.INSTANCES) {

            SINGLE_BLOCK_MACHINES.put(
                    instance.registryName(),
                    ITEMS.register(
                            instance.registryName(),
                            () -> new BlockItem(
                                    BlockRegistry
                                            .getSingleBlockMachine(
                                                    instance.registryName()
                                            )
                                            .get(),
                                    new Item.Properties()
                            )
                    )
            );
        }
    }

    private static void registerCoilItems() {
        for (CoilDefinition coil : CoilDefinitions.ALL) {
            COILS.put(
                    coil.id(),
                    ITEMS.register(
                            coil.itemId(),
                            () -> new BlockItem(
                                    BlockRegistry
                                            .COILS
                                            .get(coil.id())
                                            .get(),
                                    new Item.Properties()
                            )
                    )
            );
        }
    }

    private static void registerTieredItems() {
        for (MachineTier tier : MachineTier.ALL) {
            MACHINE_CASINGS.put(
                    tier.id(),
                    ITEMS.register(
                            tier.casingRegistryName(),
                            () -> new BlockItem(
                                    BlockRegistry
                                            .MACHINE_CASINGS
                                            .get(tier.id())
                                            .get(),
                                    new Item.Properties()
                            )
                    )
            );

            Map<
                    WireThickness,
                    DeferredHolder<Item, BlockItem>
                    > wires = new LinkedHashMap<>();

            Map<
                    WireThickness,
                    DeferredHolder<Item, BlockItem>
                    > insulatedWires = new LinkedHashMap<>();

            for (WireThickness thickness
                    : WireThickness.ALL) {

                String wireName =
                        EnergyWireBlock.registryName(
                                tier,
                                thickness,
                                false
                        );

                String insulatedWireName =
                        EnergyWireBlock.registryName(
                                tier,
                                thickness,
                                true
                        );

                wires.put(
                        thickness,
                        ITEMS.register(
                                wireName,
                                () -> new BlockItem(
                                        BlockRegistry
                                                .ENERGY_WIRES
                                                .get(tier.id())
                                                .get(thickness)
                                                .get(),
                                        new Item.Properties()
                                )
                        )
                );

                insulatedWires.put(
                        thickness,
                        ITEMS.register(
                                insulatedWireName,
                                () -> new BlockItem(
                                        BlockRegistry
                                                .INSULATED_ENERGY_WIRES
                                                .get(tier.id())
                                                .get(thickness)
                                                .get(),
                                        new Item.Properties()
                                )
                        )
                );
            }

            ENERGY_WIRES.put(tier.id(), wires);
            INSULATED_ENERGY_WIRES.put(
                    tier.id(),
                    insulatedWires
            );

            Map<
                    MachinePortType,
                    DeferredHolder<Item, BlockItem>
                    > ports = new LinkedHashMap<>();

            for (MachinePortType portType
                    : MachinePortType.ALL) {

                ports.put(
                        portType,
                        ITEMS.register(
                                portType.registryName(tier),
                                () -> new BlockItem(
                                        BlockRegistry
                                                .MACHINE_PORTS
                                                .get(tier.id())
                                                .get(portType)
                                                .get(),
                                        new Item.Properties()
                                )
                        )
                );
            }

            MACHINE_PORTS.put(tier.id(), ports);
        }
    }

    private static void registerStaticMachinePortItems() {
        for (StaticMachinePortType portType
                : StaticMachinePortType.ALL) {

            STATIC_MACHINE_PORTS.put(
                    portType,
                    ITEMS.register(
                            portType.id(),
                            () -> new BlockItem(
                                    BlockRegistry
                                            .STATIC_MACHINE_PORTS
                                            .get(portType)
                                            .get(),
                                    new Item.Properties()
                            )
                    )
            );
        }
    }

    private static void registerMaterialItems() {
        for (IndustrialMaterial material
                : IndustrialMaterials.ALL) {

            Map<
                    String,
                    DeferredHolder<Item, BlockItem>
                    > stoneItems = new LinkedHashMap<>();

            for (var stoneSource
                    : material.stoneSources()) {

                if (stoneSource.isExisting()) {
                    continue;
                }

                stoneItems.put(
                        stoneSource.id(),
                        ITEMS.register(
                                stoneSource.registryName(material),
                                () -> new BlockItem(
                                        BlockRegistry
                                                .getMaterialStoneBlock(
                                                        material,
                                                        stoneSource.id()
                                                )
                                                .get(),
                                        new Item.Properties()
                                )
                        )
                );
            }

            MATERIAL_STONE_ITEMS.put(
                    material.id(),
                    stoneItems
            );

            Map<
                    MaterialPart,
                    DeferredHolder<Item, ? extends Item>
                    > items = new LinkedHashMap<>();

            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (part.isFluid()) {
                    continue;
                }

                DeferredHolder<
                        Item,
                        ? extends Item
                        > item;

                if (part.isBlock()) {
                    item = ITEMS.register(
                            part.registryName(material),
                            () -> new BlockItem(
                                    BlockRegistry
                                            .getMaterialBlock(
                                                    material,
                                                    part
                                            )
                                            .get(),
                                    new Item.Properties()
                            )
                    );
                } else {
                    item = ITEMS.register(
                            part.registryName(material),
                            () -> new MaterialItem(
                                    material,
                                    part
                            )
                    );
                }

                items.put(part, item);
            }

            MATERIAL_ITEMS.put(
                    material.id(),
                    items
            );
        }
    }

    private static DeferredHolder<
            Item,
            BlockItem
            > registerBlockItem(
            String id,
            DeferredHolder<
                    ? extends net.minecraft.world.level.block.Block,
                    ? extends net.minecraft.world.level.block.Block
                    > block
    ) {
        return ITEMS.register(
                id,
                () -> new BlockItem(
                        block.get(),
                        new Item.Properties()
                )
        );
    }

    public static void register(
            IEventBus modEventBus
    ) {
        ITEMS.register(modEventBus);
    }

    public static DeferredHolder<
            Item,
            ? extends Item
            > getMaterialItem(
            IndustrialMaterial material,
            MaterialPart part
    ) {
        return MATERIAL_ITEMS
                .get(material.id())
                .get(part);
    }

    public static DeferredHolder<
            Item,
            Item
            > getSimpleItem(
            String id
    ) {
        DeferredHolder<Item, Item> item =
                SIMPLE_ITEMS.get(id);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Unknown simple item: " + id
            );
        }

        return item;
    }

    public static DeferredHolder<
            Item,
            BlockItem
            > getSimpleBlockItem(
            String id
    ) {
        DeferredHolder<Item, BlockItem> item =
                SIMPLE_BLOCK_ITEMS.get(id);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Unknown simple block item: " + id
            );
        }

        return item;
    }

    public static DeferredHolder<
            Item,
            BlockItem
            > getSimpleBlockVariantItem(
            String baseId,
            SimpleBlockVariant variant
    ) {
        Map<
                SimpleBlockVariant,
                DeferredHolder<Item, BlockItem>
                > variants =
                SIMPLE_BLOCK_VARIANT_ITEMS.get(baseId);

        if (variants == null) {
            throw new IllegalArgumentException(
                    "Unknown simple block: " + baseId
            );
        }

        DeferredHolder<Item, BlockItem> item =
                variants.get(variant);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Simple block '"
                            + baseId
                            + "' does not have variant "
                            + variant
            );
        }

        return item;
    }

    public static Collection<
            DeferredHolder<Item, ? extends Item>
            > getAllMaterialItems() {
        return MATERIAL_ITEMS
                .values()
                .stream()
                .flatMap(
                        items ->
                                items.values().stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllMaterialStoneItems() {
        return MATERIAL_STONE_ITEMS
                .values()
                .stream()
                .flatMap(
                        items ->
                                items.values().stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllMachineCasingItems() {
        return MACHINE_CASINGS.values();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllMachinePortItems() {
        return MACHINE_PORTS
                .values()
                .stream()
                .flatMap(
                        ports ->
                                ports.values().stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllStaticMachinePortItems() {
        return STATIC_MACHINE_PORTS.values();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllMultiblockControllerItems() {
        return MULTIBLOCK_CONTROLLERS.values();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllSingleBlockMachineItems() {
        return SINGLE_BLOCK_MACHINES.values();
    }

    public static Collection<
            DeferredHolder<Item, Item>
            > getAllSimpleItems() {
        return SIMPLE_ITEMS.values();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllSimpleBlockItems() {
        return SIMPLE_BLOCK_ITEMS.values();
    }

    public static DeferredHolder<
            Item,
            BlockItem
            > getActiveBlockItem(
            String id
    ) {
        DeferredHolder<Item, BlockItem> item =
                ACTIVE_BLOCK_ITEMS.get(id);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Unknown active block item: " + id
            );
        }

        return item;
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllActiveBlockItems() {
        return ACTIVE_BLOCK_ITEMS.values();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllSimpleBlockVariantItems() {
        return SIMPLE_BLOCK_VARIANT_ITEMS
                .values()
                .stream()
                .flatMap(
                        variants ->
                                variants.values().stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllCoilItems() {
        return COILS.values();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllEnergyWireItems() {
        return ENERGY_WIRES
                .values()
                .stream()
                .flatMap(
                        wires ->
                                wires.values().stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Item, BlockItem>
            > getAllInsulatedEnergyWireItems() {
        return INSULATED_ENERGY_WIRES
                .values()
                .stream()
                .flatMap(
                        wires ->
                                wires.values().stream()
                )
                .toList();
    }
}
