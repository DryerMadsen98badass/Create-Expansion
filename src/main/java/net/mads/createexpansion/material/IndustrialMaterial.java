package net.mads.createexpansion.material;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record IndustrialMaterial(
        String id,
        String displayName,
        int color,
        String itemMaterialSet,
        String blockMaterialSet,
        Set<MaterialPart> parts,
        Map<MaterialPart, ResourceLocation> existingParts,
        int strength,
        int meltingPoint,
        int temperature,
        int radioactivity,
        Optional<String> elementSymbol,
        List<MaterialComponent> components
) {
    public IndustrialMaterial(String id, String displayName, int color, Set<MaterialPart> parts) {
        this(id, displayName, color, "dull", "dull", parts, Map.of(), 1, 300, 300, 0, Optional.empty(), List.of());
    }

    public IndustrialMaterial(String id, String displayName, int color, Set<MaterialPart> parts, Map<MaterialPart, ResourceLocation> existingParts) {
        this(id, displayName, color, "dull", "dull", parts, existingParts, 1, 300, 300, 0, Optional.empty(), List.of());
    }

    public boolean has(MaterialPart part) {
        return parts.contains(part);
    }

    public boolean hasExistingPart(MaterialPart part) {
        return existingParts.containsKey(part);
    }

    public ResourceLocation existingPart(MaterialPart part) {
        return existingParts.get(part);
    }

    public int castTemperature() {
        return Math.round(meltingPoint * 0.5F);
    }

    public int temperatureFor(MaterialPart part) {
        if (part == MaterialPart.MOLTEN_FLUID) {
            return meltingPoint;
        }

        if (part.name().startsWith("CAST_") || part.name().startsWith("HOT_CAST_")) {
            return castTemperature();
        }

        return temperature;
    }

    public String formula() {
        if (elementSymbol.isPresent()) {
            return elementSymbol.get();
        }

        return compoundFormula(false);
    }

    public String compoundFormula(boolean nested) {
        if (elementSymbol.isPresent()) {
            return elementSymbol.get();
        }

        if (components.isEmpty()) {
            return "";
        }

        StringBuilder formula = new StringBuilder();
        for (MaterialComponent component : components) {
            boolean compoundComponent = component.material().elementSymbol().isEmpty() && !component.material().components().isEmpty();
            String componentFormula = component.material().compoundFormula(compoundComponent);
            if (componentFormula.isBlank()) {
                componentFormula = component.material().displayName();
            }

            formula.append(componentFormula);
            if (component.amount() > 1) {
                formula.append(component.amount());
            }
        }

        String result = formula.toString();
        return nested ? "(" + result + ")" : result;
    }
}
