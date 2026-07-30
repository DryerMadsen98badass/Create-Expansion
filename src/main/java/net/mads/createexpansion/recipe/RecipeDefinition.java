package net.mads.createexpansion.recipe;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.machine.interaction.BlockInteraction;
import net.mads.createexpansion.machine.interaction.MachineCondition;
import net.mads.createexpansion.machine.interaction.MachineModifier;
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
import java.util.Objects;
import java.util.Optional;

/**
 * Typesafe public builder for recipes processed by the CE machine runtime.
 *
 * <p>Create recipes intentionally use their existing Create builders instead.</p>
 */
public final class RecipeDefinition {
    private RecipeTypeDefinition type;
    private String id;
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
    private Optional<Integer> requiredTemp = Optional.empty();
    private final List<ResourceLocation> requiredLogic = new ArrayList<>();
    private final List<ResourceLocation> optionalLogic = new ArrayList<>();
    private final List<BlockInteraction> blockInteractions = new ArrayList<>();
    private final List<MachineCondition> conditions = new ArrayList<>();
    private final List<MachineModifier> modifiers = new ArrayList<>();

    private RecipeDefinition() {
    }

    public static RecipeDefinition recipe() {
        return new RecipeDefinition();
    }

    public RecipeDefinition recipeDefinition(Option option) {
        Objects.requireNonNull(option, "Recipe option").apply(this);
        return this;
    }

    /**
     * Options shown by autocomplete inside {@code .recipeDefinition(Option...)}.
     */
    @FunctionalInterface
    public interface Option {
        void apply(RecipeDefinition definition);

        /** Defines the path used below the recipe type's generated data folder. */
        static Option id(String id) {
            return definition -> definition.id = id;
        }

        /** Selects the CE recipe type that owns and validates this recipe. */
        static Option recipeType(RecipeTypeDefinition type) {
            return definition -> definition.type = Objects.requireNonNull(type);
        }

        /** Adds a consumed item input. */
        static Option inputItem(String itemId, int count) {
            return definition -> definition.itemInputs.add(SizedIngredient.of(item(itemId), count));
        }

        /** Adds a consumed item input. */
        static Option inputItem(ItemLike item, int count) {
            return definition -> definition.itemInputs.add(SizedIngredient.of(item, count));
        }

        /** Adds a consumed item-tag input. */
        static Option inputTag(String tagId, int count) {
            return definition -> definition.itemInputs.add(SizedIngredient.of(itemTag(tagId), count));
        }

        /** Adds a consumed fluid input measured in millibuckets. */
        static Option inputFluid(String fluidId, int amount) {
            return definition -> definition.fluidInputs.add(SizedFluidIngredient.of(fluid(fluidId), amount));
        }

        /** Adds an item requirement that must remain present and is not consumed. */
        static Option notConsumableItem(String itemId, int count) {
            return definition -> definition.notConsumableItems.add(SizedIngredient.of(item(itemId), count));
        }

        /** Adds an item requirement that must remain present and is not consumed. */
        static Option notConsumableItem(ItemLike item, int count) {
            return definition -> definition.notConsumableItems.add(SizedIngredient.of(item, count));
        }

        /** Adds a fluid requirement that must remain present and is not consumed. */
        static Option notConsumableFluid(String fluidId, int amount) {
            return definition -> definition.notConsumableFluids.add(
                    SizedFluidIngredient.of(fluid(fluidId), amount)
            );
        }

        /** Adds an item output with a guaranteed result. */
        static Option outputItem(String itemId, int count) {
            return definition -> definition.itemOutputs.add(new CEChancedItemOutput(
                    new ItemStack(item(itemId), count),
                    CEChancedItemOutput.MAX_CHANCE
            ));
        }

        /** Adds an item output with a guaranteed result. */
        static Option outputItem(ItemLike item, int count) {
            return definition -> definition.itemOutputs.add(new CEChancedItemOutput(
                    new ItemStack(item, count),
                    CEChancedItemOutput.MAX_CHANCE
            ));
        }

