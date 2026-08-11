package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.machine.machines.electric.multiblock.machines.heater;
import net.mads.createexpansion.machine.machines.electric.multiblock.machines.ph_test_machine;
import net.mads.createexpansion.machine.machines.kinetic.multiblock.dirty_assembly_machine;
import net.mads.createexpansion.machine.machines.without_energy.multiblock.blast_furnace;
import net.mads.createexpansion.machine.machines.without_energy.multiblock.coke_oven;

import java.util.List;

public final class MultiblockDefinitions {
    public static final List<MultiblockDefinition> ALL = List.of(
            heater.DEFINITION,
            ph_test_machine.DEFINITION,
            dirty_assembly_machine.DEFINITION,
            blast_furnace.DEFINITION,
            coke_oven.DEFINITION
    );

    private MultiblockDefinitions() {
    }

    public static void bootstrap() {
        ALL.forEach(MultiblockRegistry::register);
    }

    public static List<MultiblockControllerDefinition> controllers() {
        return ALL.stream()
                .map(MultiblockDefinition::controller)
                .toList();
    }
}
