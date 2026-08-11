package net.mads.createexpansion.machine.machines.kinetic.singleblock;

import net.mads.createexpansion.block.MiningTier;
import net.mads.createexpansion.block.MiningTool;
import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.SingleBlockDefinition;
import net.mads.createexpansion.recipe.CERecipeTypes;

import java.util.List;

public final class KineticSingleBlockMachines {
    public static final SingleBlockDefinition BRASS_KINETIC_FLUID_SOLIDIFIER =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("brass_kinetic_fluid_solidifier"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Brass Kinetic Fluid Solidifier"))
                    .machineDefinition(SingleBlockDefinition.Option.kineticInput(SingleBlockDefinition.MachineSide.BACK))
                    .machineDefinition(SingleBlockDefinition.Option.startSu(4.0D))
                    .machineDefinition(SingleBlockDefinition.Option.minRpm(128))
                    .machineDefinition(SingleBlockDefinition.Option.maxRpm(256))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.FLUID_SOLIDIFIER))
                    .machineDefinition(SingleBlockDefinition.Option.slots(1, 1, 1, 0))
                    .machineDefinition(SingleBlockDefinition.Option.onlyTier(MachineTier.LV))
                    .machineDefinition(SingleBlockDefinition.Option.progressBar(ProgressBar.EXTRACT))

                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.STONE))

                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/machines/overlay/brass_fluid_solidifier/brass_gearbox"))
                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/machines/overlay/brass_fluid_solidifier/brass_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/machines/overlay/brass_fluid_solidifier/brass_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/machines/overlay/brass_fluid_solidifier/brass_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/machines/overlay/brass_fluid_solidifier/brass_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/machines/overlay/brass_fluid_solidifier/brass_casing"))

                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/brass_fluid_solidifier/fan_overlay_1", "block/machines/overlay/brass_fluid_solidifier/fan_overlay_1", "block/machines/overlay/brass_fluid_solidifier/fan_overlay_2", "block/machines/overlay/brass_fluid_solidifier/fan_overlay_3", "block/machines/overlay/brass_fluid_solidifier/fan_overlay_4"))
                    .build();

   // public static final SingleBlockDefinition TEST_KINETIC_OUTPUT_MACHINE =
   //         SingleBlockDefinition.machine()
   //                 .machineDefinition(SingleBlockDefinition.Option.id("test_kinetic_output_machine"))
   //                 .machineDefinition(SingleBlockDefinition.Option.displayName("Test Kinetic Output Machine"))
   //                 .machineDefinition(SingleBlockDefinition.Option.kineticOutput(SingleBlockDefinition.MachineSide.RIGHT))
   //                 .machineDefinition(SingleBlockDefinition.Option.startSu(8.0D))
   //                 .machineDefinition(SingleBlockDefinition.Option.outputRpm(64))
   //                 .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.LIQUID_FUEL_BOILER))
   //                 .machineDefinition(SingleBlockDefinition.Option.slots(0, 0, 1, 0))
   //                 .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.PICKAXE))
   //                 .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.IRON))
   //                 .build();

    public static final List<SingleBlockDefinition> ALL = List.of(
            BRASS_KINETIC_FLUID_SOLIDIFIER
    );

    private KineticSingleBlockMachines() {
    }
}
