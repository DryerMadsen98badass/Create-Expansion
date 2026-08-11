package net.mads.createexpansion.machine.machines.electric.multiblock.machines;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition.Option;

import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.*;

public final class heater {
    public static final int CE_PER_TEMPERATURE_PER_COIL_AREA = 500;

    private static final char a = 'a';
    private static final char d = 'd';
    private static final char c = 'c';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.machine()
            .machineDefinition(MultiblockControllerDefinition.Option.id("large_heater"))
            .machineDefinition(MultiblockControllerDefinition.Option.displayName("Large Heater"))
            .machineDefinition(MultiblockControllerDefinition.Option.frontTexture("block/casings/universal_textures/casing").color(MachineTier.ULV.color()))
            .machineDefinition(MultiblockControllerDefinition.Option.backTexture("block/casings/universal_textures/casing").color(MachineTier.ULV.color()))
            .machineDefinition(MultiblockControllerDefinition.Option.leftTexture("block/casings/universal_textures/casing").color(MachineTier.ULV.color()))
            .machineDefinition(MultiblockControllerDefinition.Option.rightTexture("block/casings/universal_textures/casing").color(MachineTier.ULV.color()))
            .machineDefinition(MultiblockControllerDefinition.Option.topTexture("block/casings/universal_textures/casing").color(MachineTier.ULV.color()))
            .machineDefinition(MultiblockControllerDefinition.Option.bottomTexture("block/casings/universal_textures/casing").color(MachineTier.ULV.color()))
            .machineDefinition(MultiblockControllerDefinition.Option.frontOverlay(
                    "block/machines/overlay/foundry/foundry_off",
                    "block/machines/overlay/foundry/foundry_on"
            ))
            .build();

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.machine()
            .machineDefinition(Option.id("large_heater"))
            .machineDefinition(Option.controller(CONTROLLER))
            .machineDefinition(Option.displayName("Large Heater"))
            .machineDefinition(Option.tooltip(
                    "Electric heat source for the Foundry.",
                    "Coils decide the target heat.",
                    "Energy use: ceil(coil heat * coil count * foundry inside blocks / 500) CE/t.",
                    "Example: 1 kanthal coil, 2 inside blocks = ceil(1500 * 1 * 2 / 500) = 6 CE/t."
            ))
            .machineDefinition(Option.externalHeatSource())
            .machineDefinition(Option.inputOnly(20))
            .machineDefinition(Option.variant("4", pattern -> pattern
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a, a, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(controller, d, d, d, d, d, d, d, d), row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a, a, a))
            ))
            .machineDefinition(Option.variant("3", pattern -> pattern
                    .layer(row(d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(controller, d, d, d, d, d, d), row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a))
            ))
            .machineDefinition(Option.variant("2", pattern -> pattern
                    .layer(row(d, d, d, d, d),          row(a, a, a, a, a))
                    .layer(row(d, d, d, d, d),          row(a, c, c, c, a))
                    .layer(row(controller, d, d, d, d), row(a, c, c, c, a))
                    .layer(row(d, d, d, d, d),          row(a, c, c, c, a))
                    .layer(row(d, d, d, d, d),          row(a, a, a, a, a))
            ))
            .machineDefinition(Option.variant("1", pattern -> pattern
                    .layer(row(d, d, d),          row(a, a, a))
                    .layer(row(controller, d, d), row(a, c, a))
                    .layer(row(d, d, d),          row(a, a, a))
            ))
            .machineDefinition(Option.where(a, block("create_expansion:ulv_machine_casing")))
            .machineDefinition(Option.where(d, block("create_expansion:ulv_machine_casing").or(ability(MultiblockAbility.ENERGY_INPUT).max(8)).overlay("create_expansion:block/lv_machine_casing")))
            .machineDefinition(Option.where(c, coils()))
            .build();

    private heater() {
    }

    public static int energyPerTick(int coilHeat, int coilCount, int foundryInsideBlocks) {
        if (coilHeat <= 0 || coilCount <= 0 || foundryInsideBlocks <= 0) {
            return 0;
        }
        long numerator = (long) coilHeat * coilCount * foundryInsideBlocks;
        return (int) Math.max(1, (numerator + CE_PER_TEMPERATURE_PER_COIL_AREA - 1) / CE_PER_TEMPERATURE_PER_COIL_AREA);
    }
}
