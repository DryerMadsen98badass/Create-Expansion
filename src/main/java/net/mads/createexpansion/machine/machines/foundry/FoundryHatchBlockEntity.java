package net.mads.createexpansion.machine.machines.foundry;

import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class FoundryHatchBlockEntity extends BlockEntity {
    private final IFluidHandler fluidHandler = new HatchFluidHandler();
    private final IItemHandler itemHandler = new BusItemHandler();
    private BlockPos cachedController;

    public FoundryHatchBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FOUNDRY_HATCH.get(), pos, state);
    }

    public IFluidHandler fluidCapability() {
        return fluidHandler;
    }

    @Nullable
    public IItemHandler itemCapability() {
        return hatchType() == FoundryHatchType.INPUT_BUS ? itemHandler : null;
    }

    public FoundryHatchType hatchType() {
        if (getBlockState().getBlock() instanceof FoundryHatchBlock hatch) {
            return hatch.type();
        }
        return FoundryHatchType.INPUT;
    }

    public void attachToController(BlockPos controllerPos) {
        cachedController = controllerPos.immutable();
        setChanged();
    }

    public void clearCachedController() {
        cachedController = null;
        setChanged();
    }

    @Nullable
    public FoundryControllerBlockEntity controller() {
        if (level == null) {
            return null;
        }

        if (cachedController == null) {
            findController();
        }
        if (cachedController == null) {
            return null;
        }

        BlockEntity blockEntity = level.getBlockEntity(cachedController);
        if (blockEntity instanceof FoundryControllerBlockEntity controller && controller.isFormed() && controller.hasAttachedHatch(worldPosition)) {
            return controller;
        }
        cachedController = null;
        return null;
    }

    private void findController() {
        if (level == null) {
            return;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int radius = 12;
        for (int x = worldPosition.getX() - radius; x <= worldPosition.getX() + radius; x++) {
            for (int y = worldPosition.getY() - radius; y <= worldPosition.getY() + radius; y++) {
                for (int z = worldPosition.getZ() - radius; z <= worldPosition.getZ() + radius; z++) {
                    cursor.set(x, y, z);
                    if (level.getBlockState(cursor).getBlock() != BlockRegistry.FOUNDRY_CONTROLLER.get()
                            && level.getBlockState(cursor).getBlock() != BlockRegistry.CREATIVE_FOUNDRY_CONTROLLER.get()) {
                        continue;
                    }
                    BlockEntity blockEntity = level.getBlockEntity(cursor);
                    if (blockEntity instanceof FoundryControllerBlockEntity controller && controller.isFormed() && controller.hasAttachedHatch(worldPosition)) {
                        cachedController = cursor.immutable();
                        return;
                    }
                }
            }
        }
    }

    private final class HatchFluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            FoundryControllerBlockEntity controller = controller();
            return controller == null ? 1 : Math.max(1, controller.fluids().size());
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            FoundryControllerBlockEntity controller = controller();
            if (controller == null) {
                return FluidStack.EMPTY;
            }
            var fluids = controller.fluids();
            return tank >= 0 && tank < fluids.size() ? fluids.get(tank) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            FoundryControllerBlockEntity controller = controller();
            return controller == null ? 0 : controller.capacityMb();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return hatchType().input();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            FoundryControllerBlockEntity controller = controller();
            if (controller == null || !hatchType().input()) {
                return 0;
            }
            return controller.fill(resource, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FoundryControllerBlockEntity controller = controller();
            if (controller == null || !hatchType().output()) {
                return FluidStack.EMPTY;
            }
            return controller.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FoundryControllerBlockEntity controller = controller();
            if (controller == null || !hatchType().output()) {
                return FluidStack.EMPTY;
            }
            return controller.drain(maxDrain, action);
        }
    }

    private final class BusItemHandler implements IItemHandler {
        @Override
        public int getSlots() {
            FoundryControllerBlockEntity controller = controller();
            return controller == null ? 0 : controller.meltingSlotCount();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            FoundryControllerBlockEntity controller = controller();
            if (controller == null || stack.isEmpty()) {
                return stack;
            }
            return controller.insertMeltingItem(stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            FoundryControllerBlockEntity controller = controller();
            return controller != null && controller.canInsertMeltingItem(stack);
        }
    }
}
