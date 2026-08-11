package net.mads.createexpansion.machine.machines.electric.multiblock.machines;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition.Option;
import net.mads.createexpansion.recipe.CERecipeTypes;

import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.ability;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.block;

public final class ph_test_machine {
    private static final char CASING = 'c';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.machine()
            .machineDefinition(MultiblockControllerDefinition.Option.id("ph_test_machine"))
            .machineDefinition(MultiblockControllerDefinition.Option.displayName("pH Test Machine"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontTexture("minecraft:block/dirt"))
            .machineDefinition(MultiblockControllerDefinition.Option.backTexture("minecraft:block/dirt"))
            .machineDefinition(MultiblockControllerDefinition.Option.leftTexture("minecraft:block/dirt"))
            .machineDefinition(MultiblockControllerDefinition.Option.rightTexture("minecraft:block/dirt"))
            .machineDefinition(MultiblockControllerDefinition.Option.topTexture("minecraft:block/dirt"))
            .machineDefinition(MultiblockControllerDefinition.Option.bottomTexture("minecraft:block/dirt"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontOverlay(
                    "minecraft:block/dirt",
                    "minecraft:block/dirt"
            ))
            .build();

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.machine()
            .machineDefinition(Option.id("ph_test_machine"))
            .machineDefinition(Option.controller(CONTROLLER))
            .machineDefinition(Option.displayName("pH Test Machine"))
            .machineDefinition(Option.recipeType(CERecipeTypes.AUTOCLAVE))
            .machineDefinition(Option.energyUsage(2))
            .machineDefinition(Option.phRange(6.0, 9.0))
            .machineDefinition(Option.machineDurability(1000))
            .machineDefinition(Option.tooltip(
                    "Temporary 3x3x3 pH test multiblock.",
                    "Runs Autoclave recipes using CE.",
                    "Replace Dirt with Item Input, Item Output, Energy Input and pH Hatches."
            ))
            .machineDefinition(Option.variant("1", pattern -> pattern
                    .layer(
                            row(CASING, CASING, CASING),
                            row(CASING, CASING, CASING),
                            row(CASING, CASING, CASING)
                    )
                    .layer(
                            row(CASING, CASING, CASING),
                            row(controller, CASING, CASING),
                            row(CASING, CASING, CASING)
                    )
                    .layer(
                            row(CASING, CASING, CASING),
                            row(CASING, CASING, CASING),
                            row(CASING, CASING, CASING)
                    )
            ))
            .machineDefinition(Option.where(CASING,
                    block("minecraft:dirt")
                            .or(ability(MultiblockAbility.ITEM_INPUT).overlay("minecraft:block/dirt"))
                            .or(ability(MultiblockAbility.ITEM_OUTPUT).overlay("minecraft:block/dirt"))
                            .or(ability(MultiblockAbility.ENERGY_INPUT).overlay("minecraft:block/dirt"))
                            .or(ability(MultiblockAbility.PH_INPUT).overlay("minecraft:block/dirt"))
                            .or(ability(MultiblockAbility.REDSTONE).overlay("minecraft:block/dirt"))
            ))
            .build();

    private ph_test_machine() {
    }
}
