package net.mads.createexpansion.machine.machines.foundry;

import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;

public class FoundryMoldCasterBlockEntity extends BlockEntity {
    private static final int SOLIDIFYING_VISUAL_TICKS = 20;
    private final IItemHandler itemHandler = new CasterItemHandler();
    private ItemStack mold = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private ItemStack pendingMold = ItemStack.EMPTY;
    private FluidStack castingFluid = FluidStack.EMPTY;
    private int progress;
    private int duration;
    private int solidifyingTicks;

    public FoundryMoldCasterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FOUNDRY_MOLD_CASTER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FoundryMoldCasterBlockEntity caster) {
        if (level.isClientSide()) {
            return;
        }

        if (caster.progress <= 0) {
            if (caster.solidifyingTicks > 0) {
                caster.solidifyingTicks--;
                if (caster.solidifyingTicks == 0) {
                    if (!caster.pendingMold.isEmpty()) {
                        caster.mold = caster.pendingMold.copy();
                        caster.pendingMold = ItemStack.EMPTY;
                    }
                    caster.castingFluid = FluidStack.EMPTY;
                }
                caster.contentChanged();
            }
            return;
        }

        caster.progress--;
        if (caster.progress > 0) {
            caster.contentChanged();
            return;
        }

        if (caster.output.isEmpty()) {
            caster.output = caster.pendingOutput.copy();
        } else if (ItemStack.isSameItemSameComponents(caster.output, caster.pendingOutput)) {
            caster.output.grow(caster.pendingOutput.getCount());
        }
        caster.pendingOutput = ItemStack.EMPTY;
        caster.solidifyingTicks = SOLIDIFYING_VISUAL_TICKS;
        caster.duration = 0;
        caster.contentChanged();
    }

    public IItemHandler itemCapability() {
        return itemHandler;
    }

    public ItemStack moldForRender() {
        if (!pendingMold.isEmpty() && solidifyingTicks > 0 && solidifyingTicks <= SOLIDIFYING_VISUAL_TICKS / 2) {
            return pendingMold.copy();
        }
        return mold.copy();
    }

    public ItemStack outputForRender() {
        return output.copy();
    }

    public FluidStack castingFluidForRender() {
        return castingFluid.copy();
    }

    public boolean hasCastingVisual() {
        return (progress > 0 || solidifyingTicks > 0) && !castingFluid.isEmpty();
    }

    public boolean busy() {
        return progress > 0 || solidifyingTicks > 0 || !pendingOutput.isEmpty();
    }

    public int tryStartCasting(FluidStack availableFluid, int fluidTemperature) {
        if (busy() || mold.isEmpty()) {
            return 0;
        }

        FoundryCastingRecipes.CastRecipe recipe = FoundryCastingRecipes.recipe(mold, availableFluid);
        if (recipe == null || !outputCanAccept(recipe.output())) {
            return 0;
        }

        int moldMeltingPoint = recipe.moldMaterial().meltingPoint();
        if (fluidTemperature > moldMeltingPoint) {
            mold = ItemStack.EMPTY;
            pendingMold = ItemStack.EMPTY;
            castingFluid = FluidStack.EMPTY;
            solidifyingTicks = 0;
            contentChanged();
            return recipe.shape().amountMb();
        }

        if (fluidTemperature > recipe.moldMaterial().castTemperature()) {
            ItemStack hotMold = FoundryCastingRecipes.hotMoldFor(recipe.moldMaterial(), recipe.shape());
            pendingMold = hotMold.isEmpty() ? ItemStack.EMPTY : hotMold.copyWithCount(1);
        } else {
            pendingMold = ItemStack.EMPTY;
        }

        pendingOutput = recipe.output().copyWithCount(1);
        castingFluid = availableFluid.copyWithAmount(recipe.shape().amountMb());
        solidifyingTicks = 0;
        duration = recipe.shape().durationTicks();
        progress = duration;
        contentChanged();
        return recipe.shape().amountMb();
    }

    public InteractionResult useHeldItem(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            extractToPlayer(player);
            return InteractionResult.SUCCESS;
        }

        if (!mold.isEmpty() || !FoundryCastingRecipes.isNormalMold(held)) {
            return InteractionResult.PASS;
        }

        mold = held.copyWithCount(1);
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        contentChanged();
        return InteractionResult.SUCCESS;
    }

    public void extractToPlayer(Player player) {
        if (!output.isEmpty() && player.addItem(output.copy())) {
            output = ItemStack.EMPTY;
            contentChanged();
            return;
        }

        if (!busy() && !mold.isEmpty() && player.addItem(mold.copy())) {
            mold = ItemStack.EMPTY;
            contentChanged();
        }
    }

    private boolean outputCanAccept(ItemStack stack) {
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, stack) && output.getCount() < output.getMaxStackSize();
    }

    private void contentChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!mold.isEmpty()) {
            tag.put("Mold", mold.saveOptional(registries));
        }
        if (!output.isEmpty()) {
            tag.put("Output", output.saveOptional(registries));
        }
        if (!pendingOutput.isEmpty()) {
            tag.put("PendingOutput", pendingOutput.saveOptional(registries));
        }
        if (!pendingMold.isEmpty()) {
            tag.put("PendingMold", pendingMold.saveOptional(registries));
        }
        if (!castingFluid.isEmpty()) {
            tag.put("CastingFluid", castingFluid.saveOptional(registries));
        }
        tag.putInt("Progress", progress);
        tag.putInt("Duration", duration);
        tag.putInt("SolidifyingTicks", solidifyingTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        mold = tag.contains("Mold") ? ItemStack.parseOptional(registries, tag.getCompound("Mold")) : ItemStack.EMPTY;
        output = tag.contains("Output") ? ItemStack.parseOptional(registries, tag.getCompound("Output")) : ItemStack.EMPTY;
        pendingOutput = tag.contains("PendingOutput") ? ItemStack.parseOptional(registries, tag.getCompound("PendingOutput")) : ItemStack.EMPTY;
        pendingMold = tag.contains("PendingMold") ? ItemStack.parseOptional(registries, tag.getCompound("PendingMold")) : ItemStack.EMPTY;
        castingFluid = tag.contains("CastingFluid") ? FluidStack.parseOptional(registries, tag.getCompound("CastingFluid")) : FluidStack.EMPTY;
        progress = tag.getInt("Progress");
        duration = tag.getInt("Duration");
        solidifyingTicks = tag.getInt("SolidifyingTicks");
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

    private final class CasterItemHandler implements IItemHandler {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? output.copy() : mold.copy();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot != 1 || stack.isEmpty() || !mold.isEmpty() || !FoundryCastingRecipes.isNormalMold(stack)) {
                return stack;
            }

            ItemStack remaining = stack.copy();
            if (!simulate) {
                mold = remaining.copyWithCount(1);
                contentChanged();
            }
            remaining.shrink(1);
            return remaining;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0) {
                return ItemStack.EMPTY;
            }
            if (slot == 0) {
                if (output.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                int extractedAmount = Math.min(amount, output.getCount());
                ItemStack extracted = output.copyWithCount(extractedAmount);
                if (!simulate) {
                    output.shrink(extractedAmount);
                    if (output.isEmpty()) {
                        output = ItemStack.EMPTY;
                    }
                    contentChanged();
                }
                return extracted;
            }
            if (slot != 1 || busy() || !output.isEmpty() || mold.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack extracted = mold.copyWithCount(1);
            if (!simulate) {
                mold = ItemStack.EMPTY;
                contentChanged();
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 1 && FoundryCastingRecipes.isNormalMold(stack);
        }
    }
}
