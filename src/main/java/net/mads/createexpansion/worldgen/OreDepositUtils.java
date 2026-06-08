package net.mads.createexpansion.worldgen;

import java.util.List;

final class OreDepositUtils {
    private OreDepositUtils() {
    }

    static OreDeposit pickDeposit(List<OreDeposit> candidates, int roll) {
        int cursor = roll;
        for (OreDeposit deposit : candidates) {
            cursor -= deposit.weight();
            if (cursor < 0) {
                return deposit;
            }
        }

        return candidates.get(candidates.size() - 1);
    }

    static long mixedSeed(long seed, int a, int b, int c) {
        long value = seed;
        value ^= (long) a * 0x9E3779B97F4A7C15L;
        value ^= (long) b * 0xC2B2AE3D27D4EB4FL;
        value ^= (long) c * 0x165667B19E3779F9L;
        return mix(value);
    }

    static long mix(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return value;
    }

    static double randomUnit(long seed) {
        return (double) (mix(seed) >>> 11) * 0x1.0p-53;
    }
}
