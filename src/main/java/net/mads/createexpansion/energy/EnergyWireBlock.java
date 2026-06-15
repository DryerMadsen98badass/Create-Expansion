package net.mads.createexpansion.energy;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.mads.createexpansion.machine.WrenchPickupHelper;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.multiblock.MultiblockAbility;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;

import java.util.EnumMap;
import java.util.Map;

public class EnergyWireBlock extends Block implements EntityBlock, IWrenchable {
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty DISABLED_DOWN = BooleanProperty.create("disabled_down");
    public static final BooleanProperty DISABLED_UP = BooleanProperty.create("disabled_up");
    public static final BooleanProperty DISABLED_NORTH = BooleanProperty.create("disabled_north");
    public static final BooleanProperty DISABLED_SOUTH = BooleanProperty.create("disabled_south");
    public static final BooleanProperty DISABLED_WEST = BooleanProperty.create("disabled_west");
    public static final BooleanProperty DISABLED_EAST = BooleanProperty.create("disabled_east");

    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = new EnumMap<>(Direction.class);
    private static final Map<Direction, BooleanProperty> DISABLED_PROPERTY_BY_DIRECTION = new EnumMap<>(Direction.class);

    static {
        PROPERTY_BY_DIRECTION.put(Direction.DOWN, DOWN);
        PROPERTY_BY_DIRECTION.put(Direction.UP, UP);
        PROPERTY_BY_DIRECTION.put(Direction.NORTH, NORTH);
        PROPERTY_BY_DIRECTION.put(Direction.SOUTH, SOUTH);
        PROPERTY_BY_DIRECTION.put(Direction.WEST, WEST);
        PROPERTY_BY_DIRECTION.put(Direction.EAST, EAST);
        DISABLED_PROPERTY_BY_DIRECTION.put(Direction.DOWN, DISABLED_DOWN);
        DISABLED_PROPERTY_BY_DIRECTION.put(Direction.UP, DISABLED_UP);
        DISABLED_PROPERTY_BY_DIRECTION.put(Direction.NORTH, DISABLED_NORTH);
        DISABLED_PROPERTY_BY_DIRECTION.put(Direction.SOUTH, DISABLED_SOUTH);
        DISABLED_PROPERTY_BY_DIRECTION.put(Direction.WEST, DISABLED_WEST);
        DISABLED_PROPERTY_BY_DIRECTION.put(Direction.EAST, DISABLED_EAST);
    }

    private final MachineTier tier;
    private final WireThickness thickness;
    private final boolean insulated;
    private final VoxelShape[] shapes;

    public EnergyWireBlock(MachineTier tier, WireThickness thickness, boolean insulated) {
        super(BlockBehaviour.Properties.of()
                .strength(1.0F, 2.0F)
                .sound(insulated ? SoundType.WOOL : SoundType.METAL)
                .noOcclusion());
        this.tier = tier;
        this.thickness = thickness;
        this.insulated = insulated;
        this.shapes = buildShapes(thickness.pixels());
        registerDefaultState(stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(DISABLED_DOWN, false)
                .setValue(DISABLED_UP, false)
                .setValue(DISABLED_NORTH, false)
                .setValue(DISABLED_SOUTH, false)
                .setValue(DISABLED_WEST, false)
                .setValue(DISABLED_EAST, false));
    }

    public MachineTier tier() {
        return tier;
    }

    public WireThickness thickness() {
        return thickness;
    }

    public boolean insulated() {
        return insulated;
    }

    public int maxAmps() {
        int amps = MachineTierStats.ceBaseAmps(tier) * thickness.baseAmps();
        return insulated ? amps * 2 : amps;
    }

    public static boolean hasEnabledConnection(BlockState state, Direction direction) {
        return state.getBlock() instanceof EnergyWireBlock
                && !state.getValue(DISABLED_PROPERTY_BY_DIRECTION.get(direction));
    }

    public static boolean wiresConnect(BlockState state, Direction direction, BlockState neighborState) {
        return state.getBlock() instanceof EnergyWireBlock
                && neighborState.getBlock() instanceof EnergyWireBlock
                && hasEnabledConnection(state, direction)
                && hasEnabledConnection(neighborState, direction.getOpposite());
    }

    public String registryName() {
        return registryName(tier, thickness, insulated);
    }

    public static String registryName(MachineTier tier, WireThickness thickness, boolean insulated) {
        if (insulated) {
            return "insulated_" + thickness.id() + "_" + tier.id() + "_wire";
        }
        return thickness.id() + "_" + tier.id() + "_wire";
    }

