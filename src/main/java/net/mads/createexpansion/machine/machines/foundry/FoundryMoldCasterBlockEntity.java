package net.mads.createexpansion.machine.machines.foundry;

import net.mads.createexpansion.material.recipes.FoundryCastingRecipes;
import net.mads.createexpansion.material.recipes.CasterTransformationRecipes;
import net.mads.createexpansion.recipe.recipes.foundry.CasterTransformationRecipe;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class FoundryMoldCasterBlockEntity extends BlockEntity {
    private static final int SOLIDIFYING_VISUAL_TICKS = 20;
    private static final int MB_PER_FILL_SECOND = 144;
    private final IItemHandler itemHandler = new CasterItemHandler();
    private final IFluidHandler fluidHandler = new CasterFluidHandler();
    private ItemStack mold = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private ItemStack pendingMold = ItemStack.EMPTY;
    private FluidStack castingFluid = FluidStack.EMPTY;
    private int fillingTicks;
    private int fillingDuration;
    private int pendingFluidTemperature;
    private int progress;
    private int duration;
    private int solidifyingTicks;
    private boolean consumeMoldAfterCast;

    public FoundryMoldCasterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FOUNDRY_MOLD_CASTER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FoundryMoldCasterBlockEntity caster) {
        if (level.isClientSide()) {
            caster.tickClientAnimation();
            return;
        }

        if (caster.fillingTicks > 0) {
            caster.fillingTicks--;
            if (caster.fillingTicks == 0) {
                caster.startCastingAfterFill();
                caster.contentChanged();
            } else {
                caster.progressChanged();
            }
            return;
        }

        if (caster.progress <= 0) {
            if (caster.solidifyingTicks > 0) {
                caster.solidifyingTicks--;
                if (caster.solidifyingTicks == 0) {
                    if (caster.consumeMoldAfterCast) {
                        caster.mold = ItemStack.EMPTY;
                        caster.consumeMoldAfterCast = false;
                    } else if (!caster.pendingMold.isEmpty()) {
                        caster.mold = caster.pendingMold.copy();
                        caster.pendingMold = ItemStack.EMPTY;
                    }
                    caster.castingFluid = FluidStack.EMPTY;
                    caster.contentChanged();
                } else {
                    caster.progressChanged();
                }
            }
            return;
        }

        caster.progress--;
        if (caster.progress > 0) {
            caster.progressChanged();
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

    private void tickClientAnimation() {
        if (fillingTicks > 0) {
            fillingTicks--;
        } else if (progress > 0) {
            progress--;
        } else if (solidifyingTicks > 0) {
            solidifyingTicks--;
        }
    }

    public IItemHandler itemCapability() {
        return itemHandler;
    }

    public IFluidHandler fluidCapability() {
        return fluidHandler;
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
        return (fillingTicks > 0 || hasStoredFluid() || progress > 0 || solidifyingTicks > 0) && !castingFluid.isEmpty();
    }

    public float castingFillForRender(float partialTick) {
        if (castingFluid.isEmpty()) {
            return 0;
        }

        if (fillingDuration > 0 && fillingTicks > 0) {
            return 1.0F - Math.max(0, fillingTicks - partialTick) / (float) fillingDuration;
        }

        if (hasStoredFluid()) {
            int capacity = currentMoldCapacity();
            return capacity <= 0 ? 0 : Math.min(1.0F, castingFluid.getAmount() / (float) capacity);
        }

        return 1.0F;
    }

    public boolean busy() {
        return fillingTicks > 0 || progress > 0 || solidifyingTicks > 0 || !pendingOutput.isEmpty();
    }

    private boolean hasStoredFluid() {
        return !castingFluid.isEmpty() && progress <= 0 && solidifyingTicks <= 0;
    }

    public int tryStartCasting(FluidStack availableFluid, int fluidTemperature) {
        if (busy() || mold.isEmpty()) {
            return 0;
        }

        FoundryCastingRecipes.CastRecipe recipe = FoundryCastingRecipes.recipe(mold, availableFluid);
        if (recipe == null) {
            return tryStartTransformation(availableFluid);
        }

        if (!outputCanAccept(recipe.output())) {
            return 0;
        }

        int moldMeltingPoint = recipe.moldMaterial().meltingPoint();
        if (fluidTemperature > moldMeltingPoint) {
            mold = ItemStack.EMPTY;
            pendingMold = ItemStack.EMPTY;
            castingFluid = FluidStack.EMPTY;
            fillingTicks = 0;
            fillingDuration = 0;
            pendingFluidTemperature = 0;
            solidifyingTicks = 0;
            contentChanged();
            return recipe.shape().amountMb();
        }

        pendingFluidTemperature = fluidTemperature;
        if (fluidTemperature > recipe.moldMaterial().castTemperature()) {
            ItemStack hotMold = FoundryCastingRecipes.hotMoldFor(recipe.moldMaterial(), recipe.shape());
            pendingMold = hotMold.isEmpty() ? ItemStack.EMPTY : hotMold.copyWithCount(1);
        } else {
            pendingMold = ItemStack.EMPTY;
        }

        pendingOutput = recipe.output().copyWithCount(1);
        castingFluid = availableFluid.copyWithAmount(recipe.shape().amountMb());
        solidifyingTicks = 0;
        consumeMoldAfterCast = false;
        duration = recipe.shape().durationTicks();
        progress = 0;
        fillingDuration = fillDurationTicks(recipe.shape().amountMb());
        fillingTicks = fillingDuration;
        contentChanged();
        return recipe.shape().amountMb();
    }

    private int tryStartTransformation(FluidStack availableFluid) {
        CasterTransformationRecipe recipe = CasterTransformationRecipes.recipe(level, mold, availableFluid);
        if (recipe == null || !outputCanAccept(recipe.result())) {
            return 0;
        }

        int amountMb = recipe.fluid().getAmount();
        pendingOutput = recipe.result().copyWithCount(1);
        pendingMold = ItemStack.EMPTY;
        castingFluid = availableFluid.copyWithAmount(amountMb);
        pendingFluidTemperature = 0;
        solidifyingTicks = 0;
        consumeMoldAfterCast = true;
        duration = FoundryCastingRecipes.durationTicks(amountMb);
        progress = 0;
        fillingDuration = fillDurationTicks(amountMb);
        fillingTicks = fillingDuration;
        contentChanged();
        return amountMb;
    }

    private void startCastingAfterFill() {
        fillingDuration = 0;
        progress = duration;
        pendingFluidTemperature = 0;
    }

    private int currentMoldCapacity() {
        if (mold.isEmpty()) {
            return 0;
        }

        FoundryCastingRecipes.CastShape shape = FoundryCastingRecipes.shapeForMold(mold);
        if (shape != null) {
            return shape.amountMb();
        }
        return CasterTransformationRecipes.isTemplate(mold) ? CasterTransformationRecipes.MOLD_AMOUNT_MB : 0;
    }

    public static int fillDurationTicks(int amountMb) {
        return Math.max(1, Math.round(amountMb * (20.0F / MB_PER_FILL_SECOND)));
    }

    public InteractionResult useHeldItem(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            extractToPlayer(player);
            return InteractionResult.SUCCESS;
        }

        if (!mold.isEmpty() || hasStoredFluid() || !isAcceptedMoldItem(held)) {
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

        if (!busy() && !hasStoredFluid() && !mold.isEmpty() && player.addItem(mold.copy())) {
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

    private void progressChanged() {
        setChanged();
        if (level != null && !level.isClientSide() && level.getGameTime() % 5L == 0L) {
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
        tag.putInt("FillingTicks", fillingTicks);
        tag.putInt("FillingDuration", fillingDuration);
        tag.putInt("PendingFluidTemperature", pendingFluidTemperature);
        tag.putInt("Progress", progress);
        tag.putInt("Duration", duration);
        tag.putInt("SolidifyingTicks", solidifyingTicks);
        tag.putBoolean("ConsumeMoldAfterCast", consumeMoldAfterCast);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        mold = tag.contains("Mold") ? ItemStack.parseOptional(registries, tag.getCompound("Mold")) : ItemStack.EMPTY;
        output = tag.contains("Output") ? ItemStack.parseOptional(registries, tag.getCompound("Output")) : ItemStack.EMPTY;
        pendingOutput = tag.contains("PendingOutput") ? ItemStack.parseOptional(registries, tag.getCompound("PendingOutput")) : ItemStack.EMPTY;
        pendingMold = tag.contains("PendingMold") ? ItemStack.parseOptional(registries, tag.getCompound("PendingMold")) : ItemStack.EMPTY;
        castingFluid = tag.contains("CastingFluid") ? FluidStack.parseOptional(registries, tag.getCompound("CastingFluid")) : FluidStack.EMPTY;
        fillingTicks = tag.getInt("FillingTicks");
        fillingDuration = tag.getInt("FillingDuration");
        pendingFluidTemperature = tag.getInt("PendingFluidTemperature");
        progress = tag.getInt("Progress");
        duration = tag.getInt("Duration");
        solidifyingTicks = tag.getInt("SolidifyingTicks");
        consumeMoldAfterCast = tag.getBoolean("ConsumeMoldAfterCast");
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
            if (slot != 1 || stack.isEmpty() || !mold.isEmpty() || !isAcceptedMoldItem(stack)) {
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
            if (slot != 1 || busy() || hasStoredFluid() || !output.isEmpty() || mold.isEmpty()) {
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
            return slot == 1 && isAcceptedMoldItem(stack);
        }
    }

    private final class CasterFluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? castingFluid.copy() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0 || mold.isEmpty() || busy()) {
                return 0;
            }

            FoundryCastingRecipes.CastShape shape = FoundryCastingRecipes.shapeForMold(mold);
            return shape == null ? 0 : shape.amountMb();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 && canAcceptFluid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!canAcceptFluid(resource)) {
                return 0;
            }

            int capacity = getTankCapacity(0);
            if (capacity <= 0) {
                return 0;
            }

            int stored = castingFluid.isEmpty() ? 0 : castingFluid.getAmount();
            int fillAmount = Math.min(resource.getAmount(), capacity - stored);
            if (fillAmount <= 0) {
                return 0;
            }

            if (action.execute()) {
                if (castingFluid.isEmpty()) {
                    castingFluid = resource.copyWithAmount(fillAmount);
                } else {
                    castingFluid.grow(fillAmount);
                }

                if (castingFluid.getAmount() >= capacity) {
                    int fluidTemperature = castingFluidMaterialTemperature();
                    tryStartCasting(castingFluid.copy(), fluidTemperature);
                } else {
                    contentChanged();
                }
            }

            return fillAmount;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }

        private boolean canAcceptFluid(FluidStack stack) {
            if (stack.isEmpty() || mold.isEmpty() || busy()) {
                return false;
            }
            if (!castingFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(castingFluid, stack)) {
                return false;
            }

            int capacity = getTankCapacity(0);
            if (capacity <= 0 || castingFluid.getAmount() >= capacity) {
                return false;
            }

            FluidStack recipeStack = stack.copyWithAmount(capacity);
            FoundryCastingRecipes.CastRecipe recipe = FoundryCastingRecipes.recipe(mold, recipeStack);
            if (recipe != null) {
                return outputCanAccept(recipe.output());
            }
            CasterTransformationRecipe transformation = CasterTransformationRecipes.recipe(level, mold, recipeStack);
            return transformation != null && outputCanAccept(transformation.result());
        }

        private int castingFluidMaterialTemperature() {
            var target = net.mads.createexpansion.material.MaterialLookup.find(castingFluid);
            return target == null ? 0 : target.material().meltingPoint();
        }
    }

    private static boolean isAcceptedMoldItem(ItemStack stack) {
        return FoundryCastingRecipes.isNormalMold(stack) || CasterTransformationRecipes.isTemplate(stack);
    }
}
