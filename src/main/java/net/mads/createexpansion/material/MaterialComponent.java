package net.mads.createexpansion.material;

public record MaterialComponent(IndustrialMaterial material, int amount) {
    public MaterialComponent {
        if (amount < 1) {
            throw new IllegalArgumentException("Material component amount must be 1 or higher");
        }
    }
}
