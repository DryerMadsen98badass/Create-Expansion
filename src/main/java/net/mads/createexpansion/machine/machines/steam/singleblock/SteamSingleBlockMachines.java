package net.mads.createexpansion.machine.machines.steam.singleblock;

import net.mads.createexpansion.block.MiningTier;
import net.mads.createexpansion.block.MiningTool;
import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.SingleBlockDefinition;
import net.mads.createexpansion.recipe.CERecipeTypes;

import java.util.List;

public final class SteamSingleBlockMachines {

    public static final SingleBlockDefinition STEAM_FORGE_HAMMER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_forge_hammer"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Forge Hammer"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_FORGE_HAMMER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(25))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.HAMMER_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/forge_hammer/overlay_front_active_1", "block/machines/overlay/forge_hammer/overlay_front_active_1", "block/machines/overlay/forge_hammer/overlay_front_active_2", "block/machines/overlay/forge_hammer/overlay_front_active_3","block/machines/overlay/forge_hammer/overlay_front_active_4","block/machines/overlay/forge_hammer/overlay_front_active_5"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_ALLOY_SMELTER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_alloy_smelter"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Alloy Smelter"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_ALLOY_SMELTER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(2, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(50))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/alloy_smelter/overlay_front", "block/machines/overlay/alloy_smelter/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_AUTOCLAVE =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_autoclave"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Autoclave"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_AUTOCLAVE))
                    .machineDefinition(SingleBlockDefinition.Option.slots(3, 3, 1, 1))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(50))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/autoclave/overlay_front", "block/machines/overlay/autoclave/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_INDUCTION_CHAMBER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_induction_chamber"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Induction Chamber"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_INDUCTION_CHAMBER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(2, 2, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(50))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/induction_chamber/overlay_front", "block/machines/overlay/induction_chamber/overlay_front_active_1", "block/machines/overlay/induction_chamber/overlay_front_active_2", "block/machines/overlay/induction_chamber/overlay_front_active_3", "block/machines/overlay/induction_chamber/overlay_front_active_4"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_EXTRACTOR =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_extractor"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Extractor"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_EXTRACTOR))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 1))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(75))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/extractor/overlay_front", "block/machines/overlay/extractor/overlay_front_active_1", "block/machines/overlay/extractor/overlay_front_active_2", "block/machines/overlay/extractor/overlay_front_active_3","block/machines/overlay/extractor/overlay_front_active_4","block/machines/overlay/extractor/overlay_front_active_5"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_COMPRESSOR =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_compressor"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Compressor"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_COMPRESSOR))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(25))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.COMPRESS_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/compressor/overlay_front", "block/machines/overlay/compressor/overlay_front_active_1", "block/machines/overlay/compressor/overlay_front_active_2", "block/machines/overlay/compressor/overlay_front_active_3","block/machines/overlay/compressor/overlay_front_active_4","block/machines/overlay/compressor/overlay_front_active_5","block/machines/overlay/compressor/overlay_front_active_6","block/machines/overlay/compressor/overlay_front_active_7","block/machines/overlay/compressor/overlay_front_active_8","block/machines/overlay/compressor/overlay_front_active_9"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_DISTILLERY =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_distillery"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Distillery"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.STEAM_DISTILLERY))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 1, 1))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(25))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.overlay("block/machines/overlay/distillery/overlay_front", "block/machines/overlay/distillery/overlay_front_active_1", "block/machines/overlay/distillery/overlay_front_active_2", "block/machines/overlay/distillery/overlay_front_active_3"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();


    public static final List<SingleBlockDefinition> ALL = List.of(
            STEAM_FORGE_HAMMER,
            STEAM_ALLOY_SMELTER,
            STEAM_INDUCTION_CHAMBER,
            STEAM_AUTOCLAVE,
            STEAM_EXTRACTOR,
            STEAM_COMPRESSOR,
            STEAM_DISTILLERY
    );

    private SteamSingleBlockMachines() {
    }
}
