package net.mads.createexpansion.menu;

import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.mads.createexpansion.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MultiblockControllerMenu extends AbstractContainerMenu {
    private final MultiblockControllerBlockEntity blockEntity;
    private final ContainerData data;
    private final int playerInventoryStart;

    public MultiblockControllerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, clientBlockEntity(playerInventory, buffer), new SimpleContainerData(CERecipeMenuData.COUNT));
    }

    public MultiblockControllerMenu(int containerId, Inventory playerInventory, MultiblockControllerBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, serverData(blockEntity));
    }

    private MultiblockControllerMenu(int containerId, Inventory playerInventory, MultiblockControllerBlockEntity blockEntity, ContainerData data) {
        super(MenuRegistry.MULTIBLOCK_CONTROLLER.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        addDataSlots(data);
        this.playerInventoryStart = slots.size();
        addPlayerInventory(playerInventory);
    }

    private static MultiblockControllerBlockEntity clientBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        if (buffer == null) {
            return null;
        }

        BlockPos pos = buffer.readBlockPos();
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        return blockEntity instanceof MultiblockControllerBlockEntity controller ? controller : null;
    }

    private static ContainerData serverData(MultiblockControllerBlockEntity controller) {
        return new CERecipeMenuData(controller, controller::isFormed);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        int x = 31;
        int y = 116;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, x + column * 18, y + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (index >= playerInventoryStart && !moveItemStackTo(original, playerInventoryStart, slots.size(), false)) {
            return ItemStack.EMPTY;
        }

        if (original.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) {
            return true;
        }

        BlockPos pos = blockEntity.getBlockPos();
        return player.level() == blockEntity.getLevel()
                && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    public MultiblockControllerBlockEntity blockEntity() {
        return blockEntity;
    }

    public boolean formed() {
        return data.get(CERecipeMenuData.FORMED) == 1;
    }

    public boolean processing() {
        return data.get(CERecipeMenuData.PROCESSING) == 1;
    }

    public int progress() {
        return data.get(CERecipeMenuData.PROGRESS);
    }

    public int duration() {
        return data.get(CERecipeMenuData.DURATION);
    }

    public int parallel() {
        return Math.max(1, data.get(CERecipeMenuData.PARALLEL));
    }
}
