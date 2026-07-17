package net.mads.createexpansion.machine.machines.foundry;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.mads.createexpansion.machine.WrenchPickupHelper;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FoundryDrainBlock extends HorizontalDirectionalBlock implements EntityBlock, IWrenchable {
    public static final MapCodec<FoundryDrainBlock> CODEC = simpleCodec(FoundryDrainBlock::new);
    public static final BooleanProperty POURING = BooleanProperty.create("pouring");
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(5, 4, 0, 6, 7, 3),
            Block.box(6, 4, 0, 10, 5, 3),
            Block.box(10, 4, 0, 11, 7, 3)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(10, 4, 13, 11, 7, 16),
            Block.box(6, 4, 13, 10, 5, 16),
            Block.box(5, 4, 13, 6, 7, 16)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(0, 4, 10, 3, 7, 11),
            Block.box(0, 4, 6, 3, 5, 10),
            Block.box(0, 4, 5, 3, 7, 6)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(13, 4, 5, 16, 7, 6),
            Block.box(13, 4, 6, 16, 5, 10),
            Block.box(13, 4, 10, 16, 7, 11)
    );

    public FoundryDrainBlock() {
        this(BlockBehaviour.Properties.of()
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL));
    }

    public FoundryDrainBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POURING, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = outputDirection(context);
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(POURING, false);
    }

    private Direction outputDirection(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).getBlock() == net.mads.createexpansion.registry.BlockRegistry.FOUNDRY_OUTPUT_HATCH.get()) {
                return direction.getOpposite();
            }
        }
        return context.getHorizontalDirection().getOpposite();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POURING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state.getValue(FACING));
    }

    private VoxelShape shapeFor(Direction facing) {
        return switch (facing) {
            case NORTH -> SHAPE_SOUTH;
            case SOUTH -> SHAPE_NORTH;
            case WEST -> SHAPE_EAST;
            case EAST -> SHAPE_WEST;
            default -> SHAPE_SOUTH;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FoundryDrainBlockEntity drain) {
            drain.tryPour();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FoundryDrainBlockEntity drain) {
            drain.tryPour();
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return WrenchPickupHelper.pickup(this, state, context);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FoundryDrainBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != BlockEntityRegistry.FOUNDRY_DRAIN.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) ->
                FoundryDrainBlockEntity.tick(tickLevel, tickPos, tickState, (FoundryDrainBlockEntity) blockEntity);
    }
}
