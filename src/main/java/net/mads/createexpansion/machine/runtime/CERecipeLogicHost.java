package net.mads.createexpansion.machine.runtime;

import java.util.Optional;

public interface CERecipeLogicHost {
    boolean recipeMachineReady();

    Optional<CERecipeExecution> findAndConsumeRecipeInputs();

    CERecipeTickResult consumeRecipeTick(CERecipeExecution execution);

    boolean canCompleteRecipe(CERecipeExecution execution);

    void completeRecipe(CERecipeExecution execution);

    void onRecipeLogicChanged(boolean activeChanged);
}
