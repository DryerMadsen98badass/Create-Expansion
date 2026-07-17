package net.mads.createexpansion.machine;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;

import java.util.Set;

public class MachinePortBlock extends DirectionalKineticBlock implements IBE<MachinePortBlockEntity> {
    public static final MapCodec<MachinePortBlock> CODEC = simpleCodec(properties ->
            new MachinePortBlock(MachineTier.LV, MachinePortType.INPUT_BUS, properties));

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 16, 16);

    private final MachineTier tier;
    private final MachinePortType portType;
    private final StaticMachinePortType staticPortType;

    public MachinePortBlock(MachineTier tier, MachinePortType portType) {
        this(tier, portType, BlockBehaviour.Properties.of()
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL));
    }

    public MachinePortBlock(MachineTier tier, MachinePortType portType, BlockBehaviour.Properties properties) {
        super(properties);
        this.tier = tier;
        this.portType = portType;
        this.staticPortType = null;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public MachinePortBlock(StaticMachinePortType staticPortType) {
        this(staticPortType, BlockBehaviour.Properties.of()
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL));
    }

    public MachinePortBlock(StaticMachinePortType staticPortType, BlockBehaviour.Properties properties) {
        super(properties);
        this.tier = null;
        this.portType = null;
        this.staticPortType = staticPortType;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public MachineTier tier() {
        return tier;
    }

    public boolean hasTier() {
        return tier != null;
    }

    public MachinePortType portType() {
        return portType;
    }

    public StaticMachinePortType staticPortType() {
        return staticPortType;
    }

    public boolean usesTint() {
        return tier != null || (staticPortType != null && staticPortType.tinted());
    }

    public int tintColor() {
        if (tier != null) {
            return tier.color();
        }

        return staticPortType != null ? staticPortType.tintColor() : -1;
    }

    public MachineTier effectiveTier() {
        return tier != null ? tier : staticPortType.tier();
    }

    public Set<MultiblockAbility> abilities() {
        return portType != null ? portType.abilities() : staticPortType.abilities();
    }

    public boolean isKineticPort() {
        return abilities().contains(MultiblockAbility.KINETIC_INPUT) || abilities().contains(MultiblockAbility.KINETIC_OUTPUT);
    }

    @Override
    protected MapCodec<? extends DirectionalKineticBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction nearestLookingDirection = context.getNearestLookingDirection();
        boolean sneakPlacing = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return defaultBlockState().setValue(FACING, sneakPlacing ? nearestLookingDirection : nearestLookingDirection.getOpposite());
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        Direction facing = originalState.getValue(FACING);
        if (facing.getAxis() == targetedFace.getAxis()) {
            return originalState;
        }

        return originalState.setValue(FACING, facing.getClockWise(targetedFace.getAxis()));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return WrenchPickupHelper.pickup(this, state, context);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (WrenchPickupHelper.isHoldingWrench(player)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MachinePortBlockEntity port) {
            ((IPlayerExtension) player).openMenu(port, pos);
        }
        return InteractionResult.SUCCESS;
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
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return isKineticPort() && face == state.getValue(FACING);
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
    public Class<MachinePortBlockEntity> getBlockEntityClass() {
        return MachinePortBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MachinePortBlockEntity> getBlockEntityType() {
        return BlockEntityRegistry.MACHINE_PORT.get();
    }
}