        /**
         * Adds a chanced item output. Chance uses the CE scale where
         * {@link CEChancedItemOutput#MAX_CHANCE} is guaranteed.
         */
        static Option chancedOutput(String itemId, int count, int chance) {
            return definition -> definition.itemOutputs.add(
                    new CEChancedItemOutput(new ItemStack(item(itemId), count), chance)
            );
        }

        /** Adds a fluid output measured in millibuckets. */
        static Option outputFluid(String fluidId, int amount) {
            return definition -> definition.fluidOutputs.add(new FluidStack(fluid(fluidId), amount));
        }

        /** Defines the base processing duration in ticks. */
        static Option duration(int duration) {
            return definition -> definition.duration = duration;
        }

        /** Defines CE consumed per tick before tier runtime scaling. */
        static Option CEt(int cet) {
            return definition -> definition.cet = Math.abs(cet);
        }

        /** Defines CE consumed per tick from the selected voltage tier. */
        static Option CEt(MachineTier tier) {
            return CEt((int) Math.min(Integer.MAX_VALUE, Math.max(1L, MachineTierStats.ceTier(tier) / 2L)));
        }

        /** Defines CE generated per tick before tier runtime scaling. */
        static Option generateCEt(int cet) {
            return definition -> definition.cet = -Math.abs(cet);
        }

        /** Defines CE generated per tick from the selected voltage tier. */
        static Option generateCEt(MachineTier tier) {
            return generateCEt((int) Math.min(Integer.MAX_VALUE, Math.max(1L, MachineTierStats.ceTier(tier) / 2L)));
        }

        /** Requires an integrated circuit configuration from 1 through 32. */
        static Option circuit(int circuit) {
            if (circuit < 1 || circuit > 32) {
                throw new IllegalArgumentException("Circuit must be between 1 and 32");
            }
            return definition -> definition.circuit = Optional.of(circuit);
        }

        /** Defines the minimum machine tier allowed to run the recipe. */
        static Option tier(MachineTier tier) {
            return definition -> definition.tier = Optional.of(Objects.requireNonNull(tier).id());
        }

        /** Defines the minimum kinetic tier allowed to run the recipe. */
        static Option kinetic(MachineTier tier) {
            return definition -> definition.kineticTier = Optional.of(Objects.requireNonNull(tier).id());
        }

        /** Defines the minimum accepted rotational speed. */
        static Option minRpm(int rpm) {
            return definition -> definition.minRpm = Optional.of(rpm);
        }

        /** Defines the maximum accepted rotational speed. */
        static Option maxRpm(int rpm) {
            return definition -> definition.maxRpm = Optional.of(rpm);
        }

        /** Defines the inclusive rotational speed range accepted by the recipe. */
        static Option rpmRange(int minRpm, int maxRpm) {
            return definition -> {
                definition.minRpm = Optional.of(minRpm);
                definition.maxRpm = Optional.of(maxRpm);
            };
        }

        /** Defines the minimum machine or coil temperature required by the recipe. */
        static Option temperature(int requiredTemperature) {
            if (requiredTemperature <= 0) {
                throw new IllegalArgumentException("Temperature requirement must be positive");
            }
            return definition -> definition.requiredTemp = Optional.of(requiredTemperature);
        }

        /** Adds a custom logic requirement that must be available. */
        static Option requiredLogic(CERecipeLogicDefinition logic) {
            return definition -> definition.requiredLogic.add(Objects.requireNonNull(logic).id());
        }

        /** Adds custom logic that can be used when available but is not mandatory. */
        static Option optionalLogic(CERecipeLogicDefinition logic) {
            return definition -> definition.optionalLogic.add(Objects.requireNonNull(logic).id());
        }

        /** Adds a world block/fluid interaction required only by this recipe. */
        static Option blockInteraction(BlockInteraction interaction) {
            return definition -> definition.blockInteractions.add(Objects.requireNonNull(interaction));
        }