    public static String displayName(MachineTier tier, WireThickness thickness, boolean insulated) {
        if (insulated) {
            return "Insulated " + thickness.displayName() + " " + tier.displayName() + " Wire";
        }
        return thickness.displayName() + " " + tier.displayName() + " Wire";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, DISABLED_DOWN, DISABLED_UP, DISABLED_NORTH, DISABLED_SOUTH, DISABLED_WEST, DISABLED_EAST);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && level instanceof Level serverLevel) {
            CEEnergyNetwork.invalidate(serverLevel);
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), shouldConnect(state, direction, neighborState));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        CEEnergyNetwork.invalidate(level);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            CEEnergyNetwork.invalidate(level);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Direction direction : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), shouldConnect(state, direction, level.getBlockState(pos.relative(direction))));
        }
        return state;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Direction direction = targetedConnection(context);
        BlockPos pos = context.getClickedPos();
        BlockPos neighborPos = pos.relative(direction);
        LevelAccessor level = context.getLevel();
        BlockState neighborState = level.getBlockState(neighborPos);

        boolean disabled = !state.getValue(DISABLED_PROPERTY_BY_DIRECTION.get(direction));
        BlockState updated = state
                .setValue(DISABLED_PROPERTY_BY_DIRECTION.get(direction), disabled)
                .setValue(PROPERTY_BY_DIRECTION.get(direction), !disabled && connectsTo(neighborState));

        if (!level.isClientSide()) {
            level.setBlock(pos, updated, 3);
            if (neighborState.getBlock() instanceof EnergyWireBlock) {
                Direction opposite = direction.getOpposite();
                BlockState updatedNeighbor = neighborState
                        .setValue(DISABLED_PROPERTY_BY_DIRECTION.get(opposite), disabled)
                        .setValue(PROPERTY_BY_DIRECTION.get(opposite), !disabled && connectsTo(updated));
                level.setBlock(neighborPos, updatedNeighbor, 3);
            }
            if (level instanceof Level serverLevel) {
                CEEnergyNetwork.invalidate(serverLevel);
            }
            IWrenchable.playRotateSound(context.getLevel(), pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return WrenchPickupHelper.pickup(this, state, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[shapeIndex(state)];
    }

    private static boolean connectsTo(BlockState state) {
        if (state.getBlock() instanceof EnergyWireBlock || state.getBlock() instanceof CreativeEnergyBlock) {
            return true;
        }
        return state.getBlock() instanceof MachinePortBlock port
                && (port.abilities().contains(MultiblockAbility.ENERGY_INPUT)
                || port.abilities().contains(MultiblockAbility.ENERGY_OUTPUT));
    }

    private static boolean shouldConnect(BlockState state, Direction direction, BlockState neighborState) {
        if (state.getValue(DISABLED_PROPERTY_BY_DIRECTION.get(direction))) {
            return false;
        }
        if (neighborState.getBlock() instanceof EnergyWireBlock) {
            return !neighborState.getValue(DISABLED_PROPERTY_BY_DIRECTION.get(direction.getOpposite()));
        }
        return connectsTo(neighborState);
    }

    private static Direction targetedConnection(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 hit = context.getClickLocation();
        double x = hit.x - (pos.getX() + 0.5D);
        double y = hit.y - (pos.getY() + 0.5D);
        double z = hit.z - (pos.getZ() + 0.5D);
        double absX = Math.abs(x);
        double absY = Math.abs(y);
        double absZ = Math.abs(z);

        if (absX >= absY && absX >= absZ) {
            return x >= 0 ? Direction.EAST : Direction.WEST;
        }
        if (absY >= absX && absY >= absZ) {
            return y >= 0 ? Direction.UP : Direction.DOWN;
        }
        return z >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static VoxelShape[] buildShapes(int pixels) {
        VoxelShape[] result = new VoxelShape[64];
        double min = (16 - pixels) / 2.0D;
        double max = min + pixels;
        VoxelShape center = Block.box(min, min, min, max, max, max);

        for (int index = 0; index < result.length; index++) {
            VoxelShape shape = center;
            if ((index & bit(Direction.DOWN)) != 0) {
                shape = Shapes.or(shape, Block.box(min, 0, min, max, min, max));
            }
            if ((index & bit(Direction.UP)) != 0) {
                shape = Shapes.or(shape, Block.box(min, max, min, max, 16, max));
            }
            if ((index & bit(Direction.NORTH)) != 0) {
                shape = Shapes.or(shape, Block.box(min, min, 0, max, max, min));
            }
            if ((index & bit(Direction.SOUTH)) != 0) {
                shape = Shapes.or(shape, Block.box(min, min, max, max, max, 16));
            }
            if ((index & bit(Direction.WEST)) != 0) {
                shape = Shapes.or(shape, Block.box(0, min, min, min, max, max));
            }
            if ((index & bit(Direction.EAST)) != 0) {
                shape = Shapes.or(shape, Block.box(max, min, min, 16, max, max));
            }
            result[index] = shape;
        }
        return result;
    }

    private static int shapeIndex(BlockState state) {
        int index = 0;
        for (Direction direction : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
                index |= bit(direction);
            }
        }
        return index;
    }

    private static int bit(Direction direction) {
        return 1 << direction.ordinal();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyWireBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != BlockEntityRegistry.ENERGY_WIRE.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) ->
                EnergyWireBlockEntity.tick(tickLevel, tickPos, tickState, (EnergyWireBlockEntity) blockEntity);
    }
}
