package net.mads.createexpansion.machine;

import net.mads.createexpansion.block.MiningTier;
import net.mads.createexpansion.block.MiningTool;
import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.interaction.BlockInteraction;
import net.mads.createexpansion.machine.interaction.MachineCondition;
import net.mads.createexpansion.machine.interaction.MachineModifier;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Declarative definition of a machine that occupies one block.
 */
public final class SingleBlockDefinition {
    private final String id;
    private final String displayName;
    private final SingleBlockMachinePower power;
    private final SingleBlockMachineResource resource;
    private final SingleBlockMachineResourceMode resourceMode;
    private final MachineTier startTier;
    private final List<ResourceLocation> recipeTypes;
    private final Slots slots;
    private final int steamCapacity;
    private final int steamUsage;
    private final int cet;
    private final List<StackRequirement> inputItems;
    private final List<StackRequirement> inputFluids;
    private final List<StackRequirement> outputItems;
    private final List<StackRequirement> outputFluids;
    private final List<BlockInteraction> blockInteractions;
    private final List<MachineCondition> conditions;
    private final List<MachineModifier> modifiers;
    private final List<String> overlayFrames;
    private final MiningTier miningTier;
    private final EnumSet<MiningTool> miningTools;
    @Nullable
    private final ProgressBar progressBar;

    private SingleBlockDefinition(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.power = builder.power;
        this.resource = builder.resource;
        this.resourceMode = builder.resourceMode;
        this.startTier = builder.startTier;
        this.recipeTypes = List.copyOf(builder.recipeTypes);
        this.slots = builder.slots;
        this.steamCapacity = builder.steamCapacity;
        this.steamUsage = builder.steamUsage;
        this.cet = builder.cet;
        this.inputItems = List.copyOf(builder.inputItems);
        this.inputFluids = List.copyOf(builder.inputFluids);
        this.outputItems = List.copyOf(builder.outputItems);
        this.outputFluids = List.copyOf(builder.outputFluids);
        this.blockInteractions = List.copyOf(builder.blockInteractions);
        this.conditions = List.copyOf(builder.conditions);
        this.modifiers = List.copyOf(builder.modifiers);
        this.overlayFrames = List.copyOf(builder.overlayFrames);
        this.miningTier = builder.miningTier;
        this.miningTools = builder.miningTools.clone();
        this.progressBar = builder.progressBar;
        validate();
    }

    public static Builder machine() {
        return new Builder();
    }

