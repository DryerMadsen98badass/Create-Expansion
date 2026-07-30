package net.mads.createexpansion.fluid;

import net.mads.createexpansion.material.IndustrialSubstance;
import net.mads.createexpansion.material.MaterialComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record IndustrialFluid(
        String id,
        String displayName,
        int color,
        Kind kind,
        int temperature,
        int density,
        int viscosity,
        int lightLevel,
        List<MaterialComponent> components,
        Optional<ResourceLocation> existingFluid
) implements IndustrialSubstance {
    public IndustrialFluid {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Industrial fluid id cannot be blank");
        if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("Industrial fluid display name cannot be blank");
        if (kind == null) throw new IllegalArgumentException("Industrial fluid kind cannot be null");
        if (lightLevel < 0 || lightLevel > 15) throw new IllegalArgumentException("Industrial fluid light level must be between 0 and 15");
        components = List.copyOf(components);
        existingFluid = existingFluid == null ? Optional.empty() : existingFluid;
    }

    public boolean isGas() { return kind == Kind.GAS; }
    public boolean isLiquid() { return kind == Kind.LIQUID; }
    public boolean isMolten() { return kind == Kind.MOLTEN; }
    public boolean hasComponents() { return !components.isEmpty(); }
    public boolean hasExistingFluid() { return existingFluid.isPresent(); }

    public ResourceLocation existingFluidId() {
        return existingFluid.orElseThrow(() -> new IllegalStateException(
                "Industrial fluid '" + id + "' does not reference an existing fluid"));
    }

    @Override
    public int componentTemperature() { return temperature; }

    @Override
    public String formula() { return formula(false); }

    @Override
    public String formula(boolean nested) {
        if (components.isEmpty()) return "";
        StringBuilder formula = new StringBuilder();
        for (MaterialComponent component : components) {
            IndustrialSubstance substance = component.substance();
            String componentFormula = substance.formula(true);
            if (componentFormula.isBlank()) componentFormula = substance.displayName();
            formula.append(componentFormula);
            if (component.amount() > 1) formula.append(component.amount());
        }
        String result = formula.toString();
        return nested ? "(" + result + ")" : result;
    }

    public String textureName() {
        return switch (kind) {
            case LIQUID -> "liquid";
            case GAS -> "gas";
            case MOLTEN -> "molten";
        };
    }

    public String registryName() { return kind == Kind.MOLTEN ? "molten_" + id : id; }
    public String bucketName() { return registryName() + "_bucket"; }
    public String localizedName() { return kind == Kind.MOLTEN ? "Molten " + displayName : displayName; }
    public String bucketDisplayName() { return localizedName() + " Bucket"; }

    public enum Kind { LIQUID, GAS, MOLTEN }
}
