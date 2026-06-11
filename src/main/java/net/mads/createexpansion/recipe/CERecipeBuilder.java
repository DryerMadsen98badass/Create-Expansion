package net.mads.createexpansion.recipe;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CERecipeBuilder {
    private final CERecipeTypeDefinition type;
    private final String id;
    private final List<SizedIngredient> itemInputs = new ArrayList<>();
    private final List<SizedFluidIngredient> fluidInputs = new ArrayList<>();
    private final List<SizedIngredient> notConsumableItems = new ArrayList<>();
    private final List<SizedFluidIngredient> notConsumableFluids = new ArrayList<>();
    private final List<CEChancedItemOutput> itemOutputs = new ArrayList<>();
    private final List<FluidStack> fluidOutputs = new ArrayList<>();
    private int duration = 100;
    private int cet;
    private Optional<Integer> circuit = Optional.empty();
    private Optional<String> tier = Optional.empty();
    private Optional<String> kineticTier = Optional.empty();
    private Optional<Integer> minRpm = Optional.empty();
    private Optional<Integer> maxRpm = Optional.empty();
    private final List<ResourceLocation> requiredLogic = new ArrayList<>();
    private final List<ResourceLocation> optionalLogic = new ArrayList<>();

    CERecipeBuilder(CERecipeTypeDefinition type, String id) {
        this.type = type;
        this.id = id;
    }

    public CERecipeBuilder inputItem(String itemId) {
        return inputItem(itemId, 1);
    }

    public CERecipeBuilder inputItem(String itemId, int count) {
        return inputItem(item(itemId), count);
    }

    public CERecipeBuilder inputItem(ItemLike item, int count) {
        itemInputs.add(SizedIngredient.of(item, count));
        return this;
    }

    public CERecipeBuilder inputTag(String tagId, int count) {
        itemInputs.add(SizedIngredient.of(itemTag(tagId), count));
        return this;
    }

    public CERecipeBuilder inputFluid(String fluidId, int amount) {
        fluidInputs.add(SizedFluidIngredient.of(fluid(fluidId), amount));
        return this;
    }

    public CERecipeBuilder notConsumableItem(String itemId) {
        return notConsumableItem(itemId, 1);
    }

    public CERecipeBuilder notConsumableItem(String itemId, int count) {
        return notConsumableItem(item(itemId), count);
    }

    public CERecipeBuilder notConsumableItem(ItemLike item, int count) {
        notConsumableItems.add(SizedIngredient.of(item, count));
        return this;
    }

    public CERecipeBuilder notConsumableFluid(String fluidId, int amount) {
        notConsumableFluids.add(SizedFluidIngredient.of(fluid(fluidId), amount));
        return this;
    }

    public CERecipeBuilder outputItem(String itemId) {
        return outputItem(itemId, 1);
    }

    public CERecipeBuilder outputItem(String itemId, int count) {
        return chancedOutput(itemId, count, CEChancedItemOutput.MAX_CHANCE);
    }

    public CERecipeBuilder outputItem(ItemLike item, int count) {
        itemOutputs.add(new CEChancedItemOutput(new ItemStack(item, count), CEChancedItemOutput.MAX_CHANCE));
        return this;
    }

    public CERecipeBuilder chancedOutput(String itemId, int chance) {
        return chancedOutput(itemId, 1, chance);
    }

    public CERecipeBuilder chancedOutput(String itemId, int count, int chance) {
        itemOutputs.add(new CEChancedItemOutput(new ItemStack(item(itemId), count), chance));
        return this;
    }

    public CERecipeBuilder outputFluid(String fluidId, int amount) {
        fluidOutputs.add(new FluidStack(fluid(fluidId), amount));
        return this;
    }

    public CERecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public CERecipeBuilder CEt(int cet) {
        this.cet = Math.abs(cet);
        return this;
    }

    public CERecipeBuilder cet(int cet) {
        return CEt(cet);
    }

    public CERecipeBuilder generateCEt(int cet) {
        this.cet = -Math.abs(cet);
        return this;
    }

    public CERecipeBuilder circuit(int circuit) {
        if (circuit < 1 || circuit > 32) {
            throw new IllegalArgumentException("Circuit must be between 1 and 32");
        }
        this.circuit = Optional.of(circuit);
        return this;
    }

    public CERecipeBuilder tier(MachineTier tier) {
        this.tier = Optional.of(tier.id());
        return this;
    }

    public CERecipeBuilder kinetic(MachineTier tier) {
        this.kineticTier = Optional.of(tier.id());
        return this;
    }

    public CERecipeBuilder minRpm(int rpm) {
        this.minRpm = Optional.of(rpm);
        return this;
    }

    public CERecipeBuilder maxRpm(int rpm) {
        this.maxRpm = Optional.of(rpm);
        return this;
    }

    public CERecipeBuilder rpmRange(int minRpm, int maxRpm) {
        return minRpm(minRpm).maxRpm(maxRpm);
    }

    public CERecipeBuilder logic(CERecipeLogicDefinition logic) {
        return requiredLogic(logic);
    }

    public CERecipeBuilder requiredLogic(CERecipeLogicDefinition logic) {
        requiredLogic.add(logic.id());
        return this;
    }

    public CERecipeBuilder optionalLogic(CERecipeLogicDefinition logic) {
        optionalLogic.add(logic.id());
        return this;
    }

    public CERecipe build() {
        validate();
        return new CERecipe(type.id(), itemInputs, fluidInputs, notConsumableItems, notConsumableFluids, itemOutputs, fluidOutputs, duration, cet, circuit, tier, kineticTier, minRpm, maxRpm, requiredLogic, optionalLogic);
    }

    public void save(RecipeOutput output) {
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, type.id().getPath() + "/" + id), build(), null);
    }

    private void validate() {
        if (duration <= 0) {
            throw new IllegalStateException("Recipe " + id + " must have a positive duration");
        }
        if (itemInputs.size() > type.maxItemInputs()) {
            throw new IllegalStateException("Recipe " + id + " has too many item inputs for " + type.id());
        }
        if (itemOutputs.size() > type.maxItemOutputs()) {
            throw new IllegalStateException("Recipe " + id + " has too many item outputs for " + type.id());
        }
        if (fluidInputs.size() > type.maxFluidInputs()) {
            throw new IllegalStateException("Recipe " + id + " has too many fluid inputs for " + type.id());
        }
        if (fluidOutputs.size() > type.maxFluidOutputs()) {
            throw new IllegalStateException("Recipe " + id + " has too many fluid outputs for " + type.id());
        }
        if (!type.usesRpm() && (minRpm.isPresent() || maxRpm.isPresent())) {
            throw new IllegalStateException("Recipe " + id + " uses RPM but " + type.id() + " does not allow RPM");
        }
        if (!type.usesRpm() && kineticTier.isPresent()) {
            throw new IllegalStateException("Recipe " + id + " uses kinetic tier but " + type.id() + " does not allow RPM");
        }
        validateEnergyMode();
        if (maxRpm.isPresent() && minRpm.isPresent() && maxRpm.get() < minRpm.get()) {
            throw new IllegalStateException("Recipe " + id + " has max RPM lower than min RPM");
        }
        validateLogic(requiredLogic);
        validateLogic(optionalLogic);
    }

    private void validateLogic(List<ResourceLocation> logicIds) {
        for (ResourceLocation logicId : logicIds) {
            if (!type.supportsLogic(logicId)) {
                throw new IllegalStateException("Recipe " + id + " uses logic " + logicId + " but " + type.id() + " does not support it");
            }
        }
    }

    private void validateEnergyMode() {
        if (cet == 0) {
            return;
        }

        CERecipeTypeDefinition.EnergyMode energyMode = type.energyMode();
        if (energyMode == CERecipeTypeDefinition.EnergyMode.NONE) {
            throw new IllegalStateException("Recipe " + id + " uses CE but " + type.id() + " does not allow energy");
        }
        if (cet > 0 && energyMode == CERecipeTypeDefinition.EnergyMode.GENERATES) {
            throw new IllegalStateException("Recipe " + id + " consumes CE but " + type.id() + " only generates energy");
        }
        if (cet < 0 && energyMode == CERecipeTypeDefinition.EnergyMode.CONSUMES) {
            throw new IllegalStateException("Recipe " + id + " generates CE but " + type.id() + " only consumes energy");
        }
    }

    private static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    private static Item item(String itemId) {
        return BuiltInRegistries.ITEM.get(id(itemId));
    }

    private static Fluid fluid(String fluidId) {
        return BuiltInRegistries.FLUID.get(id(fluidId));
    }

    private static TagKey<Item> itemTag(String tagId) {
        return ItemTags.create(id(tagId));
    }
}
