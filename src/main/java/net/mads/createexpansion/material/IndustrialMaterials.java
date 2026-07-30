package net.mads.createexpansion.material;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.fluid.FluidPart;
import net.mads.createexpansion.fluid.IndustrialFluid;
import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static net.mads.createexpansion.fluid.FluidPart.FLUID;
import static net.mads.createexpansion.material.MaterialPart.*;

public class IndustrialMaterials {

    public static final IndustrialMaterial IRON = material("iron", "Iron", 0xD8D8D8)
            .element("Fe")
            .strength(6)
            .meltingPoint(700)
            .allMetals()
            .allMaterials()
            .smeltingSelf()
            .existing(ORE, "minecraft:iron_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_iron_ore")
            .existing(RAW_ORE, "minecraft:raw_iron")
            .existing(RAW_BLOCK, "minecraft:raw_iron_block")
            .existing(INGOT, "minecraft:iron_ingot")
            .existing(NUGGET, "minecraft:iron_nugget")
            .existing(BLOCK, "minecraft:iron_block")
            .existing(CRUSHED_ORE, "create:crushed_raw_iron")
            .existing(PLATE, "create:iron_sheet")
            .existing(ROTOR, "create:propeller")
            .build();

    public static final IndustrialMaterial ZINC = material("zinc", "Zinc", 0xC8C8B0)
            .element("Zn")
            .strength(3)
            .meltingPoint(420)
            .allMetals()
            .allMaterials()
            .smeltingSelf()
            .existing(ORE, "create:zinc_ore")
            .existing(DEEPSLATE_ORE, "create:deepslate_zinc_ore")
            .existing(RAW_ORE, "create:raw_zinc")
            .existing(RAW_BLOCK, "create:raw_zinc_block")
            .existing(INGOT, "create:zinc_ingot")
            .existing(NUGGET, "create:zinc_nugget")
            .existing(BLOCK, "create:zinc_block")
            .existing(CRUSHED_ORE, "create:crushed_raw_zinc")
            .build();

    public static final IndustrialMaterial GOLD = material("gold", "Gold", 0xF8D64E)
            .element("Au")
            .strength(3)
            .meltingPoint(1064)
            .allMetals()
            .allMaterials()
            .smeltingSelf()
            .existing(ORE, "minecraft:gold_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_gold_ore")
            .existing(RAW_ORE, "minecraft:raw_gold")
            .existing(RAW_BLOCK, "minecraft:raw_gold_block")
            .existing(INGOT, "minecraft:gold_ingot")
            .existing(NUGGET, "minecraft:gold_nugget")
            .existing(BLOCK, "minecraft:gold_block")
            .existing(CRUSHED_ORE, "create:crushed_raw_gold")
            .existing(PLATE, "create:golden_sheet")
            .build();

    public static final IndustrialMaterial COPPER = material("copper", "Copper", 0xD17A45)
            .element("Cu")
            .strength(4)
            .meltingPoint(700)
            .allMetals()
            .allMaterials()
            .smeltingSelf()
            .existing(ORE, "minecraft:copper_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_copper_ore")
            .existing(RAW_ORE, "minecraft:raw_copper")
            .existing(RAW_BLOCK, "minecraft:raw_copper_block")
            .existing(INGOT, "minecraft:copper_ingot")
            .existing(NUGGET, "create:copper_nugget")
            .existing(BLOCK, "minecraft:copper_block")
            .existing(CRUSHED_ORE, "create:crushed_raw_copper")
            .existing(PLATE, "create:copper_sheet")
            .build();

