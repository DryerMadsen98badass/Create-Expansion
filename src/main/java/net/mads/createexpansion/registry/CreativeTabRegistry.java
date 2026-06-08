package net.mads.createexpansion.registry;

import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.mads.createexpansion.CreateExpansion;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateExpansion.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_expansion.industry"))
            .icon(() -> new ItemStack(ItemRegistry.getMaterialItem(IndustrialMaterials.BRONZE, MaterialPart.INGOT).get()))
            .displayItems((parameters, output) -> {
                ItemRegistry.getAllMultiblockControllerItems().forEach(item -> output.accept(item.get()));
                ItemRegistry.getAllMachineCasingItems().forEach(item -> output.accept(item.get()));
                ItemRegistry.getAllMachinePortItems().forEach(item -> output.accept(item.get()));
                ItemRegistry.getAllStaticMachinePortItems().forEach(item -> output.accept(item.get()));
            })
            .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS_TAB = CREATIVE_MODE_TABS.register("materials", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_expansion.materials"))
            .icon(() -> new ItemStack(ItemRegistry.getMaterialItem(IndustrialMaterials.HEMATITE, MaterialPart.CRUSHED_ORE).get()))
            .displayItems((parameters, output) -> {
                ItemRegistry.getAllMaterialItems().forEach(item -> output.accept(item.get()));
                FluidRegistry.getAllBucketItems().forEach(item -> output.accept(item.get()));
            })
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
