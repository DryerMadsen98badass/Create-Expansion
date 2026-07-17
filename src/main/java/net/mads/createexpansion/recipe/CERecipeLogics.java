package net.mads.createexpansion.recipe;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class CERecipeLogics {
    public static final CERecipeLogicDefinition LUBRICATED = logic("lubricated", "Lubricated");
    public static final CERecipeLogicDefinition COIL_TEMP = logic("coil_temp", "Coil Temperature");

    public static final List<CERecipeLogicDefinition> ALL = List.of(
            LUBRICATED,
            COIL_TEMP
    );

    private CERecipeLogics() {
    }

    public static CERecipeLogicDefinition logic(String id, String displayName) {
        return new CERecipeLogicDefinition(id(id), displayName);
    }

    public static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }
}
