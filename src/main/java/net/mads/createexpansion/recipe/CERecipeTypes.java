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
    public static final RecipeTypeDefinition LARGE_KINETIC_CENTRIFUGE = LargeKineticCentrifugeRecipeType.LARGE_KINETIC_CENTRIFUGE;
    public static final RecipeTypeDefinition ELECTRIC_CENTRIFUGE = ElectricCentrifugeRecipeType.ELECTRIC_CENTRIFUGE;
    public static final RecipeTypeDefinition ELECTROLYSER = ElectrolyserRecipeType.ELECTROLYSER;
    public static final RecipeTypeDefinition STEAM_FORGE_HAMMER = SteamforgehammerRecipeType.STEAM_FORGE_HAMMER;
    public static final RecipeTypeDefinition STEAM_ALLOY_SMELTER = SteamalloysmelterRecipeType.STEAM_ALLOY_SMELTER;
    public static final RecipeTypeDefinition STEAM_AUTOCLAVE = SteamautoclaveRecipeType.STEAM_AUTOCLAVE;
    public static final RecipeTypeDefinition STEAM_INDUCTION_CHAMBER = SteaminductionchamberRecipeType.STEAM_INDUCTION_CHAMBER;
    public static final RecipeTypeDefinition STEAM_EXTRACTOR = SteamextractorRecipeType.STEAM_EXTRACTOR;
    public static final RecipeTypeDefinition STEAM_COMPRESSOR = SteamcompressorRecipeType.STEAM_COMPRESSOR;
    public static final RecipeTypeDefinition STEAM_DISTILLERY = SteamdistilleryRecipeType.STEAM_DISTILLERY;


    public static final List<RecipeTypeDefinition> ALL = List.of(
            BLAST_FURNACE,
            COKE_OVEN,
            LARGE_KINETIC_CENTRIFUGE,
            ELECTRIC_CENTRIFUGE,
            ELECTROLYSER,
            STEAM_FORGE_HAMMER,
            STEAM_ALLOY_SMELTER,
            STEAM_AUTOCLAVE,
            STEAM_INDUCTION_CHAMBER,
            STEAM_EXTRACTOR,
            STEAM_COMPRESSOR,
            STEAM_DISTILLERY
    );

    private static final Map<ResourceLocation, RecipeTypeDefinition> BY_ID = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(RecipeTypeDefinition::id, Function.identity()));

    private CERecipeTypes() {
    }

    public static RecipeTypeDefinition byId(ResourceLocation id) {
        return BY_ID.get(id);
    }
}
