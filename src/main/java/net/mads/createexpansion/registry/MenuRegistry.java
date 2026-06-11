package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.menu.MachinePortMenu;
import net.mads.createexpansion.menu.MultiblockControllerMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, CreateExpansion.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<MachinePortMenu>> MACHINE_PORT =
            MENUS.register("machine_port", () -> IMenuTypeExtension.create(MachinePortMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<MultiblockControllerMenu>> MULTIBLOCK_CONTROLLER =
            MENUS.register("multiblock_controller", () -> IMenuTypeExtension.create(MultiblockControllerMenu::new));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
