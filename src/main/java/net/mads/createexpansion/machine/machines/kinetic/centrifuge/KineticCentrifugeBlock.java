package net.mads.createexpansion.machine.machines.kinetic.centrifuge;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KineticCentrifugeBlock extends KineticBlock implements IBE<KineticCentrifugeBlockEntity> {
    public static final String ID = KineticCentrifugeRegistration.ID;
    public static final MapCodec<KineticCentrifugeBlock> CODEC = simpleCodec(KineticCentrifugeBlock::new);

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 14, 16);

    public KineticCentrifugeBlock() {
        this(BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.WOOD)
                .noOcclusion());
    }

    public KineticCentrifugeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends KineticBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        for (BlockPos partPos : partPositions(pos)) {
            if (!context.getLevel().getBlockState(partPos).canBeReplaced(context)) {
                return null;
            }
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            placeParts(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            removeParts(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useCentrifugeItem(level, pos, player, hand, stack);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return useCentrifugeWithoutItem(level, pos, player);
    }

    public static ItemInteractionResult useCentrifugeItem(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof KineticCentrifugeBlockEntity centrifuge)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        return centrifuge.handleHeldItem(player, hand, stack)
                ? ItemInteractionResult.SUCCESS
                : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static InteractionResult useCentrifugeWithoutItem(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof KineticCentrifugeBlockEntity centrifuge)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return centrifuge.extractToPlayer(player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public Class<KineticCentrifugeBlockEntity> getBlockEntityClass() {
        return KineticCentrifugeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticCentrifugeBlockEntity> getBlockEntityType() {
        return BlockEntityRegistry.KINETIC_CENTRIFUGE.get();
    }

    private static Iterable<BlockPos> partPositions(BlockPos center) {
        return BlockPos.betweenClosed(center.offset(-1, 0, -1), center.offset(1, 0, 1));
    }

    private static void placeParts(Level level, BlockPos center) {
        for (BlockPos current : partPositions(center)) {
            if (current.equals(center)) {
                continue;
            }

            BlockPos partPos = current.immutable();
            level.setBlock(partPos, BlockRegistry.KINETIC_CENTRIFUGE_PART.get().defaultBlockState(), 3);
            if (level.getBlockEntity(partPos) instanceof KineticCentrifugePartBlockEntity part) {
                part.setControllerPos(center);
            }
        }
    }

    private static void removeParts(Level level, BlockPos center) {
        for (BlockPos current : partPositions(center)) {
            if (current.equals(center)) {
                continue;
            }

            BlockPos partPos = current.immutable();
            if (!level.getBlockState(partPos).is(BlockRegistry.KINETIC_CENTRIFUGE_PART.get())) {
                continue;
            }
            if (level.getBlockEntity(partPos) instanceof KineticCentrifugePartBlockEntity part && center.equals(part.controllerPos())) {
                level.removeBlock(partPos, false);
            }
        }
    }
}
