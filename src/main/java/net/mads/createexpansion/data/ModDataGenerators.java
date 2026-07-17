package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            event.addProvider(new MaterialBlockTagProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
            event.addProvider(new CERecipeProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
            event.addProvider(new FoundryWashingRecipeProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
            event.addProvider(new CoilLootProvider(event.getGenerator().getPackOutput()));
        }

        if (event.includeClient()) {
            event.addProvider(new ModLanguageProvider(event.getGenerator().getPackOutput()));
            event.addProvider(new MaterialItemModelProvider(event.getGenerator().getPackOutput(), event.getExistingFileHelper()));
            event.addProvider(new MaterialBlockStateProvider(event.getGenerator().getPackOutput(), event.getExistingFileHelper()));
            event.addProvider(new MachineCasingModelProvider(event.getGenerator().getPackOutput()));
            event.addProvider(new MachinePortModelProvider(event.getGenerator().getPackOutput()));
            event.addProvider(new EnergyWireModelProvider(event.getGenerator().getPackOutput()));
            event.addProvider(new CoilModelProvider(event.getGenerator().getPackOutput()));
        }

        CreateExpansion.LOGGER.info("Generating Create Expansion data");
    }
}
