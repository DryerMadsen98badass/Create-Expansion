package net.mads.createexpansion.energy;

import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.core.Direction;

public interface CEEnergyContainer {
    MachineTier tier();

    int acceptEnergyFromNetwork(Direction side, int voltage, int amperage);

    boolean inputsEnergy(Direction side);

    boolean outputsEnergy(Direction side);

    int changeEnergy(int differenceAmount);

    int getEnergyStored();

    int getEnergyCapacity();

    int getInputAmperage();

    int getInputVoltage();

    int getOutputAmperage();

    int getOutputVoltage();

    default int getEnergyCanBeInserted() {
        return getEnergyCapacity() - getEnergyStored();
    }

    default int addEnergy(int energyToAdd) {
        return changeEnergy(energyToAdd);
    }

    default int removeEnergy(int energyToRemove) {
        return -changeEnergy(-energyToRemove);
    }

    default int voltage() {
        return getInputVoltage();
    }

    default int stored() {
        return getEnergyStored();
    }

    default int capacity() {
        return getEnergyCapacity();
    }

    default int maxInputAmps() {
        return getInputAmperage();
    }

    default int maxOutputAmps() {
        return getOutputAmperage();
    }

    default boolean canInput(Direction side) {
        return inputsEnergy(side);
    }

    default boolean canOutput(Direction side) {
        return outputsEnergy(side);
    }

    default int insert(int amount, boolean simulate) {
        int inserted = Math.max(0, Math.min(amount, getEnergyCanBeInserted()));
        if (!simulate && inserted > 0) {
            changeEnergy(inserted);
        }
        return inserted;
    }

    default int extract(int amount, boolean simulate) {
        int extracted = Math.max(0, Math.min(amount, getEnergyStored()));
        if (!simulate && extracted > 0) {
            changeEnergy(-extracted);
        }
        return extracted;
    }
}
