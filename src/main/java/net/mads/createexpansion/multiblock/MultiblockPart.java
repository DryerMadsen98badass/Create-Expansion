package net.mads.createexpansion.multiblock;

import net.mads.createexpansion.machine.MachineTier;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MultiblockPart {
    Set<MultiblockAbility> abilities();

    @Nullable
    MachineTier partTier();

    default boolean hasAbility(MultiblockAbility ability) {
        return abilities().contains(ability);
    }

    void attachToMultiblock(BlockPos controllerPos);

    void detachFromMultiblock();

    BlockPos controllerPos();
}
