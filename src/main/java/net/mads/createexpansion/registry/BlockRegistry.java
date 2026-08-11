package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.ActiveBlockDefinition;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.block.DirectionalSimpleBlock;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlockVariant;
import net.mads.createexpansion.energy.CreativeEnergyBlock;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.WireThickness;
import net.mads.createexpansion.machine.FireboxBlock;
import net.mads.createexpansion.machine.MachineCasingBlock;
import net.mads.createexpansion.machine.MachineDefinition;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.SingleBlockMachineBlock;
import net.mads.createexpansion.machine.SingleBlockMachineInstance;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.machine.coil.CoilBlock;
import net.mads.createexpansion.machine.coil.CoilDefinition;
import net.mads.createexpansion.machine.coil.CoilDefinitions;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockRegistrations;
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
import net.mads.createexpansion.transport.FluidTransportRegistrations;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(
                    BuiltInRegistries.BLOCK,
                    CreateExpansion.MOD_ID
            );

    public static final Map<
            String,
            DeferredHolder<Block, MachineCasingBlock>
            > MACHINE_CASINGS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    MachinePortType,
                    DeferredHolder<Block, MachinePortBlock>
                    >
            > MACHINE_PORTS = new LinkedHashMap<>();

    public static final Map<
            StaticMachinePortType,
            DeferredHolder<Block, MachinePortBlock>
            > STATIC_MACHINE_PORTS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Block, MultiblockControllerBlock>
            > MULTIBLOCK_CONTROLLERS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Block, SingleBlockMachineBlock>
            > SINGLE_BLOCK_MACHINES = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Block, CoilBlock>
            > COILS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Block, FireboxBlock>
            > ACTIVE_BLOCKS = new LinkedHashMap<>();

    public static final Map<
            String,
            DeferredHolder<Block, Block>
            > SIMPLE_BLOCKS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    SimpleBlockVariant,
                    DeferredHolder<Block, ? extends Block>
                    >
            > SIMPLE_BLOCK_VARIANTS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<String, DeferredHolder<Block, Block>>
            > MATERIAL_STONE_BLOCKS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    MaterialPart,
                    DeferredHolder<Block, ? extends Block>
                    >
            > MATERIAL_BLOCKS = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    WireThickness,
                    DeferredHolder<Block, EnergyWireBlock>
                    >
            > ENERGY_WIRES = new LinkedHashMap<>();

    public static final Map<
            String,
            Map<
                    WireThickness,
                    DeferredHolder<Block, EnergyWireBlock>
                    >
            > INSULATED_ENERGY_WIRES = new LinkedHashMap<>();

    public static final DeferredHolder<
            Block,
            CreativeEnergyBlock
            > CREATIVE_ENERGY_PROVIDER =
            BLOCKS.register(
                    "creative_energy_provider",
                    () -> new CreativeEnergyBlock(true)
            );

    public static final DeferredHolder<
            Block,
            CreativeEnergyBlock
            > CREATIVE_ENERGY_CONSUMER =
            BLOCKS.register(
                    "creative_energy_consumer",
                    () -> new CreativeEnergyBlock(false)
            );

    public static final DeferredHolder<
            Block,
            FireboxBlock
            > FIREBRICK_FIREBOX =
            registerActiveBlock(
                    SimpleBlocks.ACTIVE.getFirst()
            );

    public static final DeferredHolder<
            Block,
            KineticSifterBlock
            > KINETIC_SIFTER =
            KineticSifterRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticCentrifugeBlock
            > KINETIC_CENTRIFUGE =
            KineticCentrifugeRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticCentrifugePartBlock
            > KINETIC_CENTRIFUGE_PART =
            KineticCentrifugeRegistration.registerPartBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticLatheBlock
            > KINETIC_LATHE =
            KineticLatheRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticRollingMillBlock
            > KINETIC_ROLLING_MILL =
            KineticRollingMillRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticWireDrawerBlock
            > KINETIC_WIRE_DRAWER =
            KineticWireDrawerRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticWireDrawerPartBlock
            > KINETIC_WIRE_DRAWER_PART =
            KineticWireDrawerRegistration.registerPartBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            HydraulicPressBlock
            > HYDRAULIC_PRESS =
            HydraulicPressRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            KineticCoilingMachineBlock
            > SPRING_COILING_MACHINE =
            KineticCoilingMachineRegistration.registerBlock(BLOCKS);

    public static final DeferredHolder<
            Block,
            FoundryCasingBlock
            > FOUNDRY_CASING =
            BLOCKS.register(
                    "foundry_casing",
                    () -> new FoundryCasingBlock()
            );

    public static final DeferredHolder<
            Block,
            FoundryControllerBlock
            > FOUNDRY_CONTROLLER =
            BLOCKS.register(
                    "foundry_controller",
                    () -> new FoundryControllerBlock()
            );

    public static final DeferredHolder<
            Block,
            FoundryControllerBlock
            > CREATIVE_FOUNDRY_CONTROLLER =
            BLOCKS.register(
                    "creative_foundry_controller",
                    () -> new FoundryControllerBlock()
            );

    public static final DeferredHolder<
            Block,
            FoundryHatchBlock
            > FOUNDRY_INPUT_HATCH =
            BLOCKS.register(
                    FoundryHatchType.INPUT.id(),
                    () -> new FoundryHatchBlock(
                            FoundryHatchType.INPUT
                    )
            );

    public static final DeferredHolder<
            Block,
            FoundryHatchBlock
            > FOUNDRY_OUTPUT_HATCH =
            BLOCKS.register(
                    FoundryHatchType.OUTPUT.id(),
                    () -> new FoundryHatchBlock(
                            FoundryHatchType.OUTPUT
                    )
            );

    public static final DeferredHolder<
            Block,
            FoundryHatchBlock
            > FOUNDRY_INPUT_BUS =
            BLOCKS.register(
                    FoundryHatchType.INPUT_BUS.id(),
                    () -> new FoundryHatchBlock(
                            FoundryHatchType.INPUT_BUS
                    )
            );

    public static final DeferredHolder<
            Block,
            FoundryDrainBlock
            > FOUNDRY_DRAIN =
            BLOCKS.register(
                    "foundry_drain",
                    () -> new FoundryDrainBlock()
            );

    public static final DeferredHolder<
            Block,
            FoundryMoldCasterBlock
            > FOUNDRY_MOLD_CASTER =
            BLOCKS.register(
                    "foundry_mold_caster",
                    () -> new FoundryMoldCasterBlock()
            );

    static {
        FluidTransportRegistrations.registerBlocks(BLOCKS);
        registerMultiblockControllers();
        registerSingleBlockMachines();
        registerCoils();
        registerActiveBlocks();
        registerTieredBlocks();
        registerStaticMachinePorts();
        registerMaterialBlocks();
        registerSimpleBlocks();
    }

    private BlockRegistry() {
    }

    private static void registerMultiblockControllers() {
        MultiblockRegistrations.registerControllerBlocks(
                BLOCKS,
                MULTIBLOCK_CONTROLLERS
        );
    }

    private static void registerSingleBlockMachines() {
        for (SingleBlockMachineInstance instance
                : MachineDefinition.INSTANCES) {

            SINGLE_BLOCK_MACHINES.put(
                    instance.registryName(),
                    BLOCKS.register(
                            instance.registryName(),
                            () -> new SingleBlockMachineBlock(
                                    instance
                            )
                    )
            );
        }
    }

    private static void registerCoils() {
        for (CoilDefinition coil : CoilDefinitions.ALL) {
            COILS.put(
                    coil.id(),
                    BLOCKS.register(
                            coil.blockId(),
                            () -> new CoilBlock(coil)
                    )
            );
        }
    }

    private static DeferredHolder<
            Block,
            FireboxBlock
            > registerActiveBlock(
            ActiveBlockDefinition definition
    ) {
        DeferredHolder<Block, FireboxBlock> block =
                BLOCKS.register(
                        definition.id(),
                        () -> new FireboxBlock(activeBlockProperties(definition), definition)
                );

        ACTIVE_BLOCKS.put(
                definition.id(),
                block
        );

        return block;
    }

    private static BlockBehaviour.Properties activeBlockProperties(
            ActiveBlockDefinition definition
    ) {
        return BlockBehaviour.Properties.of()
                .requiresCorrectToolForDrops()
                .strength(
                        definition.hardness(),
                        definition.resistance()
                )
                .lightLevel(
                        state ->
                                state.getValue(
                                        FireboxBlock.ACTIVE
                                )
                                        ? 12
                                        : 0
                )
                .sound(SoundType.STONE);
    }

    private static void registerActiveBlocks() {
        for (ActiveBlockDefinition definition
                : SimpleBlocks.ACTIVE) {

            if (ACTIVE_BLOCKS.containsKey(
                    definition.id()
            )) {
                continue;
            }

            registerActiveBlock(definition);
        }
    }

    private static void registerTieredBlocks() {
        for (MachineTier tier : MachineTier.ALL) {
            MACHINE_CASINGS.put(
                    tier.id(),
                    BLOCKS.register(
                            tier.casingRegistryName(),
                            () -> new MachineCasingBlock(tier)
                    )
            );

            Map<
                    WireThickness,
                    DeferredHolder<Block, EnergyWireBlock>
                    > wires = new LinkedHashMap<>();

            Map<
                    WireThickness,
                    DeferredHolder<Block, EnergyWireBlock>
                    > insulatedWires = new LinkedHashMap<>();

            for (WireThickness thickness : WireThickness.ALL) {
                wires.put(
                        thickness,
                        BLOCKS.register(
                                EnergyWireBlock.registryName(
                                        tier,
                                        thickness,
                                        false
                                ),
                                () -> new EnergyWireBlock(
                                        tier,
                                        thickness,
                                        false
                                )
                        )
                );

                insulatedWires.put(
                        thickness,
                        BLOCKS.register(
                                EnergyWireBlock.registryName(
                                        tier,
                                        thickness,
                                        true
                                ),
                                () -> new EnergyWireBlock(
                                        tier,
                                        thickness,
                                        true
                                )
                        )
                );
            }

            ENERGY_WIRES.put(
                    tier.id(),
                    wires
            );

            INSULATED_ENERGY_WIRES.put(
                    tier.id(),
                    insulatedWires
            );

            Map<
                    MachinePortType,
                    DeferredHolder<Block, MachinePortBlock>
                    > ports = new LinkedHashMap<>();

            for (MachinePortType portType
                    : MachinePortType.ALL) {

                ports.put(
                        portType,
                        BLOCKS.register(
                                portType.registryName(tier),
                                () -> new MachinePortBlock(
                                        tier,
                                        portType
                                )
                        )
                );
            }

            MACHINE_PORTS.put(
                    tier.id(),
                    ports
            );
        }
    }

    private static void registerStaticMachinePorts() {
        for (StaticMachinePortType portType
                : StaticMachinePortType.ALL) {

            STATIC_MACHINE_PORTS.put(
                    portType,
                    BLOCKS.register(
                            portType.id(),
                            () -> new MachinePortBlock(
                                    portType
                            )
                    )
            );
        }
    }

    private static void registerMaterialBlocks() {
        for (IndustrialMaterial material
                : IndustrialMaterials.ALL) {

            Map<
                    String,
                    DeferredHolder<Block, Block>
                    > stoneBlocks =
                    new LinkedHashMap<>();

            for (var stoneSource
                    : material.stoneSources()) {

                if (stoneSource.isExisting()) {
                    continue;
                }

                stoneBlocks.put(
                        stoneSource.id(),
                        BLOCKS.register(
                                stoneSource.registryName(
                                        material
                                ),
                                () -> new Block(
                                        BlockBehaviour
                                                .Properties
                                                .of()
                                )
                        )
                );
            }

            MATERIAL_STONE_BLOCKS.put(
                    material.id(),
                    stoneBlocks
            );

            Map<
                    MaterialPart,
                    DeferredHolder<Block, ? extends Block>
                    > blocks =
                    new LinkedHashMap<>();

            for (MaterialPart part
                    : material.parts()) {

                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (!part.isBlock()) {
                    continue;
                }

                DeferredHolder<
                        Block,
                        MaterialBlock
                        > block =
                        BLOCKS.register(
                                part.registryName(material),
                                () -> new MaterialBlock(
                                        material,
                                        part
                                )
                        );

                blocks.put(
                        part,
                        block
                );
            }

            MATERIAL_BLOCKS.put(
                    material.id(),
                    blocks
            );
        }
    }

    private static void registerSimpleBlocks() {
        for (SimpleBlockDefinition definition
                : SimpleBlocks.ALL) {

            DeferredHolder<Block, Block> baseBlock =
                    BLOCKS.register(
                            definition.id(),
                            () -> createSimpleBlock(definition)
                    );

            SIMPLE_BLOCKS.put(
                    definition.id(),
                    baseBlock
            );

            Map<
                    SimpleBlockVariant,
                    DeferredHolder<Block, ? extends Block>
                    > variants =
                    new LinkedHashMap<>();

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                variants.put(
                        variant,
                        registerSimpleBlockVariant(
                                definition,
                                variant,
                                baseBlock
                        )
                );
            }

            SIMPLE_BLOCK_VARIANTS.put(
                    definition.id(),
                    variants
            );
        }
    }

    private static Block createSimpleBlock(
            SimpleBlockDefinition definition
    ) {
        BlockBehaviour.Properties properties =
                simpleBlockProperties(definition);

        if (definition.hasFaceTextures()) {
            return new DirectionalSimpleBlock(properties);
        }

        return new Block(properties);
    }

    private static BlockBehaviour.Properties simpleBlockProperties(
            SimpleBlockDefinition definition
    ) {
        return BlockBehaviour.Properties.of()
                .strength(
                        definition.hardness(),
                        definition.resistance()
                )
                .requiresCorrectToolForDrops();
    }

    private static DeferredHolder<
            Block,
            ? extends Block
            > registerSimpleBlockVariant(
            SimpleBlockDefinition definition,
            SimpleBlockVariant variant,
            DeferredHolder<Block, Block> baseBlock
    ) {
        String registryName =
                definition.variantId(variant);

        return switch (variant) {
            case SLAB ->
                    BLOCKS.register(
                            registryName,
                            () -> new SlabBlock(
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );

            case STAIR ->
                    BLOCKS.register(
                            registryName,
                            () -> new StairBlock(
                                    baseBlock
                                            .get()
                                            .defaultBlockState(),
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );

            case WALL ->
                    BLOCKS.register(
                            registryName,
                            () -> new WallBlock(
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );

            case FENCE ->
                    BLOCKS.register(
                            registryName,
                            () -> new FenceBlock(
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );

            case FENCE_GATE ->
                    BLOCKS.register(
                            registryName,
                            () -> new FenceGateBlock(
                                    WoodType.OAK,
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );

            case BUTTON ->
                    BLOCKS.register(
                            registryName,
                            () -> new ButtonBlock(
                                    BlockSetType.OAK,
                                    30,
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );

            case PRESSURE_PLATE ->
                    BLOCKS.register(
                            registryName,
                            () -> new PressurePlateBlock(
                                    BlockSetType.OAK,
                                    copyProperties(
                                            baseBlock.get()
                                    )
                            )
                    );
        };
    }

    private static BlockBehaviour.Properties copyProperties(
            Block block
    ) {
        return BlockBehaviour.Properties.ofFullCopy(
                block
        );
    }

    public static void register(
            IEventBus modEventBus
    ) {
        BLOCKS.register(modEventBus);
    }

    public static DeferredHolder<
            Block,
            ? extends Block
            > getMaterialBlock(
            IndustrialMaterial material,
            MaterialPart part
    ) {
        Map<
                MaterialPart,
                DeferredHolder<Block, ? extends Block>
                > blocks =
                MATERIAL_BLOCKS.get(
                        material.id()
                );

        if (blocks == null) {
            throw new IllegalArgumentException(
                    "Unknown material: "
                            + material.id()
            );
        }

        DeferredHolder<
                Block,
                ? extends Block
                > block =
                blocks.get(part);

        if (block == null) {
            throw new IllegalArgumentException(
                    "Material '"
                            + material.id()
                            + "' does not have block part "
                            + part
            );
        }

        return block;
    }

    public static DeferredHolder<
            Block,
            Block
            > getMaterialStoneBlock(
            IndustrialMaterial material,
            String stoneId
    ) {
        Map<
                String,
                DeferredHolder<Block, Block>
                > blocks =
                MATERIAL_STONE_BLOCKS.get(
                        material.id()
                );

        if (blocks == null) {
            throw new IllegalArgumentException(
                    "Unknown material: "
                            + material.id()
            );
        }

        DeferredHolder<Block, Block> block =
                blocks.get(stoneId);

        if (block == null) {
            throw new IllegalArgumentException(
                    "Material '"
                            + material.id()
                            + "' does not have stone block '"
                            + stoneId
                            + "'"
            );
        }

        return block;
    }

    public static DeferredHolder<
            Block,
            Block
            > getSimpleBlock(
            String id
    ) {
        DeferredHolder<Block, Block> block =
                SIMPLE_BLOCKS.get(id);

        if (block == null) {
            throw new IllegalArgumentException(
                    "Unknown simple block: "
                            + id
            );
        }

        return block;
    }

    public static DeferredHolder<
            Block,
            ? extends Block
            > getSimpleBlockVariant(
            String baseId,
            SimpleBlockVariant variant
    ) {
        Map<
                SimpleBlockVariant,
                DeferredHolder<Block, ? extends Block>
                > variants =
                SIMPLE_BLOCK_VARIANTS.get(baseId);

        if (variants == null) {
            throw new IllegalArgumentException(
                    "Unknown simple block: "
                            + baseId
            );
        }

        DeferredHolder<
                Block,
                ? extends Block
                > block =
                variants.get(variant);

        if (block == null) {
            throw new IllegalArgumentException(
                    "Simple block '"
                            + baseId
                            + "' does not have variant "
                            + variant
            );
        }

        return block;
    }

    public static Collection<
            DeferredHolder<Block, Block>
            > getAllSimpleBlocks() {
        return SIMPLE_BLOCKS.values();
    }

    public static DeferredHolder<
            Block,
            FireboxBlock
            > getActiveBlock(
            String id
    ) {
        DeferredHolder<Block, FireboxBlock> block =
                ACTIVE_BLOCKS.get(id);

        if (block == null) {
            throw new IllegalArgumentException(
                    "Unknown active block: "
                            + id
            );
        }

        return block;
    }

    public static Collection<
            DeferredHolder<Block, FireboxBlock>
            > getAllActiveBlocks() {
        return ACTIVE_BLOCKS.values();
    }

    public static Collection<
            DeferredHolder<Block, ? extends Block>
            > getAllSimpleBlockVariants() {
        return SIMPLE_BLOCK_VARIANTS
                .values()
                .stream()
                .flatMap(
                        variants ->
                                variants
                                        .values()
                                        .stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Block, Block>
            > getAllMaterialStoneBlocks() {
        return MATERIAL_STONE_BLOCKS
                .values()
                .stream()
                .flatMap(
                        blocks ->
                                blocks
                                        .values()
                                        .stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Block, ? extends Block>
            > getAllMaterialBlocks() {
        return MATERIAL_BLOCKS
                .values()
                .stream()
                .flatMap(
                        blocks ->
                                blocks
                                        .values()
                                        .stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Block, MachineCasingBlock>
            > getAllMachineCasings() {
        return MACHINE_CASINGS.values();
    }

    public static Collection<
            DeferredHolder<Block, MachinePortBlock>
            > getAllMachinePorts() {
        return MACHINE_PORTS
                .values()
                .stream()
                .flatMap(
                        ports ->
                                ports
                                        .values()
                                        .stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Block, MachinePortBlock>
            > getAllStaticMachinePorts() {
        return STATIC_MACHINE_PORTS.values();
    }

    public static Collection<
            DeferredHolder<Block, MultiblockControllerBlock>
            > getAllMultiblockControllers() {
        return MULTIBLOCK_CONTROLLERS.values();
    }

    public static DeferredHolder<
            Block,
            SingleBlockMachineBlock
            > getSingleBlockMachine(
            String id
    ) {
        DeferredHolder<Block, SingleBlockMachineBlock> block =
                SINGLE_BLOCK_MACHINES.get(id);

        if (block == null) {
            throw new IllegalArgumentException(
                    "Unknown singleblock machine: "
                            + id
            );
        }

        return block;
    }

    public static Collection<
            DeferredHolder<Block, SingleBlockMachineBlock>
            > getAllSingleBlockMachines() {
        return SINGLE_BLOCK_MACHINES.values();
    }

    public static Collection<
            DeferredHolder<Block, CoilBlock>
            > getAllCoils() {
        return COILS.values();
    }

    public static Collection<
            DeferredHolder<Block, EnergyWireBlock>
            > getAllEnergyWires() {
        return ENERGY_WIRES
                .values()
                .stream()
                .flatMap(
                        wires ->
                                wires
                                        .values()
                                        .stream()
                )
                .toList();
    }

    public static Collection<
            DeferredHolder<Block, EnergyWireBlock>
            > getAllInsulatedEnergyWires() {
        return INSULATED_ENERGY_WIRES
                .values()
                .stream()
                .flatMap(
                        wires ->
                                wires
                                        .values()
                                        .stream()
                )
                .toList();
    }
}
