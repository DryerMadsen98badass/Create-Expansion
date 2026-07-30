package net.mads.createexpansion.recipe.recipetypes;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyRecipe;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyRecipeInput;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public final class AssemblyRecipeType implements IRecipeTypeInfo {
    public static final AssemblyRecipeType INSTANCE = new AssemblyRecipeType();
    public static final String NAME = "assembly";
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, NAME);
    public static final int MAX_JEI_STEPS = 25;

    private AssemblyRecipeType() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) RecipeRegistry.ASSEMBLY_RECIPE_SERIALIZER.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) RecipeRegistry.ASSEMBLY_RECIPE_TYPE.get();
    }

    /**
     * Matcher bare baseblokken. Bruk findAllForStart(...) når en ny assembly
     * skal startes, slik at alle oppskrifter med samme start beholdes.
     */
    public Optional<RecipeHolder<AssemblyRecipe>> find(
            AssemblyRecipeInput input,
            Level level
    ) {
        return level.getRecipeManager().getRecipeFor(
                RecipeRegistry.ASSEMBLY_RECIPE_TYPE.get(),
                input,
                level
        );
    }

    /**
     * Returnerer alle oppskrifter som matcher både baseblokken og første
     * handling. Listen må beholdes til senere handlinger har skilt oppskriftene.
     */
    public List<RecipeHolder<AssemblyRecipe>> findAllForStart(
            AssemblyRecipeInput input,
            ItemStack held,
            Level level
    ) {
        return level.getRecipeManager()
                .getAllRecipesFor(
                        RecipeRegistry.ASSEMBLY_RECIPE_TYPE.get()
                )
                .stream()
                .filter(holder ->
                        holder.value().matchesBase(input.base())
                )
                .filter(holder ->
                        holder.value().matchesAction(0, held)
                )
                .toList();
    }

    /**
     * Beholdt for kode som bare trenger én oppskrift.
     */
    public Optional<RecipeHolder<AssemblyRecipe>> findForStart(
            AssemblyRecipeInput input,
            ItemStack held,
            Level level
    ) {
        return findAllForStart(input, held, level)
                .stream()
                .findFirst();
    }
}
