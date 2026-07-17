package net.mads.createexpansion.machine.machines.kinetic.sifter;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.mads.createexpansion.recipe.recipes.sifter.SiftingRecipe;
import net.mads.createexpansion.recipe.recipetypes.SiftingRecipeType;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class KineticSifterBlockEntity extends KineticBlockEntity implements Clearable {
    private final ItemStackHandler inputInv;
    private final ItemStackHandler outputInv;
    private final IItemHandler itemCapability;
    private int timer;
    private SiftingRecipe lastRecipe;

    public KineticSifterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputInv = createInventory(1);
        this.outputInv = createInventory(9);
        this.itemCapability = new SifterInventoryHandler();
    }

    public KineticSifterBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistry.KINETIC_SIFTER.get(), pos, state);
    }

    public IItemHandler itemCapability() {
        return itemCapability;
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
        if (getSpeed() == 0 || !isSpeedRequirementFulfilled()) {
            return;
        }
        if (inputInv.getStackInSlot(0).isEmpty()) {
            timer = 0;
            return;
        }

        RecipeWrapper input = new RecipeWrapper(inputInv);
        if (lastRecipe == null || !lastRecipe.matches(input, level)) {
            Optional<RecipeHolder<SiftingRecipe>> recipe = SiftingRecipeType.INSTANCE.find(input, level);
            if (recipe.isEmpty()) {
                timer = 0;
                return;
            }
            lastRecipe = recipe.get().value();
        }

        if (!canFitResults(lastRecipe)) {
            return;
        }
        if (!lastRecipe.canProcessAtRpm(getSpeed())) {
            timer = 0;
            return;
        }

        if (timer <= 0) {
            timer = Math.max(lastRecipe.getProcessingDuration(), 100);
            sendData();
            return;
        }

        timer -= getProcessingSpeed();
        if (timer <= 0) {
            process();
        }
    }

    public boolean insertHeldItem(Player player, ItemStack held) {
        if (level == null || held.isEmpty() || !canProcess(held)) {
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
        tag.put("InputInventory", inputInv.serializeNBT(registries));
        tag.put("OutputInventory", outputInv.serializeNBT(registries));
        super.write(tag, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        timer = tag.getInt("Timer");
        if (tag.contains("InputInventory")) {
            inputInv.deserializeNBT(registries, tag.getCompound("InputInventory"));
        }
        if (tag.contains("OutputInventory")) {
            outputInv.deserializeNBT(registries, tag.getCompound("OutputInventory"));
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
    }

    @Override
    public void clearContent() {
        inputInv.setStackInSlot(0, ItemStack.EMPTY);
        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            outputInv.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    private void process() {
        if (level == null || lastRecipe == null) {
            return;
        }

        RecipeWrapper input = new RecipeWrapper(inputInv);
        if (!lastRecipe.matches(input, level)) {
            Optional<RecipeHolder<SiftingRecipe>> recipe = SiftingRecipeType.INSTANCE.find(input, level);
            if (recipe.isEmpty()) {
                return;
            }
            lastRecipe = recipe.get().value();
        }
        if (!canFitResults(lastRecipe)) {
            return;
        }
        if (!lastRecipe.canProcessAtRpm(getSpeed())) {
            timer = 0;
            return;
        }

        ItemStack inputStack = inputInv.getStackInSlot(0);
        ItemStack craftingRemainingItem = inputStack.getCraftingRemainingItem();
        inputStack.shrink(1);
        inputInv.setStackInSlot(0, inputStack);

        lastRecipe.rollResults(level.random)
                .forEach(stack -> ItemHandlerHelper.insertItemStacked(outputInv, stack, false));
        if (!craftingRemainingItem.isEmpty()) {
            ItemHandlerHelper.insertItemStacked(outputInv, craftingRemainingItem, false);
        }

        timer = 0;
        contentChanged();
    }

    private boolean canFitResults(SiftingRecipe recipe) {
        ItemStackHandler outputTest = new ItemStackHandler(outputInv.getSlots());
        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            outputTest.setStackInSlot(slot, outputInv.getStackInSlot(slot).copy());
        }

        for (ItemStack stack : recipe.getRollableResultsAsItemStacks()) {
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(outputTest, stack.copy(), false);
            if (!remaining.isEmpty()) {
                return false;
            }
        }

        ItemStack remainingItem = inputInv.getStackInSlot(0).getCraftingRemainingItem();
        return remainingItem.isEmpty() || ItemHandlerHelper.insertItemStacked(outputTest, remainingItem, false).isEmpty();
    }

    private boolean canProcess(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }

        ItemStackHandler tester = new ItemStackHandler(1);
        tester.setStackInSlot(0, stack.copyWithCount(1));
        RecipeWrapper input = new RecipeWrapper(tester);
        return lastRecipe != null && lastRecipe.matches(input, level)
                || SiftingRecipeType.INSTANCE.find(input, level).isPresent();
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

    private void contentChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
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

    private class SifterInventoryHandler extends CombinedInvWrapper {
        private SifterInventoryHandler() {
            super(inputInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot))) {
                return false;
            }
            return canProcess(stack) && super.isItemValid(slot, stack);
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
            if (inputInv == getHandlerFromIndex(getIndexForSlot(slot))) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }
}
