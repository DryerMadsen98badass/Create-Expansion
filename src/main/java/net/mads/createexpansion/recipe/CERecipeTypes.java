package net.mads.createexpansion.recipe;

import net.mads.createexpansion.recipe.recipetypes.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CERecipeTypes {
    public static final RecipeTypeDefinition BLAST_FURNACE = BlastFurnaceRecipeType.BLAST_FURNACE;
    public static final RecipeTypeDefinition COKE_OVEN = CokeOvenRecipeType.COKE_OVEN;
    public static final RecipeTypeDefinition CENTRIFUGE = CentrifugeRecipeType.CENTRIFUGE;
    /** @deprecated Recipes are no longer tied to electric power. */
    @Deprecated(forRemoval = false)
    public static final RecipeTypeDefinition ELECTRIC_CENTRIFUGE = CENTRIFUGE;
    /** @deprecated Recipes are no longer tied to kinetic power. */
    @Deprecated(forRemoval = false)
    public static final RecipeTypeDefinition LARGE_KINETIC_CENTRIFUGE = CENTRIFUGE;
    public static final RecipeTypeDefinition ELECTROLYSER = ElectrolyserRecipeType.ELECTROLYSER;
    public static final RecipeTypeDefinition FORGE_HAMMER = ForgeHammerRecipeType.FORGE_HAMMER;
    public static final RecipeTypeDefinition ALLOY_SMELTER = AlloySmelterRecipeType.ALLOY_SMELTER;
    public static final RecipeTypeDefinition AUTOCLAVE = AutoclaveRecipeType.AUTOCLAVE;
    public static final RecipeTypeDefinition INDUCTION_CHAMBER = InductionChamberRecipeType.INDUCTION_CHAMBER;
    public static final RecipeTypeDefinition EXTRACTOR = ExtractorRecipeType.EXTRACTOR;
    public static final RecipeTypeDefinition COMPRESSOR = CompressorRecipeType.COMPRESSOR;
    public static final RecipeTypeDefinition DISTILLERY = DistilleryRecipeType.DISTILLERY;
    public static final RecipeTypeDefinition TREE_EXTRACTING = TreeExtractingRecipeType.TREE_EXTRACTING;
    public static final RecipeTypeDefinition SPRINKLING = SprinklingRecipeType.SPRINKLING;
    public static final RecipeTypeDefinition SOLID_FUEL_BOILER = SolidFuelBoilerRecipeType.SOLID_FUEL_BOILER;
    public static final RecipeTypeDefinition LIQUID_FUEL_BOILER = LiquidFuelBoilerRecipeType.LIQUID_FUEL_BOILER;
    public static final RecipeTypeDefinition FLUID_SOLIDIFIER = FluidSolidifierRecipeType.FLUID_SOLIDIFIER;
    public static final RecipeTypeDefinition DIRTY_ASSEMBLER = DirtyAssemblerRecipeType.DIRTY_ASSEMBLER;


    public static final List<RecipeTypeDefinition> ALL = List.of(
            BLAST_FURNACE,
            COKE_OVEN,
            CENTRIFUGE,
            ELECTROLYSER,
            FORGE_HAMMER,
            ALLOY_SMELTER,
            AUTOCLAVE,
            INDUCTION_CHAMBER,
            EXTRACTOR,
            COMPRESSOR,
            DISTILLERY,
            TREE_EXTRACTING,
            SPRINKLING,
            SOLID_FUEL_BOILER,
            LIQUID_FUEL_BOILER,
            FLUID_SOLIDIFIER,
            DIRTY_ASSEMBLER
    );

    private static final Map<ResourceLocation, RecipeTypeDefinition> BY_ID = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(RecipeTypeDefinition::id, Function.identity()));

    private CERecipeTypes() {
    }

    public static RecipeTypeDefinition byId(ResourceLocation id) {
        return BY_ID.get(id);
    }
}
