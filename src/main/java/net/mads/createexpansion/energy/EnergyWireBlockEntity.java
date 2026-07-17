package net.mads.createexpansion.energy;

import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyWireBlockEntity extends BlockEntity {
    private static final int MELT_HEAT = 1000;
    private static final int FLOW_VISIBLE_TICKS = 40;

    private int currentVoltage;
    private int currentAmperage;
    private int currentCEt;
    private int displayVoltage;
    private int displayCEt;
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

    public int currentCEt() {
        return isFlowVisible() ? (displayCEt > 0 ? displayCEt : currentCEt) : 0;
    }

    public int displayVoltage() {
        return isFlowVisible() ? (displayVoltage > 0 ? displayVoltage : currentVoltage) : 0;
    }

    public void incrementAmperage(int amps, int voltage) {
        if (level == null || level.isClientSide() || amps <= 0 || voltage <= 0) {
            return;
        }

        resetFlowForNewTick();

        currentAmperage += amps;
        currentCEt += amps * voltage;
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

    public void incrementLoad(int cePerTick, int voltage) {
        if (level == null || level.isClientSide() || cePerTick <= 0 || voltage <= 0) {
            return;
        }

        resetFlowForNewTick();

        displayCEt += cePerTick;
        displayVoltage = Math.max(displayVoltage, voltage);

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
        if ((wire.currentAmperage != 0 || wire.currentCEt != 0 || wire.displayCEt != 0) && !wire.isFlowVisible()) {
            wire.currentAmperage = 0;
            wire.currentVoltage = 0;
            wire.currentCEt = 0;
            wire.displayVoltage = 0;
            wire.displayCEt = 0;
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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Heat", heat);
        writeFlowUpdate(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        heat = Math.max(0, tag.getInt("Heat"));
        readFlowUpdate(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private boolean isFlowVisible() {
        return level != null && lastTransferTick >= 0 && level.getGameTime() - lastTransferTick <= FLOW_VISIBLE_TICKS;
    }

    private void resetFlowForNewTick() {
        long now = level.getGameTime();
        if (lastTransferTick != now) {
            currentAmperage = 0;
            currentVoltage = 0;
            currentCEt = 0;
            displayVoltage = 0;
            displayCEt = 0;
            lastTransferTick = now;
        }
    }

    private void writeFlowUpdate(CompoundTag tag) {
        tag.putInt("CurrentVoltage", currentVoltage());
        tag.putInt("CurrentAmperage", currentAmperage());
        tag.putInt("CurrentCEt", currentCEt());
        tag.putInt("DisplayVoltage", displayVoltage());
    }

    private void readFlowUpdate(CompoundTag tag) {
        currentVoltage = Math.max(0, tag.getInt("CurrentVoltage"));
        currentAmperage = Math.max(0, tag.getInt("CurrentAmperage"));
        currentCEt = Math.max(0, tag.getInt("CurrentCEt"));
        displayVoltage = Math.max(0, tag.getInt("DisplayVoltage"));
        displayCEt = currentCEt;
        if (currentAmperage > 0 || currentCEt > 0 || displayVoltage > 0) {
            lastTransferTick = level == null ? 0 : level.getGameTime();
        }
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
