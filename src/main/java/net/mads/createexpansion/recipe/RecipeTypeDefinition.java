package net.mads.createexpansion.recipe;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.gui.ProgressBar;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Declarative definition of one CE recipe type.
 */
public record RecipeTypeDefinition(
        ResourceLocation id,
        String displayName,
        int maxItemInputs,
        int maxItemOutputs,
        int maxFluidInputs,
        int maxFluidOutputs,
        KineticMode kineticMode,
        EnergyMode energyMode,
        List<ResourceLocation> supportedLogic,
        ProgressBar progressBar
) {
    public RecipeTypeDefinition {
        supportedLogic = List.copyOf(supportedLogic);
        progressBar = Objects.requireNonNullElse(progressBar, ProgressBar.ARROW);
    }

    public boolean supportsLogic(ResourceLocation logicId) {
        return supportedLogic.contains(logicId);
    }

    public boolean usesRpm() {
        return acceptsRpm();
    }

    public boolean acceptsRpm() {
        return kineticMode == KineticMode.CONSUMES || kineticMode == KineticMode.BOTH;
    }

    public boolean outputsRpm() {
        return kineticMode == KineticMode.GENERATES || kineticMode == KineticMode.BOTH;
    }

    public enum KineticMode {
        NONE,
        CONSUMES,
        GENERATES,
        BOTH
    }

    public enum EnergyMode {
        NONE,
        CONSUMES,
        GENERATES,
        BOTH
    }

    public static Builder recipeType() {
        return new Builder();
    }

    public static ResourceLocation id(String id) {
        return id.contains(":")
                ? ResourceLocation.parse(id)
                : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    /**
     * Options shown by autocomplete inside {@code .recipeTypeDefinition(Option...)}.
     */
    @FunctionalInterface
    public interface Option {
        void apply(Builder builder);

        /** Defines the registry path of the recipe type. */
        static Option id(String id) {
            return builder -> builder.id = RecipeTypeDefinition.id(id);
        }

        /** Defines the user-facing name shown in JEI and machine interfaces. */
        static Option displayName(String displayName) {
            return builder -> builder.displayName = displayName;
        }

        /** Defines maximum item and fluid inputs and outputs, in that order. */
        static Option maxIO(int itemInputs, int itemOutputs, int fluidInputs, int fluidOutputs) {
            return builder -> {
                builder.maxItemInputs = itemInputs;
                builder.maxItemOutputs = itemOutputs;
                builder.maxFluidInputs = fluidInputs;
                builder.maxFluidOutputs = fluidOutputs;
            };
        }

        /** Defines whether recipes can consume or generate kinetic rotation. */
        static Option kineticMode(KineticMode kineticMode) {
            return builder -> builder.kineticMode = Objects.requireNonNull(kineticMode);
        }

        /** Defines whether recipes can consume or generate CE. */
        static Option energyMode(EnergyMode energyMode) {
            return builder -> builder.energyMode = Objects.requireNonNull(energyMode);
        }

        /** Adds a supported custom recipe-logic capability. */
        static Option logic(CERecipeLogicDefinition logic) {
            return builder -> builder.supportedLogic.add(Objects.requireNonNull(logic).id());
        }

        /**
         * Selects the default progress bar used by JEI and machines using this recipe type.
         * A machine definition may override it for its own GUI.
         */
        static Option progressBar(ProgressBar progressBar) {
            return builder -> builder.progressBar = Objects.requireNonNull(progressBar);
        }
    }

    public static final class Builder {
        private ResourceLocation id;
        private String displayName;
        private int maxItemInputs;
        private int maxItemOutputs;
        private int maxFluidInputs;
        private int maxFluidOutputs;
        private KineticMode kineticMode = KineticMode.NONE;
        private EnergyMode energyMode = EnergyMode.CONSUMES;
        private final List<ResourceLocation> supportedLogic = new ArrayList<>();
        private ProgressBar progressBar = ProgressBar.ARROW;

        private Builder() {
        }

        public Builder recipeTypeDefinition(Option option) {
            Objects.requireNonNull(option, "Recipe type option").apply(this);
            return this;
        }

        public RecipeTypeDefinition build() {
            if (id == null) {
                throw new IllegalStateException("Recipe type is missing Option.id(...)");
            }
            if (displayName == null || displayName.isBlank()) {
                displayName = id.getPath();
            }
            if (maxItemInputs < 0 || maxItemOutputs < 0 || maxFluidInputs < 0 || maxFluidOutputs < 0) {
                throw new IllegalStateException("Recipe type " + id + " cannot have negative IO limits");
            }
            return new RecipeTypeDefinition(
                    id,
                    displayName,
                    maxItemInputs,
                    maxItemOutputs,
                    maxFluidInputs,
                    maxFluidOutputs,
                    kineticMode,
                    energyMode,
                    supportedLogic,
                    progressBar
            );
        }
    }
}
