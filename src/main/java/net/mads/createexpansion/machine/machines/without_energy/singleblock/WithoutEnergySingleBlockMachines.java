package net.mads.createexpansion.machine.machines.without_energy.singleblock;

import net.mads.createexpansion.block.MiningTier;
import net.mads.createexpansion.block.MiningTool;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.SingleBlockDefinition;
import net.mads.createexpansion.machine.SingleBlockMachinePower;
import net.mads.createexpansion.machine.SingleBlockMachineResource;
import net.mads.createexpansion.machine.SingleBlockMachineResourceMode;
import net.mads.createexpansion.machine.interaction.BlockInteraction;
import net.mads.createexpansion.recipe.CERecipeTypes;

import java.util.List;

public final class WithoutEnergySingleBlockMachines {
    public static final SingleBlockDefinition TREE_EXTRACTOR =
            SingleBlockDefinition.machine()
                    .machineDefinition(SingleBlockDefinition.Option.id("tree_extractor"))
                    .machineDefinition(SingleBlockDefinition.Option.displayName("Tree Extractor"))
                    .machineDefinition(SingleBlockDefinition.Option.resource(
                            SingleBlockMachinePower.NONE,
                            SingleBlockMachineResource.NONE,
                            SingleBlockMachineResourceMode.NONE
                    ))
                    .machineDefinition(SingleBlockDefinition.Option.tier(MachineTier.NONE))
                    .machineDefinition(SingleBlockDefinition.Option.recipeType(CERecipeTypes.TREE_EXTRACTING))
                    .machineDefinition(SingleBlockDefinition.Option.slots(0, 1, 0, 1))
                    .machineDefinition(SingleBlockDefinition.Option.blockInteraction(BlockInteraction.treeExtract().at(0, 0, 1)))

                    .machineDefinition(SingleBlockDefinition.Option.bottomTexture("block/machines/machines/kinetic/centrifuge/andesite_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.topTexture("block/machines/machines/kinetic/centrifuge/andesite_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.leftTexture("block/machines/machines/kinetic/centrifuge/andesite_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.rightTexture("block/machines/machines/kinetic/centrifuge/andesite_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.frontTexture("block/machines/machines/kinetic/centrifuge/andesite_casing"))
                    .machineDefinition(SingleBlockDefinition.Option.backTexture("block/casings/casing/andesite_casing_with_face"))
                    .machineDefinition(SingleBlockDefinition.Option.frontOverlay("block/machines/overlay/distillery/overlay_front", "block/machines/overlay/distillery/overlay_front_active_1", "block/machines/overlay/distillery/overlay_front_active_2", "block/machines/overlay/distillery/overlay_front_active_3"))
                    .machineDefinition(SingleBlockDefinition.Option.mineableWith(MiningTool.AXE, MiningTool.PICKAXE))
                    .machineDefinition(SingleBlockDefinition.Option.miningTier(MiningTier.STONE))
                    .build();

    public static final List<SingleBlockDefinition> ALL = List.of(
            TREE_EXTRACTOR
    );

    private WithoutEnergySingleBlockMachines() {
    }
}
