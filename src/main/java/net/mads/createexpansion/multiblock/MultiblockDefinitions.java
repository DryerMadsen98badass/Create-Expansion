package net.mads.createexpansion.multiblock;

import net.mads.createexpansion.multiblock.machines.TestFoundry;

import java.util.List;

public final class MultiblockDefinitions {
    public static final List<MultiblockDefinition> ALL = List.of(
            TestFoundry.DEFINITION
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
