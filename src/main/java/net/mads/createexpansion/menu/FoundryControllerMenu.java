package net.mads.createexpansion.menu;

import net.mads.createexpansion.machine.machines.foundry.FoundryControllerBlockEntity;
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
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FoundryControllerMenu extends AbstractContainerMenu {
    private static final int DATA_FORMED = 0;
    private static final int DATA_CAPACITY = 1;
    private static final int DATA_FLUID_AMOUNT = 2;
    private static final int DATA_TEMPERATURE = 3;
    private static final int DATA_MELTING_SLOTS = 4;
    private static final int DATA_COUNT = 5;
    public static final int VISIBLE_MELTING_COLUMNS = 3;
    public static final int VISIBLE_MELTING_ROWS = 5;
    public static final int VISIBLE_MELTING_SLOTS = VISIBLE_MELTING_COLUMNS * VISIBLE_MELTING_ROWS;

    private final FoundryControllerBlockEntity blockEntity;
    private final ContainerData data;
    private int meltingScrollRow;

    public FoundryControllerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, clientBlockEntity(playerInventory, buffer));
    }

    public FoundryControllerMenu(int containerId, Inventory playerInventory, FoundryControllerBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, serverData(blockEntity));
    }

    private FoundryControllerMenu(int containerId, Inventory playerInventory, FoundryControllerBlockEntity blockEntity, ContainerData data) {
        super(MenuRegistry.FOUNDRY_CONTROLLER.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        addDataSlots(data);
        addMeltingSlots();
        addPlayerInventory(playerInventory);
    }

    private static FoundryControllerBlockEntity clientBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        if (buffer == null) {
            return null;
        }

        BlockPos pos = buffer.readBlockPos();
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        return blockEntity instanceof FoundryControllerBlockEntity foundry ? foundry : null;
    }

    private static ContainerData serverData(FoundryControllerBlockEntity controller) {
        if (controller == null) {
            return new SimpleContainerData(DATA_COUNT);
        }

        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_FORMED -> controller.isFormed() ? 1 : 0;
                    case DATA_CAPACITY -> controller.capacityMb();
                    case DATA_FLUID_AMOUNT -> controller.fluidAmount();
                    case DATA_TEMPERATURE -> controller.temperature();
                    case DATA_MELTING_SLOTS -> controller.meltingSlotCount();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    private void addPlayerInventory(Inventory playerInventory) {
        int inventoryX = 10;
        int inventoryY = 136;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9,
                        inventoryX + column * 18,
                        inventoryY + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column,
                    inventoryX + column * 18,
                    inventoryY + 58));
        }
    }

    private void addMeltingSlots() {
        if (blockEntity == null) {
            return;
        }
        int startX = 9;
        int startY = 30;
        for (int row = 0; row < VISIBLE_MELTING_ROWS; row++) {
            for (int column = 0; column < VISIBLE_MELTING_COLUMNS; column++) {
                int visibleIndex = column + row * VISIBLE_MELTING_COLUMNS;
                addSlot(new MeltingSlot(visibleIndex, startX + column * 18, startY + row * 18));
            }
        }
    }

    public boolean scrollMeltingSlots(double delta) {
        if (blockEntity == null) {
            return false;
        }
        int maxRow = Math.max(0, (blockEntity.meltingSlotCount() + VISIBLE_MELTING_COLUMNS - 1) / VISIBLE_MELTING_COLUMNS - VISIBLE_MELTING_ROWS);
        int previous = meltingScrollRow;
        meltingScrollRow = Math.max(0, Math.min(maxRow, meltingScrollRow + (delta < 0 ? 1 : -1)));
        return previous != meltingScrollRow;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            return scrollMeltingSlots(-1);
        }
        if (id == 1) {
            return scrollMeltingSlots(1);
        }
        if (id >= 10000 && id <= 20000 && blockEntity != null && blockEntity.isCreativeTemperatureController()) {
            blockEntity.setCreativeTemperature(id - 10000);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (slot instanceof MeltingSlot) {
            if (!moveItemStackTo(original, VISIBLE_MELTING_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (blockEntity == null || !blockEntity.canInsertMeltingItem(original)) {
                return ItemStack.EMPTY;
            }
            ItemStack remaining = blockEntity.insertMeltingItem(original, false);
            if (remaining.getCount() == original.getCount()) {
                return ItemStack.EMPTY;
            }
            original.setCount(remaining.getCount());
        }

        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
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

    public FoundryControllerBlockEntity blockEntity() {
        return blockEntity;
    }

    public boolean formed() {
        return data.get(DATA_FORMED) == 1;
    }

    public int capacityMb() {
        return data.get(DATA_CAPACITY);
    }

    public int fluidAmount() {
        return data.get(DATA_FLUID_AMOUNT);
    }

    public int temperature() {
        return data.get(DATA_TEMPERATURE);
    }

    public boolean creativeTemperatureController() {
        return blockEntity != null && blockEntity.isCreativeTemperatureController();
    }

    public int meltingScrollRow() {
        return meltingScrollRow;
    }

    public int meltingSlotCount() {
        return data.get(DATA_MELTING_SLOTS);
    }

    public int actualMeltingSlot(int visibleIndex) {
        return meltingScrollRow * VISIBLE_MELTING_COLUMNS + visibleIndex;
    }

    public float meltingProgress(int visibleIndex) {
        if (blockEntity == null) {
            return 0.0F;
        }
        int slot = actualMeltingSlot(visibleIndex);
        int duration = blockEntity.meltingDuration(slot);
        return duration <= 0 ? 0.0F : Math.min(1.0F, (float) blockEntity.meltingProgress(slot) / duration);
    }

    public List<FluidStack> fluids() {
        return blockEntity == null ? List.of() : blockEntity.fluids();
    }

    private class MeltingSlot extends Slot {
        private final int visibleIndex;

        MeltingSlot(int visibleIndex, int x, int y) {
            super(blockEntity.meltingContainer(), visibleIndex, x, y);
            this.visibleIndex = visibleIndex;
        }

        private int actualSlot() {
            return actualMeltingSlot(visibleIndex);
        }

        @Override
        public boolean isActive() {
            return actualSlot() < meltingSlotCount();
        }

        @Override
        public ItemStack getItem() {
            return isActive() ? blockEntity.meltingContainer().getItem(actualSlot()) : ItemStack.EMPTY;
        }

        @Override
        public boolean hasItem() {
            return !getItem().isEmpty();
        }

        @Override
        public void set(ItemStack stack) {
            if (isActive()) {
                blockEntity.meltingContainer().setItem(actualSlot(), stack);
            }
        }

        @Override
        public ItemStack remove(int amount) {
            return isActive() ? blockEntity.meltingContainer().removeItem(actualSlot(), amount) : ItemStack.EMPTY;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isActive() && blockEntity.meltingContainer().canPlaceItem(actualSlot(), stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
