package net.mads.createexpansion.recipe;

import net.mads.createexpansion.recipe.recipetypes.TestFoundryRecipeType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CERecipeTypes {
    public static final CERecipeTypeDefinition TEST_FOUNDRY = TestFoundryRecipeType.TEST_FOUNDRY;

    public static final List<CERecipeTypeDefinition> ALL = List.of(
            TEST_FOUNDRY
    );

    private static final Map<ResourceLocation, CERecipeTypeDefinition> BY_ID = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(CERecipeTypeDefinition::id, Function.identity()));

    private CERecipeTypes() {
    }

    public static CERecipeTypeDefinition.Builder type(String id) {
        return CERecipeTypeDefinition.builder(id);
    }

    public static CERecipeTypeDefinition byId(ResourceLocation id) {
        return BY_ID.get(id);
    }
}
