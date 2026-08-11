package net.mads.createexpansion.machine.machines.steam.singleblock;

import net.mads.createexpansion.block.MiningTier;
import net.mads.createexpansion.block.MiningTool;
import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.SingleBlockDefinition;
import net.mads.createexpansion.machine.interaction.MachineCondition;
import net.mads.createexpansion.machine.interaction.AreaValue;
import net.mads.createexpansion.machine.interaction.BlockInteraction;
import net.mads.createexpansion.machine.interaction.MachineArea;
import net.mads.createexpansion.recipe.CERecipeTypes;

import java.util.List;

public final class SteamSingleBlockMachines {

    public static final SingleBlockDefinition STEAM_FORGE_HAMMER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_forge_hammer"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Forge Hammer"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.FORGE_HAMMER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(200))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.HAMMER_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/forge_hammer/overlay_front_active_1", "block/machines/overlay/forge_hammer/overlay_front_active_1", "block/machines/overlay/forge_hammer/overlay_front_active_2", "block/machines/overlay/forge_hammer/overlay_front_active_3","block/machines/overlay/forge_hammer/overlay_front_active_4","block/machines/overlay/forge_hammer/overlay_front_active_5"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_ALLOY_SMELTER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_alloy_smelter"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Alloy Smelter"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.ALLOY_SMELTER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(2, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(150))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/alloy_smelter/overlay_front", "block/machines/overlay/alloy_smelter/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_AUTOCLAVE =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_autoclave"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Autoclave"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.AUTOCLAVE))
                    .machineDefinition(SingleBlockDefinition.Option.slots(3, 3, 2, 1))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(100))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/autoclave/overlay_front", "block/machines/overlay/autoclave/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_INDUCTION_CHAMBER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_induction_chamber"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Induction Chamber"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.INDUCTION_CHAMBER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(2, 2, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(200))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/induction_chamber/overlay_front", "block/machines/overlay/induction_chamber/overlay_front_active_1", "block/machines/overlay/induction_chamber/overlay_front_active_2", "block/machines/overlay/induction_chamber/overlay_front_active_3", "block/machines/overlay/induction_chamber/overlay_front_active_4"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_EXTRACTOR =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_extractor"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Extractor"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.EXTRACTOR))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 1))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(200))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/extractor/overlay_front", "block/machines/overlay/extractor/overlay_front_active_1", "block/machines/overlay/extractor/overlay_front_active_2", "block/machines/overlay/extractor/overlay_front_active_3","block/machines/overlay/extractor/overlay_front_active_4","block/machines/overlay/extractor/overlay_front_active_5"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_COMPRESSOR =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_compressor"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Compressor"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.COMPRESSOR))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(100))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.COMPRESS_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/compressor/overlay_front", "block/machines/overlay/compressor/overlay_front_active_1", "block/machines/overlay/compressor/overlay_front_active_2", "block/machines/overlay/compressor/overlay_front_active_3","block/machines/overlay/compressor/overlay_front_active_4","block/machines/overlay/compressor/overlay_front_active_5","block/machines/overlay/compressor/overlay_front_active_6","block/machines/overlay/compressor/overlay_front_active_7","block/machines/overlay/compressor/overlay_front_active_8","block/machines/overlay/compressor/overlay_front_active_9"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition STEAM_DISTILLERY =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_distillery"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Steam Distillery"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.DISTILLERY))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 1, 1))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(50))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.EXTRACT_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/distillery/overlay_front", "block/machines/overlay/distillery/overlay_front_active_1", "block/machines/overlay/distillery/overlay_front_active_2", "block/machines/overlay/distillery/overlay_front_active_3"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();

    public static final SingleBlockDefinition COPPER_SOLID_FUEL_BOILER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("copper_solid_fuel_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Copper Solid Fuel Boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.SOLID_FUEL_BOILER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 0, 1, 1))

                    .machineDefinition(SingleBlockDefinition.Option.temperature(60, 120, 20, 1, 1,
                            SingleBlockDefinition.Option.inputFluid("minecraft:water", 1, 17),
                            SingleBlockDefinition.Option.outputFluid("create_expansion:steam", 100, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/casings/casing/bricked_copper_casing_top"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/casings/casing/bricked_copper_casing_bottom"))

                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/boilers/solid_boiler_front", "block/machines/overlay/boilers/solid_boiler_front_active_1"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))

                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Input water and eny furnace fuel and wait for the boiler to heat up"))
                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Steam: 100 mb/t"))
                    .build();

    public static final SingleBlockDefinition COPPER_SOLAR_BOILER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("copper_solar_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Copper Solar Boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.slots(0, 0, 1, 1))

                    .machineDefinition(SingleBlockDefinition.Option.temperature(60, 120, 20, 1, 1,
                            SingleBlockDefinition.Option.condition(MachineCondition.canSeeSky().at(0, 1, 0)),
                            SingleBlockDefinition.Option.condition(MachineCondition.time(0, 12000)),
                            SingleBlockDefinition.Option.inputFluid("minecraft:water", 1, 34),
                            SingleBlockDefinition.Option.outputFluid("create_expansion:steam", 50, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/casings/casing/bricked_copper_casing_top"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/casings/casing/bricked_copper_casing_bottom"))

                    .machineDefinition(SingleBlockDefinition.Option.topOverlay("block/machines/overlay/boilers/bronze_solar_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))

                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Requires direct access to the sky and daylight to heat water"))
                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Steam: 50 mb/t"))
                    .build();

    public static final SingleBlockDefinition BRONZE_SOLID_FUEL_BOILER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("bronze_solid_fuel_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Bronze Solid Fuel Boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.SOLID_FUEL_BOILER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 0, 1, 1))

                    .machineDefinition(SingleBlockDefinition.Option.temperature(60, 120, 20, 1, 1,
                            SingleBlockDefinition.Option.inputFluid("minecraft:water", 2, 17),
                            SingleBlockDefinition.Option.outputFluid("create_expansion:steam", 200, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/casings/casing/bricked_bronze_casing_top"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/casings/casing/bricked_bronze_casing_bottom"))

                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/boilers/solid_boiler_front", "block/machines/overlay/boilers/solid_boiler_front_active_1"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))

                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Input water and eny furnace fuel and wait for the boiler to heat up"))
                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Steam: 200 mb/t"))
                    .build();

    public static final SingleBlockDefinition BRONZE_SOLAR_BOILER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("bronze_solar_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Bronze Solar Boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.slots(0, 0, 1, 1))

                    .machineDefinition(SingleBlockDefinition.Option.temperature(60, 120, 20, 1, 1,
                            SingleBlockDefinition.Option.condition(MachineCondition.canSeeSky().at(0, 1, 0)),
                            SingleBlockDefinition.Option.condition(MachineCondition.time(0, 12000)),
                            SingleBlockDefinition.Option.inputFluid("minecraft:water", 1, 17),
                            SingleBlockDefinition.Option.outputFluid("create_expansion:steam", 100, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/casings/casing/bricked_bronze_casing_top"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/casings/casing/bricked_bronze_casing_bottom"))

                    .machineDefinition(SingleBlockDefinition.Option.topOverlay("block/machines/overlay/boilers/bronze_solar_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))

                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Requires direct access to the sky and daylight to heat water"))
                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Steam: 100 mb/t"))
                    .build();

    public static final SingleBlockDefinition COPPER_LIQUID_FUEL_BOILER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("copper_liquid_fuel_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Copper Liquid Fuel Boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.LIQUID_FUEL_BOILER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(0, 0, 2, 1))

                    .machineDefinition(SingleBlockDefinition.Option.temperature(60, 120, 20, 1, 1,
                            SingleBlockDefinition.Option.inputFluid("minecraft:water", 1, 17),
                            SingleBlockDefinition.Option.outputFluid("create_expansion:steam", 100, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/casings/casing/bricked_copper_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/casings/casing/bricked_copper_casing_top"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/casings/casing/bricked_copper_casing_bottom"))

                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/boilers/liquid_boiler_overlay_front", "block/machines/overlay/boilers/liquid_boiler_overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))

                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Input water and fuel and wait for the boiler to heat up"))
                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Steam: 100 mb/t"))
                    .build();

    public static final SingleBlockDefinition BRONZE_LIQUID_FUEL_BOILER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("bronze_liquid_fuel_boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Bronze Liquid Fuel Boiler"))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.LIQUID_FUEL_BOILER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(0, 0, 2, 1))

                    .machineDefinition(SingleBlockDefinition.Option.temperature(60, 120, 20, 1, 1,
                            SingleBlockDefinition.Option.inputFluid("minecraft:water", 2, 17),
                            SingleBlockDefinition.Option.outputFluid("create_expansion:steam", 200, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/casings/casing/bricked_bronze_casing_side"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/casings/casing/bricked_bronze_casing_top"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/casings/casing/bricked_bronze_casing_bottom"))

                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/boilers/liquid_boiler_overlay_front", "block/machines/overlay/boilers/liquid_boiler_overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))

                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Input water and fuel and wait for the boiler to heat up"))
                    .machineDefinition(SingleBlockDefinition.Option.tooltip("Steam: 200 mb/t"))
                    .build();



    /**
     * Steam-driven crop sprinkler. Copper covers 5 x 5 and performs one growth
     * attempt every five ticks. Bronze is generated automatically, covers 7 x 7
     * and performs two attempts per interval. Overlay is intentionally omitted.
     */
    public static final SingleBlockDefinition STEAM_SPRINKLER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("steam_sprinkler"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Sprinkler"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesSteam())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.STEAM_COPPER))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.SPRINKLING))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 0, 1, 0))
                    .machineDefinition(SingleBlockDefinition.Option.steamCapacity(8000))
                    .machineDefinition(SingleBlockDefinition.Option.steamUsage(100))
                    .machineDefinition(SingleBlockDefinition.Option.noDurationReset())
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW_BRONZE))
                    .machineDefinition(SingleBlockDefinition.Option.area(
                            MachineArea.area("spray_area")
                                    .include(MachineArea.box()
                                            .left(AreaValue.fixed(2).plusPerTier(1))
                                            .right(AreaValue.fixed(2).plusPerTier(1))
                                            .bottom(2)
                                            .top(2)
                                            .front(AreaValue.fixed(2).plusPerTier(1))
                                            .back(AreaValue.fixed(2).plusPerTier(1)))
                    ))
                    .machineDefinition(SingleBlockDefinition.Option.blockInteraction(
                            BlockInteraction.sprinkler()
                                    .inArea("spray_area")
                                    .interval(5)
                                    .actionsPerInterval(1)
                                    .actionMultiplierPerTier(2)
                    ))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/autoclave/overlay_front", "block/machines/overlay/autoclave/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.backOverlay("block/machines/overlay/autoclave/overlay_front", "block/machines/overlay/autoclave/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.rightOverlay("block/machines/overlay/autoclave/overlay_front", "block/machines/overlay/autoclave/overlay_front_active"))
                    .machineDefinition(SingleBlockDefinition.Option.leftOverlay("block/machines/overlay/autoclave/overlay_front", "block/machines/overlay/autoclave/overlay_front_active"))

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
            STEAM_DISTILLERY,
            STEAM_SPRINKLER,
            COPPER_SOLID_FUEL_BOILER,
            COPPER_SOLAR_BOILER,
            BRONZE_SOLID_FUEL_BOILER,
            BRONZE_SOLAR_BOILER,
            BRONZE_LIQUID_FUEL_BOILER,
            COPPER_LIQUID_FUEL_BOILER
    );

    private SteamSingleBlockMachines() {
    }
}
