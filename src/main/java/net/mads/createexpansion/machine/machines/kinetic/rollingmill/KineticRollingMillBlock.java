package net.mads.createexpansion.machine.machines.kinetic.rollingmill;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlock;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class KineticRollingMillBlock extends AbstractSimpleKineticMachineBlock<KineticRollingMillBlockEntity> {
    public static final MapCodec<KineticRollingMillBlock> CODEC = simpleCodec(properties -> new KineticRollingMillBlock());
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public KineticRollingMillBlock() {
        super();
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.EAST));
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
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            direction = direction.getOpposite();
        }
        return defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public Class<KineticRollingMillBlockEntity> getBlockEntityClass() {
        return KineticRollingMillBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticRollingMillBlockEntity> getBlockEntityType() {
        return BlockEntityRegistry.KINETIC_ROLLING_MILL.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
