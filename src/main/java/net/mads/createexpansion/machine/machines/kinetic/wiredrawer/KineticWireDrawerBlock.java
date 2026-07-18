package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlock;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KineticWireDrawerBlock extends AbstractSimpleKineticMachineBlock<KineticWireDrawerBlockEntity> {
    public static final MapCodec<KineticWireDrawerBlock> CODEC = simpleCodec(properties -> new KineticWireDrawerBlock());
    public static final net.minecraft.world.level.block.state.properties.DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 16, 16);

    public KineticWireDrawerBlock() {
        super();
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected MapCodec<? extends KineticBlock> codec() {
        return CODEC;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = facing.getOpposite();
        }
        BlockPos partPos = partPos(context.getClickedPos(), facing);
        if (!context.getLevel().getBlockState(partPos).canBeReplaced(context)) {
            return null;
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockPos partPos = partPos(pos, state.getValue(FACING));
            level.setBlock(partPos, BlockRegistry.KINETIC_WIRE_DRAWER_PART.get().defaultBlockState(), 3);
            if (level.getBlockEntity(partPos) instanceof KineticWireDrawerPartBlockEntity part) {
                part.setControllerPos(pos);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            BlockPos partPos = partPos(pos, state.getValue(FACING));
            if (level.getBlockState(partPos).is(BlockRegistry.KINETIC_WIRE_DRAWER_PART.get())
                    && level.getBlockEntity(partPos) instanceof KineticWireDrawerPartBlockEntity part
                    && pos.equals(part.controllerPos())) {
                level.removeBlock(partPos, false);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public Class<KineticWireDrawerBlockEntity> getBlockEntityClass() {
        return KineticWireDrawerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticWireDrawerBlockEntity> getBlockEntityType() {
        return BlockEntityRegistry.KINETIC_WIRE_DRAWER.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    static BlockPos partPos(BlockPos controllerPos, Direction facing) {
        return controllerPos.relative(facing);
    }
}
