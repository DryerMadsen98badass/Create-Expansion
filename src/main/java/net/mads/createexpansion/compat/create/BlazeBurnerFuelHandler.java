package net.mads.createexpansion.compat.create;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.mads.createexpansion.recipe.recipetypes.BlazeBurnerFuelRecipeType;
import net.mads.createexpansion.recipe.recipes.blazeburnerrecipes.BlazeBurnerFuelRecipe;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Map;
import java.util.WeakHashMap;

public final class BlazeBurnerFuelHandler {
    public static final int FLUID_CAPACITY = 4000;
    private static final int REFUEL_CHECK_INTERVAL_TICKS = 100;
    private static final int REFUEL_THRESHOLD_TICKS = 1000;
    private static final Map<BlazeBurnerBlockEntity, FallbackState> FALLBACK_STATES = new WeakHashMap<>();

    private BlazeBurnerFuelHandler() {
    }

    public static boolean tryConsumeItemFuel(BlazeBurnerBlockEntity burner, ItemStack stack, boolean simulate) {
        if (burner.getLevel() == null || burner.getLevel().isClientSide || stack.isEmpty()) {
            return false;
        }

        return BlazeBurnerFuelRecipeType.INSTANCE.findItem(stack, burner.getLevel())
                .map(holder -> applyRecipe(burner, holder.value(), simulate, stack))
                .orElse(false);
    }

    public static void tryConsumeBufferedFluid(BlazeBurnerBlockEntity burner) {
        if (burner.getLevel() == null || burner.getLevel().isClientSide || burner.isCreative()) {
            return;
        }
        if (burner.getLevel().getGameTime() % REFUEL_CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        if (burner.getRemainingBurnTime() > REFUEL_THRESHOLD_TICKS) {
            return;
        }
        CEBlazeBurnerExtension extension = CEBlazeBurnerExtension.tryOf(burner);
        if (extension != null && extension.createExpansion$hasPendingCustomFuelTransition()) {
            return;
        }

        FluidStack buffer = getFluidBuffer(burner);
        if (buffer.isEmpty()) {
            return;
        }

        BlazeBurnerFuelRecipeType.INSTANCE.findFluid(buffer, burner.getLevel()).ifPresent(holder -> {
            BlazeBurnerFuelRecipe recipe = holder.value();
            int amount = recipe.fluidAmount();
            if (amount <= 0 || buffer.getAmount() < amount) {
                return;
            }

            buffer.shrink(amount);
            setFluidBuffer(burner, buffer);
            addCustomFuel(burner, recipe.superheated(), recipe.heated());
        });
    }

    public static IFluidHandler fluidCapability(BlazeBurnerBlockEntity burner) {
        return new FluidInputHandler(burner);
    }

    private static boolean applyRecipe(
            BlazeBurnerBlockEntity burner,
            BlazeBurnerFuelRecipe recipe,
            boolean simulate,
            ItemStack input
    ) {
        if (!recipe.matchesItem(input)) {
            return false;
        }
        if (!simulate) {
            applyCustomFuel(burner, recipe.superheated(), recipe.heated());
        }
        return true;
    }

    public static FluidStack getFluidBuffer(BlazeBurnerBlockEntity burner) {
        CEBlazeBurnerExtension extension = CEBlazeBurnerExtension.tryOf(burner);
        if (extension != null) {
            return extension.createExpansion$getFluidBuffer();
        }
        return fallback(burner).fluidBuffer.copy();
    }

    public static void setFluidBuffer(BlazeBurnerBlockEntity burner, FluidStack stack) {
        CEBlazeBurnerExtension extension = CEBlazeBurnerExtension.tryOf(burner);
        if (extension != null) {
            extension.createExpansion$setFluidBuffer(stack);
            return;
        }
        fallback(burner).fluidBuffer = stack.isEmpty() ? FluidStack.EMPTY : stack.copy();
    }

    public static void applyCustomFuel(BlazeBurnerBlockEntity burner, int superheatedTicks, int heatedTicks) {
        CEBlazeBurnerExtension extension = CEBlazeBurnerExtension.tryOf(burner);
        if (extension != null) {
            extension.createExpansion$applyCustomFuel(superheatedTicks, heatedTicks);
            return;
        }
        // If the mixin config failed to load, avoid crashing. The fallback buffer still accepts
        // fluid, but custom heat cannot be applied without access to Create's protected fuel state.
        fallback(burner).heatedAfterSuperheated = Math.max(0, heatedTicks);
    }

    public static void addCustomFuel(BlazeBurnerBlockEntity burner, int superheatedTicks, int heatedTicks) {
        CEBlazeBurnerExtension extension = CEBlazeBurnerExtension.tryOf(burner);
        if (extension != null) {
            extension.createExpansion$addCustomFuel(superheatedTicks, heatedTicks);
            return;
        }
        fallback(burner).heatedAfterSuperheated = Math.max(0, heatedTicks);
    }

    private static FallbackState fallback(BlazeBurnerBlockEntity burner) {
        return FALLBACK_STATES.computeIfAbsent(burner, ignored -> new FallbackState());
    }

    private static final class FallbackState {
        private FluidStack fluidBuffer = FluidStack.EMPTY;
        private int heatedAfterSuperheated;
    }

    private static final class FluidInputHandler implements IFluidHandler {
        private final BlazeBurnerBlockEntity burner;

        private FluidInputHandler(BlazeBurnerBlockEntity burner) {
            this.burner = burner;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? BlazeBurnerFuelHandler.getFluidBuffer(burner) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? FLUID_CAPACITY : 0;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if (tank != 0 || stack.isEmpty() || burner.getLevel() == null) {
                return false;
            }
            return BlazeBurnerFuelRecipeType.INSTANCE.findFluid(stack.copyWithAmount(FLUID_CAPACITY), burner.getLevel()).isPresent();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !isFluidValid(0, resource)) {
                return 0;
            }

            FluidStack stored = BlazeBurnerFuelHandler.getFluidBuffer(burner);
            if (!stored.isEmpty() && !FluidStack.isSameFluidSameComponents(stored, resource)) {
                return 0;
            }

            int space = FLUID_CAPACITY - stored.getAmount();
            int accepted = Math.min(space, resource.getAmount());
            if (accepted <= 0) {
                return 0;
            }

            if (action.execute()) {
                FluidStack updated = stored.isEmpty() ? resource.copyWithAmount(accepted) : stored.copy();
                if (!stored.isEmpty()) {
                    updated.grow(accepted);
                }
                BlazeBurnerFuelHandler.setFluidBuffer(burner, updated);
                BlazeBurnerFuelHandler.tryConsumeBufferedFluid(burner);
                burner.setChanged();
            }
            return accepted;
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
