package net.mads.createexpansion.machine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public record MachineDefinition(String id, String displayName, MachineTier tier) {
    public static final List<MachineDefinition> ALL = Stream.<List<MachineDefinition>>of(
            // Add machines here later, for example:
            // MachineDefinition.forTiers("electric_furnace", "Electric Furnace", MachineTier.LV, MachineTier.MV, MachineTier.HV)
    ).flatMap(List::stream).toList();

    public static List<MachineDefinition> forTiers(String id, String displayName, MachineTier... tiers) {
        return Arrays.stream(tiers)
                .map(tier -> new MachineDefinition(id, displayName, tier))
                .toList();
    }

    public String controllerRegistryName() {
        return tier.id() + "_" + id + "_controller";
    }

    public String casingRegistryName() {
        return tier.casingRegistryName();
    }
}
