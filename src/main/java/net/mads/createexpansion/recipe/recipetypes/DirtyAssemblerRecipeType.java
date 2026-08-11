package net.mads.createexpansion.recipe.recipetypes;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.mads.createexpansion.recipe.RecipeTypeDefinition.Option;

public final class DirtyAssemblerRecipeType {
    public static final int SUCCESS_CHANCE = 2_000;
    public static final int TICKS_PER_ITEM = 100;
    public static final int MAX_SEQUENTIAL_INPUTS = 16;
    public static final int BASE_INPUTS = 1;
    public static final int MAX_ITEM_INPUTS = BASE_INPUTS + MAX_SEQUENTIAL_INPUTS;
    public static final int MAX_ITEM_OUTPUTS = 1;
    public static final int MAX_FLUID_INPUTS = 0;
    public static final int MAX_FLUID_OUTPUTS = 0;
    public static final MachineTier BASE_TIER = MachineTier.LV;

    public static final RecipeTypeDefinition DIRTY_ASSEMBLER = RecipeTypeDefinition.recipeType()
            .recipeTypeDefinition(Option.id("dirty_assembler"))
            .recipeTypeDefinition(Option.displayName("Dirty Assembler"))
            .recipeTypeDefinition(Option.maxIO(
                    MAX_ITEM_INPUTS,
                    MAX_ITEM_OUTPUTS,
                    MAX_FLUID_INPUTS,
                    MAX_FLUID_OUTPUTS
            ))
            .recipeTypeDefinition(Option.baseBlockInput())
            .recipeTypeDefinition(Option.progressBar(ProgressBar.ARROW_BRONZE))
            .build();

    private DirtyAssemblerRecipeType() {
    }
}