    public List<SingleBlockMachineInstance> expand() {
        return MachineTier.expandSingleBlockTiers(startTier)
                .stream()
                .map(tier -> new SingleBlockMachineInstance(this, tier))
                .toList();
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public SingleBlockMachinePower power() {
        return power;
    }

    public SingleBlockMachineResource resource() {
        return resource;
    }

    public SingleBlockMachineResourceMode resourceMode() {
        return resourceMode;
    }

    public MachineTier startTier() {
        return startTier;
    }

    public List<ResourceLocation> recipeTypes() {
        return recipeTypes;
    }

    public boolean matchesRecipeType(ResourceLocation recipeType) {
        return recipeTypes.contains(recipeType);
    }

    public Slots slots() {
        return slots;
    }

    public int steamCapacity() {
        return steamCapacity;
    }

    public int steamUsage() {
        return steamUsage;
    }

    public int cet() {
        return cet;
    }

    public List<StackRequirement> inputItems() {
        return inputItems;
    }

    public List<StackRequirement> inputFluids() {
        return inputFluids;
    }

    public List<StackRequirement> outputItems() {
        return outputItems;
    }

    public List<StackRequirement> outputFluids() {
        return outputFluids;
    }

    public List<BlockInteraction> blockInteractions() {
        return blockInteractions;
    }

    public List<MachineCondition> conditions() {
        return conditions;
    }

    public List<MachineModifier> modifiers() {
        return modifiers;
    }

    public List<String> overlayFrames() {
        return overlayFrames;
    }

    public String idleOverlay() {
        return overlayFrames.getFirst();
    }

    public List<String> activeOverlays() {
        return overlayFrames.size() <= 1 ? List.of() : overlayFrames.subList(1, overlayFrames.size());
    }

    public MiningTier miningTier() {
        return miningTier;
    }

    public Set<MiningTool> miningTools() {
        return Collections.unmodifiableSet(miningTools);
    }

    @Nullable
    public ProgressBar progressBar() {
        return progressBar;
    }

    public ProgressBar resolvedProgressBar() {
        if (progressBar != null) {
            return progressBar;
        }
        return recipeTypes.stream()
                .map(CERecipeTypes::byId)
                .filter(Objects::nonNull)
                .map(RecipeTypeDefinition::progressBar)
                .findFirst()
                .orElse(ProgressBar.ARROW);
    }

    public float hardness() {
        return miningTier.hardness();
    }

    public float resistance() {
        return miningTier.resistance();
    }

    private void validate() {
        if (id == null || id.isBlank() || !ResourceLocation.isValidPath(id)) {
            throw new IllegalArgumentException("Invalid singleblock machine id: " + id);
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Singleblock machine display name cannot be blank: " + id);
        }
        if (power == null || resource == null || resourceMode == null || startTier == null) {
            throw new IllegalArgumentException("Singleblock machine has an incomplete resource definition: " + id);
        }
        if (power == SingleBlockMachinePower.STEAM && !startTier.isSteam()) {
            throw new IllegalArgumentException("Steam machine must use a steam tier: " + id);
        }
        if (power == SingleBlockMachinePower.ELECTRIC && !startTier.isElectric()) {
            throw new IllegalArgumentException("Electric machine must use an electric tier: " + id);
        }
        if (overlayFrames.isEmpty()) {
            throw new IllegalArgumentException("Singleblock machine must define at least one overlay: " + id);
        }
        if (slots.itemInputs() < 0 || slots.itemOutputs() < 0
                || slots.fluidInputs() < 0 || slots.fluidOutputs() < 0) {
            throw new IllegalArgumentException("Singleblock machine cannot have negative slots: " + id);
        }
        if (recipeTypes.stream().distinct().count() != recipeTypes.size()) {
            throw new IllegalArgumentException("Singleblock machine contains duplicate recipe types: " + id);
        }
        for (ResourceLocation recipeTypeId : recipeTypes) {
            RecipeTypeDefinition recipeType = CERecipeTypes.byId(recipeTypeId);
            if (recipeType == null) {
                throw new IllegalArgumentException("Unknown CE recipe type " + recipeTypeId + " on machine " + id);
            }
            if (slots.itemInputs() < recipeType.maxItemInputs()
                    || slots.itemOutputs() < recipeType.maxItemOutputs()
                    || slots.fluidInputs() < recipeType.maxFluidInputs()
                    || slots.fluidOutputs() < recipeType.maxFluidOutputs()) {
                throw new IllegalArgumentException(
                        "Singleblock machine " + id + " does not provide enough slots for " + recipeTypeId
                );
            }
        }
        if (resource == SingleBlockMachineResource.STEAM && steamCapacity <= 0) {
            throw new IllegalArgumentException("Steam machine must have a positive steam capacity: " + id);
        }
        if (resource == SingleBlockMachineResource.STEAM && steamUsage <= 0) {
            throw new IllegalArgumentException("Steam machine must have a positive steam usage: " + id);
        }
    }

    public record Slots(int itemInputs, int itemOutputs, int fluidInputs, int fluidOutputs) {
    }

    public record StackRequirement(String id, int amount, int durationTicks) {
    }

    /**
     * Options shown by autocomplete inside {@code .machineDefinition(Option...)} for singleblocks.
     */
    @FunctionalInterface
    public interface Option {
        void apply(Builder builder);

        /** Defines the registry path shared by every generated tier of the machine. */
        static Option id(String id) {
            return builder -> builder.id = id;
        }

        /** Defines the user-facing base name. The generated tier name is added automatically. */
        static Option displayName(String displayName) {
            return builder -> builder.displayName = displayName;
        }

        /** Configures a steam-consuming machine. */
        static Option consumesSteam() {
            return builder -> {
                builder.power = SingleBlockMachinePower.STEAM;
                builder.resource = SingleBlockMachineResource.STEAM;
                builder.resourceMode = SingleBlockMachineResourceMode.CONSUMES;
            };
        }

        /** Configures a steam-producing machine. */
        static Option producesSteam() {
            return builder -> {
                builder.power = SingleBlockMachinePower.STEAM;
                builder.resource = SingleBlockMachineResource.STEAM;
                builder.resourceMode = SingleBlockMachineResourceMode.PRODUCES;
            };
        }

        /** Configures an energy-consuming electric machine. */
        static Option consumesEnergy() {
            return builder -> {
                builder.power = SingleBlockMachinePower.ELECTRIC;
                builder.resource = SingleBlockMachineResource.ENERGY;
                builder.resourceMode = SingleBlockMachineResourceMode.CONSUMES;
            };
        }

        /** Configures an energy-producing electric machine. */
        static Option producesEnergy() {
            return builder -> {
                builder.power = SingleBlockMachinePower.ELECTRIC;
                builder.resource = SingleBlockMachineResource.ENERGY;
                builder.resourceMode = SingleBlockMachineResourceMode.PRODUCES;
            };
        }

        /** Configures a custom power, resource, and direction combination. */
        static Option resource(
                SingleBlockMachinePower power,
                SingleBlockMachineResource resource,
                SingleBlockMachineResourceMode mode
        ) {
            return builder -> {
                builder.power = Objects.requireNonNull(power);
                builder.resource = Objects.requireNonNull(resource);
                builder.resourceMode = Objects.requireNonNull(mode);
            };
        }

        /**
         * Defines the first generated tier. Steam copper expands through bronze; electric tiers
         * expand from the selected tier through every registered higher electric tier.
         */
        static Option tier(MachineTier tier) {
            return builder -> builder.startTier = Objects.requireNonNull(tier);
        }

        /** Adds a CE recipe type that this machine can process. */
        static Option recipeType(RecipeTypeDefinition recipeType) {
            return builder -> builder.recipeTypes.add(Objects.requireNonNull(recipeType).id());
        }

        /** Defines item input, item output, fluid input, and fluid output slot counts. */
        static Option slots(int itemInputs, int itemOutputs, int fluidInputs, int fluidOutputs) {
            return builder -> builder.slots = new Slots(itemInputs, itemOutputs, fluidInputs, fluidOutputs);
        }

        /** Defines the copper-tier internal steam capacity in millibuckets. */
        static Option steamCapacity(int steamCapacity) {
            return builder -> builder.steamCapacity = steamCapacity;
        }

        /** Defines copper-tier steam consumed or produced per processing tick. */
        static Option steamUsage(int steamUsage) {
            return builder -> builder.steamUsage = steamUsage;
        }

        /** Defines fallback CE consumed or produced per processing tick. */
        static Option CEt(int cet) {
            return builder -> builder.cet = cet;
        }

        /**
         * Adds a timed non-recipe item input operation. This is stored by the definition but its
         * general runtime behavior is intentionally deferred.
         */
        static Option inputItem(String itemId, int amount, int durationTicks) {
            return builder -> builder.inputItems.add(new StackRequirement(itemId, amount, durationTicks));
        }

        /**
         * Adds a timed non-recipe fluid input operation. This is stored by the definition but its
         * general runtime behavior is intentionally deferred.
         */
        static Option inputFluid(String fluidId, int amount, int durationTicks) {
            return builder -> builder.inputFluids.add(new StackRequirement(fluidId, amount, durationTicks));
        }

        /** Adds a deferred timed non-recipe item output operation. */
        static Option outputItem(String itemId, int amount, int durationTicks) {
            return builder -> builder.outputItems.add(new StackRequirement(itemId, amount, durationTicks));
        }

        /** Adds a deferred timed non-recipe fluid output operation. */
        static Option outputFluid(String fluidId, int amount, int durationTicks) {
            return builder -> builder.outputFluids.add(new StackRequirement(fluidId, amount, durationTicks));
        }

        /** Adds a world block/fluid interaction that applies to every recipe this machine runs. */
        static Option blockInteraction(BlockInteraction interaction) {
            return builder -> builder.blockInteractions.add(Objects.requireNonNull(interaction));
        }

        /** Adds a world block/fluid interaction that applies to every recipe this machine runs. */
        static Option blockInteraction(BlockInteraction.Builder interaction) {
            return blockInteraction(Objects.requireNonNull(interaction).build());
        }

        /** Adds a world condition that applies to every recipe this machine runs. */
        static Option condition(MachineCondition condition) {
            return builder -> builder.conditions.add(Objects.requireNonNull(condition));
        }

        /** Adds an ordered modifier that applies to recipes this machine runs. */
        static Option modifier(MachineModifier modifier) {
            return builder -> builder.modifiers.add(Objects.requireNonNull(modifier));
        }

        /** Adds an ordered modifier that applies to recipes this machine runs. */
        static Option modifier(MachineModifier.Builder modifier) {
            return modifier(Objects.requireNonNull(modifier).build());
        }

        /**
         * Defines the idle front overlay followed by any number of active animation frames.
         * Active frames advance every five ticks while the shared recipe logic is working.
         */
        static Option overlay(String idleOverlay, String... activeOverlays) {
            return builder -> {
                builder.overlayFrames.clear();
                builder.overlayFrames.add(idleOverlay);
                Collections.addAll(builder.overlayFrames, activeOverlays);
            };
        }

        /** Selects the tool types capable of mining the generated machine blocks. */
        static Option mineableWith(MiningTool tool, MiningTool... moreTools) {
            return builder -> {
                builder.miningTools = EnumSet.of(tool);
                if (moreTools != null) {
                    Collections.addAll(builder.miningTools, moreTools);
                }
            };
        }

        /** Selects hardness, resistance, and required mining level. */
        static Option miningTier(MiningTier miningTier) {
            return builder -> builder.miningTier = Objects.requireNonNull(miningTier);
        }

        /**
         * Overrides the recipe type's progress bar for this machine GUI.
         * JEI continues to use the recipe type's default bar.
         */
        static Option progressBar(ProgressBar progressBar) {
            return builder -> builder.progressBar = Objects.requireNonNull(progressBar);
        }
    }

    public static final class Builder {
        private String id;
        private String displayName;
        private SingleBlockMachinePower power = SingleBlockMachinePower.NONE;
        private SingleBlockMachineResource resource = SingleBlockMachineResource.NONE;
        private SingleBlockMachineResourceMode resourceMode = SingleBlockMachineResourceMode.NONE;
        private MachineTier startTier = MachineTier.ULV;
        private final List<ResourceLocation> recipeTypes = new ArrayList<>();
        private Slots slots = new Slots(0, 0, 0, 0);
        private int steamCapacity;
        private int steamUsage;
        private int cet;
        private final List<StackRequirement> inputItems = new ArrayList<>();
        private final List<StackRequirement> inputFluids = new ArrayList<>();
        private final List<StackRequirement> outputItems = new ArrayList<>();
        private final List<StackRequirement> outputFluids = new ArrayList<>();
        private final List<BlockInteraction> blockInteractions = new ArrayList<>();
        private final List<MachineCondition> conditions = new ArrayList<>();
        private final List<MachineModifier> modifiers = new ArrayList<>();
        private final List<String> overlayFrames = new ArrayList<>();
        private MiningTier miningTier = MiningTier.STONE;
        private EnumSet<MiningTool> miningTools = EnumSet.of(MiningTool.PICKAXE);
        private ProgressBar progressBar;

        private Builder() {
        }

        public Builder machineDefinition(Option option) {
            Objects.requireNonNull(option, "Singleblock machine option").apply(this);
            return this;
        }

        public SingleBlockDefinition build() {
            return new SingleBlockDefinition(this);
        }
    }
}
