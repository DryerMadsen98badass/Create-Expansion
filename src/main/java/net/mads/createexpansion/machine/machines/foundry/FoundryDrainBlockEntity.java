package net.mads.createexpansion.machine.machines.foundry;

import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FoundryDrainBlockEntity extends BlockEntity {
    private static final int AUTO_INTERVAL = 20;
    private int cooldown;
    private int pouringTicks;

    public FoundryDrainBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FOUNDRY_DRAIN.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FoundryDrainBlockEntity drain) {
        if (level.isClientSide()) {
            return;
        }

        if (drain.pouringTicks > 0) {
            drain.pouringTicks--;
            drain.updatePouringState();
            drain.setChanged();
        }

        if (!level.hasNeighborSignal(pos)) {
            drain.cooldown = 0;
            return;
        }

        if (drain.cooldown > 0) {
            drain.cooldown--;
            return;
        }

        drain.cooldown = AUTO_INTERVAL;
        drain.tryPour();
    }

    public boolean pouring() {
        return pouringTicks > 0;
    }

    public void tryPour() {
        if (level == null) {
            return;
        }

        FoundryHatchBlockEntity hatch = outputHatchBeside();
        if (hatch == null) {
            return;
        }

        FoundryControllerBlockEntity controller = hatch.controller();
        if (controller == null) {
            return;
        }

        BlockEntity target = level.getBlockEntity(worldPosition.below());
        if (!(target instanceof FoundryMoldCasterBlockEntity caster)) {
            return;
        }

        IFluidHandler fluidHandler = hatch.fluidCapability();
        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            FluidStack available = fluidHandler.getFluidInTank(tank);
            if (available.isEmpty()) {
                continue;
            }

            int amount = caster.tryStartCasting(available.copy(), controller.temperature());
            if (amount <= 0) {
                continue;
            }

            FluidStack request = available.copyWithAmount(amount);
            FluidStack drained = fluidHandler.drain(request, FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                pouringTicks = FoundryMoldCasterBlockEntity.fillDurationTicks(amount);
                updatePouringState();
                setChanged();
            }
            return;
        }
    }

    private void updatePouringState() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        boolean pouring = pouringTicks > 0;
        if (state.hasProperty(FoundryDrainBlock.POURING) && state.getValue(FoundryDrainBlock.POURING) != pouring) {
            level.setBlock(worldPosition, state.setValue(FoundryDrainBlock.POURING, pouring), 3);
        }
    }

    private FoundryHatchBlockEntity outputHatchBeside() {
        if (level == null) {
            return null;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos hatchPos = worldPosition.relative(direction);
            if (level.getBlockState(hatchPos).getBlock() != BlockRegistry.FOUNDRY_OUTPUT_HATCH.get()) {
                continue;
            }
            BlockEntity blockEntity = level.getBlockEntity(hatchPos);
            if (blockEntity instanceof FoundryHatchBlockEntity hatch) {
                return hatch;
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Cooldown", cooldown);
        tag.putInt("PouringTicks", pouringTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        cooldown = tag.getInt("Cooldown");
        pouringTicks = tag.getInt("PouringTicks");
    }
}