        /** Adds a world block/fluid interaction required only by this recipe. */
        static Option blockInteraction(BlockInteraction.Builder interaction) {
            return blockInteraction(Objects.requireNonNull(interaction).build());
        }

        /** Adds a world condition required only by this recipe. */
        static Option condition(MachineCondition condition) {
            return definition -> definition.conditions.add(Objects.requireNonNull(condition));
        }

        /** Adds an ordered modifier available only to this recipe. */
        static Option modifier(MachineModifier modifier) {
            return definition -> definition.modifiers.add(Objects.requireNonNull(modifier));
        }

        /** Adds an ordered modifier available only to this recipe. */
        static Option modifier(MachineModifier.Builder modifier) {
            return modifier(Objects.requireNonNull(modifier).build());
        }
    }

    public CERecipe build() {
        validate();
        return new CERecipe(
                type.id(),
                itemInputs,
                fluidInputs,
                notConsumableItems,
                notConsumableFluids,
                itemOutputs,
                fluidOutputs,
                duration,
                cet,
                circuit,
                tier,
                kineticTier,
                minRpm,
                maxRpm,
                requiredTemp,
                requiredLogic,
                optionalLogic,
                blockInteractions,
                conditions,
                modifiers
        );
    }

    public void save(RecipeOutput output) {
        validateIdentity();
        output.accept(
                ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, type.id().getPath() + "/" + id),
                build(),
                null
        );
    }

    private void validate() {
        validateIdentity();
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
        if (!type.acceptsRpm() && (minRpm.isPresent() || maxRpm.isPresent() || kineticTier.isPresent())) {
            throw new IllegalStateException("Recipe " + id + " uses RPM but " + type.id() + " does not allow RPM");
        }
        validateEnergyMode();
        if (maxRpm.isPresent() && minRpm.isPresent() && maxRpm.get() < minRpm.get()) {
            throw new IllegalStateException("Recipe " + id + " has max RPM lower than min RPM");
        }
        validateLogic(requiredLogic);
        validateLogic(optionalLogic);
    }

    private void validateIdentity() {
        if (type == null) {
            throw new IllegalStateException("Recipe is missing Option.recipeType(...)");
        }
        if (id == null || id.isBlank() || !ResourceLocation.isValidPath(id)) {
            throw new IllegalStateException("Recipe has an invalid or missing Option.id(...): " + id);
        }
    }

    private void validateLogic(List<ResourceLocation> logicIds) {
        for (ResourceLocation logicId : logicIds) {
            if (!type.supportsLogic(logicId)) {
                throw new IllegalStateException(
                        "Recipe " + id + " uses logic " + logicId + " but " + type.id() + " does not support it"
                );
            }
        }
    }

    private void validateEnergyMode() {
        if (cet == 0) {
            return;
        }
        RecipeTypeDefinition.EnergyMode energyMode = type.energyMode();
        if (energyMode == RecipeTypeDefinition.EnergyMode.NONE) {
            throw new IllegalStateException("Recipe " + id + " uses CE but " + type.id() + " does not allow energy");
        }
        if (cet > 0 && energyMode == RecipeTypeDefinition.EnergyMode.GENERATES) {
            throw new IllegalStateException("Recipe " + id + " consumes CE but " + type.id() + " only generates energy");
        }
        if (cet < 0 && energyMode == RecipeTypeDefinition.EnergyMode.CONSUMES) {
            throw new IllegalStateException("Recipe " + id + " generates CE but " + type.id() + " only consumes energy");
        }
    }

    private static ResourceLocation resourceId(String id) {
        return id.contains(":")
                ? ResourceLocation.parse(id)
                : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    private static Item item(String itemId) {
        return BuiltInRegistries.ITEM.get(resourceId(itemId));
    }

    private static Fluid fluid(String fluidId) {
        return BuiltInRegistries.FLUID.get(resourceId(fluidId));
    }

    private static TagKey<Item> itemTag(String tagId) {
        return ItemTags.create(resourceId(tagId));
    }
}
