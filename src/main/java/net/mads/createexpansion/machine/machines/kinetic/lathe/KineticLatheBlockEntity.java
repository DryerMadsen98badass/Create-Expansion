package net.mads.createexpansion.machine.machines.kinetic.lathe;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.mads.createexpansion.recipe.recipes.lathe.TurningRecipe;
import net.mads.createexpansion.recipe.recipes.lathe.TurningRecipeInput;
import net.mads.createexpansion.recipe.recipetypes.TurningRecipeType;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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

import java.util.List;
import java.util.Optional;

public class KineticLatheBlockEntity extends KineticBlockEntity implements Clearable {
    private final ItemStackHandler inputInv;
    private final ItemStackHandler outputInv;
    private final IItemHandler itemCapability;
    private int timer;
    private TurningRecipe lastRecipe;

    public KineticLatheBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputInv = createInventory(1);
        this.outputInv = createInventory(1);
        this.itemCapability = new LatheInventoryHandler();
    }

    public KineticLatheBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityRegistry.KINETIC_LATHE.get(), pos, state);
    }

    public IItemHandler itemCapability() {
        return itemCapability;
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
        if (getSpeed() == 0 || !isSpeedRequirementFulfilled()) {
            timer = 0;
            return;
        }

        TurningRecipeInput input = recipeInput();
        if (input.isEmpty()) {
            timer = 0;
            return;
        }

        if (lastRecipe == null || !lastRecipe.matches(input, level)) {
            Optional<RecipeHolder<TurningRecipe>> recipe = TurningRecipeType.INSTANCE.find(input, level);
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

        TurningRecipeInput input = recipeInput();
        if (!lastRecipe.matches(input, level)) {
            Optional<RecipeHolder<TurningRecipe>> recipe = TurningRecipeType.INSTANCE.find(input, level);
            if (recipe.isEmpty()) {
                timer = 0;
                return;
            }
            lastRecipe = recipe.get().value();
        }
        if (!canProcess(lastRecipe, input)) {
            return;
        }

        ItemStack inputStack = inputInv.getStackInSlot(0);
        ItemStack craftingRemainingItem = inputStack.getCraftingRemainingItem();
        inputStack.shrink(1);
        inputInv.setStackInSlot(0, inputStack);
        if (!craftingRemainingItem.isEmpty()) {
            ItemHandlerHelper.insertItemStacked(outputInv, craftingRemainingItem, false);
        }

        lastRecipe.rollResults(level.random)
                .forEach(stack -> ItemHandlerHelper.insertItemStacked(outputInv, stack, false));

        timer = 0;
        contentChanged();
    }

    private boolean canProcess(TurningRecipe recipe, TurningRecipeInput input) {
        return recipe.matches(input, level) && canFitResults(recipe);
    }

    private boolean canFitResults(TurningRecipe recipe) {
        ItemStackHandler outputTest = new ItemStackHandler(outputInv.getSlots());
        for (int slot = 0; slot < outputInv.getSlots(); slot++) {
            outputTest.setStackInSlot(slot, outputInv.getStackInSlot(slot).copy());
        }

        for (ItemStack stack : recipe.possibleResults()) {
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(outputTest, stack.copy(), false);
            if (!remaining.isEmpty()) {
                return false;
            }
        }

        ItemStack remainingItem = inputInv.getStackInSlot(0).getCraftingRemainingItem();
        return remainingItem.isEmpty() || ItemHandlerHelper.insertItemStacked(outputTest, remainingItem, false).isEmpty();
    }

    private boolean canAcceptItem(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }
        return level.getRecipeManager().getAllRecipesFor(RecipeRegistry.TURNING_RECIPE_TYPE.get()).stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.matchesItem(stack));
    }

    private TurningRecipeInput recipeInput() {
        return new TurningRecipeInput(inputInv.getStackInSlot(0), Math.round(Math.abs(getSpeed())));
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

    private class LatheInventoryHandler extends CombinedInvWrapper {
        private LatheInventoryHandler() {
            super(inputInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot))) {
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
            if (inputInv == getHandlerFromIndex(getIndexForSlot(slot))) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }
}
