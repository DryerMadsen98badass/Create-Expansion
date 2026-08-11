package net.mads.createexpansion.machine.machines.without_energy.multiblock;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition.Option;
import net.mads.createexpansion.recipe.CERecipeTypes;

import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.*;

public class coke_oven {


    private static final char a = 'a';
    private static final char b = 'b';
    private static final char i = 'i';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.machine()
            .machineDefinition(MultiblockControllerDefinition.Option.id("coke_oven"))
            .machineDefinition(MultiblockControllerDefinition.Option.displayName("Coke Oven"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontTexture("create_expansion:block/casings/casing/silica_bricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.backTexture("create_expansion:block/casings/casing/silica_bricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.leftTexture("create_expansion:block/casings/casing/silica_bricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.rightTexture("create_expansion:block/casings/casing/silica_bricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.topTexture("create_expansion:block/casings/casing/silica_bricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.bottomTexture("create_expansion:block/casings/casing/silica_bricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontOverlay(
                    "block/machines/overlay/coke_oven/coke_oven_off",
                    "block/machines/overlay/coke_oven/coke_oven_on"
            ))
            .build();

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.machine()
            .machineDefinition(Option.id("coke_oven"))
            .machineDefinition(Option.controller(CONTROLLER))
            .machineDefinition(Option.displayName("Coke Oven"))
            .machineDefinition(Option.recipeType(CERecipeTypes.COKE_OVEN))
            .machineDefinition(Option.tooltip(
                    "Smelts ore into molten metal using solid fuel.",
                    "Requires a constant supply of coke or charcoal.",
                    "The interior chamber holds the ore, fuel, and flux."
            ))
            .machineDefinition(Option.variant("1", pattern -> pattern
                    .layer( row(a, a, a), row(a, a, a),          row(a, a, a))
                    .layer( row(a, a, a), row(controller, i, a), row(a, a, a))
                    .layer( row(a, a, a), row(a, a, a),          row(a, a, a))
            ))
            .machineDefinition(Option.where(a, block("create_expansion:silica_bricks").min(22).or(ability(needed)).overlay("create_expansion:block/silica_bricks")))
            .machineDefinition(Option.where(i, air()))
            .build();
}
