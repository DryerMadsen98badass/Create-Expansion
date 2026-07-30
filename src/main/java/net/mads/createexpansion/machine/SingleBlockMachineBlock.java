package net.mads.createexpansion.machine;

import com.mojang.serialization.MapCodec;
import net.mads.createexpansion.energy.CEEnergyNetwork;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SingleBlockMachineBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<SingleBlockMachineBlock> CODEC =
            simpleCodec(properties -> new SingleBlockMachineBlock(properties, null));

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty OVERLAY_FRAME = IntegerProperty.create("overlay_frame", 0, 9);

    private final SingleBlockMachineInstance instance;

    public SingleBlockMachineBlock(
            SingleBlockMachineInstance instance
    ) {
        this(
                BlockBehaviour.Properties.of()
                        .strength(
                                instance.definition().hardness(),
                                instance.definition().resistance()
                        )
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.METAL),
                instance
        );
    }

    public SingleBlockMachineBlock(
            BlockBehaviour.Properties properties,
            @Nullable SingleBlockMachineInstance instance
    ) {
        super(properties);
        this.instance = instance;
        registerDefaultState(
                stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(ACTIVE, false)
                        .setValue(OVERLAY_FRAME, 0)
        );
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public SingleBlockMachineInstance instance() {
        return instance;
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new SingleBlockMachineBlockEntity(
                pos,
                state
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (instance == null) {
            return;
        }

        if (instance.definition().resource() == SingleBlockMachineResource.STEAM
                && instance.definition().resourceMode() == SingleBlockMachineResourceMode.CONSUMES) {
            tooltip.add(Component.literal("Steam: " + instance.steamUsage() + " mB/t")
                    .withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.literal("Processes at " + instance.tier().steamDurationMultiplier() + "x recipe duration")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public BlockState getStateForPlacement(
            BlockPlaceContext context
    ) {
        return defaultBlockState()
                .setValue(
                        FACING,
                        context.getHorizontalDirection().getOpposite()
                );
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return type == BlockEntityRegistry.SINGLE_BLOCK_MACHINE.get()
                ? (tickerLevel, pos, tickerState, blockEntity) ->
                ((SingleBlockMachineBlockEntity) blockEntity).serverTick()
                : null;
    }

    @Override
    public RenderShape getRenderShape(
            BlockState state
    ) {
        return RenderShape.MODEL;
    }

    @Override
    protected void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (instance != null && instance.definition().power() == SingleBlockMachinePower.ELECTRIC) {
            CEEnergyNetwork.invalidate(level);
        }
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean movedByPiston
    ) {
        if (!state.is(newState.getBlock())
                && instance != null
                && instance.definition().power() == SingleBlockMachinePower.ELECTRIC) {
            CEEnergyNetwork.invalidate(level);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof SingleBlockMachineBlockEntity machine) {
            ((IPlayerExtension) player).openMenu(machine, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void stepOn(
            Level level,
            BlockPos pos,
            BlockState state,
            Entity entity
    ) {
        if (!level.isClientSide()
                && state.getValue(ACTIVE)
                && entity instanceof LivingEntity living) {
            living.hurt(
                    level.damageSources().hotFloor(),
                    2.0F
            );
        }

        super.stepOn(
                level,
                pos,
                state,
                entity
        );
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.getValue(ACTIVE)
                || instance == null
                || instance.definition().power() != SingleBlockMachinePower.STEAM) {
            return;
        }

        level.addParticle(
                ParticleTypes.CLOUD,
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D,
                (random.nextDouble() - 0.5D) * 0.02D,
                0.04D,
                (random.nextDouble() - 0.5D) * 0.02D
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(
                FACING,
                ACTIVE,
                OVERLAY_FRAME
        );
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Block.box(
                0,
                0,
                0,
                16,
                16,
                16
        );
    }
}
