package net.mads.createexpansion.recipe;

import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public final class CERecipeLookup {
    private CERecipeLookup() {
    }

    public static List<RecipeHolder<CERecipe>> byType(RecipeManager manager, CERecipeTypeDefinition type) {
        return manager.getAllRecipesFor(RecipeRegistry.MACHINE_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.value().recipeType().equals(type.id()))
                .toList();
    }

    public static Optional<RecipeHolder<CERecipe>> find(Level level, CERecipeTypeDefinition type, CERecipeInput input) {
        return byType(level.getRecipeManager(), type).stream()
                .filter(recipe -> recipe.value().matches(input, level))
                .findFirst();
    }
}
