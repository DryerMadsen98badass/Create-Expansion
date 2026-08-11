package net.mads.createexpansion.material;

import net.mads.createexpansion.machine.MachineTier;
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
        Set<MaterialPart> existingRecipeParts,
        Map<MaterialPart, ResourceLocation> customPartTextures,
        int strength,
        int meltingPoint,
        boolean hasExplicitStrength,
        boolean hasExplicitMeltingPoint,
        int temperature,
        int radioactivity,
        Optional<String> elementSymbol,
        List<MaterialComponent> components,
        double furnaceFuelItems,
        boolean furnaceFuelSet,
        Map<MaterialPart, Double> furnaceFuelParts,
        List<MaterialStoneSource> stoneSources,
        Optional<MachineTier> centrifugeTier,
        int centrifugeInputCount,
        Optional<MachineTier> electrolyserTier,
        int electrolyserInputCount,
        Optional<IndustrialMaterial> smeltingResult,
        boolean smeltingSelf
) implements IndustrialSubstance {
    public IndustrialMaterial {
        parts = Set.copyOf(parts);
        existingParts = Map.copyOf(existingParts);
        existingRecipeParts = Set.copyOf(existingRecipeParts);
        customPartTextures = Map.copyOf(customPartTextures);
        furnaceFuelParts = Map.copyOf(furnaceFuelParts);
        elementSymbol = elementSymbol == null ? Optional.empty() : elementSymbol;
        components = List.copyOf(components);
        stoneSources = List.copyOf(stoneSources);
        centrifugeTier = centrifugeTier == null ? Optional.empty() : centrifugeTier;
        electrolyserTier = electrolyserTier == null ? Optional.empty() : electrolyserTier;
        smeltingResult = smeltingResult == null ? Optional.empty() : smeltingResult;
    }

    public IndustrialMaterial(String id, String displayName, int color, Set<MaterialPart> parts) {
        this(id, displayName, color, "dull", "dull", parts, Map.of(), Set.of(), Map.of(),
                1, 300, false, false, 300, 0, Optional.empty(), List.of(), 0, false,
                Map.of(), List.of(), Optional.empty(), 0, Optional.empty(), 0, Optional.empty(), false);
    }

    public IndustrialMaterial(String id, String displayName, int color, Set<MaterialPart> parts,
                              Map<MaterialPart, ResourceLocation> existingParts) {
        this(id, displayName, color, "dull", "dull", parts, existingParts, Set.of(), Map.of(),
                1, 300, false, false, 300, 0, Optional.empty(), List.of(), 0, false,
                Map.of(), List.of(), Optional.empty(), 0, Optional.empty(), 0, Optional.empty(), false);
    }

    public boolean has(MaterialPart part) { return parts.contains(part); }
    public boolean hasExistingPart(MaterialPart part) { return existingParts.containsKey(part); }
    public ResourceLocation existingPart(MaterialPart part) { return existingParts.get(part); }
    public boolean hasExistingRecipe(MaterialPart part) { return existingRecipeParts.contains(part); }
    public boolean hasCustomPartTexture(MaterialPart part) { return customPartTextures.containsKey(part); }
    public ResourceLocation customPartTexture(MaterialPart part) { return customPartTextures.get(part); }

    public int castTemperature() { return Math.round(meltingPoint * 0.5F); }

    public int temperatureFor(MaterialPart part) {
        if (part == MaterialPart.MOLTEN_FLUID) return meltingPoint;
        if (part.name().startsWith("CAST_") || part.name().startsWith("HOT_CAST_")) return castTemperature();
        return temperature;
    }

    @Override
    public int componentTemperature() { return meltingPoint; }

    public boolean isFurnaceFuel() { return furnaceFuelSet; }

    public int furnaceBurnTimeTicks() {
        return Math.max(1, (int) Math.round(furnaceFuelItems * 200));
    }

    public boolean isFurnaceFuel(MaterialPart part) {
        return furnaceFuelParts.containsKey(part);
    }

    public int furnaceBurnTimeTicks(MaterialPart part) {
        Double fuelItems = furnaceFuelParts.get(part);
        if (fuelItems == null) {
            throw new IllegalArgumentException("Material part is not a furnace fuel: " + id + " " + part);
        }
        return Math.max(1, (int) Math.round(fuelItems * 200));
    }

    @Override
    public String formula() { return formula(false); }

    @Override
    public String formula(boolean nested) {
        if (elementSymbol.isPresent()) return elementSymbol.get();
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

    public String compoundFormula(boolean nested) { return formula(nested); }
}