    public static final IndustrialMaterial TIN = material("tin", "Tin", 0xD6D6C8).element("Sn").strength(2).meltingPoint(232).allMetals().allMaterials().smeltingSelf().build();
    public static final IndustrialMaterial HYDROGEN = elementMaterial("hydrogen", "Hydrogen", "H", 0xE8F8FF, 1, -259).fluidOnly().build();
    public static final IndustrialMaterial HELIUM = elementMaterial("helium", "Helium", "He", 0xD9FFFF, 1, -272).fluidOnly().build();
    public static final IndustrialMaterial LITHIUM = elementMaterial("lithium", "Lithium", "Li", 0xCC80FF, 2, 181).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial BERYLLIUM = elementMaterial("beryllium", "Beryllium", "Be", 0xC2FF00, 5, 1287).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial BORON = elementMaterial("boron", "Boron", "B", 0xFFB5B5, 6, 2076).gemElement().build();
    public static final IndustrialMaterial CARBON = elementMaterial("carbon", "Carbon", "C", 0x303030, 10, 3550).gemElement().build();
    public static final IndustrialMaterial NITROGEN = elementMaterial("nitrogen", "Nitrogen", "N", 0x3050F8, 1, -210).fluidOnly().build();
    public static final IndustrialMaterial OXYGEN = elementMaterial("oxygen", "Oxygen", "O", 0xFF0D0D, 1, -219).fluidOnly().build();
    public static final IndustrialMaterial FLUORINE = elementMaterial("fluorine", "Fluorine", "F", 0x90E050, 1, -220).fluidOnly().build();
    public static final IndustrialMaterial NEON = elementMaterial("neon", "Neon", "Ne", 0xB3E3F5, 1, -249).fluidOnly().build();
    public static final IndustrialMaterial SODIUM = elementMaterial("sodium", "Sodium", "Na", 0xAB5CF2, 1, 98).solidMetal().build();
    public static final IndustrialMaterial MAGNESIUM = elementMaterial("magnesium", "Magnesium", "Mg", 0x8AFF00, 3, 650).solidMetal().build();
    public static final IndustrialMaterial ALUMINUM = elementMaterial("aluminum", "Aluminum", "Al", 0xBFA6A6, 3, 660).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial SILICON = elementMaterial("silicon", "Silicon", "Si", 0xF0C8A0, 5, 1414).gemElement().build();
    public static final IndustrialMaterial PHOSPHORUS = elementMaterial("phosphorus", "Phosphorus", "P", 0xFF8000, 2, 44).gemElement().build();
    public static final IndustrialMaterial SULFUR = elementMaterial("sulfur", "Sulfur", "S", 0xFFFF30, 2, 115).gemElement().build();
    public static final IndustrialMaterial CHLORINE = elementMaterial("chlorine", "Chlorine", "Cl", 0x1FF01F, 1, -101).fluidOnly().build();
    public static final IndustrialMaterial ARGON = elementMaterial("argon", "Argon", "Ar", 0x80D1E3, 1, -189).fluidOnly().build();
    public static final IndustrialMaterial POTASSIUM = elementMaterial("potassium", "Potassium", "K", 0x8F40D4, 1, 64).solidMetal().build();
    public static final IndustrialMaterial CALCIUM = elementMaterial("calcium", "Calcium", "Ca", 0x3DFF00, 3, 842).solidMetal().build();
    public static final IndustrialMaterial SCANDIUM = elementMaterial("scandium", "Scandium", "Sc", 0xE6E6E6, 5, 1541).solidMetal().build();
    public static final IndustrialMaterial TITANIUM = elementMaterial("titanium", "Titanium", "Ti", 0xBFC2C7, 7, 1668).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial VANADIUM = elementMaterial("vanadium", "Vanadium", "V", 0xA6A6AB, 7, 1910).solidMetal().build();
    public static final IndustrialMaterial CHROMIUM = elementMaterial("chromium", "Chromium", "Cr", 0x8A99C7, 8, 1907).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial MANGANESE = elementMaterial("manganese", "Manganese", "Mn", 0x9C7AC7, 5, 1246).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial COBALT = elementMaterial("cobalt", "Cobalt", "Co", 0x5C7AC7, 6, 1495).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial NICKEL = elementMaterial("nickel", "Nickel", "Ni", 0x50D050, 6, 1455).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial GALLIUM = elementMaterial("gallium", "Gallium", "Ga", 0xC28F8F, 2, 30).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial GERMANIUM = elementMaterial("germanium", "Germanium", "Ge", 0x668F8F, 4, 938).gemElement().build();
    public static final IndustrialMaterial ARSENIC = elementMaterial("arsenic", "Arsenic", "As", 0xBD80E3, 3, 817).gemElement().build();
    public static final IndustrialMaterial SELENIUM = elementMaterial("selenium", "Selenium", "Se", 0xFFA100, 2, 221).gemElement().build();
    public static final IndustrialMaterial BROMINE = elementMaterial("bromine", "Bromine", "Br", 0xA62929, 1, -7).fluidOnly().build();
    public static final IndustrialMaterial KRYPTON = elementMaterial("krypton", "Krypton", "Kr", 0x5CB8D1, 1, -157).fluidOnly().build();
    public static final IndustrialMaterial RUBIDIUM = elementMaterial("rubidium", "Rubidium", "Rb", 0x702EB0, 1, 39).solidMetal().build();
    public static final IndustrialMaterial STRONTIUM = elementMaterial("strontium", "Strontium", "Sr", 0x00FF00, 3, 777).solidMetal().build();
    public static final IndustrialMaterial YTTRIUM = elementMaterial("yttrium", "Yttrium", "Y", 0x94FFFF, 5, 1526).solidMetal().build();
    public static final IndustrialMaterial ZIRCONIUM = elementMaterial("zirconium", "Zirconium", "Zr", 0x94E0E0, 7, 1855).solidMetal().build();
    public static final IndustrialMaterial NIOBIUM = elementMaterial("niobium", "Niobium", "Nb", 0x73C2C9, 8, 2477).solidMetal().build();
    public static final IndustrialMaterial MOLYBDENUM = elementMaterial("molybdenum", "Molybdenum", "Mo", 0x54B5B5, 8, 2623).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial TECHNETIUM = elementMaterial("technetium", "Technetium", "Tc", 0x3B9E9E, 6, 2157).radioactivity(2).solidMetal().build();
    public static final IndustrialMaterial RUTHENIUM = elementMaterial("ruthenium", "Ruthenium", "Ru", 0x248F8F, 7, 2334).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial RHODIUM = elementMaterial("rhodium", "Rhodium", "Rh", 0x0A7D8C, 7, 1964).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial PALLADIUM = elementMaterial("palladium", "Palladium", "Pd", 0x006985, 5, 1555).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial SILVER = elementMaterial("silver", "Silver", "Ag", 0xC0C0C0, 4, 962).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial CADMIUM = elementMaterial("cadmium", "Cadmium", "Cd", 0xFFD98F, 2, 321).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial INDIUM = elementMaterial("indium", "Indium", "In", 0xA67573, 2, 157).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial ANTIMONY = elementMaterial("antimony", "Antimony", "Sb", 0x9E63B5, 3, 631).gemElement().build();
    public static final IndustrialMaterial TELLURIUM = elementMaterial("tellurium", "Tellurium", "Te", 0xD47A00, 3, 450).gemElement().build();
    public static final IndustrialMaterial IODINE = elementMaterial("iodine", "Iodine", "I", 0x940094, 1, 114).gemElement().build();
    public static final IndustrialMaterial XENON = elementMaterial("xenon", "Xenon", "Xe", 0x429EB0, 1, -112).fluidOnly().build();
    public static final IndustrialMaterial CESIUM = elementMaterial("cesium", "Cesium", "Cs", 0x57178F, 1, 28).solidMetal().build();
    public static final IndustrialMaterial BARIUM = elementMaterial("barium", "Barium", "Ba", 0x00C900, 3, 727).solidMetal().build();
    public static final IndustrialMaterial LANTHANUM = elementMaterial("lanthanum", "Lanthanum", "La", 0x70D4FF, 4, 920).solidMetal().build();
    public static final IndustrialMaterial CERIUM = elementMaterial("cerium", "Cerium", "Ce", 0xFFFFC7, 4, 798).solidMetal().build();
    public static final IndustrialMaterial PRASEODYMIUM = elementMaterial("praseodymium", "Praseodymium", "Pr", 0xD9FFC7, 4, 931).solidMetal().build();
    public static final IndustrialMaterial NEODYMIUM = elementMaterial("neodymium", "Neodymium", "Nd", 0xC7FFC7, 4, 1024).solidMetal().build();
    public static final IndustrialMaterial PROMETHIUM = elementMaterial("promethium", "Promethium", "Pm", 0xA3FFC7, 4, 1042).radioactivity(2).solidMetal().build();
    public static final IndustrialMaterial SAMARIUM = elementMaterial("samarium", "Samarium", "Sm", 0x8FFFC7, 4, 1072).solidMetal().build();
    public static final IndustrialMaterial EUROPIUM = elementMaterial("europium", "Europium", "Eu", 0x61FFC7, 3, 822).solidMetal().build();
    public static final IndustrialMaterial GADOLINIUM = elementMaterial("gadolinium", "Gadolinium", "Gd", 0x45FFC7, 4, 1313).solidMetal().build();
    public static final IndustrialMaterial TERBIUM = elementMaterial("terbium", "Terbium", "Tb", 0x30FFC7, 4, 1356).solidMetal().build();
    public static final IndustrialMaterial DYSPROSIUM = elementMaterial("dysprosium", "Dysprosium", "Dy", 0x1FFFC7, 4, 1412).solidMetal().build();
    public static final IndustrialMaterial HOLMIUM = elementMaterial("holmium", "Holmium", "Ho", 0x00FF9C, 4, 1474).solidMetal().build();
    public static final IndustrialMaterial ERBIUM = elementMaterial("erbium", "Erbium", "Er", 0x00E675, 4, 1529).solidMetal().build();
    public static final IndustrialMaterial THULIUM = elementMaterial("thulium", "Thulium", "Tm", 0x00D452, 4, 1545).solidMetal().build();
    public static final IndustrialMaterial YTTERBIUM = elementMaterial("ytterbium", "Ytterbium", "Yb", 0x00BF38, 3, 824).solidMetal().build();
    public static final IndustrialMaterial LUTETIUM = elementMaterial("lutetium", "Lutetium", "Lu", 0x00AB24, 5, 1663).solidMetal().build();
    public static final IndustrialMaterial HAFNIUM = elementMaterial("hafnium", "Hafnium", "Hf", 0x4DC2FF, 7, 2233).solidMetal().build();
    public static final IndustrialMaterial TANTALUM = elementMaterial("tantalum", "Tantalum", "Ta", 0x4DA6FF, 9, 3017).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial TUNGSTEN = elementMaterial("tungsten", "Tungsten", "W", 0x4D91FF, 10, 3422).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial RHENIUM = elementMaterial("rhenium", "Rhenium", "Re", 0x267DAB, 9, 3186).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial OSMIUM = elementMaterial("osmium", "Osmium", "Os", 0x266696, 9, 3033).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial IRIDIUM = elementMaterial("iridium", "Iridium", "Ir", 0x175487, 9, 2446).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial PLATINUM = elementMaterial("platinum", "Platinum", "Pt", 0xD0D0E0, 6, 1768).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial MERCURY = elementMaterial("mercury", "Mercury", "Hg", 0xB8B8D0, 1, -39).fluidOnly().build();
    public static final IndustrialMaterial THALLIUM = elementMaterial("thallium", "Thallium", "Tl", 0xA6544D, 2, 304).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial LEAD = elementMaterial("lead", "Lead", "Pb", 0x575961, 2, 327).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial BISMUTH = elementMaterial("bismuth", "Bismuth", "Bi", 0x9E4FB5, 2, 271).solidMetal().smeltingSelf().build();
    public static final IndustrialMaterial POLONIUM = elementMaterial("polonium", "Polonium", "Po", 0xAB5C00, 2, 254).radioactivity(4).gemElement().build();
    public static final IndustrialMaterial ASTATINE = elementMaterial("astatine", "Astatine", "At", 0x754F45, 1, 302).radioactivity(3).gemElement().build();
    public static final IndustrialMaterial RADON = elementMaterial("radon", "Radon", "Rn", 0x428296, 1, -71).radioactivity(4).fluidOnly().build();
    public static final IndustrialMaterial FRANCIUM = elementMaterial("francium", "Francium", "Fr", 0x420066, 1, 27).radioactivity(4).solidMetal().build();
    public static final IndustrialMaterial RADIUM = elementMaterial("radium", "Radium", "Ra", 0x007D00, 3, 700).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial ACTINIUM = elementMaterial("actinium", "Actinium", "Ac", 0x70ABFA, 4, 1050).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial THORIUM = elementMaterial("thorium", "Thorium", "Th", 0x00BAFF, 5, 1750).radioactivity(3).solidMetal().build();
    public static final IndustrialMaterial PROTACTINIUM = elementMaterial("protactinium", "Protactinium", "Pa", 0x00A1FF, 5, 1572).radioactivity(4).solidMetal().build();
    public static final IndustrialMaterial URANIUM = elementMaterial("uranium", "Uranium", "U", 0x008FFF, 6, 1132).radioactivity(4).solidMetal().build();
    public static final IndustrialMaterial NEPTUNIUM = elementMaterial("neptunium", "Neptunium", "Np", 0x0080FF, 5, 644).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial PLUTONIUM = elementMaterial("plutonium", "Plutonium", "Pu", 0x006BFF, 5, 640).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial AMERICIUM = elementMaterial("americium", "Americium", "Am", 0x545CF2, 4, 1176).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial CURIUM = elementMaterial("curium", "Curium", "Cm", 0x785CE3, 5, 1345).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial BERKELIUM = elementMaterial("berkelium", "Berkelium", "Bk", 0x8A4FE3, 4, 986).radioactivity(5).solidMetal().build();
    public static final IndustrialMaterial CALIFORNIUM = elementMaterial("californium", "Californium", "Cf", 0xA136D4, 4, 900).radioactivity(6).solidMetal().build();
    public static final IndustrialMaterial EINSTEINIUM = elementMaterial("einsteinium", "Einsteinium", "Es", 0xB31FD4, 3, 860).radioactivity(6).solidMetal().build();
    public static final IndustrialMaterial FERMIUM = elementMaterial("fermium", "Fermium", "Fm", 0xB31FBA, 3, 1527).radioactivity(6).solidMetal().build();
    public static final IndustrialMaterial MENDELEVIUM = elementMaterial("mendelevium", "Mendelevium", "Md", 0xB30DA6, 3, 827).radioactivity(6).solidMetal().build();
    public static final IndustrialMaterial NOBELIUM = elementMaterial("nobelium", "Nobelium", "No", 0xBD0D87, 3, 827).radioactivity(6).solidMetal().build();
    public static final IndustrialMaterial LAWRENCIUM = elementMaterial("lawrencium", "Lawrencium", "Lr", 0xC70066, 3, 1627).radioactivity(6).solidMetal().build();
    public static final IndustrialMaterial RUTHERFORDIUM = elementMaterial("rutherfordium", "Rutherfordium", "Rf", 0xCC0059, 4, 2100).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial DUBNIUM = elementMaterial("dubnium", "Dubnium", "Db", 0xD1004F, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial SEABORGIUM = elementMaterial("seaborgium", "Seaborgium", "Sg", 0xD90045, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial BOHRIUM = elementMaterial("bohrium", "Bohrium", "Bh", 0xE00038, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial HASSIUM = elementMaterial("hassium", "Hassium", "Hs", 0xE6002E, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial MEITNERIUM = elementMaterial("meitnerium", "Meitnerium", "Mt", 0xEB0026, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial DARMSTADTIUM = elementMaterial("darmstadtium", "Darmstadtium", "Ds", 0xF0001F, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial ROENTGENIUM = elementMaterial("roentgenium", "Roentgenium", "Rg", 0xF50017, 4, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial COPERNICIUM = elementMaterial("copernicium", "Copernicium", "Cn", 0xFA000F, 2, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial NIHONIUM = elementMaterial("nihonium", "Nihonium", "Nh", 0xFA2A00, 2, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial FLEROVIUM = elementMaterial("flerovium", "Flerovium", "Fl", 0xFA5400, 2, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial MOSCOVIUM = elementMaterial("moscovium", "Moscovium", "Mc", 0xFA7D00, 2, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial LIVERMORIUM = elementMaterial("livermorium", "Livermorium", "Lv", 0xFAA600, 2, 0).radioactivity(7).solidMetal().build();
    public static final IndustrialMaterial TENNESSINE = elementMaterial("tennessine", "Tennessine", "Ts", 0xFAD000, 1, 0).radioactivity(7).fluidOnly().build();
    public static final IndustrialMaterial OGANESSON = elementMaterial("oganesson", "Oganesson", "Og", 0xFAFA00, 1, 0).radioactivity(7).fluidOnly().build();

    public static final IndustrialFluid WATER = fluid("water", "Water", 0x3F76E4)
            .contains(component(HYDROGEN, 2), component(OXYGEN, 1))
            .existing(FLUID, "minecraft:water")
            .build();

    public static final IndustrialFluid CRUDE_OIL = fluid("crude_oil", "Crude Oil", 0x19130D)
            .viscosity(2000)
            .build();

    public static final IndustrialFluid ORGANIC_BINDER = fluid("organic_binder", "Organic Binder", 0x6B4526)
            .temperature(320)
            .density(1120)
            .viscosity(3000)
            .build();

    public static final IndustrialFluid STEAM = gas("steam", "Steam", 0xE6E6E6)
            .contains(component(HYDROGEN, 2), component(OXYGEN, 1))
            .temperature(400)
            .density(-200)
            .viscosity(50)
            .build();

    public static final IndustrialFluid CONCRETE = fluid("concrete", "Concrete", 0x8A8A8A)
            .temperature(300)
            .density(2400)
            .viscosity(6000)
            .build();

    public static final IndustrialFluid CREOSOTE_OIL = fluid("creosote_oil", "Creosote Oil", 0x2B1A0E)
            .temperature(350)
            .density(1100)
            .viscosity(1500)
            .build();

    public static final IndustrialFluid PLANT_OIL = fluid("plant_oil", "Plant Oil", 0xC9A227)
            .temperature(300)
            .density(920)
            .viscosity(900)
            .build();

    public static final IndustrialFluid BIOFUEL = fluid("biofuel", "Biofuel", 0xB58A20)
            .temperature(300)
            .density(880)
            .viscosity(650)
            .build();

    public static final IndustrialFluid BIOLUBRICANT = fluid("biolubricant", "Biolubricant", 0xA88A2A)
            .temperature(300)
            .density(900)
            .viscosity(1800)
            .build();

    public static final IndustrialFluid ETHANOL = fluid("ethanol", "Ethanol", 0xE8E1B0)
            .contains(component(CARBON, 2), component(HYDROGEN, 6), component(OXYGEN, 1))
            .temperature(300)
            .density(789)
            .viscosity(1)
            .build();

    public static final IndustrialFluid FERMENTATION_MASH = fluid("fermentation_mash", "Fermentation Mash", 0x8C6A32)
            .temperature(300)
            .density(1050)
            .viscosity(700)
            .build();

    public static final IndustrialFluid FERMENTED_MASH = fluid("fermented_mash", "Fermented Mash", 0x6E4F2A)
            .temperature(305)
            .density(1030)
            .viscosity(550)
            .build();

    public static final IndustrialFluid INDUSTRIAL_RESIN = fluid("industrial_resin", "Industrial Resin", 0x7A4A1F)
            .temperature(320)
            .density(1080)
            .viscosity(2400)
            .build();

    public static final IndustrialFluid RAW_RESIN = fluid("raw_resin", "Raw Resin", 0x8A5A24)
            .temperature(300)
            .density(1050)
            .viscosity(3200)
            .build();

    public static final IndustrialMaterial BRONZE = material("bronze", "Bronze", 0xCD7F32)
            .alloyOf(component(COPPER, 3), component(TIN, 1))
            .strength(2)
            .allMaterials()
            .build();

    public static final IndustrialMaterial BRASS = material("brass", "Brass", 0xD6A84F)
            .alloyOf(component(COPPER, 3), component(ZINC, 1))
            .strength(2)
            .allMaterials()
            .existing(INGOT, "create:brass_ingot")
            .existing(NUGGET, "create:brass_nugget")
            .existing(BLOCK, "create:brass_block")
            .existing(PLATE, "create:brass_sheet")
            .build();

    public static final IndustrialMaterial ELECTRUM = material("electrum", "Electrum", 0xE8D06A)
            .contains(component(GOLD, 1), component(SILVER, 1))
            .strength(3)
            .allMetals()
            .allMaterials()
            .build();

    public static final IndustrialMaterial DIAMOND = material("diamond", "Diamond", 0x5DECF5)
            .strength(10)
            .contains(component(CARBON, 1))
            .allGems()
            .existing(ORE, "minecraft:diamond_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_diamond_ore")
            .existing(GEM, "minecraft:diamond")
            .existing(BLOCK, "minecraft:diamond_block")
            .existingRecipe(BLOCK)
            .build();

    public static final IndustrialMaterial EMERALD = material("emerald", "Emerald", 0x17DD62)
            .contains(component(BERYLLIUM, 3), component(ALUMINUM, 2), component(SILICON, 6), component(OXYGEN, 18))
            .allGems()
            .existing(ORE, "minecraft:emerald_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_emerald_ore")
            .existing(GEM, "minecraft:emerald")
            .existing(BLOCK, "minecraft:emerald_block")
            .existingRecipe(BLOCK)
            .build();

    public static final IndustrialMaterial LAPIS = material("lapis", "Lapis", 0x2D56B3)
            .contains(component(SODIUM, 8), component(ALUMINUM, 6), component(SILICON, 6), component(OXYGEN, 24), component(SULFUR, 2))
            .allGems()
            .existing(ORE, "minecraft:lapis_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_lapis_ore")
            .existing(GEM, "minecraft:lapis_lazuli")
            .existing(BLOCK, "minecraft:lapis_block")
            .existingRecipe(BLOCK)
            .build();

    public static final IndustrialMaterial REDSTONE = material("redstone", "Redstone", 0xD31B1B)
            .contains(component(ALUMINUM, 2), component(CARBON, 8), component(GOLD, 1))
            .allGems()
            .existing(ORE, "minecraft:redstone_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_redstone_ore")
            .existing(BLOCK, "minecraft:redstone_block")
            .existingRecipe(BLOCK)
            .existing(DUST, "minecraft:redstone")
            .build();

    public static final IndustrialMaterial QUARTZ = material("quartz", "Quartz", 0xE7E1D2)
            .contains(component(SILICON, 1), component(OXYGEN, 2))
            .allGems()
            .existing(NETHERRACK_ORE, "minecraft:nether_quartz_ore")
            .existing(GEM, "minecraft:quartz")
            .existing(BLOCK, "minecraft:quartz_block")
            .existingRecipe(BLOCK)
            .build();

    public static final IndustrialMaterial GRAPHITE = oreMineral("graphite", "Graphite", 0x252525)
            .contains(component(CARBON, 1))
            .build();
    public static final IndustrialMaterial SODALITE = material("sodalite", "Sodalite", 0x315CA8)
            .contains(component(SODIUM, 8), component(ALUMINUM, 6), component(SILICON, 6), component(OXYGEN, 24), component(CHLORINE, 2))
            .allGems()
            .build();
    public static final IndustrialMaterial LAZURITE = material("lazurite", "Lazurite", 0x2449A3)
            .contains(component(SODIUM, 8), component(ALUMINUM, 6), component(SILICON, 6), component(OXYGEN, 24), component(SULFUR, 2))
            .allGems()
            .build();

    public static final IndustrialMaterial HEMATITE = oreMineral("hematite", "Hematite", 0x8A1F1F)
            .contains(component(IRON, 2), component(OXYGEN, 3))
            .smelting(IRON)
            .build();
    public static final IndustrialMaterial MAGNETITE = oreMineral("magnetite", "Magnetite", 0x2D2D32)
            .contains(component(IRON, 3), component(OXYGEN, 4))
            .smelting(IRON)
            .build();
    public static final IndustrialMaterial GOETHITE = oreMineral("goethite", "Goethite", 0x8B5A2B)
            .contains(component(IRON, 1), component(OXYGEN, 2), component(HYDROGEN, 1))
            .smelting(IRON)
            .build();
    public static final IndustrialMaterial SIDERITE = oreMineral("siderite", "Siderite", 0x9A8A62)
            .contains(component(IRON, 1), component(CARBON, 1), component(OXYGEN, 3))
            .smelting(IRON)
            .build();
    public static final IndustrialMaterial PYRITE = oreMineral("pyrite", "Pyrite", 0xC8A846)
            .contains(component(IRON, 1), component(SULFUR, 2))
            .build();
    public static final IndustrialMaterial CHALCOPYRITE = oreMineral("chalcopyrite", "Chalcopyrite", 0xB8862E)
            .contains(component(COPPER, 1), component(IRON, 1), component(SULFUR, 2))
            .smelting(COPPER)
            .build();
    public static final IndustrialMaterial MALACHITE = oreMineral("malachite", "Malachite", 0x1FA060)
            .contains(component(COPPER, 2), component(CARBON, 1), component(OXYGEN, 5), component(HYDROGEN, 2))
            .smelting(COPPER)
            .build();
    public static final IndustrialMaterial AZURITE = oreMineral("azurite", "Azurite", 0x2456B8)
            .contains(component(COPPER, 3), component(CARBON, 2), component(OXYGEN, 8), component(HYDROGEN, 2))
            .smelting(COPPER)
            .build();
    public static final IndustrialMaterial CUPRITE = oreMineral("cuprite", "Cuprite", 0x8A2020)
            .contains(component(COPPER, 2), component(OXYGEN, 1))
            .smelting(COPPER)
            .build();
    public static final IndustrialMaterial CHALCOCITE = oreMineral("chalcocite", "Chalcocite", 0x3B3B3B)
            .contains(component(COPPER, 2), component(SULFUR, 1))
            .smelting(COPPER)
            .build();
    public static final IndustrialMaterial CASSITERITE = oreMineral("cassiterite", "Cassiterite", 0x5A4638)
            .contains(component(TIN, 1), component(OXYGEN, 2))
            .smelting(TIN)
            .build();
    public static final IndustrialMaterial STANNITE = oreMineral("stannite", "Stannite", 0x4A3A32)
            .contains(component(COPPER, 2), component(IRON, 1), component(TIN, 1), component(SULFUR, 4))
            .smelting(TIN)
            .build();
    public static final IndustrialMaterial SPHALERITE = oreMineral("sphalerite", "Sphalerite", 0x7A5A36)
            .contains(component(ZINC, 1), component(SULFUR, 1))
            .smelting(ZINC)
            .build();
    public static final IndustrialMaterial SMITHSONITE = oreMineral("smithsonite", "Smithsonite", 0xB7C8B0)
            .contains(component(ZINC, 1), component(CARBON, 1), component(OXYGEN, 3))
            .smelting(ZINC)
            .build();
    public static final IndustrialMaterial ZINCITE = oreMineral("zincite", "Zincite", 0xC7482E)
            .contains(component(ZINC, 1), component(OXYGEN, 1))
            .smelting(ZINC)
            .build();
    public static final IndustrialMaterial GALENA = oreMineral("galena", "Galena", 0x575961)
            .contains(component(LEAD, 1), component(SULFUR, 1))
            .smelting(LEAD)
            .build();
    public static final IndustrialMaterial CERUSSITE = oreMineral("cerussite", "Cerussite", 0xD8D8C8)
            .contains(component(LEAD, 1), component(CARBON, 1), component(OXYGEN, 3))
            .smelting(LEAD)
            .build();
    public static final IndustrialMaterial ANGLESITE = oreMineral("anglesite", "Anglesite", 0xD0D0B8)
            .contains(component(LEAD, 1), component(SULFUR, 1), component(OXYGEN, 4))
            .smelting(LEAD)
            .build();
    public static final IndustrialMaterial BAUXITE = oreMineral("bauxite", "Bauxite", 0xB46A3C)
            .contains(component(ALUMINUM, 2), component(OXYGEN, 4), component(HYDROGEN, 2), component(IRON, 1))
            .smelting(ALUMINUM)
            .build();
    public static final IndustrialMaterial GIBBSITE = oreMineral("gibbsite", "Gibbsite", 0xE0D8D0)
            .contains(component(ALUMINUM, 1), component(OXYGEN, 3), component(HYDROGEN, 3))
            .smelting(ALUMINUM)
            .build();
    public static final IndustrialMaterial BOEHMITE = oreMineral("boehmite", "Boehmite", 0xD4C7B8)
            .contains(component(ALUMINUM, 1), component(OXYGEN, 2), component(HYDROGEN, 1))
            .smelting(ALUMINUM)
            .build();
    public static final IndustrialMaterial CORUNDUM = oreMineral("corundum", "Corundum", 0xB8B8C8)
            .contains(component(ALUMINUM, 2), component(OXYGEN, 3))
            .build();
    public static final IndustrialMaterial PENTLANDITE = oreMineral("pentlandite", "Pentlandite", 0x8A7656)
            .contains(component(IRON, 4), component(NICKEL, 5), component(SULFUR, 8))
            .smelting(NICKEL)
            .build();
    public static final IndustrialMaterial MILLERITE = oreMineral("millerite", "Millerite", 0xBDA64A)
            .contains(component(NICKEL, 1), component(SULFUR, 1))
            .smelting(NICKEL)
            .build();
    public static final IndustrialMaterial GARNIERITE = oreMineral("garnierite", "Garnierite", 0x7FBF7F)
            .contains(component(NICKEL, 1), component(MAGNESIUM, 3), component(SILICON, 2), component(OXYGEN, 9), component(HYDROGEN, 4))
            .smelting(NICKEL)
            .build();
    public static final IndustrialMaterial COBALTITE = oreMineral("cobaltite", "Cobaltite", 0x5B5C8A)
            .contains(component(COBALT, 1), component(ARSENIC, 1), component(SULFUR, 1))
            .smelting(COBALT)
            .build();
    public static final IndustrialMaterial SKUTTERUDITE = oreMineral("skutterudite", "Skutterudite", 0x55505A)
            .contains(component(COBALT, 1), component(NICKEL, 1), component(ARSENIC, 3))
            .build();
    public static final IndustrialMaterial ERYTHRITE = oreMineral("erythrite", "Erythrite", 0xC44A86)
            .contains(component(COBALT, 3), component(ARSENIC, 2), component(OXYGEN, 16), component(HYDROGEN, 16))
            .smelting(COBALT)
            .build();
    public static final IndustrialMaterial PYROLUSITE = oreMineral("pyrolusite", "Pyrolusite", 0x303030)
            .contains(component(MANGANESE, 1), component(OXYGEN, 2))
            .smelting(MANGANESE)
            .build();
    public static final IndustrialMaterial RHODOCHROSITE = oreMineral("rhodochrosite", "Rhodochrosite", 0xC8789B)
            .contains(component(MANGANESE, 1), component(CARBON, 1), component(OXYGEN, 3))
            .smelting(MANGANESE)
            .build();
    public static final IndustrialMaterial MANGANITE = oreMineral("manganite", "Manganite", 0x4A3F3F)
            .contains(component(MANGANESE, 1), component(OXYGEN, 2), component(HYDROGEN, 1))
            .smelting(MANGANESE)
            .build();
    public static final IndustrialMaterial CHROMITE = oreMineral("chromite", "Chromite", 0x24242A)
            .contains(component(IRON, 1), component(CHROMIUM, 2), component(OXYGEN, 4))
            .smelting(CHROMIUM)
            .build();
    public static final IndustrialMaterial ILMENITE = oreMineral("ilmenite", "Ilmenite", 0x2B2B30)
            .contains(component(IRON, 1), component(TITANIUM, 1), component(OXYGEN, 3))
            .smelting(TITANIUM)
            .build();
    public static final IndustrialMaterial RUTILE = oreMineral("rutile", "Rutile", 0xA6422B)
            .contains(component(TITANIUM, 1), component(OXYGEN, 2))
            .smelting(TITANIUM)
            .build();
    public static final IndustrialMaterial ANATASE = oreMineral("anatase", "Anatase", 0x4E5A78)
            .contains(component(TITANIUM, 1), component(OXYGEN, 2))
            .smelting(TITANIUM)
            .build();
    public static final IndustrialMaterial ACANTHITE = oreMineral("acanthite", "Acanthite", 0x353538)
            .contains(component(SILVER, 2), component(SULFUR, 1))
            .smelting(SILVER)
            .build();
    public static final IndustrialMaterial CHLORARGYRITE = oreMineral("chlorargyrite", "Chlorargyrite", 0xCFC8A8)
            .contains(component(SILVER, 1), component(CHLORINE, 1))
            .smelting(SILVER)
            .build();
    public static final IndustrialMaterial PROUSTITE = oreMineral("proustite", "Proustite", 0xA5182F)
            .contains(component(SILVER, 3), component(ARSENIC, 1), component(SULFUR, 3))
            .smelting(SILVER)
            .build();
    public static final IndustrialMaterial NATIVE_GOLD = oreMineral("native_gold", "Native Gold", 0xF8D64E)
            .contains(component(GOLD, 1))
            .build();
    public static final IndustrialMaterial CALAVERITE = oreMineral("calaverite", "Calaverite", 0xB6A05C)
            .contains(component(GOLD, 1), component(TELLURIUM, 2))
            .smelting(GOLD)
            .build();
    public static final IndustrialMaterial WOLFRAMITE = oreMineral("wolframite", "Wolframite", 0x2E2B2A)
            .contains(component(IRON, 1), component(MANGANESE, 1), component(TUNGSTEN, 1), component(OXYGEN, 4))
            .smelting(TUNGSTEN)
            .build();
    public static final IndustrialMaterial SCHEELITE = oreMineral("scheelite", "Scheelite", 0xD7D29A)
            .contains(component(CALCIUM, 1), component(TUNGSTEN, 1), component(OXYGEN, 4))
            .smelting(TUNGSTEN)
            .build();
    public static final IndustrialMaterial MOLYBDENITE = oreMineral("molybdenite", "Molybdenite", 0x4F5660)
            .contains(component(MOLYBDENUM, 1), component(SULFUR, 2))
            .smelting(MOLYBDENUM)
            .build();
    public static final IndustrialMaterial WULFENITE = oreMineral("wulfenite", "Wulfenite", 0xE07A22)
            .contains(component(LEAD, 1), component(MOLYBDENUM, 1), component(OXYGEN, 4))
            .build();
    public static final IndustrialMaterial NATIVE_PLATINUM = oreMineral("native_platinum", "Native Platinum", 0xD0D0E0)
            .contains(component(PLATINUM, 1))
            .build();
    public static final IndustrialMaterial SPERRYLITE = oreMineral("sperrylite", "Sperrylite", 0xC0B8B0)
            .contains(component(PLATINUM, 1), component(ARSENIC, 2))
            .smelting(PLATINUM)
            .build();
    public static final IndustrialMaterial COOPERITE = oreMineral("cooperite", "Cooperite", 0xB8B088)
            .contains(component(PLATINUM, 1), component(SULFUR, 1))
            .smelting(PLATINUM)
            .build();
    public static final IndustrialMaterial BRAGGITE = oreMineral("braggite", "Braggite", 0x9B9480)
            .contains(component(PLATINUM, 1), component(PALLADIUM, 1), component(NICKEL, 1), component(SULFUR, 1))
            .build();
    public static final IndustrialMaterial STIBIOPALLADINITE = oreMineral("stibiopalladinite", "Stibiopalladinite", 0xA89080)
            .contains(component(PALLADIUM, 5), component(ANTIMONY, 2))
            .smelting(PALLADIUM)
            .build();
    public static final IndustrialMaterial URANINITE = oreMineral("uraninite", "Uraninite", 0x1F2A1F)
            .contains(component(URANIUM, 1), component(OXYGEN, 2))
            .radioactivity(4)
            .build();
    public static final IndustrialMaterial PITCHBLENDE = oreMineral("pitchblende", "Pitchblende", 0x161A16)
            .contains(component(URANIUM, 3), component(OXYGEN, 8))
            .radioactivity(4)
            .build();
    public static final IndustrialMaterial CARNOTITE = oreMineral("carnotite", "Carnotite", 0xD4C024)
            .contains(component(POTASSIUM, 2), component(URANIUM, 2), component(VANADIUM, 2), component(OXYGEN, 11), component(HYDROGEN, 6))
            .radioactivity(4)
            .build();
    public static final IndustrialMaterial MONAZITE = oreMineral("monazite", "Monazite", 0xA8743E)
            .contains(component(CERIUM, 1), component(LANTHANUM, 1), component(NEODYMIUM, 1), component(THORIUM, 1), component(PHOSPHORUS, 1), component(OXYGEN, 4))
            .radioactivity(2)
            .build();
    public static final IndustrialMaterial THORITE = oreMineral("thorite", "Thorite", 0x4E3A28)
            .contains(component(THORIUM, 1), component(SILICON, 1), component(OXYGEN, 4))
            .radioactivity(3)
            .build();
    public static final IndustrialMaterial SPODUMENE = oreMineral("spodumene", "Spodumene", 0xB9E0C0)
            .contains(component(LITHIUM, 1), component(ALUMINUM, 1), component(SILICON, 2), component(OXYGEN, 6))
            .smelting(LITHIUM)
            .build();
    public static final IndustrialMaterial LEPIDOLITE = oreMineral("lepidolite", "Lepidolite", 0xC99AD8)
            .contains(component(POTASSIUM, 1), component(LITHIUM, 1), component(ALUMINUM, 4), component(SILICON, 3), component(RUBIDIUM, 1), component(OXYGEN, 12), component(FLUORINE, 1), component(HYDROGEN, 1))
            .smelting(LITHIUM)
            .build();
    public static final IndustrialMaterial PETALITE = oreMineral("petalite", "Petalite", 0xE5D8E8)
            .contains(component(LITHIUM, 1), component(ALUMINUM, 1), component(SILICON, 4), component(OXYGEN, 10))
            .smelting(LITHIUM)
            .build();
    public static final IndustrialMaterial MAGNESITE = oreMineral("magnesite", "Magnesite", 0xD8D8CC)
            .contains(component(MAGNESIUM, 1), component(CARBON, 1), component(OXYGEN, 3))
            .build();
    public static final IndustrialMaterial DOLOMITE = oreMineral("dolomite", "Dolomite", 0xD0C7B8)
            .contains(component(CALCIUM, 1), component(MAGNESIUM, 1), component(CARBON, 2), component(OXYGEN, 6))
            .build();
    public static final IndustrialMaterial CARNALLITE = oreMineral("carnallite", "Carnallite", 0xDDBBA0)
            .contains(component(POTASSIUM, 1), component(MAGNESIUM, 1), component(CHLORINE, 3), component(HYDROGEN, 12), component(OXYGEN, 6))
            .build();
    public static final IndustrialMaterial CALCITE = oreMineral("calcite", "Calcite", 0xE5E0D0)
            .contains(component(CALCIUM, 1), component(CARBON, 1), component(OXYGEN, 3))
            .existing(STONES, "create:calcite")
            .stone()
            .build();
    public static final IndustrialMaterial GYPSUM = oreMineral("gypsum", "Gypsum", 0xE8E8E0)
            .contains(component(CALCIUM, 1), component(SULFUR, 1), component(OXYGEN, 6), component(HYDROGEN, 4))
            .build();
    public static final IndustrialMaterial FLUORITE = oreMineral("fluorite", "Fluorite", 0x8ED1D1)
            .contains(component(CALCIUM, 1), component(FLUORINE, 2))
            .build();
    public static final IndustrialMaterial APATITE = oreMineral("apatite", "Apatite", 0x7FB8D8)
            .contains(component(CALCIUM, 5), component(PHOSPHORUS, 3), component(OXYGEN, 12), component(FLUORINE, 1), component(CHLORINE, 1), component(HYDROGEN, 1))
            .build();
    public static final IndustrialMaterial HALITE = oreMineral("halite", "Halite", 0xD8E8F8)
            .contains(component(SODIUM, 1), component(CHLORINE, 1))
            .build();
    public static final IndustrialMaterial TRONA = oreMineral("trona", "Trona", 0xD9D0C0)
            .contains(component(SODIUM, 3), component(HYDROGEN, 5), component(CARBON, 2), component(OXYGEN, 8))
            .build();
    public static final IndustrialMaterial NATRON = oreMineral("natron", "Natron", 0xE0E0D8)
            .contains(component(SODIUM, 2), component(CARBON, 1), component(OXYGEN, 13), component(HYDROGEN, 20))
            .build();
    public static final IndustrialMaterial SYLVITE = oreMineral("sylvite", "Sylvite", 0xE8D8D8)
            .contains(component(POTASSIUM, 1), component(CHLORINE, 1))
            .build();
    public static final IndustrialMaterial ORTHOCLASE = oreMineral("orthoclase", "Orthoclase", 0xD0A8A0)
            .contains(component(POTASSIUM, 1), component(ALUMINUM, 1), component(SILICON, 3), component(OXYGEN, 8))
            .build();
    public static final IndustrialMaterial BASTNASITE = oreMineral("bastnasite", "Bastnasite", 0xC8A060)
            .contains(component(CERIUM, 1), component(LANTHANUM, 1), component(CARBON, 1), component(OXYGEN, 3), component(FLUORINE, 1))
            .build();
    public static final IndustrialMaterial XENOTIME = oreMineral("xenotime", "Xenotime", 0x7F8060)
            .contains(component(YTTRIUM, 1), component(PHOSPHORUS, 1), component(OXYGEN, 4))
            .build();
    public static final IndustrialMaterial TOPAZ = material("topaz", "Topaz", 0xF7C65A)
            .contains(component(ALUMINUM, 2), component(SILICON, 1), component(OXYGEN, 6), component(FLUORINE, 1), component(HYDROGEN, 1))
            .allGems()
            .build();
    public static final IndustrialMaterial COAL = material("coal", "Coal", 0x161616)
            .contains(component(CARBON, 1))
            .allGems()
            .parts(DIORITE_ORE, ANDESITE_ORE, GRANITE_ORE, TUFF_ORE, NETHERRACK_ORE, BLACKSTONE_ORE, END_STONE_ORE)
            .existing(GEM, "minecraft:coal")
            .existing(ORE, "minecraft:coal_ore")
            .existing(DEEPSLATE_ORE, "minecraft:deepslate_coal_ore")
            .existing(BLOCK, "minecraft:coal_block")
            .furnaceFuel(GEM, 8)
            .furnaceFuel(DUST, 8)
            .furnaceFuel(FLAWLESS_GEM, 16)
            .furnaceFuel(EXQUISITE_GEM, 32)
            .build();
    public static final IndustrialMaterial RUBY = material("ruby", "Ruby", 0xC01838)
            .contains(component(ALUMINUM, 2), component(OXYGEN, 3), component(CHROMIUM, 1))
            .allGems()
            .build();
    public static final IndustrialMaterial SAPPHIRE = material("sapphire", "Sapphire", 0x2B55C0)
            .contains(component(ALUMINUM, 2), component(OXYGEN, 3), component(IRON, 1), component(TITANIUM, 1))
            .allGems()
            .build();
    public static final IndustrialMaterial ANDESITE = material("andesite", "Andesite", 0xB8B8B8)
            .contains(component(CALCIUM, 1), component(ALUMINUM, 2), component(SILICON, 2), component(OXYGEN, 8))
            .parts(DUST)
            .stone()
            .build();
    public static final IndustrialMaterial STONE = material("stone", "Stone", 0xB8B8B8)
            .parts(DUST)
            .existing(STONES, "minecraft:stone")
            .stone()
            .build();
    public static final IndustrialMaterial TUFF = material("tuff", "Tuff", 0xB8B8B8)
            .contains(component(GOLD, 1), component(ZINC, 1), component(IRON, 1), component(COPPER, 1))
            .parts(DUST)
            .stone()
            .build();
    public static final IndustrialMaterial FELDSPAR = material("feldspar", "Feldspar", 0xD8C4B0)
            .contains(component(POTASSIUM, 1), component(ALUMINUM, 1), component(SILICON, 3), component(OXYGEN, 8))
            .parts(DUST)
            .build();
    public static final IndustrialMaterial MICA = material("mica", "Mica", 0x3A3128)
            .contains(component(POTASSIUM, 1), component(ALUMINUM, 3), component(SILICON, 3), component(OXYGEN, 12), component(HYDROGEN, 2))
            .parts(DUST)
            .build();
    public static final IndustrialMaterial GRANITE = material("granite", "Granite", 0xA77D73)
            .contains(component(QUARTZ, 3), component(FELDSPAR, 5), component(MICA, 1))
            .parts(DUST)
            .stone()
            .build();
    public static final IndustrialMaterial BIO_CHAR = material("bio_char", "Bio Char", 0x1F1A14)
            .parts(DUST)
            .furnaceFuel(1)
            .build();
    public static final IndustrialMaterial ACTIVATED_CARBON = material("activated_carbon", "Activated Carbon", 0x171717)
            .parts(DUST)
            .contains(component(CARBON, 1))
            .build();
    public static final IndustrialMaterial ANDESITE_ALLOY = material("andesite_alloy", "Andesite Alloy", 0xB8B8B8)
            .contains(component(ANDESITE, 9), component(IRON, 1))
            .strength(2)
            .meltingPoint(1200)
            .allMaterials()
            .existing(INGOT, "create:andesite_alloy")
            .existing(BLOCK, "create:andesite_alloy_block")
            .centrifuge(MachineTier.ULV, 1)
            .build();
    public static final IndustrialMaterial AMPHIBOLE = material("amphibole", "Amphibole", 0x2E332D)
            .contains(component(CALCIUM, 2), component(MAGNESIUM, 4), component(IRON, 1), component(ALUMINUM, 1), component(SILICON, 7), component(OXYGEN, 22), component(HYDROGEN, 2))
            .parts(DUST)
            .build();
    public static final IndustrialMaterial DEEPSLATE = material("deepslate", "Deepslate", 0x4A4A4F)
            .contains(component(FELDSPAR, 3), component(AMPHIBOLE, 2), component(IRON, 1), component(MAGNESIUM, 1))
            .parts(DUST)
            .stone()
            .build();
    public static final IndustrialMaterial DIORITE = material("diorite", "Diorite", 0xC8C8C2)
            .contains(component(FELDSPAR, 5), component(AMPHIBOLE, 3), component(QUARTZ, 1))
            .parts(DUST)
            .stone()
            .build();
    public static final IndustrialMaterial ASURINE = material("asurine", "Asurine", 0x587E9C)
            .contains(component(FELDSPAR, 3), component(AMPHIBOLE, 1), component(COPPER, 1), component(ZINC, 1))
            .parts(DUST)
            .existing(STONES, "create:asurine")
            .build();
    public static final IndustrialMaterial CRIMSITE = material("crimsite", "Crimsite", 0x8F4A3A)
            .contains(component(FELDSPAR, 3), component(AMPHIBOLE, 1), component(IRON, 2))
            .parts(DUST)
            .existing(STONES, "create:crimsite")
            .build();
    public static final IndustrialMaterial LIMESTONE = material("limestone", "Limestone", 0xC8C4A8)
            .contains(component(CALCIUM, 1), component(CARBON, 1), component(OXYGEN, 3))
            .parts(DUST)
            .existing(STONES, "create:limestone")
            .build();
    public static final IndustrialMaterial OCHRUM = material("ochrum", "Ochrum", 0xC99A42)
            .contains(component(FELDSPAR, 2), component(QUARTZ, 1), component(IRON, 1), component(GOLD, 1))
            .parts(DUST)
            .existing(STONES, "create:ochrum")
            .build();
    public static final IndustrialMaterial SCORIA = material("scoria", "Scoria", 0x3A2E2A)
            .contains(component(AMPHIBOLE, 2), component(SILICON, 2), component(OXYGEN, 6), component(IRON, 1), component(MAGNESIUM, 1))
            .parts(DUST)
            .existing(STONES, "create:scoria")
            .build();
    public static final IndustrialMaterial IRON_ANDESITE_COMPOUND = material("iron_andesite_compound", "Iron Andesite Compound", 0xB8B8B8)
            .contains(component(ANDESITE, 8), component(IRON, 1))
            .parts(DUST)
            .centrifuge(MachineTier.ULV)
            .build();
    public static final IndustrialMaterial SCORCHIA = material("scorchia", "Scorchia", 0x2B2724)
            .contains(component(SCORIA, 2), component(CARBON, 1), component(SULFUR, 1))
            .parts(DUST)
            .existing(STONES, "create:scorchia")
            .build();
    public static final IndustrialMaterial NETHERRACK = material("netherrack", "Netherrack", 0x706037)
            .contains(component(QUARTZ, 1), component(CARBON, 5), component(SULFUR, 2), component(GOLD, 1))
            .parts(DUST)
            .existing(STONES, "minecraft:netherrack")
            .existing(DUST, "create:cinder_flour")
            .existingRecipe(DUST)
            .build();
    public static final IndustrialMaterial NETHER_BRICK = material("nether_brick", "Nether Brick", 0x2c2c2c)
            .contains(component(QUARTZ, 1), component(CARBON, 5), component(SULFUR, 2), component(GOLD, 1))
            .parts(DUST)
            .existing(INGOT, "minecraft:nether_brick")
            .build();
    public static final IndustrialMaterial IRON_OXIDE = material("iron_oxide", "Iron Oxide", 0x7db7a8)
            .contains(component(OXYGEN, 1), component(IRON, 1))
            .parts(DUST)
            .build();
    public static final IndustrialMaterial CLAY = material("clay", "Clay", 0x728acb)
            .contains(component(SILICON, 50), component(ALUMINUM, 20), component(IRON_OXIDE, 5), component(WATER, 25))
            .parts(DUST)
            .existing(INGOT, "minecraft:clay_ball")
            .build();
    public static final IndustrialMaterial VERIDIUM = material("veridium", "Veridium", 0x4E8A63)
            .contains(component(FELDSPAR, 3), component(AMPHIBOLE, 1), component(COPPER, 2))
            .parts(DUST)
            .existing(STONES, "create:veridium")
            .build();
    public static final IndustrialMaterial WROUGHT_IRON = material("wrought_iron", "Wrought Iron", 0x878787)
            .contains(component(IRON, 1))
            .strength(7)
            .meltingPoint(750)
            .allMaterials()
            .build();
    public static final IndustrialMaterial FERTILIZER = material("fertilizer", "Fertilizer", 0x6F7F3A)
            .parts(DUST)
            .build();
    public static final IndustrialMaterial STEEL = material("steel", "Steel", 0x626262)
            .contains(component(IRON, 1))
            .strength(8)
            .meltingPoint(800)
            .allMaterials()
            .build();
    public static final IndustrialMaterial MINERAL_ASH = material("mineral_ash", "Mineral Ash", 0x8A8878)
            .parts(DUST)
            .build();
    public static final IndustrialMaterial TREATED_WOOD = material("treated_wood", "Treated Wood", 0x673d0d)
            .strength(1)
            .parts(SCREW, BOLT, ROD, LONG_ROD, DUST, FRAME, SMALL_GEAR, SMALL_DUST, TINY_DUST, GEAR, PLATE)
            .build();
    public static final IndustrialMaterial WOOD = material("wood", "Wood", 0xab7a43)
            .strength(1)
            .parts(SCREW, BOLT, LONG_ROD, DUST, FRAME, SMALL_GEAR, SMALL_DUST, TINY_DUST, GEAR, PLATE)
            .existing(ROD, "minecraft:stick")
            .build();


    public static final List<IndustrialMaterial> ELEMENTS = List.of(
            HYDROGEN,
            HELIUM,
            LITHIUM,
            BERYLLIUM,
            BORON,
            CARBON,
            NITROGEN,
            OXYGEN,
            FLUORINE,
            NEON,
            SODIUM,
            MAGNESIUM,
            ALUMINUM,
            SILICON,
            PHOSPHORUS,
            SULFUR,
            CHLORINE,
            ARGON,
            POTASSIUM,
            CALCIUM,
            SCANDIUM,
            TITANIUM,
            VANADIUM,
            CHROMIUM,
            MANGANESE,
            IRON,
            COBALT,
            NICKEL,
            COPPER,
            ZINC,
            GALLIUM,
            GERMANIUM,
            ARSENIC,
            SELENIUM,
            BROMINE,
            KRYPTON,
            RUBIDIUM,
            STRONTIUM,
            YTTRIUM,
            ZIRCONIUM,
            NIOBIUM,
            MOLYBDENUM,
            TECHNETIUM,
            RUTHENIUM,
            RHODIUM,
            PALLADIUM,
            SILVER,
            CADMIUM,
            INDIUM,
            TIN,
            ANTIMONY,
            TELLURIUM,
            IODINE,
            XENON,
            CESIUM,
            BARIUM,
            LANTHANUM,
            CERIUM,
            PRASEODYMIUM,
            NEODYMIUM,
            PROMETHIUM,
            SAMARIUM,
            EUROPIUM,
            GADOLINIUM,
            TERBIUM,
            DYSPROSIUM,
            HOLMIUM,
            ERBIUM,
            THULIUM,
            YTTERBIUM,
            LUTETIUM,
            HAFNIUM,
            TANTALUM,
            TUNGSTEN,
            RHENIUM,
            OSMIUM,
            IRIDIUM,
            PLATINUM,
            GOLD,
            MERCURY,
            THALLIUM,
            LEAD,
            BISMUTH,
            POLONIUM,
            ASTATINE,
            RADON,
            FRANCIUM,
            RADIUM,
            ACTINIUM,
            THORIUM,
            PROTACTINIUM,
            URANIUM,
            NEPTUNIUM,
            PLUTONIUM,
            AMERICIUM,
            CURIUM,
            BERKELIUM,
            CALIFORNIUM,
            EINSTEINIUM,
            FERMIUM,
            MENDELEVIUM,
            NOBELIUM,
            LAWRENCIUM,
            RUTHERFORDIUM,
            DUBNIUM,
            SEABORGIUM,
            BOHRIUM,
            HASSIUM,
            MEITNERIUM,
            DARMSTADTIUM,
            ROENTGENIUM,
            COPERNICIUM,
            NIHONIUM,
            FLEROVIUM,
            MOSCOVIUM,
            LIVERMORIUM,
            TENNESSINE,
            OGANESSON
    );

    public static final List<IndustrialFluid> FLUIDS = List.of(
            WATER,
            CRUDE_OIL,
            STEAM,
            CONCRETE,
            CREOSOTE_OIL,
            PLANT_OIL,
            BIOFUEL,
            ETHANOL,
            FERMENTATION_MASH,
            FERMENTED_MASH,
            BIOLUBRICANT,
            INDUSTRIAL_RESIN,
            RAW_RESIN,
            ORGANIC_BINDER
    );

    public static final List<IndustrialMaterial> MATERIALS = List.of(
                    BRONZE,
                    MINERAL_ASH,
                    FERTILIZER,
                    NETHER_BRICK,
                    NETHERRACK,
                    CLAY,
                    DEEPSLATE,
                    ASURINE,
                    CRIMSITE,
                    LIMESTONE,
                    OCHRUM,
                    SCORIA,
                    SCORCHIA,
                    VERIDIUM,
                    DIORITE,
                    AMPHIBOLE,
                    FELDSPAR,
                    MICA,
                    GRANITE,
                    BIO_CHAR,
                    STONE,
                    STEEL,
                    WROUGHT_IRON,
                    BRASS,
                    ELECTRUM,
                    ANDESITE_ALLOY,
                    DIAMOND,
                    EMERALD,
                    LAPIS,
                    REDSTONE,
                    QUARTZ,
                    GRAPHITE,
                    SODALITE,
                    LAZURITE,
                    HEMATITE,
                    MAGNETITE,
                    GOETHITE,
                    SIDERITE,
                    PYRITE,
                    CHALCOPYRITE,
                    MALACHITE,
                    AZURITE,
                    CUPRITE,
                    CHALCOCITE,
                    CASSITERITE,
                    STANNITE,
                    SPHALERITE,
                    SMITHSONITE,
                    ZINCITE,
                    GALENA,
                    CERUSSITE,
                    ANGLESITE,
                    BAUXITE,
                    GIBBSITE,
                    BOEHMITE,
                    CORUNDUM,
                    PENTLANDITE,
                    MILLERITE,
                    GARNIERITE,
                    COBALTITE,
                    SKUTTERUDITE,
                    ERYTHRITE,
                    PYROLUSITE,
                    RHODOCHROSITE,
                    MANGANITE,
                    CHROMITE,
                    ILMENITE,
                    RUTILE,
                    ANATASE,
                    ACANTHITE,
                    CHLORARGYRITE,
                    PROUSTITE,
                    NATIVE_GOLD,
                    CALAVERITE,
                    WOLFRAMITE,
                    SCHEELITE,
                    MOLYBDENITE,
                    WULFENITE,
                    NATIVE_PLATINUM,
                    SPERRYLITE,
                    COOPERITE,
                    BRAGGITE,
                    STIBIOPALLADINITE,
                    URANINITE,
                    PITCHBLENDE,
                    CARNOTITE,
                    MONAZITE,
                    THORITE,
                    ACTIVATED_CARBON,
                    SPODUMENE,
                    LEPIDOLITE,
                    PETALITE,
                    MAGNESITE,
                    DOLOMITE,
                    COAL,
                    CARNALLITE,
                    CALCITE,
                    GYPSUM,
                    FLUORITE,
                    APATITE,
                    HALITE,
                    TRONA,
                    NATRON,
                    SYLVITE,
                    ORTHOCLASE,
                    BASTNASITE,
                    XENOTIME,
                    TOPAZ,
                    RUBY,
                    SAPPHIRE,
                    ANDESITE,
                    TUFF,
                    TREATED_WOOD,
                    IRON_ANDESITE_COMPOUND,
                    WOOD
            );

    /**
     * Compatibility list used by existing code that needs every IndustrialMaterial.
     * New code should normally choose ELEMENTS or MATERIALS explicitly.
     */
    public static final List<IndustrialMaterial> ALL = java.util.stream.Stream.concat(
            ELEMENTS.stream(),
            MATERIALS.stream()
    ).toList();

    public static final List<IndustrialSubstance> SUBSTANCES = java.util.stream.Stream.concat(
            ALL.stream().map(material -> (IndustrialSubstance) material),
            FLUIDS.stream().map(fluid -> (IndustrialSubstance) fluid)
    ).toList();

    private static MaterialBuilder material(String id, String displayName, int color) {
        return new MaterialBuilder(id, displayName, color);
    }

    private static MaterialBuilder oreMineral(String id, String displayName, int color) {
        return material(id, displayName, color).allOreProcessing();
    }

    private static MaterialBuilder elementMaterial(String id, String displayName, String symbol, int color, int strength, int meltingPoint) {
        return material(id, displayName, color)
                .element(symbol)
                .strength(strength)
                .meltingPoint(meltingPoint);
    }

    private static ResourceLocation location(String id) {
        String[] parts = id.split(":", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected resource location like 'minecraft:iron_ingot', got '" + id + "'");
        }

        return ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
    }

    private static ResourceLocation textureLocation(String id) {
        if (id.contains(":")) {
            return location(id);
        }

        return ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    private static MaterialComponent component(IndustrialSubstance substance, int amount) {
        return new MaterialComponent(substance, amount);
    }

    private static FormulaComponent component(String symbol, int amount) {
        return new FormulaComponent(symbol, amount);
    }

    public static FluidBuilder fluid(String id, String displayName, int color) {
        return new FluidBuilder(id, displayName, color, IndustrialFluid.Kind.LIQUID)
                .density(1000)
                .temperature(300)
                .viscosity(1000);
    }

    public static FluidBuilder gas(String id, String displayName, int color) {
        return new FluidBuilder(id, displayName, color, IndustrialFluid.Kind.GAS)
                .density(-100)
                .temperature(300)
                .viscosity(100);
    }

    public static FluidBuilder molten(String id, String displayName, int color, int temperature) {
        return new FluidBuilder(id, displayName, color, IndustrialFluid.Kind.MOLTEN)
                .density(2000)
                .temperature(temperature)
                .viscosity(6000)
                .lightLevel(10);
    }

    public static final class FluidBuilder {
        private final String id;
        private final String displayName;
        private final int color;
        private final IndustrialFluid.Kind kind;
        private final List<MaterialComponent> components = new java.util.ArrayList<>();
        private int temperature;
        private int density;
        private int viscosity;
        private int lightLevel;
        private ResourceLocation existingFluid;

        private FluidBuilder(String id, String displayName, int color, IndustrialFluid.Kind kind) {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("Industrial fluid id cannot be blank");
            if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("Industrial fluid display name cannot be blank");
            this.id = id;
            this.displayName = displayName;
            this.color = color;
            this.kind = kind;
        }

        public FluidBuilder contains(MaterialComponent... components) {
            if (components == null) throw new IllegalArgumentException("Fluid components cannot be null");
            for (MaterialComponent component : components) {
                if (component == null) throw new IllegalArgumentException("Fluid component cannot be null");
                this.components.add(component);
            }
            return this;
        }

        public FluidBuilder existing(FluidPart part, String fluidId) {
            if (part != FLUID) throw new IllegalArgumentException("Unsupported fluid part: " + part);
            this.existingFluid = location(fluidId);
            return this;
        }

        public FluidBuilder temperature(int temperature) { this.temperature = temperature; return this; }
        public FluidBuilder density(int density) { this.density = density; return this; }

        public FluidBuilder viscosity(int viscosity) {
            if (viscosity < 0) throw new IllegalArgumentException("Fluid viscosity cannot be negative");
            this.viscosity = viscosity;
            return this;
        }

        public FluidBuilder lightLevel(int lightLevel) {
            if (lightLevel < 0 || lightLevel > 15) throw new IllegalArgumentException("Fluid light level must be between 0 and 15");
            this.lightLevel = lightLevel;
            return this;
        }

        public IndustrialFluid build() {
            return new IndustrialFluid(id, displayName, color, kind, temperature, density, viscosity,
                    lightLevel, List.copyOf(components), Optional.ofNullable(existingFluid));
        }
    }

    private static final class MaterialBuilder {
        private final String id;
        private final String displayName;
        private final int color;
        private final EnumSet<MaterialPart> parts = EnumSet.noneOf(MaterialPart.class);
        private final Map<MaterialPart, ResourceLocation> existingParts = new LinkedHashMap<>();
        private final Set<MaterialPart> existingRecipeParts = EnumSet.noneOf(MaterialPart.class);
        private final Map<MaterialPart, ResourceLocation> customPartTextures = new LinkedHashMap<>();
        private String itemMaterialSet = "dull";
        private String blockMaterialSet = "dull";
        private int strength = 1;
        private int meltingPoint = 300;
        private int temperature = 300;
        private int radioactivity;
        private String elementSymbol;
        private final List<MaterialComponent> components = new java.util.ArrayList<>();
        private final List<FormulaComponent> formulaComponents = new java.util.ArrayList<>();
        private final List<MaterialStoneSource> stoneSources = new java.util.ArrayList<>();
        private boolean strengthSet;
        private boolean meltingPointSet;
        private double furnaceFuelItems;
        private boolean furnaceFuelSet;
        private final Map<MaterialPart, Double> furnaceFuelParts = new LinkedHashMap<>();
        private MachineTier centrifugeTier;
        private int centrifugeInputCount;
        private MachineTier electrolyserTier;
        private IndustrialMaterial smeltingResult;
        private boolean smeltingSelf;

        private MaterialBuilder(String id, String displayName, int color) {
            this.id = id;
            this.displayName = displayName;
            this.color = color;
        }

        private MaterialBuilder allMetals() {
            return addParts(
                    ORE,
                    DEEPSLATE_ORE,
                    DIORITE_ORE,
                    ANDESITE_ORE,
                    GRANITE_ORE,
                    TUFF_ORE,
                    NETHERRACK_ORE,
                    BLACKSTONE_ORE,
                    END_STONE_ORE,
                    RAW_ORE,
                    RAW_BLOCK,
                    CRUSHED_ORE,
                    WASHED_CRUSHED_ORE,
                    REFINED_ORE,
                    IMPURE_DUST,
                    PURIFIED_DUST,
                    DUST,
                    SMALL_DUST,
                    TINY_DUST,
                    INGOT,
                    NUGGET,
                    BLOCK,
                    FRAME
            );
        }

        private MaterialBuilder allGems() {
            return addParts(
                    ORE,
                    DEEPSLATE_ORE,
                    DIORITE_ORE,
                    ANDESITE_ORE,
                    GRANITE_ORE,
                    TUFF_ORE,
                    NETHERRACK_ORE,
                    BLACKSTONE_ORE,
                    END_STONE_ORE,
                    RAW_ORE,
                    RAW_BLOCK,
                    CRUSHED_ORE,
                    WASHED_CRUSHED_ORE,
                    REFINED_ORE,
                    IMPURE_DUST,
                    PURIFIED_DUST,
                    DUST,
                    SMALL_DUST,
                    TINY_DUST,
                    GEM,
                    FLAWLESS_GEM,
                    EXQUISITE_GEM,
                    BLOCK
            );
        }

        private MaterialBuilder allOreProcessing() {
            return addParts(
                    ORE,
                    DEEPSLATE_ORE,
                    DIORITE_ORE,
                    ANDESITE_ORE,
                    GRANITE_ORE,
                    TUFF_ORE,
                    NETHERRACK_ORE,
                    BLACKSTONE_ORE,
                    END_STONE_ORE,
                    RAW_ORE,
                    CRUSHED_ORE,
                    WASHED_CRUSHED_ORE,
                    REFINED_ORE,
                    IMPURE_DUST,
                    PURIFIED_DUST,
                    DUST,
                    SMALL_DUST,
                    TINY_DUST
            );
        }

        private MaterialBuilder allMaterials() {
            return addParts(
                    INGOT,
                    NUGGET,
                    BLOCK,
                    DUST,
                    TINY_DUST,
                    PLATE,
                    DOUBLE_PLATE,
                    FOIL,
                    ROD,
                    LONG_ROD,
                    BOLT,
                    SCREW,
                    WIRE,
                    FINE_WIRE,
                    RING,
                    SMALL_RING,
                    LARGE_RING,
                    GEAR,
                    SMALL_GEAR,
                    LARGE_GEAR,
                    BEARING_BALL,
                    BEARING,
                    SPRING,
                    COIL,
                    ROTOR,
                    TOOL_HEAD_BUZZ_SAW,
                    FRAME,
                    MOLTEN_FLUID,
                    CAST_INGOT,
                    CAST_NUGGET,
                    CAST_BLOCK,
                    CAST_PLATE,
                    CAST_ROD,
                    CAST_LONG_ROD,
                    CAST_BOLT,
                    CAST_SCREW,
                    CAST_RING,
                    CAST_SMALL_RING,
                    CAST_LARGE_RING,
                    CAST_GEAR,
                    CAST_SMALL_GEAR,
                    CAST_BEARING_BALL,
                    CAST_BEARING,
                    CAST_ROTOR,
                    CAST_NUGGET_MOLD,
                    CAST_BEARING_BALL_MOLD,
                    CAST_ROTOR_MOLD,
                    CAST_INGOT_MOLD,
                    CAST_PLATE_MOLD,
                    CAST_ROD_MOLD,
                    CAST_LONG_ROD_MOLD,
                    CAST_BOLT_MOLD,
                    CAST_RING_MOLD,
                    CAST_SMALL_RING_MOLD,
                    CAST_LARGE_RING_MOLD,
                    CAST_GEAR_MOLD,
                    CAST_SMALL_GEAR_MOLD,
                    CAST_BEARING_MOLD,
                    CAST_SCREW_MOLD,
                    HOT_CAST_NUGGET_MOLD,
                    HOT_CAST_BEARING_BALL_MOLD,
                    HOT_CAST_ROTOR_MOLD,
                    HOT_CAST_INGOT_MOLD,
                    HOT_CAST_PLATE_MOLD,
                    HOT_CAST_ROD_MOLD,
                    HOT_CAST_LONG_ROD_MOLD,
                    HOT_CAST_BOLT_MOLD,
                    HOT_CAST_RING_MOLD,
                    HOT_CAST_SMALL_RING_MOLD,
                    HOT_CAST_LARGE_RING_MOLD,
                    HOT_CAST_GEAR_MOLD,
                    HOT_CAST_SMALL_GEAR_MOLD,
                    HOT_CAST_BEARING_MOLD,
                    HOT_CAST_SCREW_MOLD,
                    REINFORCED_PLATE,
                    DENSE_PLATE,
                    HEAT_EXCHANGER_PLATE
            );
        }

        private MaterialBuilder solidMetal() {
            return allMetals().allMaterials();
        }

        private MaterialBuilder gemElement() {
            return allGems();
        }

        private MaterialBuilder fluidOnly() {
            return addParts(MOLTEN_FLUID);
        }

        private MaterialBuilder parts(MaterialPart... parts) {
            return addParts(parts);
        }

        private MaterialBuilder element(String symbol) {
            this.elementSymbol = symbol;
            return this;
        }

        private MaterialBuilder alloyOf(MaterialComponent... components) {
            this.components.addAll(List.of(components));
            return this;
        }

        private MaterialBuilder contains(MaterialComponent... components) {
            return alloyOf(components);
        }

        private MaterialBuilder components(FormulaComponent... components) {
            this.formulaComponents.addAll(List.of(components));
            return this;
        }

        private MaterialBuilder existing(MaterialPart part, String id) {
            if (part == STONES) {
                addExistingStoneSource("stone", id);
                return this;
            }
            existingParts.put(part, location(id));
            return this;
        }

        private MaterialBuilder existingRecipe(MaterialPart part) {
            existingRecipeParts.add(part);
            return this;
        }


        private MaterialBuilder notexisting(MaterialPart part, String texturePath) {
            if (part == STONES) {
                stoneSources.add(MaterialStoneSource.generated("stone", textureLocation(texturePath)));
                return this;
            }
            parts.add(part);
            customPartTextures.put(part, textureLocation(texturePath));
            return this;
        }


        private MaterialBuilder stone() {
            addExistingStoneSource("stone", "minecraft:" + id);
            return this;
        }

        private MaterialBuilder existing(IndustrialMaterial stoneMaterial, String blockId) {
            addExistingStoneSource(stoneMaterial.id(), blockId);
            return this;
        }

        private MaterialBuilder existing(String stoneId, String blockId) {
            addExistingStoneSource(stoneId, blockId);
            return this;
        }

        private MaterialBuilder notexistingStone(String stoneId, String texturePath) {
            stoneSources.add(MaterialStoneSource.generated(stoneId, textureLocation(texturePath)));
            return this;
        }

        private MaterialBuilder notexisting(IndustrialMaterial stoneMaterial, String texturePath) {
            return notexistingStone(stoneMaterial.id(), texturePath);
        }

        private MaterialBuilder notexisting(String stoneId, String texturePath) {
            return notexistingStone(stoneId, texturePath);
        }






        private void addExistingStoneSource(String stoneId, String blockId) {
            ResourceLocation block = location(blockId);
            for (MaterialStoneSource source : stoneSources) {
                if (source.id().equals(stoneId) && source.existingBlock().filter(block::equals).isPresent()) {
                    return;
                }
            }
            stoneSources.add(MaterialStoneSource.existing(stoneId, block));
        }

        private MaterialBuilder strength(int strength) {
            if (strength < 1) {
                throw new IllegalArgumentException("Material strength must be 1 or higher");
            }

            this.strength = strength;
            this.strengthSet = true;
            return this;
        }

        private MaterialBuilder meltingPoint(int meltingPoint) {
            this.meltingPoint = meltingPoint;
            this.meltingPointSet = true;
            return this;
        }

        private MaterialBuilder furnaceFuel(double itemsSmelted) {
            if (itemsSmelted <= 0) {
                throw new IllegalArgumentException("Furnace fuel amount must be higher than 0");
            }

            this.furnaceFuelItems = itemsSmelted;
            this.furnaceFuelSet = true;
            return this;
        }

        private MaterialBuilder furnaceFuel(MaterialPart part, double itemsSmelted) {
            if (part == null) {
                throw new IllegalArgumentException("Fuel material part cannot be null");
            }
            if (part.isFluid()) {
                throw new IllegalArgumentException("Fluid material parts cannot be furnace fuel: " + part);
            }
            if (itemsSmelted <= 0) {
                throw new IllegalArgumentException("Furnace fuel amount must be higher than 0");
            }

            addParts(part);
            furnaceFuelParts.put(part, itemsSmelted);
            return this;
        }

        private MaterialBuilder centrifuge(MachineTier tier) {
            return centrifuge(tier, 0);
        }

        private MaterialBuilder centrifuge(MachineTier tier, int inputCount) {
            if (inputCount < 0) {
                throw new IllegalArgumentException("Centrifuge input count cannot be negative");
            }
            this.centrifugeTier = tier;
            this.centrifugeInputCount = inputCount;
            return this;
        }

        private MaterialBuilder electrolyser(MachineTier tier) {
            this.electrolyserTier = tier;
            return this;
        }

        private MaterialBuilder electrolyzer(MachineTier tier) {
            return electrolyser(tier);
        }

        private MaterialBuilder smelting(IndustrialMaterial result) {
            if (result == null) {
                throw new IllegalArgumentException("Smelting result cannot be null");
            }

            this.smeltingResult = result;
            return this;
        }

        private MaterialBuilder smeltingSelf() {
            this.smeltingSelf = true;
            return this;
        }

        private MaterialBuilder temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        private MaterialBuilder radioactivity(int radioactivity) {
            if (radioactivity < 0) {
                throw new IllegalArgumentException("Material radioactivity must be 0 or higher");
            }

            this.radioactivity = radioactivity;
            return this;
        }

        private MaterialBuilder existing(MaterialPart part) {
            existingParts.put(part, ResourceLocation.withDefaultNamespace("air"));
            return this;
        }


        private MaterialBuilder materialSet(String materialSet) {
            this.itemMaterialSet = materialSet;
            this.blockMaterialSet = materialSet;
            return this;
        }

        private MaterialBuilder itemMaterialSet(String materialSet) {
            this.itemMaterialSet = materialSet;
            return this;
        }

        private MaterialBuilder blockMaterialSet(String materialSet) {
            this.blockMaterialSet = materialSet;
            return this;
        }

        private IndustrialMaterial build() {
            EnumSet<MaterialPart> materialParts = parts.isEmpty()
                    ? EnumSet.noneOf(MaterialPart.class)
                    : EnumSet.copyOf(parts);

            return new IndustrialMaterial(
                    id,
                    displayName,
                    color,
                    itemMaterialSet,
                    blockMaterialSet,
                    materialParts,
                    Map.copyOf(existingParts),
                    Set.copyOf(existingRecipeParts),
                    Map.copyOf(customPartTextures),
                    strength,
                    resolvedMeltingPoint(),
                    strengthSet,
                    meltingPointSet,
                    temperature,
                    radioactivity,
                    Optional.ofNullable(elementSymbol),
                    resolvedComponents(),
                    furnaceFuelItems,
                    furnaceFuelSet,
                    Map.copyOf(furnaceFuelParts),
                    List.copyOf(stoneSources),
                    Optional.ofNullable(centrifugeTier),
                    centrifugeInputCount,
                    Optional.ofNullable(electrolyserTier),
                    Optional.ofNullable(smeltingResult),
                    smeltingSelf
            );
        }

        private MaterialBuilder addParts(MaterialPart... parts) {
            this.parts.addAll(List.of(parts));
            return this;
        }

        private int resolvedMeltingPoint() {
            if (components.isEmpty()) {
                return meltingPoint;
            }

            int componentMeltingPoint = components.stream()
                    .mapToInt(component -> component.substance().componentTemperature())
                    .max()
                    .orElse(meltingPoint);
            return Math.max(meltingPoint, componentMeltingPoint);
        }

        private List<MaterialComponent> resolvedComponents() {
            if (!formulaComponents.isEmpty()) {
                return formulaComponents.stream()
                        .map(component -> new MaterialComponent(
                                new IndustrialMaterial(
                                        component.symbol().toLowerCase(),
                                        component.symbol(),
                                        0xFFFFFF,
                                        "dull",
                                        "dull",
                                        EnumSet.noneOf(MaterialPart.class),
                                        Map.of(),
                                        Set.of(),
                                        Map.of(),
                                        1,
                                        300,
                                        false,
                                        false,
                                        300,
                                        0,
                                        Optional.of(component.symbol()),
                                        List.of(),
                                        0,
                                        false,
                                        Map.of(),
                                        List.of(),
                                        Optional.empty(),
                                        0,
                                        Optional.empty(),
                                        Optional.empty(),
                                        false
                                ),
                                component.amount()
                        ))
                        .toList();
            }

            return List.copyOf(components);
        }
    }

    private record FormulaComponent(String symbol, int amount) {
        private FormulaComponent {
            if (amount < 1) {
                throw new IllegalArgumentException("Formula component amount must be 1 or higher");
            }
        }
    }
}
