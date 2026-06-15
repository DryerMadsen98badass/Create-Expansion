package net.mads.createexpansion.machine;

public final class MachineTierStats {
    private static final int[] ITEM_SLOTS = {1, 4, 9, 16, 25, 36, 49, 64, 81, 100};
    private static final int MAX_FLUID_CAPACITY = 1_024_000;
    private static final int MAX_IO_ITEM_SLOTS = 49;
    private static final float MAX_KINETIC_STRESS_PER_RPM = 16_384.0F;

    private MachineTierStats() {
    }

    public static int tierIndex(MachineTier tier) {
        int index = MachineTier.ALL.indexOf(tier);
        return Math.max(index, 0);
    }

    public static boolean isAtLeast(MachineTier actual, MachineTier required) {
        return tierIndex(actual) >= tierIndex(required);
    }

    public static MachineTier max(MachineTier first, MachineTier second) {
        return tierIndex(first) >= tierIndex(second) ? first : second;
    }

    public static int itemBusSlots(MachineTier tier) {
        int index = Math.min(tierIndex(tier), ITEM_SLOTS.length - 1);
        return ITEM_SLOTS[index];
    }

    public static int mufflerSlots(MachineTier tier) {
        return itemBusSlots(tier);
    }

    public static int ioInterfaceItemSlots(MachineTier tier) {
        return Math.min(itemBusSlots(tier), MAX_IO_ITEM_SLOTS);
    }

    public static int fluidTankCapacity(MachineTier tier) {
        long capacity = 4_000L << Math.min(tierIndex(tier), 30);
        return (int) Math.min(capacity, MAX_FLUID_CAPACITY);
    }

    public static int ioInterfaceFluidTanks() {
        return 4;
    }

    public static int ceCapacity(MachineTier tier) {
        long capacity = (long) ceTier(tier) * 32L;
        return (int) Math.min(capacity, Integer.MAX_VALUE);
    }

    public static int ceTier(MachineTier tier) {
        long ce = 8L;
        for (int i = 0; i < tierIndex(tier); i++) {
            ce *= 4L;
            if (ce > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
        }
        return (int) ce;
    }

    public static int ceBaseAmps(MachineTier tier) {
        int index = Math.min(tierIndex(tier), 30);
        return 1 << index;
    }

    public static MachineTier tierForCEt(int cet) {
        int required = Math.abs(cet);
        for (MachineTier tier : MachineTier.ALL) {
            if (ceTier(tier) >= required) {
                return tier;
            }
        }
        return MachineTier.ALL.get(MachineTier.ALL.size() - 1);
    }

    public static MachineTier tierForVoltage(int voltage) {
        int required = Math.abs(voltage);
        MachineTier result = MachineTier.ULV;
        for (MachineTier tier : MachineTier.ALL) {
            result = tier;
            if (ceTier(tier) >= required) {
                return tier;
            }
        }
        return result;
    }

    public static int tierOverclockFactor(MachineTier required, MachineTier actual) {
        int tierDifference = Math.max(0, tierIndex(actual) - tierIndex(required));
        return 1 << Math.min(tierDifference, 30);
    }

    public static int ceOverclockMultiplier(MachineTier required, MachineTier actual) {
        int tierDifference = Math.max(0, tierIndex(actual) - tierIndex(required));
        int multiplier = 1;
        for (int i = 0; i < tierDifference; i++) {
            if (multiplier > Integer.MAX_VALUE / 4) {
                return Integer.MAX_VALUE;
            }
            multiplier *= 4;
        }
        return multiplier;
    }

    public static float kineticStressPerRpm(MachineTier tier) {
        float stress = 16.0F;
        for (int i = 0; i < tierIndex(tier); i++) {
            stress *= 4.0F;
            if (stress >= MAX_KINETIC_STRESS_PER_RPM) {
                return MAX_KINETIC_STRESS_PER_RPM;
            }
        }
        return stress;
    }
}
