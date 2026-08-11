package net.mads.createexpansion.menu;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.mads.createexpansion.recipe.PhRange;
import net.minecraft.world.inventory.ContainerData;

final class MultiblockControllerMenuData implements ContainerData {
    static final int DURABILITY_LOW = 0;
    static final int DURABILITY_HIGH = 1;
    static final int MAX_DURABILITY = 2;
    static final int CORROSION_PER_TICK = 3;
    static final int MACHINE_PH = 4;
    static final int SAFE_PH_MIN = 5;
    static final int SAFE_PH_MAX = 6;
    static final int FLAGS = 7;
    static final int COUNT = 8;

    static final int FLAG_DURABILITY = 1;
    static final int FLAG_SAFE_PH = 1 << 1;
    static final int FLAG_PH_HATCH = 1 << 2;

    private final MultiblockControllerBlockEntity controller;

    MultiblockControllerMenuData(MultiblockControllerBlockEntity controller) {
        this.controller = controller;
    }

    @Override
    public int get(int index) {
        long durability = controller.machineDurabilityHundredths();
        return switch (index) {
            case DURABILITY_LOW -> (int) durability;
            case DURABILITY_HIGH -> (int) (durability >>> 32);
            case MAX_DURABILITY -> controller.maxMachineDurability();
            case CORROSION_PER_TICK -> controller.corrosionDamageHundredthsPerTick();
            case MACHINE_PH -> controller.machinePhHundredths();
            case SAFE_PH_MIN -> controller.safePhRange().map(PhRange::minHundredths).orElse(0);
            case SAFE_PH_MAX -> controller.safePhRange().map(PhRange::maxHundredths).orElse(0);
            case FLAGS -> flags();
            default -> 0;
        };
    }

    private int flags() {
        int flags = 0;
        if (controller.hasMachineDurability()) {
            flags |= FLAG_DURABILITY;
        }
        if (controller.safePhRange().isPresent()) {
            flags |= FLAG_SAFE_PH;
        }
        if (controller.hasPhHatch()) {
            flags |= FLAG_PH_HATCH;
        }
        return flags;
    }

    @Override
    public void set(int index, int value) {
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
