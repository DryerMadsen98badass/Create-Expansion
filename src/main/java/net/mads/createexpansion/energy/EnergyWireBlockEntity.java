package net.mads.createexpansion.energy;

import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyWireBlockEntity extends BlockEntity {
    private static final int MELT_HEAT = 1000;
    private static final int FLOW_VISIBLE_TICKS = 40;

    private int currentVoltage;
    private int currentAmperage;
    private long lastTransferTick = -1;
    private int heat;

    public EnergyWireBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENERGY_WIRE.get(), pos, blockState);
    }

    public int maxVoltage() {
        return wire().map(wire -> MachineTierStats.ceTier(wire.tier())).orElse(0);
    }

    public int maxAmperage() {
        return wire().map(EnergyWireBlock::maxAmps).orElse(0);
    }

    public int currentVoltage() {
        return isFlowVisible() ? currentVoltage : 0;
    }

    public int currentAmperage() {
        return isFlowVisible() ? currentAmperage : 0;
    }

    public void incrementAmperage(int amps, int voltage) {
        if (level == null || level.isClientSide() || amps <= 0 || voltage <= 0) {
            return;
        }

        long now = level.getGameTime();
        if (lastTransferTick != now) {
            currentAmperage = 0;
            currentVoltage = 0;
            lastTransferTick = now;
        }

        currentAmperage += amps;
        currentVoltage = Math.max(currentVoltage, voltage);

        if (voltage > maxVoltage()) {
            heat += 80;
        }
        int overAmps = currentAmperage - maxAmperage();
        if (overAmps > 0) {
            heat += overAmps * 40;
        }

        contentChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnergyWireBlockEntity wire) {
        if (level.isClientSide()) {
            return;
        }
        if (wire.heat >= MELT_HEAT) {
            level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            return;
        }
        if (wire.heat > 0 && level.getGameTime() % 20 == 0) {
            wire.heat = Math.max(0, wire.heat - 25);
            wire.contentChanged();
        }
        if (wire.currentAmperage != 0 && !wire.isFlowVisible()) {
            wire.currentAmperage = 0;
            wire.currentVoltage = 0;
            wire.contentChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Heat", heat);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        heat = Math.max(0, tag.getInt("Heat"));
    }

    private boolean isFlowVisible() {
        return level != null && lastTransferTick >= 0 && level.getGameTime() - lastTransferTick <= FLOW_VISIBLE_TICKS;
    }

    private java.util.Optional<EnergyWireBlock> wire() {
        return getBlockState().getBlock() instanceof EnergyWireBlock wire ? java.util.Optional.of(wire) : java.util.Optional.empty();
    }

    private void contentChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }
}
