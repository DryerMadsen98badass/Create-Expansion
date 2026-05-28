package net.mads.createexpansion;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.CreativeTabRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("create_expansion")
public class CreateExpansion {
    public static final String MOD_ID = "create_expansion";
    public static final Logger LOGGER = LoggerFactory.getLogger("Create Expansion");

    public CreateExpansion(IEventBus modEventBus, ModContainer modContainer) {
        // Register all registries
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        CreativeTabRegistry.register(modEventBus);
        
        modEventBus.addListener(this::commonSetup);
        
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Create Expansion is loading!");
    }
}
