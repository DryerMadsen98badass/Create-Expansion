package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.machine.machines.electric.multiblock.machines.Heater;

import java.util.List;

public final class MultiblockDefinitions {
    public static final List<MultiblockDefinition> ALL = List.of(
            Heater.DEFINITION
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
