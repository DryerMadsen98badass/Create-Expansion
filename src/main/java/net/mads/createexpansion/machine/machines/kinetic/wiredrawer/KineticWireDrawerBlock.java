package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlock;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KineticWireDrawerBlock extends AbstractSimpleKineticMachineBlock<KineticWireDrawerBlockEntity> {
    public static final MapCodec<KineticWireDrawerBlock> CODEC = simpleCodec(properties -> new KineticWireDrawerBlock());
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 16, 16);

    @Override
    protected MapCodec<? extends KineticBlock> codec() {
        return CODEC;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Z;
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
        BlockPos partPos = partPos(context.getClickedPos());
        if (!context.getLevel().getBlockState(partPos).canBeReplaced(context)) {
            return null;
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            level.setBlock(partPos(pos), BlockRegistry.KINETIC_WIRE_DRAWER_PART.get().defaultBlockState(), 3);
            if (level.getBlockEntity(partPos(pos)) instanceof KineticWireDrawerPartBlockEntity part) {
                part.setControllerPos(pos);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            BlockPos partPos = partPos(pos);
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

    static BlockPos partPos(BlockPos controllerPos) {
        return controllerPos.south();
    }
}
