package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.recipes.CreateMaterialRecipeProvider;
import net.mads.createexpansion.material.recipes.FoundryWashingRecipeProvider;
import net.mads.createexpansion.recipe.recipes.CreateRecipeExamplesProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModDataGenerators {

    private ModDataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(
            GatherDataEvent event
    ) {
        if (event.includeServer()) {
            MaterialBlockTagProvider blockTags =
                    new MaterialBlockTagProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider(),
                            event.getExistingFileHelper()
                    );

            event.addProvider(blockTags);

            event.addProvider(
                    new MaterialItemTagProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider(),
                            blockTags.contentsGetter(),
                            event.getExistingFileHelper()
                    )
            );

            event.addProvider(
                    new CERecipeProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider()
                    )
            );

            event.addProvider(
                    new FoundryWashingRecipeProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider()
                    )
            );

            event.addProvider(
                    new CreateMaterialRecipeProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider()
                    )
            );

            event.addProvider(
                    new CreateRecipeExamplesProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new RecipeRemovalProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new SimpleBlockRecipeProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new CoilLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new FireboxLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new FoundryLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new MaterialStoneLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new SimpleBlockLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new SingleBlockMachineLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new MultiblockControlerLootTableProvider(
                            event.getGenerator().getPackOutput(),
                            event.getLookupProvider()
                    )
            );
        }

        if (event.includeClient()) {
            event.addProvider(
                    new ModLanguageProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new MaterialItemModelProvider(
                            event.getGenerator().getPackOutput(),
                            event.getExistingFileHelper()
                    )
            );

            event.addProvider(
                    new MaterialBlockStateProvider(
                            event.getGenerator().getPackOutput(),
                            event.getExistingFileHelper()
                    )
            );

            event.addProvider(
                    new MachineCasingModelProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new SingleBlockMachineModelProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new MachinePortModelProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new EnergyWireModelProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new CoilModelProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            event.addProvider(
                    new FireboxModelProvider(
                            event.getGenerator().getPackOutput()
                    )
            );

            /*
             * Denne lå tidligere under client-delen i filen din.
             * Jeg lar den stå der for å unngå å endre oppførselen
             * til den eksisterende provideren.
             */
            event.addProvider(
                    new MaterialOreLootProvider(
                            event.getGenerator().getPackOutput()
                    )
            );
        }

        CreateExpansion.LOGGER.info(
                "Generating Create Expansion data"
        );
    }
}
