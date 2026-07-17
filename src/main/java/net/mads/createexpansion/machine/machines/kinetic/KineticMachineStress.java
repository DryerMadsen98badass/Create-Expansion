package net.mads.createexpansion.machine.machines.kinetic;

import com.simibubi.create.api.stress.BlockStressValues;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.registry.BlockRegistry;

public final class KineticMachineStress {
    private KineticMachineStress() {
    }

    public static void register() {
        BlockStressValues.IMPACTS.register(BlockRegistry.KINETIC_SIFTER.get(), () -> KineticSifterRegistration.STRESS_IMPACT);
        BlockStressValues.IMPACTS.register(BlockRegistry.KINETIC_CENTRIFUGE.get(), () -> KineticCentrifugeRegistration.STRESS_IMPACT);
        BlockStressValues.IMPACTS.register(BlockRegistry.KINETIC_LATHE.get(), () -> KineticLatheRegistration.STRESS_IMPACT);
        BlockStressValues.IMPACTS.register(BlockRegistry.KINETIC_ROLLING_MILL.get(), () -> KineticRollingMillRegistration.STRESS_IMPACT);
        BlockStressValues.IMPACTS.register(BlockRegistry.KINETIC_WIRE_DRAWER.get(), () -> KineticWireDrawerRegistration.STRESS_IMPACT);
        BlockStressValues.IMPACTS.register(BlockRegistry.SPRING_COILING_MACHINE.get(), () -> KineticCoilingMachineRegistration.STRESS_IMPACT);
    }
}
