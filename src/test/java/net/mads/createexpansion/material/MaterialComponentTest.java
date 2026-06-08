package net.mads.createexpansion.material;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MaterialComponentTest {

    private IndustrialMaterial dummyMaterial() {
        return new IndustrialMaterial("test", "Test", 0xFFFFFF, EnumSet.of(MaterialPart.INGOT));
    }

    @Test
    void validComponent_amountOne() {
        MaterialComponent component = new MaterialComponent(dummyMaterial(), 1);
        assertEquals(1, component.amount());
        assertNotNull(component.material());
    }

    @Test
    void validComponent_largeAmount() {
        MaterialComponent component = new MaterialComponent(dummyMaterial(), 100);
        assertEquals(100, component.amount());
    }

    @Test
    void invalidComponent_amountZero_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MaterialComponent(dummyMaterial(), 0));
    }

    @Test
    void invalidComponent_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new MaterialComponent(dummyMaterial(), -1));
    }

    @Test
    void invalidComponent_negativeAmountMessage() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new MaterialComponent(dummyMaterial(), -5)
        );
        assertEquals("Material component amount must be 1 or higher", ex.getMessage());
    }

    @Test
    void material_isPreserved() {
        IndustrialMaterial mat = dummyMaterial();
        MaterialComponent component = new MaterialComponent(mat, 3);
        assertSame(mat, component.material());
    }
}
