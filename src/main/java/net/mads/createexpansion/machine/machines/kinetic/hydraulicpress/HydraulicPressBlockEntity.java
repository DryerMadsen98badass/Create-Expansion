package net.mads.createexpansion.machine.machines.kinetic.hydraulicpress;

import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import net.mads.createexpansion.fluid.IndustrialFluids;
import net.mads.createexpansion.recipe.recipetypes.HydraulicPressingRecipeType;
import net.mads.createexpansion.recipe.recipes.hydraulicpress.HydraulicPressingRecipe;
import net.mads.createexpansion.recipe.recipes.hydraulicpress.HydraulicPressingRecipeInput;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.Optional;
import java.util.List;

public class HydraulicPressBlockEntity extends BlockEntity implements Clearable, IHaveGoggleInformation {
    public static final int STEAM_CAPACITY = 4000;
    public static final int STEAM_PER_BLOW = 2000;
    public static final int DOWN_TICKS = 40;
    public static final int UP_TICKS = 40;
    public static final int CYCLE_TICKS = DOWN_TICKS + UP_TICKS;
    public static final float HEAD_TRAVEL = 1.0F;

    private final ItemStackHandler inputInventory = createInventory();
    private final ItemStackHandler outputInventory = createInventory();
    private final FluidTank steamTank = createSteamTank();
    private final IItemHandler itemCapability = new PressInventoryHandler();
    private final IFluidHandler fluidCapability = new SteamInputHandler();

    private int cycleTicks = -1;
    private int completedBlows;
    private ResourceLocation activeRecipeId;
    private boolean activeOnDepot;

