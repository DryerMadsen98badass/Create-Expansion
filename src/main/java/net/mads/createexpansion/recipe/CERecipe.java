package net.mads.createexpansion.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CERecipe(
        ResourceLocation recipeType,
        List<SizedIngredient> itemInputs,
        List<SizedFluidIngredient> fluidInputs,
        List<SizedIngredient> notConsumableItems,
        List<SizedFluidIngredient> notConsumableFluids,
        List<CEChancedItemOutput> itemOutputs,
        List<FluidStack> fluidOutputs,
        int duration,
        int cet,
        Optional<Integer> circuit,
        Optional<String> tier,
        Optional<String> kineticTier,
        Optional<Integer> minRpm,
        Optional<Integer> maxRpm,
        Optional<Integer> requiredTemp,
        List<ResourceLocation> requiredLogic,
        List<ResourceLocation> optionalLogic
) implements Recipe<CERecipeInput> {
    public static final int DEFAULT_MAX_RPM = 256;

    public static final MapCodec<CERecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("recipe_type").forGetter(CERecipe::recipeType),
            SizedIngredient.FLAT_CODEC.listOf().optionalFieldOf("item_inputs", List.of()).forGetter(CERecipe::itemInputs),
            SizedFluidIngredient.FLAT_CODEC.listOf().optionalFieldOf("fluid_inputs", List.of()).forGetter(CERecipe::fluidInputs),
            SizedIngredient.FLAT_CODEC.listOf().optionalFieldOf("not_consumable_items", List.of()).forGetter(CERecipe::notConsumableItems),
            SizedFluidIngredient.FLAT_CODEC.listOf().optionalFieldOf("not_consumable_fluids", List.of()).forGetter(CERecipe::notConsumableFluids),
            CEChancedItemOutput.CODEC.listOf().optionalFieldOf("item_outputs", List.of()).forGetter(CERecipe::itemOutputs),
            FluidStack.CODEC.listOf().optionalFieldOf("fluid_outputs", List.of()).forGetter(CERecipe::fluidOutputs),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(CERecipe::duration),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cet").forGetter(recipe -> Math.abs(recipe.cet())),
            CodecHelpers.ENERGY_DIRECTION.optionalFieldOf("energy_direction", EnergyDirection.CONSUME).forGetter(CERecipe::energyDirection),
            ExtraCodecs.intRange(1, 32).optionalFieldOf("circuit").forGetter(CERecipe::circuit),
            ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("tier").forGetter(CERecipe::tier),
            ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("kinetic").forGetter(CERecipe::kineticTier),
            RuntimeFields.CODEC.forGetter(recipe -> new RuntimeFields(recipe.minRpm(), recipe.maxRpm(), recipe.requiredTemp())),
            LogicFields.CODEC.forGetter(recipe -> new LogicFields(recipe.requiredLogic(), recipe.optionalLogic()))
    ).apply(instance, CERecipe::fromCodec));

    public CERecipe {
        itemInputs = List.copyOf(itemInputs);
        fluidInputs = List.copyOf(fluidInputs);
        notConsumableItems = List.copyOf(notConsumableItems);
        notConsumableFluids = List.copyOf(notConsumableFluids);
        itemOutputs = List.copyOf(itemOutputs);
        fluidOutputs = List.copyOf(fluidOutputs);
        requiredLogic = List.copyOf(requiredLogic);
        optionalLogic = List.copyOf(optionalLogic);
    }

    private static CERecipe fromCodec(
            ResourceLocation recipeType,
            List<SizedIngredient> itemInputs,
            List<SizedFluidIngredient> fluidInputs,
            List<SizedIngredient> notConsumableItems,
            List<SizedFluidIngredient> notConsumableFluids,
            List<CEChancedItemOutput> itemOutputs,
            List<FluidStack> fluidOutputs,
            int duration,
            int cet,
            EnergyDirection energyDirection,
            Optional<Integer> circuit,
            Optional<String> tier,
            Optional<String> kineticTier,
            RuntimeFields runtimeFields,
            LogicFields logicFields
    ) {
        int signedCet = energyDirection == EnergyDirection.GENERATE ? -cet : cet;
        return new CERecipe(recipeType, itemInputs, fluidInputs, notConsumableItems, notConsumableFluids, itemOutputs, fluidOutputs, duration, signedCet, circuit, tier, kineticTier, runtimeFields.minRpm(), runtimeFields.maxRpm(), runtimeFields.requiredTemp(), logicFields.requiredLogic(), logicFields.optionalLogic());
    }

    private record RuntimeFields(Optional<Integer> minRpm, Optional<Integer> maxRpm, Optional<Integer> requiredTemp) {
        private static final MapCodec<RuntimeFields> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_rpm").forGetter(RuntimeFields::minRpm),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_rpm").forGetter(RuntimeFields::maxRpm),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("temp").forGetter(RuntimeFields::requiredTemp)
        ).apply(instance, RuntimeFields::new));
    }

    private record LogicFields(List<ResourceLocation> requiredLogic, List<ResourceLocation> optionalLogic) {
        private static final MapCodec<LogicFields> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.listOf().optionalFieldOf("required_logic", List.of()).forGetter(LogicFields::requiredLogic),
                ResourceLocation.CODEC.listOf().optionalFieldOf("optional_logic", List.of()).forGetter(LogicFields::optionalLogic)
        ).apply(instance, LogicFields::new));
    }

    public boolean generatesEnergy() {
        return cet < 0;
    }

    public int absoluteCEt() {
        return Math.abs(cet);
    }

    public EnergyDirection energyDirection() {
        return generatesEnergy() ? EnergyDirection.GENERATE : EnergyDirection.CONSUME;
    }

    public Optional<MachineTier> requiredTier() {
        return tier.flatMap(id -> MachineTier.ALL.stream().filter(machineTier -> machineTier.id().equals(id)).findFirst());
    }

    public Optional<MachineTier> requiredKineticTier() {
        return kineticTier.flatMap(id -> MachineTier.ALL.stream().filter(machineTier -> machineTier.id().equals(id)).findFirst());
    }

    public Optional<MachineTier> requiredEnergyTier() {
        return cet == 0 ? Optional.empty() : Optional.of(MachineTierStats.tierForCEt(absoluteCEt()));
    }

    public Optional<MachineTier> minimumRuntimeTier() {
        Optional<MachineTier> result = requiredTier();
        result = maxOptionalTier(result, requiredKineticTier());
        result = maxOptionalTier(result, requiredEnergyTier());
        return result;
    }

    public Optional<Integer> effectiveMaxRpm() {
        return maxRpm;
    }

    public int baseRpm() {
        if (minRpm.isPresent()) {
            return Math.max(1, minRpm.get());
        }
        if (maxRpm.isPresent() && maxRpm.get() < 64) {
            return Math.max(1, maxRpm.get());
        }
        return 64;
    }

    public int runtimeDuration(MachineTier runtimeTier, int rpm) {
        int adjustedDuration = duration;
        Optional<MachineTier> required = overclockRequirement();
        if (required.isPresent() && MachineTierStats.isAtLeast(runtimeTier, required.get())) {
            int factor = MachineTierStats.tierOverclockFactor(required.get(), runtimeTier);
            adjustedDuration = Math.max(1, (adjustedDuration + factor - 1) / factor);
        }
        return rpmAdjustedDuration(adjustedDuration, rpm);
    }

    public int runtimeCEt(MachineTier runtimeTier) {
        if (cet == 0) {
            return 0;
        }
        Optional<MachineTier> required = requiredEnergyTier();
        if (required.isEmpty() || !MachineTierStats.isAtLeast(runtimeTier, required.get())) {
            return absoluteCEt();
        }
        int multiplier = MachineTierStats.ceOverclockMultiplier(required.get(), runtimeTier);
        long value = (long) absoluteCEt() * multiplier;
        return (int) Math.min(Integer.MAX_VALUE, value);
    }

    private int rpmAdjustedDuration(int baseDuration, int rpm) {
        if (!usesRpm()) {
            return baseDuration;
        }

        int effectiveRpm = Math.max(1, rpm);
        double rpmFactor = Math.sqrt(effectiveRpm / (double) baseRpm());
        return Math.max(1, (int) Math.ceil(baseDuration / rpmFactor));
    }

    private boolean usesRpm() {
        return requiredKineticTier().isPresent() || minRpm.isPresent() || maxRpm.isPresent();
    }

    private Optional<MachineTier> overclockRequirement() {
        Optional<MachineTier> result = requiredKineticTier();
        if (result.isEmpty()) {
            result = requiredEnergyTier();
        }
        if (result.isEmpty()) {
            result = requiredTier();
        }
        return result;
    }

    private static Optional<MachineTier> maxOptionalTier(Optional<MachineTier> first, Optional<MachineTier> second) {
        if (first.isEmpty()) {
            return second;
        }
        if (second.isEmpty()) {
            return first;
        }
        return Optional.of(MachineTierStats.max(first.get(), second.get()));
    }

    @Override
    public boolean matches(CERecipeInput input, Level level) {
        if (circuit.isPresent() && !circuit.equals(input.circuit())) {
            return false;
        }
        if (minRpm.isPresent() && input.rpm() < minRpm.get()) {
            return false;
        }
        if (maxRpm.isPresent() && input.rpm() > maxRpm.get()) {
            return false;
        }
        if (usesRpm() && input.rpm() < 1) {
            return false;
        }
        if (requiredTemp.isPresent() && input.coilHeat() < requiredTemp.get()) {
            return false;
        }
        if (!input.availableLogic().containsAll(requiredLogic)) {
            return false;
        }

        Optional<MachineTier> requiredTier = requiredTier();
        if (requiredTier.isPresent() && (input.machineTier().isEmpty() || !MachineTierStats.isAtLeast(input.machineTier().get(), requiredTier.get()))) {
            return false;
        }

        Optional<MachineTier> requiredKineticTier = requiredKineticTier();
        if (requiredKineticTier.isPresent() && (input.kineticTier().isEmpty() || !MachineTierStats.isAtLeast(input.kineticTier().get(), requiredKineticTier.get()))) {
            return false;
        }

        Optional<MachineTier> requiredEnergyTier = requiredEnergyTier();
        if (requiredEnergyTier.isPresent() && !generatesEnergy()
                && (input.energyTier().isEmpty() || !MachineTierStats.isAtLeast(input.energyTier().get(), requiredEnergyTier.get()))) {
            return false;
        }

        return matchesItems(input.items(), requirements(itemInputs, notConsumableItems))
                && matchesFluids(input.fluids(), fluidRequirements(fluidInputs, notConsumableFluids));
    }

    @Override
    public ItemStack assemble(CERecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return itemOutputs.stream()
                .filter(CEChancedItemOutput::guaranteed)
                .findFirst()
                .or(() -> itemOutputs.stream().findFirst())
                .map(CEChancedItemOutput::stack)
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        itemInputs.forEach(input -> ingredients.add(input.ingredient()));
        notConsumableItems.forEach(input -> ingredients.add(input.ingredient()));
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.FURNACE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.MACHINE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.MACHINE_RECIPE_TYPE.get();
    }

    private static List<SizedIngredient> requirements(List<SizedIngredient> inputs, List<SizedIngredient> notConsumables) {
        List<SizedIngredient> requirements = new ArrayList<>(inputs.size() + notConsumables.size());
        requirements.addAll(inputs);
        requirements.addAll(notConsumables);
        return requirements;
    }

    private static List<SizedFluidIngredient> fluidRequirements(List<SizedFluidIngredient> inputs, List<SizedFluidIngredient> notConsumables) {
        List<SizedFluidIngredient> requirements = new ArrayList<>(inputs.size() + notConsumables.size());
        requirements.addAll(inputs);
        requirements.addAll(notConsumables);
        return requirements;
    }

    private static boolean matchesItems(List<ItemStack> availableStacks, List<SizedIngredient> requirements) {
        List<ItemStack> remaining = availableStacks.stream().map(ItemStack::copy).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        for (SizedIngredient requirement : requirements) {
            int required = requirement.count();
            for (ItemStack stack : remaining) {
                if (!stack.isEmpty() && requirement.ingredient().test(stack)) {
                    int taken = Math.min(required, stack.getCount());
                    stack.shrink(taken);
                    required -= taken;
                    if (required <= 0) {
                        break;
                    }
                }
            }
            if (required > 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchesFluids(List<FluidStack> availableStacks, List<SizedFluidIngredient> requirements) {
        List<FluidStack> remaining = availableStacks.stream().map(FluidStack::copy).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        for (SizedFluidIngredient requirement : requirements) {
            int required = requirement.amount();
            for (FluidStack stack : remaining) {
                if (!stack.isEmpty() && requirement.ingredient().test(stack)) {
                    int taken = Math.min(required, stack.getAmount());
                    stack.shrink(taken);
                    required -= taken;
                    if (required <= 0) {
                        break;
                    }
                }
            }
            if (required > 0) {
                return false;
            }
        }
        return true;
    }

    public enum EnergyDirection {
        CONSUME,
        GENERATE
    }
}
