package net.mads.createexpansion.item;

import net.minecraft.resources.ResourceLocation;

public record SimpleItemDefinition(String id, String displayName) {
    public SimpleItemDefinition {
        if (!ResourceLocation.isValidPath(id)) {
            throw new IllegalArgumentException("Invalid simple item id: " + id);
        }
        if (displayName.isBlank()) {
            throw new IllegalArgumentException("Simple item display name cannot be blank: " + id);
        }
    }
}
