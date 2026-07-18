package net.mads.createexpansion.machine.machines.electric.multiblock.machines;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;

import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPredicates.*;

public final class Heater {
    public static final int CE_PER_TEMPERATURE_PER_COIL_AREA = 500;

    private static final char a = 'a';
    private static final char d = 'd';
    private static final char c = 'c';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.of(
            "large_heater",
            "Large Heater",
            "block/machines/ino/casing",
            "block/machines/overlay/foundry/foundry_off",
            "block/machines/overlay/foundry/foundry_on"
    );

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.controller(CONTROLLER)
            .displayName("Large Heater")
            .tooltip(
                    "Electric heat source for the Foundry.",
                    "Coils decide the target heat.",
                    "Energy use: ceil(coil heat * coil count * foundry inside blocks / 500) CE/t.",
                    "Example: 1 kanthal coil, 2 inside blocks = ceil(1500 * 1 * 2 / 500) = 6 CE/t."
            )
            .externalHeatSource()
            .inputOnly(20)
            .variant("4", pattern -> pattern
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a, a, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(controller, d, d, d, d, d, d, d, d), row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, c, c, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a, a, a))
            )
            .variant("3", pattern -> pattern
                    .layer(row(d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(controller, d, d, d, d, d, d), row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, c, c, c, c, c, a))
                    .layer(row(d, d, d, d, d, d, d),          row(a, a, a, a, a, a, a))
            )
            .variant("2", pattern -> pattern
                    .layer(row(d, d, d, d, d),          row(a, a, a, a, a))
                    .layer(row(d, d, d, d, d),          row(a, c, c, c, a))
                    .layer(row(controller, d, d, d, d), row(a, c, c, c, a))
                    .layer(row(d, d, d, d, d),          row(a, c, c, c, a))
                    .layer(row(d, d, d, d, d),          row(a, a, a, a, a))
            )
            .variant("1", pattern -> pattern
                    .layer(row(d, d, d),          row(a, a, a))
                    .layer(row(controller, d, d), row(a, c, a))
                    .layer(row(d, d, d),          row(a, a, a))
            )
            .where(a, block("create_expansion:ulv_machine_casing"))
            .where(d, block("create_expansion:ulv_machine_casing").or(ability(MultiblockAbility.ENERGY_INPUT).max(8)).overlay("create_expansion:block/lv_machine_casing"))
            .where(c, coils())
            .build();

    private Heater() {
    }

    public static int energyPerTick(int coilHeat, int coilCount, int foundryInsideBlocks) {
        if (coilHeat <= 0 || coilCount <= 0 || foundryInsideBlocks <= 0) {
            return 0;
        }
        long numerator = (long) coilHeat * coilCount * foundryInsideBlocks;
        return (int) Math.max(1, (numerator + CE_PER_TEMPERATURE_PER_COIL_AREA - 1) / CE_PER_TEMPERATURE_PER_COIL_AREA);
    }
}
