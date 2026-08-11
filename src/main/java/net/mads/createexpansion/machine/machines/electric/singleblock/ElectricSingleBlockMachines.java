package net.mads.createexpansion.machine.machines.electric.singleblock;

import net.mads.createexpansion.machine.SingleBlockDefinition;

import java.util.List;

public final class ElectricSingleBlockMachines {
    /*
    public static final SingleBlockDefinition TEST_ELECTRIC_MACHINE =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("test_electric_machine"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Test Electric Machine"))
                    .machineDefinition(SingleBlockDefinition.Option.consumesEnergy())
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.LV))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.TEST_ELECTRIC_MACHINE))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 0, 0))
                    .machineDefinition(SingleBlockDefinition.Option.energyUsage(8))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.ARROW))
                    .machineDefinition(SingleBlockDefinition.Option.overlay(
                            "block/machines/ino/energy_output_hatch"
                    ))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
                    .build();
    */

    public static final List<SingleBlockDefinition> ALL = List.of(
    );

    private ElectricSingleBlockMachines() {
    }
}
