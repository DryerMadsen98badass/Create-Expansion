package net.mads.createexpansion.material;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MaterialPartTest {

    private IndustrialMaterial material(String id, String displayName) {
        return new IndustrialMaterial(id, displayName, 0xFFFFFF, EnumSet.of(MaterialPart.INGOT));
    }

    @Test
    void id_matchesConstructorArgument() {
        assertEquals("ingot", MaterialPart.INGOT.id());
        assertEquals("dust", MaterialPart.DUST.id());
        assertEquals("ore", MaterialPart.ORE.id());
        assertEquals("deepslate_ore", MaterialPart.DEEPSLATE_ORE.id());
        assertEquals("molten_fluid", MaterialPart.MOLTEN_FLUID.id());
    }

    @Test
    void displayName_matchesConstructorArgument() {
        assertEquals("Ingot", MaterialPart.INGOT.displayName());
        assertEquals("Dust", MaterialPart.DUST.displayName());
        assertEquals("Deepslate Ore", MaterialPart.DEEPSLATE_ORE.displayName());
    }

    @Test
    void isItem_trueForItemKind() {
        assertTrue(MaterialPart.INGOT.isItem());
        assertTrue(MaterialPart.DUST.isItem());
        assertTrue(MaterialPart.NUGGET.isItem());
        assertTrue(MaterialPart.PLATE.isItem());
        assertTrue(MaterialPart.WIRE.isItem());
    }

    @Test
    void isItem_falseForBlockAndFluid() {
        assertFalse(MaterialPart.ORE.isItem());
        assertFalse(MaterialPart.BLOCK.isItem());
        assertFalse(MaterialPart.MOLTEN_FLUID.isItem());
    }

    @Test
    void isBlock_trueForBlockKind() {
        assertTrue(MaterialPart.ORE.isBlock());
        assertTrue(MaterialPart.DEEPSLATE_ORE.isBlock());
        assertTrue(MaterialPart.BLOCK.isBlock());
        assertTrue(MaterialPart.RAW_BLOCK.isBlock());
        assertTrue(MaterialPart.FRAME.isBlock());
    }

    @Test
    void isBlock_falseForItemAndFluid() {
        assertFalse(MaterialPart.INGOT.isBlock());
        assertFalse(MaterialPart.MOLTEN_FLUID.isBlock());
    }

    @Test
    void isFluid_trueForFluidKind() {
        assertTrue(MaterialPart.MOLTEN_FLUID.isFluid());
        assertTrue(MaterialPart.SLURRY.isFluid());
        assertTrue(MaterialPart.SOLUTION.isFluid());
    }

    @Test
    void isFluid_falseForItemAndBlock() {
        assertFalse(MaterialPart.INGOT.isFluid());
        assertFalse(MaterialPart.ORE.isFluid());
    }

    @Test
    void registryName_combinesIdCorrectly() {
        IndustrialMaterial iron = material("iron", "Iron");
        assertEquals("iron_ingot", MaterialPart.INGOT.registryName(iron));
        assertEquals("iron_dust", MaterialPart.DUST.registryName(iron));
        assertEquals("iron_ore", MaterialPart.ORE.registryName(iron));
    }

    @Test
    void readableName_regularPart() {
        IndustrialMaterial iron = material("iron", "Iron");
        assertEquals("Iron Ingot", MaterialPart.INGOT.readableName(iron));
        assertEquals("Iron Dust", MaterialPart.DUST.readableName(iron));
        assertEquals("Iron Plate", MaterialPart.PLATE.readableName(iron));
    }

    @Test
    void readableName_rawOre() {
        IndustrialMaterial copper = material("copper", "Copper");
        assertEquals("Raw Copper", MaterialPart.RAW_ORE.readableName(copper));
    }

    @Test
    void readableName_block() {
        IndustrialMaterial gold = material("gold", "Gold");
        assertEquals("Block of Gold", MaterialPart.BLOCK.readableName(gold));
    }

    @Test
    void readableName_rawBlock() {
        IndustrialMaterial iron = material("iron", "Iron");
        assertEquals("Block of Raw Iron", MaterialPart.RAW_BLOCK.readableName(iron));
    }

    @Test
    void readableName_moltenFluid() {
        IndustrialMaterial tin = material("tin", "Tin");
        assertEquals("Molten Tin", MaterialPart.MOLTEN_FLUID.readableName(tin));
    }

    @Test
    void readableName_deepslateOre() {
        IndustrialMaterial iron = material("iron", "Iron");
        assertEquals("Deepslate Iron Ore", MaterialPart.DEEPSLATE_ORE.readableName(iron));
    }

    @Test
    void readableName_allStoneOres() {
        IndustrialMaterial mat = material("test", "Test");
        assertEquals("Diorite Test Ore", MaterialPart.DIORITE_ORE.readableName(mat));
        assertEquals("Andesite Test Ore", MaterialPart.ANDESITE_ORE.readableName(mat));
        assertEquals("Granite Test Ore", MaterialPart.GRANITE_ORE.readableName(mat));
        assertEquals("Tuff Test Ore", MaterialPart.TUFF_ORE.readableName(mat));
        assertEquals("Netherrack Test Ore", MaterialPart.NETHERRACK_ORE.readableName(mat));
        assertEquals("Blackstone Test Ore", MaterialPart.BLACKSTONE_ORE.readableName(mat));
        assertEquals("End Stone Test Ore", MaterialPart.END_STONE_ORE.readableName(mat));
    }

    @Test
    void kind_returnsCorrectEnum() {
        assertEquals(MaterialPart.Kind.ITEM, MaterialPart.INGOT.kind());
        assertEquals(MaterialPart.Kind.BLOCK, MaterialPart.ORE.kind());
        assertEquals(MaterialPart.Kind.FLUID, MaterialPart.MOLTEN_FLUID.kind());
    }

    @Test
    void allPartsHaveNonNullIdAndDisplayName() {
        for (MaterialPart part : MaterialPart.values()) {
            assertNotNull(part.id(), part.name() + " id should not be null");
            assertNotNull(part.displayName(), part.name() + " displayName should not be null");
            assertNotNull(part.kind(), part.name() + " kind should not be null");
            assertFalse(part.id().isEmpty(), part.name() + " id should not be empty");
            assertFalse(part.displayName().isEmpty(), part.name() + " displayName should not be empty");
        }
    }
}
