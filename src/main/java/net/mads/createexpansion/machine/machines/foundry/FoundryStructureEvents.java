package net.mads.createexpansion.machine.machines.foundry;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID)
public final class FoundryStructureEvents {
    private FoundryStructureEvents() {
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide()) {
            FoundryStructureTracker.blockChanged(level, event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide()) {
            FoundryStructureTracker.blockChanged(level, event.getPos());
        }
    }
}
