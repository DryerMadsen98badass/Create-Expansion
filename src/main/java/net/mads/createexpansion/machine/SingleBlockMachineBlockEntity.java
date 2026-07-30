package net.mads.createexpansion.machine;

import net.mads.createexpansion.recipe.CEChancedItemOutput;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeInput;
import net.mads.createexpansion.recipe.CERecipeLookup;
import net.mads.createexpansion.energy.CEEnergyContainer;
import net.mads.createexpansion.energy.CEEnergyStorage;
import net.mads.createexpansion.fluid.IndustrialFluids;
import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.menu.SingleBlockMachineMenu;
import net.mads.createexpansion.machine.interaction.InteractionContext;
import net.mads.createexpansion.machine.interaction.InteractionPhase;
import net.mads.createexpansion.machine.interaction.InteractionRuntime;
import net.mads.createexpansion.machine.interaction.InteractionWearStore;
import net.mads.createexpansion.machine.interaction.MachineModifier;
import net.mads.createexpansion.machine.interaction.ConditionFailure;
import net.mads.createexpansion.machine.runtime.CERecipeExecution;
import net.mads.createexpansion.machine.runtime.CERecipeLogic;
import net.mads.createexpansion.machine.runtime.CERecipeLogicHost;
import net.mads.createexpansion.machine.runtime.CERecipeLogicMachine;
import net.mads.createexpansion.machine.runtime.CERecipeTickResult;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SingleBlockMachineBlockEntity extends BlockEntity implements Clearable, MenuProvider, CERecipeLogicMachine {
    private static final int BLOCKED_VENT_EXPLOSION_TICKS = 100;
    private static final int MAX_ITEM_INPUT_SLOTS = 16;
    private static final int MAX_ITEM_OUTPUT_SLOTS = 16;
    private static final int MAX_FLUID_INPUT_TANKS = 8;
    private static final int MAX_FLUID_OUTPUT_TANKS = 8;
    private static final int RECIPE_FLUID_TANK_CAPACITY = 16_000;

    private final ItemStackHandler inputItems = createInventory(MAX_ITEM_INPUT_SLOTS);
    private final ItemStackHandler outputItems = createInventory(MAX_ITEM_OUTPUT_SLOTS);
    private final IItemHandler itemCapability = new MachineItemHandler();
    private final FluidTank[] inputFluids = createFluidTanks(MAX_FLUID_INPUT_TANKS);
    private final FluidTank[] outputFluids = createFluidTanks(MAX_FLUID_OUTPUT_TANKS);
    private final IFluidHandler fluidCapability = new MachineFluidHandler();
    private final FluidTank steamTank = createSteamTank();
    private final InteractionWearStore interactionWear = new InteractionWearStore();
    private final CERecipeLogic recipeLogic = new CERecipeLogic(new RecipeHost());
    private final CEEnergyStorage ceContainer = new CEEnergyStorage(
            MachineTier.LV,
            Long.MAX_VALUE,
            this::acceptsEnergyInput,
            this::allowsEnergyOutput,
            ignored -> setChangedAndSync(),
            ignored -> explodeFromOvervoltage(),
            () -> level == null ? Long.MIN_VALUE : level.getGameTime()
    ) {
        @Override
        public MachineTier tier() {
            SingleBlockMachineInstance instance = instance();
            return instance == null || !instance.tier().isElectric()
                    ? MachineTier.LV
                    : instance.tier();
        }

        @Override
        public long getEnergyCapacity() {
            return energyCapacity();
        }

        @Override
        public long getEnergyStored() {
            return energyStored;
        }

        @Override
        public long getInputVoltage() {
            return MachineTierStats.ceTier(tier());
        }

        @Override
        public long getOutputVoltage() {
            return MachineTierStats.ceTier(tier());
        }

        @Override
        public long voltage() {
            return getInputVoltage();
        }

        @Override
        public long changeEnergy(long differenceAmount) {
            long previous = energyStored;
            energyStored = Math.max(0L, Math.min(energyStored + differenceAmount, getEnergyCapacity()));
            if (energyStored != previous) {
                setChangedAndSync();
            }
            return energyStored - previous;
        }
    };

    private int blockedVentTicks;
    private long energyStored;
    private int overlayFrame;
    private int overlayFrameTicks;
    private int recipeSyncCooldown;
    private ResourceLocation preferredRecipeId;

    public SingleBlockMachineBlockEntity(
            BlockPos pos,
            BlockState blockState
    ) {
        super(
                BlockEntityRegistry.SINGLE_BLOCK_MACHINE.get(),
                pos,
                blockState
        );
    }

    public void serverTick() {
        Level level = getLevel();
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof SingleBlockMachineBlock block)) {
            return;
        }

        SingleBlockMachineInstance instance = block.instance();
        recipeLogic.serverTick();
        boolean active = recipeLogic.isActive();

        int nextOverlayFrame = active ? tickOverlayFrame(instance) : 0;
        if (state.getValue(SingleBlockMachineBlock.ACTIVE) != active
                || state.getValue(SingleBlockMachineBlock.OVERLAY_FRAME) != nextOverlayFrame) {
            state = state
                    .setValue(SingleBlockMachineBlock.ACTIVE, active)
                    .setValue(SingleBlockMachineBlock.OVERLAY_FRAME, nextOverlayFrame);
            level.setBlock(getBlockPos(), state, Block.UPDATE_ALL);
        }

        if (instance == null
                || instance.definition().power() != SingleBlockMachinePower.STEAM
                || !active) {
            blockedVentTicks = 0;
            return;
        }

        if (!isVentBlocked(level, getBlockPos().above())) {
            blockedVentTicks = 0;
            return;
        }

        blockedVentTicks++;
        if (blockedVentTicks >= BLOCKED_VENT_EXPLOSION_TICKS) {
            blockedVentTicks = 0;
            level.explode(
                    null,
                    getBlockPos().getX() + 0.5D,
                    getBlockPos().getY() + 0.5D,
                    getBlockPos().getZ() + 0.5D,
                    instance.tier().steamExplosionPower(),
                    Level.ExplosionInteraction.TNT
            );
        }
    }

    private void explodeFromOvervoltage() {
        if (level == null || level.isClientSide()) {
            return;
        }

        level.explode(
                null,
                getBlockPos().getX() + 0.5D,
                getBlockPos().getY() + 0.5D,
                getBlockPos().getZ() + 0.5D,
                4.0F,
                Level.ExplosionInteraction.TNT
        );
    }

    public IItemHandler itemCapability(Direction side) {
        if (side == Direction.UP) {
            return null;
        }

        return itemCapability;
    }

    public IFluidHandler fluidCapability(Direction side) {
        if (side == Direction.UP) {
            return null;
        }

        SingleBlockMachineInstance instance = instance();
        if (instance == null
                || (instance.definition().resource() != SingleBlockMachineResource.STEAM
                && inputFluidSlotCount() <= 0
                && outputFluidSlotCount() <= 0)) {
            return null;
        }

        return fluidCapability;
    }

    public CEEnergyContainer ceContainer() {
        return ceContainer;
    }

    public ItemStackHandler inputItems() {
        return inputItems;
    }

    public ItemStackHandler outputItems() {
        return outputItems;
    }

    public List<FluidTank> inputFluidTanks() {
        return visibleTanks(inputFluids, inputFluidSlotCount());
    }

    public List<FluidTank> outputFluidTanks() {
        return visibleTanks(outputFluids, outputFluidSlotCount());
    }

    public int itemInputSlotCount() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? 0 : Math.min(MAX_ITEM_INPUT_SLOTS, instance.definition().slots().itemInputs());
    }

    public int itemOutputSlotCount() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? 0 : Math.min(MAX_ITEM_OUTPUT_SLOTS, instance.definition().slots().itemOutputs());
    }

    public int inputFluidSlotCount() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? 0 : Math.min(MAX_FLUID_INPUT_TANKS, instance.definition().slots().fluidInputs());
    }

    public int outputFluidSlotCount() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? 0 : Math.min(MAX_FLUID_OUTPUT_TANKS, instance.definition().slots().fluidOutputs());
    }

    public int steamStored() {
        return steamTank.getFluidAmount();
    }

    public int steamCapacity() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? 0 : instance.steamCapacity();
    }

    public int steamUsage() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? 0 : instance.steamUsage();
    }

    public long energyStored() {
        return energyStored;
    }

    public long energyCapacity() {
        SingleBlockMachineInstance instance = instance();
        if (instance == null || instance.definition().power() != SingleBlockMachinePower.ELECTRIC) {
            return 0;
        }

        return MachineTierStats.ceCapacity(instance.tier());
    }

    public int energyUsage() {
        SingleBlockMachineInstance instance = instance();
        if (instance == null) {
            return 0;
        }

        return instance.definition().cet();
    }

    private boolean acceptsEnergyInput() {
        SingleBlockMachineInstance instance = instance();
        return instance != null
                && instance.definition().resource() == SingleBlockMachineResource.ENERGY
                && instance.definition().resourceMode() == SingleBlockMachineResourceMode.CONSUMES;
    }

    private boolean allowsEnergyOutput() {
        SingleBlockMachineInstance instance = instance();
        return instance != null
                && instance.definition().resource() == SingleBlockMachineResource.ENERGY
                && instance.definition().resourceMode() == SingleBlockMachineResourceMode.PRODUCES;
    }

    public int progress() {
        return recipeLogic.progress();
    }

    public int progressTotal() {
        return recipeLogic.duration();
    }

    public ProgressBar progressBar() {
        SingleBlockMachineInstance instance = instance();
        return instance == null ? ProgressBar.ARROW : instance.definition().resolvedProgressBar();
    }

    public String activeRecipeDisplay() {
        CERecipeExecution execution = recipeLogic.execution();
        return execution == null ? "" : execution.recipeType().toString();
    }

    private int tickOverlayFrame(SingleBlockMachineInstance instance) {
        int frames = instance == null ? 0 : instance.definition().activeOverlays().size();
        if (frames <= 1) {
            overlayFrame = 0;
            overlayFrameTicks = 0;
            return 0;
        }

        overlayFrameTicks++;
        if (overlayFrameTicks >= 5) {
            overlayFrameTicks = 0;
            overlayFrame = (overlayFrame + 1) % Math.min(frames, 10);
        }

        return overlayFrame;
    }

    private Optional<RecipeHolder<CERecipe>> findRecipe(Level level, SingleBlockMachineInstance instance) {
        CERecipeInput input = recipeInput(instance);
        InteractionContext context = interactionContext(level, instance);

        if (preferredRecipeId != null) {
            Optional<RecipeHolder<CERecipe>> preferred = CERecipeLookup.preferred(
                            level.getRecipeManager(),
                            preferredRecipeId,
                            java.util.Set.copyOf(instance.definition().recipeTypes())
                    )
                    .filter(holder -> holder.value().matches(input, level))
                    .filter(holder -> startInteractionsReady(instance, holder.value(), context));

            if (preferred.isPresent()) {
                return preferred;
            }
        }

        return CERecipeLookup.candidatesByTypes(level.getRecipeManager(), instance.definition().recipeTypes(), input)
                .stream()
                .filter(holder -> holder.value().matches(input, level))
                .filter(holder -> startInteractionsReady(instance, holder.value(), context))
                .sorted(java.util.Comparator.comparing(holder -> holder.id().toString()))
                .findFirst();
    }

    private boolean startInteractionsReady(SingleBlockMachineInstance instance, CERecipe recipe, InteractionContext context) {
        return InteractionRuntime.conditionsMatch(instance.definition().conditions(), context, InteractionPhase.ON_START)
                && InteractionRuntime.conditionsMatch(recipe.conditions(), context, InteractionPhase.ON_START)
                && InteractionRuntime.interactionsMatch(instance.definition().blockInteractions(), context, InteractionPhase.ON_START)
                && InteractionRuntime.interactionsMatch(recipe.blockInteractions(), context, InteractionPhase.ON_START)
                && InteractionRuntime.interactionsMatch(instance.definition().blockInteractions(), context, InteractionPhase.ON_COMPLETE)
                && InteractionRuntime.interactionsMatch(recipe.blockInteractions(), context, InteractionPhase.ON_COMPLETE);
    }

    private CERecipeInput recipeInput(SingleBlockMachineInstance instance) {
        return new CERecipeInput(
                visibleItemInputs(),
                visibleFluidInputs(),
                Optional.empty(),
                java.util.Set.of(),
                Optional.of(instance.tier().isSteam() ? MachineTier.ULV : instance.tier()),
                Optional.empty(),
                Optional.of(instance.tier().isSteam() ? MachineTier.ULV : instance.tier()),
                0,
                0
        );
    }

    private static int adjustedDuration(SingleBlockMachineInstance instance, CERecipe recipe) {
        int duration = recipe.runtimeDuration(instance.tier().isSteam() ? MachineTier.ULV : instance.tier(), 0);

        if (instance.tier().isSteam()) {
            duration *= instance.tier().steamDurationMultiplier();
        }

        return Math.max(1, duration);
    }

    private static int resourcePerTick(SingleBlockMachineInstance instance, CERecipe recipe) {
        int amount;

        if (instance.definition().resource() == SingleBlockMachineResource.STEAM) {
            amount = instance.steamUsage();
        } else if (instance.definition().resource() == SingleBlockMachineResource.ENERGY) {
            amount = recipe.runtimeCEt(instance.tier());

            if (amount <= 0) {
                amount = instance.definition().cet();
            }
        } else {
            return 0;
        }

        return instance.definition().resourceMode() == SingleBlockMachineResourceMode.PRODUCES
                ? -Math.abs(amount)
                : Math.abs(amount);
    }

    private Optional<CERecipe> recipeById(ResourceLocation recipeId) {
        Level level = getLevel();
        if (level == null) {
            return Optional.empty();
        }

        return level.getRecipeManager()
                .byKey(recipeId)
                .map(holder -> holder.value() instanceof CERecipe recipe ? recipe : null);
    }

    private boolean canProcessResource(SingleBlockMachineInstance instance, int amount) {
        if (amount == 0) {
            return true;
        }

        if (instance.definition().resource() == SingleBlockMachineResource.STEAM) {
            return amount > 0
                    ? steamTank.getFluidAmount() >= amount
                    : steamTank.getFluidAmount() - amount <= steamTank.getCapacity();
        }

        if (instance.definition().resource() == SingleBlockMachineResource.ENERGY) {
            return amount > 0
                    ? energyStored >= amount
                    : energyStored - amount <= energyCapacity();
        }

        return true;
    }

    private List<ItemStack> rollItemOutputs(CERecipe recipe) {
        List<ItemStack> outputs = new ArrayList<>();
        Level level = getLevel();

        if (level == null) {
            return outputs;
        }

        for (CEChancedItemOutput output : recipe.itemOutputs()) {
            if (output.chance() >= CEChancedItemOutput.MAX_CHANCE
                    || level.random.nextInt(CEChancedItemOutput.MAX_CHANCE) < output.chance()) {
                outputs.add(output.stack().copy());
            }
        }

        return outputs;
    }

    private boolean canFitItems(List<ItemStack> outputs) {
        List<ItemStack> simulated = new ArrayList<>();

        for (int slot = 0; slot < itemOutputSlotCount(); slot++) {
            simulated.add(outputItems.getStackInSlot(slot).copy());
        }

        for (ItemStack output : outputs) {
            ItemStack remaining = output.copy();

            for (int slot = 0; slot < simulated.size() && !remaining.isEmpty(); slot++) {
                ItemStack current = simulated.get(slot);

                if (current.isEmpty()) {
                    int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                    simulated.set(slot, remaining.copyWithCount(moved));
                    remaining.shrink(moved);
                } else if (ItemStack.isSameItemSameComponents(current, remaining)) {
                    int moved = Math.min(remaining.getCount(), current.getMaxStackSize() - current.getCount());
                    current.grow(moved);
                    remaining.shrink(moved);
                }
            }

            if (!remaining.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean canFitFluids(List<FluidStack> outputs) {
        List<FluidStack> simulated = new ArrayList<>();

        for (int slot = 0; slot < outputFluidSlotCount(); slot++) {
            simulated.add(outputFluids[slot].getFluid().copy());
        }

        for (FluidStack output : outputs) {
            FluidStack remaining = output.copy();

            for (int slot = 0; slot < simulated.size() && !remaining.isEmpty(); slot++) {
                FluidStack current = simulated.get(slot);
                int capacity = outputFluids[slot].getCapacity();

                if (current.isEmpty()) {
                    int moved = Math.min(remaining.getAmount(), capacity);
                    simulated.set(slot, remaining.copyWithAmount(moved));
                    remaining.shrink(moved);
                } else if (FluidStack.isSameFluidSameComponents(current, remaining)) {
                    int moved = Math.min(remaining.getAmount(), capacity - current.getAmount());
                    current.grow(moved);
                    remaining.shrink(moved);
                }
            }

            if (!remaining.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private void consumeInputs(CERecipe recipe) {
        recipe.itemInputs().forEach(this::consumeItemInput);
        recipe.fluidInputs().forEach(this::consumeFluidInput);
    }

    private void consumeItemInput(SizedIngredient input) {
        int remaining = input.count();

        for (int slot = 0; slot < itemInputSlotCount() && remaining > 0; slot++) {
            ItemStack stack = inputItems.getStackInSlot(slot);

            if (stack.isEmpty() || !input.ingredient().test(stack)) {
                continue;
            }

            int extracted = Math.min(remaining, stack.getCount());
            inputItems.extractItem(slot, extracted, false);
            remaining -= extracted;
        }
    }

    private void produceOutputs(CERecipeExecution execution) {
        execution.itemOutputs().forEach(this::produceItemOutput);
        execution.fluidOutputs().forEach(this::produceFluidOutput);
    }

    private InteractionContext interactionContext(Level level, SingleBlockMachineInstance instance) {
        Direction facing = getBlockState().hasProperty(SingleBlockMachineBlock.FACING)
                ? getBlockState().getValue(SingleBlockMachineBlock.FACING)
                : Direction.NORTH;

        return new InteractionContext() {
            @Override
            public Level level() {
                return level;
            }

            @Override
            public BlockPos origin() {
                return getBlockPos();
            }

            @Override
            public Direction facing() {
                return facing;
            }

            @Override
            public List<ItemStack> itemInputs() {
                return visibleItemInputs();
            }

            @Override
            public List<FluidStack> fluidInputs() {
                return visibleFluidInputs();
            }

            @Override
            public InteractionWearStore wearStore() {
                return interactionWear;
            }
        };
    }

    private void produceItemOutput(ItemStack output) {
        ItemStack remaining = output.copy();

        for (int slot = 0; slot < itemOutputSlotCount() && !remaining.isEmpty(); slot++) {
            remaining = outputItems.insertItem(slot, remaining, false);
        }
    }

    private void consumeFluidInput(SizedFluidIngredient input) {
        int remaining = input.amount();

        for (int i = 0; i < inputFluidSlotCount() && remaining > 0; i++) {
            FluidStack stack = inputFluids[i].getFluid();

            if (stack.isEmpty() || !input.ingredient().test(stack)) {
                continue;
            }

            FluidStack drained = inputFluids[i].drain(remaining, FluidAction.EXECUTE);
            remaining -= drained.getAmount();
        }
    }

    private void produceFluidOutput(FluidStack output) {
        FluidStack remaining = output.copy();

        for (int i = 0; i < outputFluidSlotCount() && !remaining.isEmpty(); i++) {
            int filled = outputFluids[i].fill(remaining, FluidAction.EXECUTE);
            remaining = remaining.copyWithAmount(remaining.getAmount() - filled);
        }
    }

    private List<ItemStack> visibleItemInputs() {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < itemInputSlotCount(); i++) {
            stacks.add(inputItems.getStackInSlot(i));
        }

        return stacks;
    }

    private List<FluidStack> visibleFluidInputs() {
        List<FluidStack> stacks = new ArrayList<>();

        for (int i = 0; i < inputFluidSlotCount(); i++) {
            stacks.add(inputFluids[i].getFluid());
        }

        return stacks;
    }

    private void resetOverlay() {
        overlayFrame = 0;
        overlayFrameTicks = 0;
    }

    public CERecipeLogic recipeLogic() {
        return recipeLogic;
    }

    private final class RecipeHost implements CERecipeLogicHost {
        @Override
        public boolean recipeMachineReady() {
            return instance() != null;
        }

        @Override
        public Optional<CERecipeExecution> findAndConsumeRecipeInputs() {
            Level level = getLevel();
            SingleBlockMachineInstance instance = instance();

            if (level == null || instance == null) {
                return Optional.empty();
            }

            Optional<RecipeHolder<CERecipe>> holder = findRecipe(level, instance);
            if (holder.isEmpty()) {
                return Optional.empty();
            }

            CERecipe recipe = holder.get().value();
            int resourcePerTick = resourcePerTick(instance, recipe);
            InteractionContext context = interactionContext(level, instance);

            Optional<MachineModifier> machineModifier =
                    InteractionRuntime.firstMatchingModifier(instance.definition().modifiers(), context);
            Optional<MachineModifier> recipeModifier =
                    InteractionRuntime.firstMatchingModifier(recipe.modifiers(), context);

            resourcePerTick = InteractionRuntime.adjustedResource(
                    resourcePerTick,
                    instance.definition().resource() == SingleBlockMachineResource.STEAM,
                    machineModifier,
                    recipeModifier
            );

            if (!canProcessResource(instance, resourcePerTick)) {
                return Optional.empty();
            }

            List<ItemStack> possibleItemOutputs = recipe.itemOutputs().stream()
                    .map(output -> output.stack().copy())
                    .toList();
            List<FluidStack> fluidOutputs = recipe.fluidOutputs().stream()
                    .map(FluidStack::copy)
                    .toList();

            if (!canFitItems(possibleItemOutputs) || !canFitFluids(fluidOutputs)) {
                return Optional.empty();
            }

            List<ItemStack> itemOutputs = rollItemOutputs(recipe);

            InteractionRuntime.applyInteractions(
                    instance.definition().blockInteractions(),
                    context,
                    InteractionPhase.ON_START
            );
            InteractionRuntime.applyInteractions(
                    recipe.blockInteractions(),
                    context,
                    InteractionPhase.ON_START
            );

            consumeInputs(recipe);
            preferredRecipeId = holder.get().id();

            return Optional.of(new CERecipeExecution(
                    holder.get().id(),
                    recipe.recipeType(),
                    InteractionRuntime.adjustedDuration(
                            adjustedDuration(instance, recipe),
                            machineModifier,
                            recipeModifier
                    ),
                    resourcePerTick,
                    1,
                    recipe.itemInputs().stream()
                            .map(input -> input.ingredient().getItems()[0].copyWithCount(input.count()))
                            .toList(),
                    List.of(),
                    itemOutputs,
                    fluidOutputs,
                    new CompoundTag()
            ));
        }

        @Override
        public CERecipeTickResult consumeRecipeTick(CERecipeExecution execution) {
            SingleBlockMachineInstance instance = instance();
            Level level = getLevel();

            if (instance == null || level == null) {
                return CERecipeTickResult.CANCEL;
            }

            Optional<CERecipe> recipe = recipeById(execution.recipeId());
            InteractionContext context = interactionContext(level, instance);

            ConditionFailure machineFailure = InteractionRuntime.failedConditionBehavior(
                    instance.definition().conditions(),
                    context,
                    InteractionPhase.WHILE_PROCESSING
            );

            ConditionFailure recipeFailure = recipe
                    .map(value -> InteractionRuntime.failedConditionBehavior(
                            value.conditions(),
                            context,
                            InteractionPhase.WHILE_PROCESSING
                    ))
                    .orElse(null);

            ConditionFailure failure = machineFailure != null ? machineFailure : recipeFailure;

            if (failure != null) {
                return failure == ConditionFailure.CANCEL
                        ? CERecipeTickResult.CANCEL
                        : failure == ConditionFailure.RESET
                        ? CERecipeTickResult.WAIT_FOR_RESOURCE
                        : CERecipeTickResult.PAUSE;
            }

            int amount = execution.resourcePerTick();
            if (amount == 0) {
                return CERecipeTickResult.CONTINUE;
            }

            if (instance.definition().resource() == SingleBlockMachineResource.STEAM) {
                if (amount > 0) {
                    if (steamTank.getFluidAmount() < amount) {
                        return CERecipeTickResult.WAIT_FOR_RESOURCE;
                    }

                    steamTank.drain(amount, FluidAction.EXECUTE);
                } else {
                    FluidRegistry.RegisteredFluid steam =
                            FluidRegistry.CHEMICAL_FLUIDS.get(IndustrialFluids.STEAM.registryName());

                    if (steam == null) {
                        return CERecipeTickResult.CANCEL;
                    }

                    int produced = -amount;

                    if (steamTank.getFluidAmount() + produced > steamTank.getCapacity()) {
                        return CERecipeTickResult.WAIT_FOR_RESOURCE;
                    }

                    steamTank.fill(
                            new FluidStack(steam.source().get(), produced),
                            FluidAction.EXECUTE
                    );
                }

                return CERecipeTickResult.CONTINUE;
            }

            if (amount > 0) {
                if (energyStored < amount) {
                    return CERecipeTickResult.WAIT_FOR_RESOURCE;
                }

                ceContainer.extract(amount, false);
            } else {
                int produced = -amount;

                if (energyStored + produced > energyCapacity()) {
                    return CERecipeTickResult.WAIT_FOR_RESOURCE;
                }

                ceContainer.insert(produced, false);
            }

            return CERecipeTickResult.CONTINUE;
        }

        @Override
        public boolean canCompleteRecipe(CERecipeExecution execution) {
            SingleBlockMachineInstance instance = instance();
            Level level = getLevel();
            Optional<CERecipe> recipe = recipeById(execution.recipeId());

            if (instance == null || level == null || recipe.isEmpty()) {
                return false;
            }

            InteractionContext context = interactionContext(level, instance);

            return canFitItems(execution.itemOutputs())
                    && canFitFluids(execution.fluidOutputs())
                    && InteractionRuntime.conditionsMatch(
                    instance.definition().conditions(),
                    context,
                    InteractionPhase.ON_COMPLETE
            )
                    && InteractionRuntime.conditionsMatch(
                    recipe.get().conditions(),
                    context,
                    InteractionPhase.ON_COMPLETE
            )
                    && InteractionRuntime.interactionsMatch(
                    instance.definition().blockInteractions(),
                    context,
                    InteractionPhase.ON_COMPLETE
            )
                    && InteractionRuntime.interactionsMatch(
                    recipe.get().blockInteractions(),
                    context,
                    InteractionPhase.ON_COMPLETE
            );
        }

        @Override
        public void completeRecipe(CERecipeExecution execution) {
            SingleBlockMachineInstance instance = instance();
            Level level = getLevel();
            Optional<CERecipe> recipe = recipeById(execution.recipeId());

            if (instance != null && level != null && recipe.isPresent()) {
                InteractionContext context = interactionContext(level, instance);

                InteractionRuntime.applyInteractions(
                        instance.definition().blockInteractions(),
                        context,
                        InteractionPhase.ON_COMPLETE
                );
                InteractionRuntime.applyInteractions(
                        recipe.get().blockInteractions(),
                        context,
                        InteractionPhase.ON_COMPLETE
                );
            }

            produceOutputs(execution);
        }

        @Override
        public void onRecipeLogicChanged(boolean activeChanged) {
            if (!recipeLogic.isActive()) {
                resetOverlay();
            }

            setChanged();

            if (activeChanged || recipeSyncCooldown <= 0) {
                recipeSyncCooldown = 5;
                syncToClient();
            } else {
                recipeSyncCooldown--;
            }
        }
    }

    private SingleBlockMachineInstance instance() {
        return getBlockState().getBlock() instanceof SingleBlockMachineBlock block
                ? block.instance()
                : null;
    }

    private static boolean isVentBlocked(
            Level level,
            BlockPos pos
    ) {
        BlockState state = level.getBlockState(pos);

        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return false;
        }

        return !state.getCollisionShape(level, pos).isEmpty();
    }

    private ItemStackHandler createInventory(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChangedAndSync();
            }
        };
    }

    private FluidTank createSteamTank() {
        return new FluidTank(1_000_000) {
            @Override
            public int getCapacity() {
                return steamCapacity();
            }

            @Override
            public int getTankCapacity(int tank) {
                return tank == 0 ? steamCapacity() : 0;
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                FluidRegistry.RegisteredFluid steam =
                        FluidRegistry.CHEMICAL_FLUIDS.get(IndustrialFluids.STEAM.registryName());

                return steam != null
                        && !stack.isEmpty()
                        && stack.getFluid() == steam.source().get();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (!isFluidValid(resource)) {
                    return 0;
                }

                int space = Math.max(0, getCapacity() - getFluidAmount());

                if (space <= 0) {
                    return 0;
                }

                return super.fill(
                        resource.copyWithAmount(Math.min(resource.getAmount(), space)),
                        action
                );
            }

            @Override
            protected void onContentsChanged() {
                setChangedAndSync();
            }
        };
    }

    private void setChangedAndSync() {
        setChanged();
        syncToClient();
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);

        tag.put("InputItems", inputItems.serializeNBT(registries));
        tag.put("OutputItems", outputItems.serializeNBT(registries));
        tag.put("SteamTank", steamTank.writeToNBT(registries, new CompoundTag()));

        saveTanks(tag, "InputFluids", inputFluids, registries);
        saveTanks(tag, "OutputFluids", outputFluids, registries);

        tag.putLong("EnergyStored", energyStored);

        if (preferredRecipeId != null) {
            tag.putString("PreferredRecipe", preferredRecipeId.toString());
        }

        interactionWear.save(tag);
        recipeLogic.save(tag, registries);
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);

        if (tag.contains("InputItems")) {
            inputItems.deserializeNBT(registries, tag.getCompound("InputItems"));
        }

        if (tag.contains("OutputItems")) {
            outputItems.deserializeNBT(registries, tag.getCompound("OutputItems"));
        }

        if (tag.contains("SteamTank")) {
            steamTank.readFromNBT(registries, tag.getCompound("SteamTank"));
        }

        loadTanks(tag, "InputFluids", inputFluids, registries);
        loadTanks(tag, "OutputFluids", outputFluids, registries);

        energyStored = Math.max(
                0L,
                Math.min(tag.getLong("EnergyStored"), energyCapacity())
        );

        preferredRecipeId = tag.contains("PreferredRecipe")
                ? ResourceLocation.parse(tag.getString("PreferredRecipe"))
                : null;

        interactionWear.load(tag);
        recipeLogic.load(tag, registries);
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
        for (int i = 0; i < inputItems.getSlots(); i++) {
            inputItems.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < outputItems.getSlots(); i++) {
            outputItems.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (FluidTank tank : inputFluids) {
            tank.setFluid(FluidStack.EMPTY);
        }

        for (FluidTank tank : outputFluids) {
            tank.setFluid(FluidStack.EMPTY);
        }
    }

    @Override
    public Component getDisplayName() {
        SingleBlockMachineInstance instance = instance();

        return Component.literal(
                instance == null
                        ? "Singleblock Machine"
                        : instance.displayName()
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            Player player
    ) {
        return new SingleBlockMachineMenu(
                containerId,
                playerInventory,
                this
        );
    }

    private class MachineItemHandler extends CombinedInvWrapper {
        private MachineItemHandler() {
            super(inputItems, outputItems);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            int outputStart = MAX_ITEM_INPUT_SLOTS;

            if (slot < outputStart || slot >= outputStart + itemOutputSlotCount()) {
                return ItemStack.EMPTY;
            }

            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot < 0 || slot >= itemInputSlotCount()) {
                return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }
    }

    private FluidTank[] createFluidTanks(int count) {
        FluidTank[] tanks = new FluidTank[count];

        for (int i = 0; i < count; i++) {
            tanks[i] = new FluidTank(RECIPE_FLUID_TANK_CAPACITY) {
                @Override
                protected void onContentsChanged() {
                    setChangedAndSync();
                }
            };
        }

        return tanks;
    }

    private static List<FluidTank> visibleTanks(FluidTank[] tanks, int count) {
        List<FluidTank> visible = new ArrayList<>();

        for (int i = 0; i < Math.min(count, tanks.length); i++) {
            visible.add(tanks[i]);
        }

        return visible;
    }

    private static void saveTanks(
            CompoundTag tag,
            String key,
            FluidTank[] tanks,
            HolderLookup.Provider registries
    ) {
        CompoundTag tanksTag = new CompoundTag();

        for (int i = 0; i < tanks.length; i++) {
            tanksTag.put(
                    Integer.toString(i),
                    tanks[i].writeToNBT(registries, new CompoundTag())
            );
        }

        tag.put(key, tanksTag);
    }

    private static void loadTanks(
            CompoundTag tag,
            String key,
            FluidTank[] tanks,
            HolderLookup.Provider registries
    ) {
        if (!tag.contains(key)) {
            return;
        }

        CompoundTag tanksTag = tag.getCompound(key);

        for (int i = 0; i < tanks.length; i++) {
            String index = Integer.toString(i);

            if (tanksTag.contains(index)) {
                tanks[i].readFromNBT(
                        registries,
                        tanksTag.getCompound(index)
                );
            }
        }
    }

    private class MachineFluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return (instance() != null
                    && instance().definition().resource() == SingleBlockMachineResource.STEAM
                    ? 1
                    : 0)
                    + inputFluidSlotCount()
                    + outputFluidSlotCount();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            FluidTank fluidTank = tank(tank);
            return fluidTank == null
                    ? FluidStack.EMPTY
                    : fluidTank.getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            FluidTank fluidTank = tank(tank);
            return fluidTank == null
                    ? 0
                    : fluidTank.getTankCapacity(0);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            FluidTank fluidTank = tank(tank);
            return fluidTank != null && fluidTank.isFluidValid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return 0;
            }

            if (steamTank.isFluidValid(resource)) {
                return steamTankAcceptsInput()
                        ? steamTank.fill(resource, action)
                        : 0;
            }

            int filled = 0;
            int remaining = resource.getAmount();

            for (int i = 0; i < inputFluidSlotCount() && remaining > 0; i++) {
                FluidStack request = resource.copyWithAmount(remaining);
                int accepted = inputFluids[i].fill(request, action);
                filled += accepted;
                remaining -= accepted;
            }

            return filled;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }

            if (steamTankAllowsOutput() && steamTank.isFluidValid(resource)) {
                FluidStack drained = steamTank.drain(resource, action);

                if (!drained.isEmpty()) {
                    return drained;
                }
            }

            for (int i = 0; i < outputFluidSlotCount(); i++) {
                FluidStack drained = outputFluids[i].drain(resource, action);

                if (!drained.isEmpty()) {
                    return drained;
                }
            }

            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (steamTankAllowsOutput()) {
                FluidStack drained = steamTank.drain(maxDrain, action);

                if (!drained.isEmpty()) {
                    return drained;
                }
            }

            for (int i = 0; i < outputFluidSlotCount(); i++) {
                FluidStack drained = outputFluids[i].drain(maxDrain, action);

                if (!drained.isEmpty()) {
                    return drained;
                }
            }

            return FluidStack.EMPTY;
        }

        private boolean steamTankVisible() {
            return instance() != null
                    && instance().definition().resource() == SingleBlockMachineResource.STEAM;
        }

        private boolean steamTankAcceptsInput() {
            return steamTankVisible()
                    && instance().definition().resourceMode() == SingleBlockMachineResourceMode.CONSUMES;
        }

        private boolean steamTankAllowsOutput() {
            return steamTankVisible()
                    && instance().definition().resourceMode() == SingleBlockMachineResourceMode.PRODUCES;
        }

        private FluidTank tank(int tank) {
            int index = tank;

            if (steamTankVisible()) {
                if (index == 0) {
                    return steamTank;
                }

                index--;
            }

            if (index < inputFluidSlotCount()) {
                return inputFluids[index];
            }

            index -= inputFluidSlotCount();

            if (index < outputFluidSlotCount()) {
                return outputFluids[index];
            }

            return null;
        }
    }
}