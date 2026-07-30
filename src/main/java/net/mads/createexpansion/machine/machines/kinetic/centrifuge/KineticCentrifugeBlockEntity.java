package net.mads.createexpansion.machine.machines.kinetic.centrifuge;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugingRecipe;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugingRecipeInput;
import net.mads.createexpansion.recipe.recipetypes.CentrifugingRecipeType;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.List;
import java.util.Optional;

public class KineticCentrifugeBlockEntity extends KineticBlockEntity implements Clearable {
    public static final int REQUIRED_BASINS = 4;
    private static final int FLUID_CAPACITY = 4000;

    private final ItemStackHandler inputInv;
    private final ItemStackHandler outputInv;
    private final FluidTank inputTank;
    private final FluidTank outputTank;
    private final IItemHandler itemCapability;
    private final IFluidHandler fluidCapability;
    private int timer;
    private int mountedBasins;
    private CentrifugingRecipe lastRecipe;

    public KineticCentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputInv = createInventory(1);
        this.outputInv = createInventory(9);
        this.inputTank = createTank();
        this.outputTank = createTank();
        this.itemCapability = new CentrifugeInventoryHandler();
        this.fluidCapability = new CentrifugeFluidHandler();
    }

    public KineticCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistry.KINETIC_CENTRIFUGE.get(), pos, state);
    }

    public IItemHandler itemCapability() {
        return itemCapability;
    }

    public IFluidHandler fluidCapability() {
        return fluidCapability;
    }

    public int mountedBasins() {
        return mountedBasins;
    }

    public boolean hasAllBasins() {
        return mountedBasins >= REQUIRED_BASINS;
    }

    public boolean isSpinning() {
        return Math.abs(getSpeed()) > 0.001F;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide()) {
            return;
        }
        if (!hasAllBasins() || getSpeed() == 0 || !isSpeedRequirementFulfilled()) {
            timer = 0;
            return;
        }

        CentrifugingRecipeInput input = recipeInput();
        if (input.isEmpty()) {
            timer = 0;
            return;
        }

        if (lastRecipe == null || !lastRecipe.matches(input, level)) {
            Optional<RecipeHolder<CentrifugingRecipe>> recipe = CentrifugingRecipeType.INSTANCE.find(input, level);
            if (recipe.isEmpty()) {
                timer = 0;
                return;
            }
            lastRecipe = recipe.get().value();
        }

        if (!canProcess(lastRecipe, input)) {
            return;
        }

        if (timer <= 0) {
            timer = Math.max(lastRecipe.processingDuration(), 100);
            sendData();
            return;
        }

        timer -= getProcessingSpeed();
        if (timer <= 0) {
            process();
        }
    }

    public boolean handleHeldItem(Player player, InteractionHand hand, ItemStack held) {
        if (level == null || held.isEmpty() || isSpinning()) {
            return false;
        }

        if (held.is(AllBlocks.BASIN.get().asItem()) && mountedBasins < REQUIRED_BASINS) {
            mountedBasins++;
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            contentChanged();
            return true;
        }

        if (FluidUtil.getFluidHandler(held.copyWithCount(1)).isPresent()
                && FluidUtil.interactWithFluidHandler(player, hand, fluidCapability)) {
            contentChanged();
            return true;
        }

        return insertHeldItem(player, held);
    }

    public boolean insertHeldItem(Player player, ItemStack held) {
        if (level == null || held.isEmpty() || isSpinning() || !canAcceptItem(held)) {
            return false;
        }

        if (player.getAbilities().instabuild) {
            ItemStack single = held.copyWithCount(1);
            return inputInv.insertItem(0, single, false).isEmpty();
        }

        ItemStack remainder = inputInv.insertItem(0, held.copy(), false);
        if (remainder.getCount() == held.getCount()) {
            return false;
        }
        held.setCount(remainder.getCount());
        return true;
    }

    public boolean extractToPlayer(Player player) {
        if (isSpinning()) {
            return false;
        }

        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            if (extractSlotToPlayer(outputInv, slot, player)) {
                return true;
            }
        }
        return extractSlotToPlayer(inputInv, 0, player);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("Timer", timer);
        tag.putInt("MountedBasins", mountedBasins);
        tag.put("InputInventory", inputInv.serializeNBT(registries));
        tag.put("OutputInventory", outputInv.serializeNBT(registries));
        tag.put("InputTank", inputTank.writeToNBT(registries, new CompoundTag()));
        tag.put("OutputTank", outputTank.writeToNBT(registries, new CompoundTag()));
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        timer = tag.getInt("Timer");
        mountedBasins = Mth.clamp(tag.getInt("MountedBasins"), 0, REQUIRED_BASINS);
        if (tag.contains("InputInventory")) {
            inputInv.deserializeNBT(registries, tag.getCompound("InputInventory"));
        }
        if (tag.contains("OutputInventory")) {
            outputInv.deserializeNBT(registries, tag.getCompound("OutputInventory"));
        }
        if (tag.contains("InputTank")) {
            inputTank.readFromNBT(registries, tag.getCompound("InputTank"));
        }
        if (tag.contains("OutputTank")) {
            outputTank.readFromNBT(registries, tag.getCompound("OutputTank"));
        }
        super.read(tag, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputInv);
        ItemHelper.dropContents(level, worldPosition, outputInv);
        if (level != null && mountedBasins > 0) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
                    new ItemStack(AllBlocks.BASIN.get(), mountedBasins));
        }
    }

    @Override
    public void clearContent() {
        inputInv.setStackInSlot(0, ItemStack.EMPTY);
        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            outputInv.setStackInSlot(slot, ItemStack.EMPTY);
        }
        inputTank.setFluid(FluidStack.EMPTY);
        outputTank.setFluid(FluidStack.EMPTY);
    }

    private void process() {
        if (level == null || lastRecipe == null) {
            return;
        }

        CentrifugingRecipeInput input = recipeInput();
        if (!lastRecipe.matches(input, level)) {
            Optional<RecipeHolder<CentrifugingRecipe>> recipe = CentrifugingRecipeType.INSTANCE.find(input, level);
            if (recipe.isEmpty()) {
                timer = 0;
                return;
            }
            lastRecipe = recipe.get().value();
        }
        if (!canProcess(lastRecipe, input)) {
            return;
        }

        if (lastRecipe.consumesItem()) {
            ItemStack inputStack = inputInv.getStackInSlot(0);
            ItemStack craftingRemainingItem = inputStack.getCraftingRemainingItem();
            inputStack.shrink(lastRecipe.consumedItemCount());
            inputInv.setStackInSlot(0, inputStack);
            if (!craftingRemainingItem.isEmpty()) {
                ItemHandlerHelper.insertItemStacked(outputInv, craftingRemainingItem, false);
            }
        }

        int fluidAmount = lastRecipe.consumedFluidAmount();
        if (fluidAmount > 0) {
            inputTank.drain(fluidAmount, FluidAction.EXECUTE);
        }

        lastRecipe.rollItemResults(level.random)
                .forEach(stack -> ItemHandlerHelper.insertItemStacked(outputInv, stack, false));
        for (FluidStack stack : lastRecipe.fluidResults()) {
            outputTank.fill(stack, FluidAction.EXECUTE);
        }

        timer = 0;
        contentChanged();
    }

    private boolean canProcess(CentrifugingRecipe recipe, CentrifugingRecipeInput input) {
        return recipe.matches(input, level) && canFitItemResults(recipe) && canFitFluidResults(recipe);
    }

    private boolean canFitItemResults(CentrifugingRecipe recipe) {
        ItemStackHandler outputTest = new ItemStackHandler(outputInv.getSlots());
        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            outputTest.setStackInSlot(slot, outputInv.getStackInSlot(slot).copy());
        }

        for (ItemStack stack : recipe.possibleItemResults()) {
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(outputTest, stack.copy(), false);
            if (!remaining.isEmpty()) {
                return false;
            }
        }

        ItemStack remainingItem = inputInv.getStackInSlot(0).getCraftingRemainingItem();
        return remainingItem.isEmpty() || ItemHandlerHelper.insertItemStacked(outputTest, remainingItem, false).isEmpty();
    }

    private boolean canFitFluidResults(CentrifugingRecipe recipe) {
        FluidStack simulated = outputTank.getFluid().copy();
        int capacity = outputTank.getCapacity();
        for (FluidStack output : recipe.fluidResults()) {
            if (output.isEmpty()) {
                continue;
            }
            if (simulated.isEmpty()) {
                simulated = output.copy();
                if (simulated.getAmount() > capacity) {
                    return false;
                }
                continue;
            }
            if (!FluidStack.isSameFluidSameComponents(simulated, output)) {
                return false;
            }
            simulated.grow(output.getAmount());
            if (simulated.getAmount() > capacity) {
                return false;
            }
        }
        return true;
    }

    private boolean canAcceptItem(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }
        return level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CENTRIFUGING_RECIPE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.matchesItem(stack));
    }

    private boolean canAcceptFluid(FluidStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }
        return level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CENTRIFUGING_RECIPE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.acceptsFluid(stack));
    }

    private CentrifugingRecipeInput recipeInput() {
        return new CentrifugingRecipeInput(inputInv.getStackInSlot(0), inputTank.getFluid(), Math.round(Math.abs(getSpeed())));
    }

    private int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16F), 1, 512);
    }

    private ItemStackHandler createInventory(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                contentChanged();
            }
        };
    }

    private FluidTank createTank() {
        return new FluidTank(FLUID_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                contentChanged();
            }
        };
    }

    private void contentChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            sendData();
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    private boolean extractSlotToPlayer(ItemStackHandler inventory, int slot, Player player) {
        ItemStack extracted = inventory.extractItem(slot, 64, false);
        if (extracted.isEmpty()) {
            return false;
        }
        if (!player.addItem(extracted)) {
            player.drop(extracted, false);
        }
        return true;
    }

    private class CentrifugeInventoryHandler extends CombinedInvWrapper {
        private CentrifugeInventoryHandler() {
            super(inputInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (isSpinning() || outputInv == getHandlerFromIndex(getIndexForSlot(slot))) {
                return false;
            }
            return canAcceptItem(stack) && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isSpinning() || inputInv == getHandlerFromIndex(getIndexForSlot(slot))) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }

    private class CentrifugeFluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return 2;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? inputTank.getFluid() : outputTank.getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? inputTank.getCapacity() : outputTank.getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 && !isSpinning() && canAcceptFluid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (isSpinning() || resource.isEmpty() || !canAcceptFluid(resource)) {
                return 0;
            }
            return inputTank.fill(resource, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (isSpinning() || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            return outputTank.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (isSpinning() || maxDrain <= 0) {
                return FluidStack.EMPTY;
            }
            return outputTank.drain(maxDrain, action);
        }
    }
}
