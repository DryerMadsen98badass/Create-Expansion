package net.mads.createexpansion.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

final class FluidSlotClickHandler {
    private FluidSlotClickHandler() {
    }

    static boolean interact(AbstractContainerMenu menu, Player player, FluidTank tank, boolean allowFill, boolean allowDrain) {
        ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            return false;
        }

        ItemStack singleContainer = carried.copyWithCount(1);
        IFluidHandlerItem handler = singleContainer.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) {
            return false;
        }

        if (allowFill && fillTankFromContainer(menu, player, carried, handler, tank)) {
            return true;
        }
        return allowDrain && fillContainerFromTank(menu, player, carried, handler, tank);
    }

    private static boolean fillTankFromContainer(
            AbstractContainerMenu menu,
            Player player,
            ItemStack carried,
            IFluidHandlerItem handler,
            FluidTank tank
    ) {
        FluidStack available = handler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
        if (available.isEmpty()) {
            return false;
        }

        int accepted = tank.fill(available, FluidAction.SIMULATE);
        if (accepted <= 0) {
            return false;
        }

        FluidStack drained = handler.drain(accepted, FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return false;
        }

        int filled = tank.fill(drained, FluidAction.EXECUTE);
        if (filled <= 0) {
            return false;
        }

        replaceOneCarried(menu, player, carried, handler.getContainer());
        return true;
    }

    private static boolean fillContainerFromTank(
            AbstractContainerMenu menu,
            Player player,
            ItemStack carried,
            IFluidHandlerItem handler,
            FluidTank tank
    ) {
        FluidStack available = tank.drain(tank.getCapacity(), FluidAction.SIMULATE);
        if (available.isEmpty()) {
            return false;
        }

        int accepted = handler.fill(available, FluidAction.SIMULATE);
        if (accepted <= 0) {
            return false;
        }

        FluidStack drained = tank.drain(accepted, FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return false;
        }

        int filled = handler.fill(drained, FluidAction.EXECUTE);
        if (filled <= 0) {
            tank.fill(drained, FluidAction.EXECUTE);
            return false;
        }

        replaceOneCarried(menu, player, carried, handler.getContainer());
        return true;
    }

    private static void replaceOneCarried(
            AbstractContainerMenu menu,
            Player player,
            ItemStack carried,
            ItemStack replacement
    ) {
        if (carried.getCount() == 1) {
            menu.setCarried(replacement);
            return;
        }

        carried.shrink(1);
        menu.setCarried(carried);
        if (!replacement.isEmpty() && !player.getInventory().add(replacement)) {
            player.drop(replacement, false);
        }
    }
}
