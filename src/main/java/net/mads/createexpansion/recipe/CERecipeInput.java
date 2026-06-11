package net.mads.createexpansion.recipe;

import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record CERecipeInput(
        List<ItemStack> items,
        List<FluidStack> fluids,
        Optional<Integer> circuit,
        Set<ResourceLocation> availableLogic,
        Optional<MachineTier> machineTier,
        Optional<MachineTier> kineticTier,
        Optional<MachineTier> energyTier,
        int rpm
) implements RecipeInput {
    public CERecipeInput {
        items = List.copyOf(items);
        fluids = List.copyOf(fluids);
        availableLogic = Set.copyOf(availableLogic);
    }

    public static CERecipeInput of(List<ItemStack> items, List<FluidStack> fluids) {
        return new CERecipeInput(items, fluids, Optional.empty(), Set.of(), Optional.empty(), Optional.empty(), Optional.empty(), 0);
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty) && fluids.stream().allMatch(FluidStack::isEmpty);
    }
}
