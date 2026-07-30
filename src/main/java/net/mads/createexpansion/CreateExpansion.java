package net.mads.createexpansion;



import net.neoforged.bus.api.IEventBus;

import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.fml.ModContainer;

import net.neoforged.fml.common.Mod;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import net.neoforged.neoforge.common.NeoForge;

import net.mads.createexpansion.registry.*;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import net.mads.createexpansion.commands.MyCommand;

import net.mads.createexpansion.data.ModDataGenerators;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinitions;
import net.mads.createexpansion.machine.machines.kinetic.KineticMachineStress;
import net.mads.createexpansion.recipe.remove.RecipeRemovalEvents;
import net.mads.createexpansion.worldgen.OreVeinLocator;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.level.LevelEvent;



@Mod("create_expansion")

public class CreateExpansion {

    public static final String MOD_ID = "create_expansion";

    public static final Logger LOGGER = LoggerFactory.getLogger("Create Expansion");

    private final ModContainer container;



    public CreateExpansion(IEventBus modEventBus, ModContainer container) {

        this.container = container;

        // Register all registries

        FluidRegistry.register(modEventBus);

        ItemRegistry.register(modEventBus);

        BlockRegistry.register(modEventBus);

        BlockEntityRegistry.register(modEventBus);

        MenuRegistry.register(modEventBus);

        RecipeRegistry.register(modEventBus);

        CreativeTabRegistry.register(modEventBus);

        WorldgenRegistry.register(modEventBus);

        CreateExpansionPartialModels.init();

        

        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(ModDataGenerators::gatherData);

        modEventBus.addListener(FluidRegistry::registerCapabilities);

        modEventBus.addListener(BlockEntityRegistry::registerCapabilities);

        

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(RecipeRemovalEvents::onDatapackSync);

    }



    private void commonSetup(final FMLCommonSetupEvent event) {

        LOGGER.info("Create Expansion is loading!");

        MultiblockDefinitions.bootstrap();
        event.enqueueWork(KineticMachineStress::register);

    }





    @SubscribeEvent

    public void onRegisterCommands(RegisterCommandsEvent event) {

        MyCommand.register(event.getDispatcher());

    }

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            OreVeinLocator.ensureSavedData(level);
        }
    }

}



