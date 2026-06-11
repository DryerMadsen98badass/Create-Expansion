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

import net.mads.createexpansion.multiblock.MultiblockDefinitions;



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


        

        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(ModDataGenerators::gatherData);

        modEventBus.addListener(FluidRegistry::registerCapabilities);

        modEventBus.addListener(BlockEntityRegistry::registerCapabilities);

        

        NeoForge.EVENT_BUS.register(this);

    }



    private void commonSetup(final FMLCommonSetupEvent event) {

        LOGGER.info("Create Expansion is loading!");

        MultiblockDefinitions.bootstrap();

    }





    @SubscribeEvent

    public void onRegisterCommands(RegisterCommandsEvent event) {

        MyCommand.register(event.getDispatcher());

    }

}



