package net.mads.createexpansion.machine;

import net.mads.createexpansion.machine.machines.electric.singleblock.ElectricSingleBlockMachines;
import net.mads.createexpansion.machine.machines.kinetic.singleblock.KineticSingleBlockMachines;
import net.mads.createexpansion.machine.machines.steam.singleblock.SteamSingleBlockMachines;
import net.mads.createexpansion.machine.machines.without_energy.singleblock.WithoutEnergySingleBlockMachines;

import java.util.List;
import java.util.stream.Stream;

public final class MachineDefinition {
    public static final List<SingleBlockDefinition> ALL =
            Stream.of(
                            SteamSingleBlockMachines.ALL,
                            WithoutEnergySingleBlockMachines.ALL,
                            ElectricSingleBlockMachines.ALL,
                            KineticSingleBlockMachines.ALL
                    )
                    .flatMap(List::stream)
                    .toList();

    public static final List<SingleBlockMachineInstance> INSTANCES =
            ALL.stream()
                    .flatMap(definition -> definition.expand().stream())
                    .toList();

    private MachineDefinition() {
    }
}
