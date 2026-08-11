package net.mads.createexpansion.machine.machines.without_energy.multiblock;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition.Option;
import net.mads.createexpansion.recipe.CERecipeTypes;

import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.*;

public final class blast_furnace {

    private static final char a = 'a';
    private static final char b = 'b';
    private static final char i = 'i';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.machine()
            .machineDefinition(MultiblockControllerDefinition.Option.id("blast_furnace"))
            .machineDefinition(MultiblockControllerDefinition.Option.displayName("Blast Furnace"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontTexture("create_expansion:block/casings/casing/firebricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.backTexture("create_expansion:block/casings/casing/firebricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.leftTexture("create_expansion:block/casings/casing/firebricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.rightTexture("create_expansion:block/casings/casing/firebricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.topTexture("create_expansion:block/casings/casing/firebricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.bottomTexture("create_expansion:block/casings/casing/firebricks"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontOverlay(
                    "block/machines/overlay/blast_furnace/blast_furnace_off",
                    "block/machines/overlay/blast_furnace/blast_furnace_on"
            ))
            .build();

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.machine()
            .machineDefinition(Option.id("blast_furnace"))
            .machineDefinition(Option.controller(CONTROLLER))
            .machineDefinition(Option.displayName("Blast Furnace"))
            .machineDefinition(Option.recipeType(CERecipeTypes.BLAST_FURNACE))
            .machineDefinition(Option.tooltip("Can input items from the top."))
            .machineDefinition(Option.worldInteraction(MultiblockDefinition.WorldInteractionType.INPUT, 0, 0, 1))
            .machineDefinition(Option.worldInteraction(MultiblockDefinition.WorldInteractionType.OUTPUT, 0, 0, 1))
            .machineDefinition(Option.variant("1", pattern -> pattern
                    .layer( row(b, b, b), row(a, a, a),          row(a, a, a), row(a, a, a))
                    .layer( row(b, b, b), row(controller, i, a), row(a, i, a), row(a, i, a))
                    .layer( row(b, b, b), row(a, a, a),          row(a, a, a), row(a, a, a))
            ))
            .machineDefinition(Option.where(a, block("create_expansion:firebricks").min(19).or(ability(needed)).overlay("create_expansion:block/firebricks")))
            .machineDefinition(Option.where(b, block("create_expansion:firebrick_firebox")))
            .machineDefinition(Option.where(i, air()))
            .build();

    private blast_furnace() {
    }
}
