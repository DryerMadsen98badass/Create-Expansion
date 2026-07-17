package net.mads.createexpansion.item;

import java.util.List;

public final class SimpleItems {
    public static final List<SimpleItemDefinition> ALL = List.of(
            item("spool", "Spool")
    );

    private SimpleItems() {
    }

    private static SimpleItemDefinition item(String id, String displayName) {
        return new SimpleItemDefinition(id, displayName);
    }
}
