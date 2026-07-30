package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab>
            CREATIVE_MODE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    CreateExpansion.MOD_ID
            );

    public static final DeferredHolder<
            CreativeModeTab,
            CreativeModeTab
            > MAIN_TAB =
            CREATIVE_MODE_TABS.register(
                    "main",
                    () -> CreativeModeTab.builder()
                            .title(
                                    Component.translatable(
                                            "itemGroup.create_expansion.industry"
                                    )
                            )
                            .icon(
                                    () -> new ItemStack(
                                            ItemRegistry
                                                    .getMaterialItem(
                                                            IndustrialMaterials.BRONZE,
                                                            MaterialPart.INGOT
                                                    )
                                                    .get()
                                    )
                            )
                            .displayItems(
                                    (parameters, output) -> {
                                        ItemRegistry
                                                .getAllMultiblockControllerItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllSingleBlockMachineItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllCoilItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllMachineCasingItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllEnergyWireItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllInsulatedEnergyWireItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        output.accept(
                                                ItemRegistry
                                                        .CREATIVE_ENERGY_PROVIDER
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .CREATIVE_ENERGY_CONSUMER
                                                        .get()
                                        );

                                        ItemRegistry
                                                .getAllActiveBlockItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllSimpleItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllSimpleBlockItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllSimpleBlockVariantItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        output.accept(
                                                ItemRegistry
                                                        .KINETIC_SIFTER
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .KINETIC_CENTRIFUGE
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .KINETIC_LATHE
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .KINETIC_ROLLING_MILL
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .KINETIC_WIRE_DRAWER
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .HYDRAULIC_PRESS
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .SPRING_COILING_MACHINE
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_CASING
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_CONTROLLER
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .CREATIVE_FOUNDRY_CONTROLLER
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_INPUT_HATCH
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_OUTPUT_HATCH
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_INPUT_BUS
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_DRAIN
                                                        .get()
                                        );

                                        output.accept(
                                                ItemRegistry
                                                        .FOUNDRY_MOLD_CASTER
                                                        .get()
                                        );

                                        ItemRegistry
                                                .getAllMachinePortItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllStaticMachinePortItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );
                                    }
                            )
                            .build()
            );

    public static final DeferredHolder<
            CreativeModeTab,
            CreativeModeTab
            > MATERIALS_TAB =
            CREATIVE_MODE_TABS.register(
                    "materials",
                    () -> CreativeModeTab.builder()
                            .title(
                                    Component.translatable(
                                            "itemGroup.create_expansion.materials"
                                    )
                            )
                            .icon(
                                    () -> new ItemStack(
                                            ItemRegistry
                                                    .getMaterialItem(
                                                            IndustrialMaterials.HEMATITE,
                                                            MaterialPart.CRUSHED_ORE
                                                    )
                                                    .get()
                                    )
                            )
                            .displayItems(
                                    (parameters, output) -> {
                                        ItemRegistry
                                                .getAllMaterialItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllMaterialStoneItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllSimpleItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        output.accept(
                                                ItemRegistry
                                                        .FIRED_BUCKET
                                                        .get()
                                        );

                                        ItemRegistry
                                                .getAllSimpleBlockItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        ItemRegistry
                                                .getAllSimpleBlockVariantItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );

                                        FluidRegistry
                                                .getAllBucketItems()
                                                .forEach(
                                                        item ->
                                                                output.accept(
                                                                        item.get()
                                                                )
                                                );
                                    }
                            )
                            .build()
            );

    private CreativeTabRegistry() {
    }

    public static void register(
            IEventBus modEventBus
    ) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