    public HydraulicPressBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.HYDRAULIC_PRESS.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HydraulicPressBlockEntity press) {
        if (level.isClientSide()) {
            press.clientTick();
        } else {
            press.serverTick();
        }
    }

    public IItemHandler itemCapability() {
        return itemCapability;
    }

    public IFluidHandler fluidCapability() {
        return fluidCapability;
    }

    public int steamAmount() {
        return steamTank.getFluidAmount();
    }

    public int completedBlows() {
        return completedBlows;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, steamTank);
    }

    public boolean isRunning() {
        return cycleTicks >= 0;
    }

    public float getRenderedHeadOffset(float partialTick) {
        if (cycleTicks < 0) {
            return 0;
        }
        float ticks = Mth.clamp(cycleTicks + partialTick, 0, CYCLE_TICKS);
        if (ticks <= DOWN_TICKS) {
            float progress = ticks / DOWN_TICKS;
            return progress * progress * progress * HEAD_TRAVEL;
        }
        float progress = (ticks - DOWN_TICKS) / UP_TICKS;
        return (1.0F - progress) * HEAD_TRAVEL;
    }

    public boolean handleHeldItem(Player player, InteractionHand hand, ItemStack held) {
        if (held.isEmpty()) {
            return false;
        }
        if (FluidUtil.getFluidHandler(held.copyWithCount(1)).isPresent()
                && FluidUtil.interactWithFluidHandler(player, hand, fluidCapability)) {
            sync();
            return true;
        }
        return insertHeldItem(player, held);
    }

    public boolean insertHeldItem(Player player, ItemStack held) {
        if (held.isEmpty() || isRunning() || completedBlows > 0 || !canAcceptItem(held)) {
            return false;
        }
        ItemStack inserted = player.getAbilities().instabuild ? held.copyWithCount(1) : held.copy();
        ItemStack remainder = inputInventory.insertItem(0, inserted, false);
        if (remainder.getCount() == inserted.getCount()) {
            return false;
        }
        if (!player.getAbilities().instabuild) {
            held.setCount(remainder.getCount());
        }
        return true;
    }

    public boolean extractToPlayer(Player player) {
        if (isRunning() || completedBlows > 0) {
            return false;
        }
        return extractSlotToPlayer(outputInventory, player) || extractSlotToPlayer(inputInventory, player);
    }

    public void dropContents() {
        if (level == null) {
            return;
        }
        ItemHelper.dropContents(level, worldPosition, inputInventory);
        ItemHelper.dropContents(level, worldPosition, outputInventory);
    }

    private void clientTick() {
        if (cycleTicks >= 0 && cycleTicks < CYCLE_TICKS) {
            cycleTicks++;
        }
    }

    private void serverTick() {
        if (cycleTicks >= 0) {
            cycleTicks++;
            if (cycleTicks == DOWN_TICKS) {
                applyImpact();
            }
            if (cycleTicks >= CYCLE_TICKS) {
                cycleTicks = -1;
                sync();
            }
            return;
        }
        tryStartBlow();
    }

    private void tryStartBlow() {
        DepotBlockEntity depot = depotBelow();
        boolean onDepot = depot != null && !depot.getHeldItem().isEmpty();
        ItemStack input = onDepot ? depot.getHeldItem() : inputInventory.getStackInSlot(0);
        if (level == null || input.isEmpty() || steamTank.getFluidAmount() < STEAM_PER_BLOW) {
            resetProgressIfInputMissing(input);
            return;
        }

        Optional<RecipeHolder<HydraulicPressingRecipe>> found = HydraulicPressingRecipeType.INSTANCE.find(
                new HydraulicPressingRecipeInput(input), level);
        if (found.isEmpty() || onDepot && input.getCount() != 1
                || !onDepot && !canFit(found.get().value().result())) {
            return;
        }

        RecipeHolder<HydraulicPressingRecipe> holder = found.get();
        if (activeRecipeId == null || !activeRecipeId.equals(holder.id())) {
            completedBlows = 0;
            activeRecipeId = holder.id();
        }
        activeOnDepot = onDepot;

        FluidStack drained = steamTank.drain(STEAM_PER_BLOW, FluidAction.EXECUTE);
        if (drained.getAmount() != STEAM_PER_BLOW) {
            return;
        }
        cycleTicks = 0;
        sync();
    }

    private void applyImpact() {
        if (level == null || activeRecipeId == null) {
            completedBlows = 0;
            activeRecipeId = null;
            activeOnDepot = false;
            sync();
            return;
        }

        DepotBlockEntity depot = activeOnDepot ? depotBelow() : null;
        ItemStack input = depot != null ? depot.getHeldItem() : inputInventory.getStackInSlot(0);
        Optional<RecipeHolder<HydraulicPressingRecipe>> found = HydraulicPressingRecipeType.INSTANCE.find(
                new HydraulicPressingRecipeInput(input), level);
        if (found.isEmpty() || !activeRecipeId.equals(found.get().id())) {
            completedBlows = 0;
            activeRecipeId = null;
            activeOnDepot = false;
            sync();
            return;
        }

        HydraulicPressingRecipe recipe = found.get().value();
        completedBlows++;
        boolean outputFits = activeOnDepot ? depot != null && input.getCount() == 1 : canFit(recipe.result());
        if (completedBlows >= recipe.blows() && outputFits) {
            if (activeOnDepot) {
                depot.setHeldItem(recipe.result());
            } else {
                inputInventory.extractItem(0, 1, false);
                outputInventory.insertItem(0, recipe.result(), false);
            }
            completedBlows = 0;
            activeRecipeId = null;
            activeOnDepot = false;
        }

        level.playSound(null, worldPosition, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, 0.9F);
        sync();
    }

    private void resetProgressIfInputMissing(ItemStack input) {
        if (!input.isEmpty() || completedBlows == 0 && activeRecipeId == null) {
            return;
        }
        completedBlows = 0;
        activeRecipeId = null;
        activeOnDepot = false;
        sync();
    }

    private DepotBlockEntity depotBelow() {
        return level != null && level.getBlockEntity(worldPosition.below(2)) instanceof DepotBlockEntity depot
                ? depot
                : null;
    }

    private boolean canAcceptItem(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return false;
        }
        return level.getRecipeManager().getAllRecipesFor(net.mads.createexpansion.registry.RecipeRegistry.HYDRAULIC_PRESSING_RECIPE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .anyMatch(recipe -> recipe.matchesItem(stack));
    }

    private boolean canFit(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack current = outputInventory.getStackInSlot(0);
        if (current.isEmpty()) {
            return result.getCount() <= outputInventory.getSlotLimit(0);
        }
        return ItemStack.isSameItemSameComponents(current, result)
                && current.getCount() + result.getCount() <= Math.min(current.getMaxStackSize(), outputInventory.getSlotLimit(0));
    }

    private boolean extractSlotToPlayer(ItemStackHandler inventory, Player player) {
        ItemStack extracted = inventory.extractItem(0, 64, false);
        if (extracted.isEmpty()) {
            return false;
        }
        if (!player.addItem(extracted)) {
            player.drop(extracted, false);
        }
        return true;
    }

    private ItemStackHandler createInventory() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                sync();
            }
        };
    }

    private FluidTank createSteamTank() {
        return new FluidTank(STEAM_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return isSteam(stack);
            }

            @Override
            protected void onContentsChanged() {
                sync();
            }
        };
    }

    private static boolean isSteam(FluidStack stack) {
        FluidRegistry.RegisteredFluid steam = FluidRegistry.CHEMICAL_FLUIDS.get(IndustrialFluids.STEAM.registryName());
        return steam != null && !stack.isEmpty() && stack.getFluid() == steam.source().get();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("InputInventory", inputInventory.serializeNBT(registries));
        tag.put("OutputInventory", outputInventory.serializeNBT(registries));
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("CycleTicks", cycleTicks);
        tag.putInt("CompletedBlows", completedBlows);
        if (activeRecipeId != null) {
            tag.putString("ActiveRecipe", activeRecipeId.toString());
        }
        tag.putBoolean("ActiveOnDepot", activeOnDepot);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("InputInventory")) {
            inputInventory.deserializeNBT(registries, tag.getCompound("InputInventory"));
        }
        if (tag.contains("OutputInventory")) {
            outputInventory.deserializeNBT(registries, tag.getCompound("OutputInventory"));
        }
        if (tag.contains("SteamTank")) {
            steamTank.readFromNBT(registries, tag.getCompound("SteamTank"));
        }
        cycleTicks = tag.contains("CycleTicks") ? tag.getInt("CycleTicks") : -1;
        completedBlows = Math.max(0, tag.getInt("CompletedBlows"));
        activeRecipeId = tag.contains("ActiveRecipe") ? ResourceLocation.tryParse(tag.getString("ActiveRecipe")) : null;
        activeOnDepot = tag.getBoolean("ActiveOnDepot");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clearContent() {
        inputInventory.setStackInSlot(0, ItemStack.EMPTY);
        outputInventory.setStackInSlot(0, ItemStack.EMPTY);
        completedBlows = 0;
        activeRecipeId = null;
        activeOnDepot = false;
        cycleTicks = -1;
        sync();
    }

    private final class PressInventoryHandler extends CombinedInvWrapper {
        private PressInventoryHandler() {
            super(inputInventory, outputInventory);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return !isRunning()
                    && completedBlows == 0
                    && inputInventory == getHandlerFromIndex(getIndexForSlot(slot))
                    && canAcceptItem(stack)
                    && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isRunning() || completedBlows > 0 || inputInventory == getHandlerFromIndex(getIndexForSlot(slot))) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }

    private final class SteamInputHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? steamTank.getFluid() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? STEAM_CAPACITY : 0;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 && isSteam(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return isSteam(resource) ? steamTank.fill(resource, action) : 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}
