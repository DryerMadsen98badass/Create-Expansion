package net.mads.createexpansion.material;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IndustrialMaterialTest {

    private IndustrialMaterial simple(String id, String displayName, int color, Set<MaterialPart> parts) {
        return new IndustrialMaterial(id, displayName, color, parts);
    }

    private IndustrialMaterial element(String id, String displayName, String symbol, int color) {
        return new IndustrialMaterial(
                id, displayName, color, "dull", "dull",
                EnumSet.of(MaterialPart.INGOT), Map.of(),
                1, 300, false, false, 300, 0,
                Optional.of(symbol), List.of()
        );
    }

    private IndustrialMaterial withMeltingPoint(int meltingPoint) {
        return new IndustrialMaterial(
                "test", "Test", 0xFFFFFF, "dull", "dull",
                EnumSet.of(MaterialPart.INGOT), Map.of(),
                1, meltingPoint, true, true, 300, 0,
                Optional.empty(), List.of()
        );
    }

    @Test
    void hasPart_returnsTrueForIncludedPart() {
        IndustrialMaterial mat = simple("test", "Test", 0xFFFFFF, EnumSet.of(MaterialPart.INGOT, MaterialPart.DUST));
        assertTrue(mat.has(MaterialPart.INGOT));
        assertTrue(mat.has(MaterialPart.DUST));
    }

    @Test
    void hasPart_returnsFalseForMissingPart() {
        IndustrialMaterial mat = simple("test", "Test", 0xFFFFFF, EnumSet.of(MaterialPart.INGOT));
        assertFalse(mat.has(MaterialPart.PLATE));
    }

    @Test
    void castTemperature_isHalfOfMeltingPoint() {
        IndustrialMaterial mat = withMeltingPoint(1538);
        assertEquals(769, mat.castTemperature());
    }

    @Test
    void castTemperature_roundsCorrectly() {
        IndustrialMaterial mat = withMeltingPoint(101);
        assertEquals(Math.round(101 * 0.5F), mat.castTemperature());
    }

    @Test
    void temperatureFor_moltenFluid_returnsMeltingPoint() {
        IndustrialMaterial mat = withMeltingPoint(1538);
        assertEquals(1538, mat.temperatureFor(MaterialPart.MOLTEN_FLUID));
    }

    @Test
    void temperatureFor_castPart_returnsCastTemperature() {
        IndustrialMaterial mat = withMeltingPoint(1000);
        assertEquals(500, mat.temperatureFor(MaterialPart.CAST_INGOT));
        assertEquals(500, mat.temperatureFor(MaterialPart.CAST_PLATE));
    }

    @Test
    void temperatureFor_hotCastPart_returnsCastTemperature() {
        IndustrialMaterial mat = withMeltingPoint(1000);
        assertEquals(500, mat.temperatureFor(MaterialPart.HOT_CAST_INGOT_MOLD));
    }

    @Test
    void temperatureFor_regularPart_returnsTemperature() {
        IndustrialMaterial mat = new IndustrialMaterial(
                "test", "Test", 0xFFFFFF, "dull", "dull",
                EnumSet.of(MaterialPart.INGOT), Map.of(),
                1, 1000, true, true, 500, 0,
                Optional.empty(), List.of()
        );
        assertEquals(500, mat.temperatureFor(MaterialPart.INGOT));
        assertEquals(500, mat.temperatureFor(MaterialPart.DUST));
    }

    @Test
    void formula_returnsSymbolWhenPresent() {
        IndustrialMaterial iron = element("iron", "Iron", "Fe", 0xD8D8D8);
        assertEquals("Fe", iron.formula());
    }

    @Test
    void formula_returnsEmptyForNoSymbolNoComponents() {
        IndustrialMaterial mat = simple("test", "Test", 0xFFFFFF, EnumSet.of(MaterialPart.INGOT));
        assertEquals("", mat.formula());
    }

    @Test
    void formula_returnsCompoundFormula() {
        IndustrialMaterial fe = element("iron", "Iron", "Fe", 0xD8D8D8);
        IndustrialMaterial o = element("oxygen", "Oxygen", "O", 0xFF0D0D);

        IndustrialMaterial compound = new IndustrialMaterial(
                "hematite", "Hematite", 0xFF0000, "dull", "dull",
                EnumSet.of(MaterialPart.DUST), Map.of(),
                1, 300, false, false, 300, 0,
                Optional.empty(),
                List.of(new MaterialComponent(fe, 2), new MaterialComponent(o, 3))
        );

        assertEquals("Fe2O3", compound.formula());
    }

    @Test
    void formula_singleAmountOmitsNumber() {
        IndustrialMaterial fe = element("iron", "Iron", "Fe", 0xD8D8D8);
        IndustrialMaterial o = element("oxygen", "Oxygen", "O", 0xFF0D0D);

        IndustrialMaterial compound = new IndustrialMaterial(
                "feo", "Iron Oxide", 0xFF0000, "dull", "dull",
                EnumSet.of(MaterialPart.DUST), Map.of(),
                1, 300, false, false, 300, 0,
                Optional.empty(),
                List.of(new MaterialComponent(fe, 1), new MaterialComponent(o, 1))
        );

        assertEquals("FeO", compound.formula());
    }

    @Test
    void compoundFormula_nested_addsParentheses() {
        IndustrialMaterial fe = element("iron", "Iron", "Fe", 0xD8D8D8);
        IndustrialMaterial o = element("oxygen", "Oxygen", "O", 0xFF0D0D);

        IndustrialMaterial innerCompound = new IndustrialMaterial(
                "feo", "Iron Oxide", 0xFF0000, "dull", "dull",
                EnumSet.of(MaterialPart.DUST), Map.of(),
                1, 300, false, false, 300, 0,
                Optional.empty(),
                List.of(new MaterialComponent(fe, 1), new MaterialComponent(o, 1))
        );

        String nested = innerCompound.compoundFormula(true);
        assertEquals("(FeO)", nested);
    }

    @Test
    void compoundFormula_notNested_noParentheses() {
        IndustrialMaterial fe = element("iron", "Iron", "Fe", 0xD8D8D8);
        IndustrialMaterial o = element("oxygen", "Oxygen", "O", 0xFF0D0D);

        IndustrialMaterial compound = new IndustrialMaterial(
                "feo", "Iron Oxide", 0xFF0000, "dull", "dull",
                EnumSet.of(MaterialPart.DUST), Map.of(),
                1, 300, false, false, 300, 0,
                Optional.empty(),
                List.of(new MaterialComponent(fe, 1), new MaterialComponent(o, 1))
        );

        assertEquals("FeO", compound.compoundFormula(false));
    }

    @Test
    void compoundFormula_elementSymbolOverridesComponents() {
        IndustrialMaterial fe = element("iron", "Iron", "Fe", 0xD8D8D8);
        assertEquals("Fe", fe.compoundFormula(false));
        assertEquals("Fe", fe.compoundFormula(true));
    }

    @Test
    void compoundFormula_emptyComponentsFallsBackToDisplayName() {
        IndustrialMaterial noSymbolNoComponents = simple("mystery", "Mystery", 0x000000, EnumSet.of(MaterialPart.DUST));

        IndustrialMaterial compound = new IndustrialMaterial(
                "compound", "Compound", 0x111111, "dull", "dull",
                EnumSet.of(MaterialPart.DUST), Map.of(),
                1, 300, false, false, 300, 0,
                Optional.empty(),
                List.of(new MaterialComponent(noSymbolNoComponents, 2))
        );

        assertEquals("Mystery2", compound.formula());
    }

    @Test
    void shortConstructor_setsDefaults() {
        IndustrialMaterial mat = simple("test", "Test", 0xABC, EnumSet.of(MaterialPart.INGOT));
        assertEquals("test", mat.id());
        assertEquals("Test", mat.displayName());
        assertEquals(0xABC, mat.color());
        assertEquals("dull", mat.itemMaterialSet());
        assertEquals("dull", mat.blockMaterialSet());
        assertEquals(1, mat.strength());
        assertEquals(300, mat.meltingPoint());
        assertFalse(mat.hasExplicitStrength());
        assertFalse(mat.hasExplicitMeltingPoint());
        assertEquals(300, mat.temperature());
        assertEquals(0, mat.radioactivity());
        assertTrue(mat.elementSymbol().isEmpty());
        assertTrue(mat.components().isEmpty());
    }

    @Test
    void radioactivity_isStoredCorrectly() {
        IndustrialMaterial mat = new IndustrialMaterial(
                "uranium", "Uranium", 0x32F032, "dull", "dull",
                EnumSet.of(MaterialPart.INGOT), Map.of(),
                1, 1132, true, true, 300, 40,
                Optional.of("U"), List.of()
        );
        assertEquals(40, mat.radioactivity());
    }
}
