package net.mads.createexpansion.machine.machines.kinetic.multiblock;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition.Option;
import net.mads.createexpansion.recipe.CERecipeTypes;

import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.ability;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.block;

public final class dirty_assembly_machine {
    private static final char a = 'a';
    private static final char b = 'b';
    private static final char c = 'c';
    private static final char d = 'd';
    private static final char e = 'e';
    private static final char f = 'f';
    private static final char g = 'g';
    private static final char h = 'h';
    private static final char i = 'i';
    private static final char j = 'j';
    private static final char k = 'k';
    private static final char l = 'l';
    private static final char m = 'm';
    private static final char n = 'n';
    private static final char o = 'o';
    private static final char p = 'p';
    private static final char q = 'q';
    private static final char r = 'r';
    private static final char s = 's';
    private static final char t = 't';
    private static final char u = 'u';
    private static final char v = 'v';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.machine()
            .machineDefinition(MultiblockControllerDefinition.Option.id("dirty_assembly_machine"))
            .machineDefinition(MultiblockControllerDefinition.Option.displayName("Dirty Assembly Machine"))
            .machineDefinition(MultiblockControllerDefinition.Option.model("create_expansion:block/brass_machine_casing"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontOverlay(
                    "block/machines/overlay/induction_chamber/overlay_front",
                    "block/machines/overlay/induction_chamber/overlay_front_active_1"
            ))
            .build();

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.machine()
            .machineDefinition(Option.id("dirty_kinetic_assembly_machine"))
            .machineDefinition(Option.controller(CONTROLLER))
            .machineDefinition(Option.displayName("Dirty Kinetic Assembly Machine"))
            .machineDefinition(Option.recipeType(CERecipeTypes.DIRTY_ASSEMBLER))
            .machineDefinition(Option.kineticInput())
            .machineDefinition(Option.sequencedInput())
            .machineDefinition(Option.tooltip(
                    "Assembles items in a fixed input order.",
                    "The first input bus holds the base block.",
                    "Each following input bus holds the next required assembly item.",
                    "Failed assemblies return the used assembly items to the output bus.",
                    "Supports from 1 to 16 ordered assembly inputs."
            ))
            .machineDefinition(Option.variant("1", pattern -> pattern
                    .layer(
                            row(a, a, a),
                            row(d, d, d),
                            row(a, a, a)
                    )
                    .layer(
                            row(b, a, c),
                            row(e, e, e),
                            row(f, g, a)
                    )
                    .layer(
                            row(a, a, a),
                            row(d, d, d),
                            row(controller, a, a)
                    )
            ))
            .machineDefinition(Option.variant("2", pattern -> pattern
                    .layer(
                            row(a, a, a, a),
                            row(d, d, d, d),
                            row(a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, c),
                            row(e, e, e, e),
                            row(f, g, h, a)
                    )
                    .layer(
                            row(a, a, a, a),
                            row(d, d, d, d),
                            row(controller, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("3", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a),
                            row(d, d, d, d, d),
                            row(a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, c),
                            row(e, e, e, e, e),
                            row(f, g, h, i, a)
                    )
                    .layer(
                            row(a, a, a, a, a),
                            row(d, d, d, d, d),
                            row(controller, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("4", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a),
                            row(d, d, d, d, d, d),
                            row(a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, c),
                            row(e, e, e, e, e, e),
                            row(f, g, h, i, j, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a),
                            row(d, d, d, d, d, d),
                            row(controller, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("5", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("6", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("7", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("8", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("9", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("10", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("11", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, q, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("12", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, q, r, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("13", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, q, r, s, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("14", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("15", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.variant("16", pattern -> pattern
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
                    .layer(
                            row(b, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, c),
                            row(e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e),
                            row(f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, a)
                    )
                    .layer(
                            row(a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a),
                            row(d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d),
                            row(controller, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a)
                    )
            ))
            .machineDefinition(Option.where(a, block("create_expansion:brass_machine_casing")))
            .machineDefinition(Option.where(b, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing")))
            .machineDefinition(Option.where(c, ability(MultiblockAbility.ITEM_OUTPUT).overlay("create_expansion:block/brass_machine_casing")))
            .machineDefinition(Option.where(d, block("create:framed_glass")))
            .machineDefinition(Option.where(e, block("create_expansion:brass_gearbox_casing")))
            .machineDefinition(Option.where(f, ability(MultiblockAbility.KINETIC_INPUT).Tier(MachineTier.MV).overlay("create_expansion:block/brass_machine_casing")))
            .machineDefinition(Option.where(g, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(1)))
            .machineDefinition(Option.where(h, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(2)))
            .machineDefinition(Option.where(i, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(3)))
            .machineDefinition(Option.where(j, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(4)))
            .machineDefinition(Option.where(k, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(5)))
            .machineDefinition(Option.where(l, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(6)))
            .machineDefinition(Option.where(m, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(7)))
            .machineDefinition(Option.where(n, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(8)))
            .machineDefinition(Option.where(o, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(9)))
            .machineDefinition(Option.where(p, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(10)))
            .machineDefinition(Option.where(q, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(11)))
            .machineDefinition(Option.where(r, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(12)))
            .machineDefinition(Option.where(s, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(13)))
            .machineDefinition(Option.where(t, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(14)))
            .machineDefinition(Option.where(u, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(15)))
            .machineDefinition(Option.where(v, ability(MultiblockAbility.ITEM_INPUT).overlay("create_expansion:block/brass_machine_casing").sequentialInput(16)))
            .build();

    private dirty_assembly_machine() {
    }
}