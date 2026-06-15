package net.mads.createexpansion.recipe;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record CERecipeTypeDefinition(
        ResourceLocation id,
        String displayName,
        int maxItemInputs,
        int maxItemOutputs,
        int maxFluidInputs,
        int maxFluidOutputs,
        KineticMode kineticMode,
        EnergyMode energyMode,
        List<ResourceLocation> supportedLogic
) {
    public CERecipeTypeDefinition {
        supportedLogic = List.copyOf(supportedLogic);
    }

    public CERecipeBuilder recipe(String id) {
        return new CERecipeBuilder(this, id);
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

    public static Builder builder(String id) {
        return new Builder(id(id));
    }

    public static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    public static final class Builder {
        private final ResourceLocation id;
        private String displayName;
        private int maxItemInputs;
        private int maxItemOutputs;
        private int maxFluidInputs;
        private int maxFluidOutputs;
        private KineticMode kineticMode = KineticMode.NONE;
        private EnergyMode energyMode = EnergyMode.CONSUMES;
        private final List<ResourceLocation> supportedLogic = new ArrayList<>();

        private Builder(ResourceLocation id) {
            this.id = id;
            this.displayName = id.getPath();
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder maxIO(int itemInputs, int itemOutputs, int fluidInputs, int fluidOutputs) {
            this.maxItemInputs = itemInputs;
            this.maxItemOutputs = itemOutputs;
            this.maxFluidInputs = fluidInputs;
            this.maxFluidOutputs = fluidOutputs;
            return this;
        }

        public Builder maxIO(int itemInputs, int itemOutputs, int fluidInputs, int fluidOutputs, boolean usesRpm, EnergyMode energyMode) {
            return maxIO(itemInputs, itemOutputs, fluidInputs, fluidOutputs)
                    .rpm(usesRpm)
                    .energyMode(energyMode);
        }

        public Builder rpm() {
            return rpm(true);
        }

        public Builder rpm(boolean usesRpm) {
            return kineticMode(usesRpm ? KineticMode.CONSUMES : KineticMode.NONE);
        }

        public Builder kineticMode(KineticMode kineticMode) {
            this.kineticMode = kineticMode;
            return this;
        }

        public Builder noKinetic() {
            return kineticMode(KineticMode.NONE);
        }

        public Builder kineticInput() {
            return kineticMode(KineticMode.CONSUMES);
        }

        public Builder kineticOutput() {
            return kineticMode(KineticMode.GENERATES);
        }

        public Builder kineticInputAndOutput() {
            return kineticMode(KineticMode.BOTH);
        }

        public Builder energyMode(EnergyMode energyMode) {
            this.energyMode = energyMode;
            return this;
        }

        public Builder noEnergy() {
            return energyMode(EnergyMode.NONE);
        }

        public Builder energyInput() {
            return energyMode(EnergyMode.CONSUMES);
        }

        public Builder energyOutput() {
            return energyMode(EnergyMode.GENERATES);
        }

        public Builder energyInputAndOutput() {
            return energyMode(EnergyMode.BOTH);
        }

        public Builder logic(CERecipeLogicDefinition logic) {
            this.supportedLogic.add(logic.id());
            return this;
        }

        public CERecipeTypeDefinition build() {
            return new CERecipeTypeDefinition(id, displayName, maxItemInputs, maxItemOutputs, maxFluidInputs, maxFluidOutputs, kineticMode, energyMode, supportedLogic);
        }

        public Builder maxIO(int itemInputs, int itemOutputs, int fluidInputs, int fluidOutputs, KineticMode kineticMode, EnergyMode energyMode) {
            return maxIO(itemInputs, itemOutputs, fluidInputs, fluidOutputs)
                    .kineticMode(kineticMode)
                    .energyMode(energyMode);
        }
    }
}
