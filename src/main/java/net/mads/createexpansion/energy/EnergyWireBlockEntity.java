package net.mads.createexpansion.energy;

import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.registry.BlockRegistry;
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

import java.util.Arrays;
import java.util.Optional;

public class EnergyWireBlockEntity extends BlockEntity {
    public static final int DEFAULT_TEMPERATURE = 293;
    public static final int INSULATION_FAILURE_TEMPERATURE = 1500;
    public static final int MELT_TEMPERATURE = 3000;

    private static final int FLOW_AVERAGE_TICKS = 20;

    private final PerTickAverage amperage = new PerTickAverage(FLOW_AVERAGE_TICKS);
    private long currentVoltage;
    private long recentVoltage;
    private long currentAmperage;
    private long lastFlowTick = Long.MIN_VALUE;
    private long syncedVoltage;
    private double syncedAmperage;
    private long lastBroadcastVoltage = Long.MIN_VALUE;
    private double lastBroadcastAmperage = Double.NaN;
    private int heatQueue;
    private int temperature = DEFAULT_TEMPERATURE;

    public EnergyWireBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.ENERGY_WIRE.get(), pos, blockState);
    }

    public long maxVoltage() {
        return wire().map(wire -> MachineTierStats.ceTier(wire.tier())).orElse(0L);
    }

    public long maxAmperage() {
        return wire().map(EnergyWireBlock::maxAmps).orElse(0);
    }

    public long currentVoltage() {
        if (level != null && level.isClientSide()) {
            return syncedVoltage;
        }
        return level == null ? 0L : currentVoltageForTick(level.getGameTime());
    }

    public double averageAmperage() {
        if (level != null && level.isClientSide()) {
            return syncedAmperage;
        }
        return level == null ? 0.0D : amperage.average(level.getGameTime());
    }

    public int temperature() {
        return temperature;
    }

    public void incrementAmperage(long amps, long voltage) {
        if (level == null || level.isClientSide() || amps <= 0L || voltage <= 0L) {
            return;
        }

        long tick = level.getGameTime();
        resetCurrentTick(tick);
        currentAmperage += amps;
        currentVoltage = Math.max(currentVoltage, voltage);
        recentVoltage = Math.max(recentVoltage, voltage);
        amperage.increment(tick, amps);

        long overAmps = currentAmperage - maxAmperage();
        if (overAmps > 0L) {
            heatQueue += saturatedInt(overAmps * 40L);
        }
        syncFlowIfChanged();
    }

    public void applyOverVoltage(long voltage) {
        if (voltage <= maxVoltage()) {
            return;
        }
        int suppliedTier = MachineTierStats.tierIndex(MachineTierStats.tierForVoltage(voltage));
        int cableTier = MachineTierStats.tierIndex(MachineTierStats.tierForVoltage(maxVoltage()));
        int tierDifference = Math.max(1, suppliedTier - cableTier);
        heatQueue += saturatedInt((long) tierDifference * 80L);
        contentChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnergyWireBlockEntity wire) {
        if (level.isClientSide()) {
            return;
        }

        wire.amperage.advance(level.getGameTime());
        wire.resetCurrentTick(level.getGameTime());
        wire.syncFlowIfChanged();

        if (wire.heatQueue > 0) {
            wire.temperature = saturatedInt((long) wire.temperature + wire.heatQueue);
            wire.heatQueue = 0;
            wire.contentChanged();
        } else if (wire.temperature > DEFAULT_TEMPERATURE) {
            int cooling = Math.max(1, (int) Math.pow(wire.temperature - DEFAULT_TEMPERATURE, 0.35D));
            wire.temperature = Math.max(DEFAULT_TEMPERATURE, wire.temperature - cooling);
            wire.contentChanged();
        }

        if (wire.temperature >= MELT_TEMPERATURE) {
            level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            return;
        }

        if (wire.temperature >= INSULATION_FAILURE_TEMPERATURE
                && state.getBlock() instanceof EnergyWireBlock cable
                && cable.insulated()
                && level.random.nextFloat() < 0.1F) {
            wire.removeInsulation(level, pos, state, cable);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Temperature", temperature);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        temperature = Math.max(DEFAULT_TEMPERATURE, tag.getInt("Temperature"));
        if (tag.contains("FlowVoltage")) {
            syncedVoltage = tag.getLong("FlowVoltage");
            syncedAmperage = tag.getDouble("FlowAmperage");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Temperature", temperature);
        tag.putLong("FlowVoltage", currentVoltage());
        tag.putDouble("FlowAmperage", averageAmperage());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void resetCurrentTick(long tick) {
        if (lastFlowTick != tick) {
            currentVoltage = 0L;
            currentAmperage = 0L;
            lastFlowTick = tick;
        }
    }

    private void removeInsulation(Level level, BlockPos pos, BlockState state, EnergyWireBlock cable) {
        var tierWires = BlockRegistry.ENERGY_WIRES.get(cable.tier().id());
        if (tierWires == null || !tierWires.containsKey(cable.thickness())) {
            return;
        }
        EnergyWireBlock replacement = tierWires.get(cable.thickness()).get();
        int oldTemperature = temperature;
        level.setBlockAndUpdate(pos, EnergyWireBlock.copyConnections(state, replacement));
        if (level.getBlockEntity(pos) instanceof EnergyWireBlockEntity replacementEntity) {
            replacementEntity.temperature = oldTemperature;
            replacementEntity.contentChanged();
        }
    }

    private long currentVoltageForTick(long tick) {
        if (lastFlowTick == tick) {
            return currentVoltage;
        }
        return amperage.average(tick) > 0.0D ? recentVoltage : 0L;
    }

    private Optional<EnergyWireBlock> wire() {
        return getBlockState().getBlock() instanceof EnergyWireBlock wire ? Optional.of(wire) : Optional.empty();
    }

    private void contentChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    private void syncFlowIfChanged() {
        if (level == null || level.isClientSide()) {
            return;
        }
        long voltage = currentVoltage();
        double average = averageAmperage();
        if (voltage == lastBroadcastVoltage && Double.compare(average, lastBroadcastAmperage) == 0) {
            return;
        }
        lastBroadcastVoltage = voltage;
        lastBroadcastAmperage = average;
        contentChanged();
    }

    private static int saturatedInt(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, value));
    }

    private static final class PerTickAverage {
        private final long[] values;
        private long lastTick = Long.MIN_VALUE;
        private int index;

        private PerTickAverage(int length) {
            values = new long[length];
        }

        private void increment(long tick, long value) {
            advance(tick);
            values[index] = saturatedAdd(values[index], value);
        }

        private double average(long tick) {
            advance(tick);
            long sum = 0L;
            for (long value : values) {
                sum = saturatedAdd(sum, value);
            }
            return sum / (double) values.length;
        }

        private void advance(long tick) {
            if (lastTick == Long.MIN_VALUE) {
                lastTick = tick;
                return;
            }
            long difference = tick - lastTick;
            if (difference <= 0L) {
                return;
            }
            if (difference >= values.length) {
                Arrays.fill(values, 0L);
                index = 0;
            } else {
                for (long offset = 0; offset < difference; offset++) {
                    index = (index + 1) % values.length;
                    values[index] = 0L;
                }
            }
            lastTick = tick;
        }

        private static long saturatedAdd(long first, long second) {
            if (second > 0L && first > Long.MAX_VALUE - second) {
                return Long.MAX_VALUE;
            }
            return first + second;
        }
    }
}
