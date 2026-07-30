package net.mads.createexpansion.machine;

public record SingleBlockMachineInstance(
        SingleBlockDefinition definition,
        MachineTier tier
) {
    public String registryName() {
        return tier.id() + "_" + definition.id();
    }

    public String displayName() {
        return tier.displayName() + " " + definition.displayName();
    }

    public int steamCapacity() {
        return definition.steamCapacity() * tier.steamCapacityMultiplier();
    }

    public int steamUsage() {
        return definition.steamUsage() * tier.steamUsageMultiplier();
    }
}
