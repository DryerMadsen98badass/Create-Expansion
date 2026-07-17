package net.mads.createexpansion.machine.coil;

import java.util.List;

public final class CoilDefinitions {
    public static final CoilDefinition COPPER = new CoilDefinition("Copper", "copper", 1000);
    public static final CoilDefinition KANTHAL = new CoilDefinition("Kanthal", "kanthal", 1500);

    public static final List<CoilDefinition> ALL = List.of(
            COPPER,
            KANTHAL
    );

    private CoilDefinitions() {
    }
}
