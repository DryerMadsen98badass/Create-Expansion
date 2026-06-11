package net.mads.createexpansion.multiblock.machines;

import net.mads.createexpansion.multiblock.MultiblockControllerDefinition;
import net.mads.createexpansion.multiblock.MultiblockDefinition;
import net.mads.createexpansion.recipe.CERecipeLogics;
import net.mads.createexpansion.recipe.CERecipeTypes;

import static net.mads.createexpansion.multiblock.MultiblockPattern.controller;
import static net.mads.createexpansion.multiblock.MultiblockPattern.row;
import static net.mads.createexpansion.multiblock.MultiblockPredicates.ability;
import static net.mads.createexpansion.multiblock.MultiblockPredicates.block;
import static net.mads.createexpansion.multiblock.MultiblockPredicates.needed;

public final class TestFoundry {
    private static final char g = 'g';

    public static final MultiblockControllerDefinition CONTROLLER = MultiblockControllerDefinition.of(
            "test_foundry_controller",
            "Test Foundry Controller",
            "block/machines/ino/casing",
            "block/machines/overlay/foundry/foundry_off",
            "block/machines/overlay/foundry/foundry_on"
    );

    public static final MultiblockDefinition DEFINITION = MultiblockDefinition.controller(CONTROLLER)
            .displayName("Test Foundry")
            .recipeTypes(CERecipeTypes.TEST_FOUNDRY)
            .variant("1", pattern -> pattern
                    .layer(row(g, g, g), row(g, g, g), row(g, g, g))
                    .layer(row(g, g, g), row(controller, g, g), row(g, g, g))
                    .layer(row(g, g, g), row(g, g, g), row(g, g, g))
            )
            .where(g, block("minecraft:dirt").min(10).or(ability(needed)).overlay("minecraft:block/dirt"))
            .build();

    private TestFoundry() {
    }
}
