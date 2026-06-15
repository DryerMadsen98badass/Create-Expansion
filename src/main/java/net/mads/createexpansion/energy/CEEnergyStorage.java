package net.mads.createexpansion.energy;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.minecraft.core.Direction;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

public class CEEnergyStorage implements CEEnergyContainer {
    private final MachineTier tier;
    private final int capacity;
    private final BooleanSupplier acceptsInput;
    private final BooleanSupplier allowsOutput;
    private final IntConsumer changeListener;
    private final IntConsumer overVoltageListener;
    private int stored;

    public CEEnergyStorage(MachineTier tier, int capacity, BooleanSupplier acceptsInput, BooleanSupplier allowsOutput, IntConsumer changeListener) {
        this(tier, capacity, acceptsInput, allowsOutput, changeListener, ignored -> {
        });
    }

    public CEEnergyStorage(MachineTier tier, int capacity, BooleanSupplier acceptsInput, BooleanSupplier allowsOutput, IntConsumer changeListener, IntConsumer overVoltageListener) {
        this.tier = tier;
        this.capacity = capacity;
        this.acceptsInput = acceptsInput;
        this.allowsOutput = allowsOutput;
        this.changeListener = changeListener;
        this.overVoltageListener = overVoltageListener;
    }

    @Override
    public MachineTier tier() {
        return tier;
    }

    @Override
    public int voltage() {
        return MachineTierStats.ceTier(tier);
    }

    @Override
    public int getEnergyStored() {
        return stored;
    }

    @Override
    public int getEnergyCapacity() {
        return capacity;
    }

    @Override
    public int getInputAmperage() {
        return acceptsInput.getAsBoolean() ? 2 : 0;
    }

    @Override
    public int getOutputAmperage() {
        return allowsOutput.getAsBoolean() ? 2 : 0;
    }

    @Override
    public int getInputVoltage() {
        return MachineTierStats.ceTier(tier);
    }

    @Override
    public int getOutputVoltage() {
        return MachineTierStats.ceTier(tier);
    }

    @Override
    public boolean inputsEnergy(Direction side) {
        return acceptsInput.getAsBoolean();
    }

    @Override
    public boolean outputsEnergy(Direction side) {
        return allowsOutput.getAsBoolean();
    }

    @Override
    public int acceptEnergyFromNetwork(Direction side, int voltage, int amperage) {
        if (!inputsEnergy(side) || voltage <= 0 || amperage <= 0) {
            return 0;
        }
        if (voltage > getInputVoltage()) {
            overVoltageListener.accept(voltage);
            return 0;
        }
        int acceptedAmps = Math.min(amperage, getInputAmperage());
        acceptedAmps = Math.min(acceptedAmps, getEnergyCanBeInserted() / voltage);
        if (acceptedAmps > 0) {
            changeEnergy(acceptedAmps * voltage);
        }
        return acceptedAmps;
    }

    @Override
    public int changeEnergy(int differenceAmount) {
        int previous = stored;
        setStored(stored + differenceAmount);
        return stored - previous;
    }

    public void setStored(int stored) {
        int next = Math.max(0, Math.min(capacity, stored));
        if (this.stored == next) {
            return;
        }
        this.stored = next;
        changeListener.accept(next);
    }
}
