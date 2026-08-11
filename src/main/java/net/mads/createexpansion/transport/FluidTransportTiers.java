package net.mads.createexpansion.transport;

import static net.mads.createexpansion.transport.FluidTransportTier.transportTier;

public final class FluidTransportTiers {
    /**
     * Create's normal mechanical pump rate in mB per RPM, per tick.
     * Change this one value if Create's pump and pipes should be faster or slower.
     */
    public static final double CREATE_PUMP_RATE = 4.0D;

    /** Create pipe limit, derived from Create's pump at the maximum 256 RPM. */
    public static final int CREATE_PIPE_RATE = Math.max(1, (int) Math.floor(CREATE_PUMP_RATE * 256.0D));


    public static final FluidTransportTier STAINLESS_BRONZE = transportTier("stainless_bronze", "Stainless Bronze")
            .color(0xB8753D)
            .pumpRate(8.0D)
            .pumpStressImpact(16.0D)
            .tankCapacity(32000)
            .build();

    private FluidTransportTiers() {
    }

    public static void bootstrap() {
        // Calling this method forces this class, and therefore every tier above, to initialize.
    }
}
