package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.energy.CEEnergyContainer;
import net.mads.createexpansion.energy.CEEnergyNetwork;
import net.mads.createexpansion.debug.CEPerformanceProfiler;
import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.machine.FireboxBlock;
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
import net.mads.createexpansion.machine.coil.CoilBlock;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeInput;
import net.mads.createexpansion.recipe.CERecipeLookup;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.mads.createexpansion.menu.MultiblockControllerMenu;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MultiblockControllerBlockEntity extends BlockEntity implements MenuProvider, CERecipeLogicMachine {
    private static final int VALIDATION_INTERVAL = 40;
    private static final int IDLE_RECIPE_CHECK_INTERVAL = 20;
    private static final int ACTIVE_SYNC_INTERVAL = 20;
    private static final int EXTERNAL_OPERATION_DURATION = 20;

    private boolean formed;
    private boolean dirty = true;
    private int validationCooldown;
    private int recipeCheckCooldown;
    private String formedVariant = "";
    private int variantLevel;
    private MachineTier formedTier;
    private int formedCoilHeat;
    private int formedCoilCount;
    private List<BlockPos> formedPositions = List.of();
    private Map<MultiblockAbility, List<BlockPos>> abilityPositions = new EnumMap<>(MultiblockAbility.class);
    private int recipeProgress;
    private int recipeDuration;
    private int activeCEt;
    private int activeSyncCooldown;
    private ResourceLocation preferredRecipeId;
    private int externalActiveTicks;
    private int externalCEt;
    private int externalWarmupTicks;
    private final InteractionWearStore interactionWear = new InteractionWearStore();
    private final CERecipeLogic recipeLogic = new CERecipeLogic(new RecipeHost());

    public MultiblockControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.MULTIBLOCK_CONTROLLER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MultiblockControllerBlockEntity controller) {
        if (level.isClientSide()) {
            return;
        }

        long profileStart = CEPerformanceProfiler.begin(level);
        try {
            if (controller.validationCooldown > 0) {
                controller.validationCooldown--;
            }

            if (controller.dirty || controller.validationCooldown <= 0) {
                controller.validateStructure(state);
                controller.validationCooldown = VALIDATION_INTERVAL;
            }

            controller.tickRecipe();
            controller.tickAutoOutputs();
        } finally {
            CEPerformanceProfiler.record(CEPerformanceProfiler.Metric.MULTIBLOCK_TICK, profileStart);
        }
    }

    public boolean isFormed() {
        return formed;
    }

    public String formedVariant() {
        return formedVariant;
    }

    public int variantLevel() {
        return variantLevel;
    }

    public MachineTier formedTier() {
        return formedTier;
    }

    public int formedCoilHeat() {
        return formedCoilHeat;
    }

    public int formedCoilCount() {
        return formedCoilCount;
    }

    public boolean hasFormedPosition(BlockPos pos) {
        return formedPositions.contains(pos);
    }

    public boolean consumeExternalHeatEnergy(int energyPerTick, int ticks) {
        if (level == null || !formed || energyPerTick <= 0 || ticks <= 0) {
            return false;
        }
        MultiblockDefinition definition = currentDefinition();
        if (definition == null || !definition.externalHeatSource()) {
            return false;
        }

        if (!transferEnergy(energyPerTick, true)) {
            return false;
        }

        boolean wasExternallyActive = externallyActive();
        boolean energyChanged = activeCEt != energyPerTick || externalCEt != energyPerTick;
        externalActiveTicks = Math.max(externalActiveTicks, ticks + 2);
        externalCEt = energyPerTick;
        activeCEt = energyPerTick;
        if (!recipeLogic.isProcessing() && !wasExternallyActive) {
            recipeProgress = 0;
            recipeDuration = externalOperationDuration(definition);
            externalWarmupTicks = 0;
            activeSyncCooldown = 0;
        } else if (energyChanged) {
            activeSyncCooldown = 0;
        }
        setActive(true);
        return true;
    }

    public boolean externalHeatReady() {
        MultiblockDefinition definition = currentDefinition();
        return definition != null
                && definition.externalHeatSource()
                && externallyActive()
                && externalWarmupTicks >= externalOperationDuration(definition);
    }

    public boolean canRunTier(MachineTier requiredTier) {
        return formed && formedTier != null && net.mads.createexpansion.machine.MachineTierStats.isAtLeast(formedTier, requiredTier);
    }

    public List<BlockPos> abilityPositions(MultiblockAbility ability) {
        return abilityPositions.getOrDefault(ability, List.of());
    }

    public void markStructureDirty() {
        dirty = true;
        validationCooldown = 0;
        setChanged();
    }

    public void clearFormation() {
        setCoilsActive(false);
        setFireboxesActive(false);
        detachParts();
        formed = false;
        formedVariant = "";
        variantLevel = 0;
        formedTier = null;
        formedCoilHeat = 0;
        formedCoilCount = 0;
        formedPositions = List.of();
        abilityPositions = new EnumMap<>(MultiblockAbility.class);
        externalActiveTicks = 0;
        externalCEt = 0;
        externalWarmupTicks = 0;
        recipeLogic.cancel();
        clearActiveRecipe();
        updateBlockFormedState(false);
        updateBlockActiveState(false);
        setChanged();
        syncToClient();
    }

    private void validateStructure(BlockState state) {
        dirty = false;
        if (level == null || !(state.getBlock() instanceof MultiblockControllerBlock controllerBlock)) {
            clearFormation();
            return;
        }

        Direction facing = state.getValue(MultiblockControllerBlock.FACING);
        MultiblockDefinition definition = currentDefinition(controllerBlock);
        if (definition == null) {
            clearFormation();
            return;
        }

        MultiblockMatchResult result = definition.tryMatch(level, worldPosition, facing);
        if (!result.matched()) {
            clearFormation();
            return;
        }

        form(result);
    }

    private void form(MultiblockMatchResult result) {
        boolean keepActive = isProcessing();
        setCoilsActive(false);
        setFireboxesActive(false);
        detachParts();
        formed = true;
        formedVariant = result.variant();
        variantLevel = result.variantLevel();
        formedTier = result.tier();
        formedCoilHeat = result.coilHeat();
        formedCoilCount = result.coilCount();
        formedPositions = result.positions();
        abilityPositions = result.abilityPositions();
        updateBlockFormedState(true);

        for (BlockPos partPos : formedPositions) {
            if (partPos.equals(worldPosition)) {
                continue;
            }

            if (level != null && level.getBlockEntity(partPos) instanceof MultiblockPart part) {
                if (part instanceof MachinePortBlockEntity port) {
                    port.setAssembledOverlayTexture(result.overlays().get(partPos));
                }
                part.attachToMultiblock(worldPosition);
            }
        }

        setChanged();
        syncToClient();
        if (keepActive) {
            setActive(true);
        }
    }

    private void tickRecipe() {
        long profileStart = CEPerformanceProfiler.begin(level);
        try {
            tickRecipeInner();
        } finally {
            CEPerformanceProfiler.record(CEPerformanceProfiler.Metric.MULTIBLOCK_RECIPE_TICK, profileStart);
        }
    }

    private void tickRecipeInner() {
        if (level == null || !formed || !(getBlockState().getBlock() instanceof MultiblockControllerBlock controllerBlock)) {
            recipeLogic.cancel();
            clearActiveRecipe();
            setActive(false);
            return;
        }

        MultiblockDefinition definition = currentDefinition(controllerBlock);
        if (definition == null || definition.recipeTypes().isEmpty()) {
            recipeLogic.cancel();
            tickExternalOperation(definition);
            return;
        }
        recipeLogic.serverTick();
    }

    private boolean externallyActive() {
        return externalActiveTicks > 0;
    }

    private void tickExternalOperation(@Nullable MultiblockDefinition definition) {
        if (definition == null || !definition.externalHeatSource() || !externallyActive()) {
            externalCEt = 0;
            externalWarmupTicks = 0;
            clearActiveRecipe();
            setActive(false);
            return;
        }

        if (!processEnergy(externalCEt)) {
            externalActiveTicks = 0;
            externalCEt = 0;
            externalWarmupTicks = 0;
            clearActiveRecipe();
            setActive(false);
            return;
        }

        externalActiveTicks--;
        int duration = externalOperationDuration(definition);
        if (recipeDuration != duration) {
            recipeDuration = duration;
        }
        recipeProgress = recipeDuration <= 1 ? 0 : (recipeProgress + 1) % recipeDuration;
        if (externalWarmupTicks < recipeDuration) {
            externalWarmupTicks++;
        }
        activeCEt = externalCEt;
        setActive(true);
        setChanged();
        syncActiveProgress();
    }

    private static int externalOperationDuration(MultiblockDefinition definition) {
        MultiblockDefinition.InputOnlyDisplay display = definition.inputOnlyDisplay();
        return display == null ? EXTERNAL_OPERATION_DURATION : display.durationTicks();
    }

    private void tickAutoOutputs() {
        if (level == null || !formed) {
            return;
        }

        Set<BlockPos> seen = new HashSet<>();
        for (BlockPos pos : sortedPositions(abilityPositions(MultiblockAbility.ITEM_OUTPUT))) {
            if (seen.add(pos) && level.getBlockEntity(pos) instanceof MachinePortBlockEntity port) {
                port.tickAutoOutput();
            }
        }
        for (BlockPos pos : sortedPositions(abilityPositions(MultiblockAbility.FLUID_OUTPUT))) {
            if (seen.add(pos) && level.getBlockEntity(pos) instanceof MachinePortBlockEntity port) {
                port.tickAutoOutput();
            }
        }
    }

    private void syncActiveProgress() {
        if (activeSyncCooldown > 0) {
            activeSyncCooldown--;
            return;
        }

        activeSyncCooldown = ACTIVE_SYNC_INTERVAL;
        syncToClient();
    }

    private Optional<RecipeHolder<CERecipe>> matchingRecipe(MultiblockDefinition definition, CERecipeInput input) {
        long profileStart = CEPerformanceProfiler.begin(level);
        try {
            return matchingRecipeInner(definition, input);
        } finally {
            CEPerformanceProfiler.record(CEPerformanceProfiler.Metric.RECIPE_LOOKUP, profileStart);
        }
    }

    private Optional<RecipeHolder<CERecipe>> matchingRecipeInner(MultiblockDefinition definition, CERecipeInput input) {
        if (level == null) {
            return Optional.empty();
        }

        if (preferredRecipeId != null) {
            Optional<RecipeHolder<CERecipe>> preferred = CERecipeLookup.preferred(
                            level.getRecipeManager(),
                            preferredRecipeId,
                            Set.copyOf(definition.recipeTypes())
                    )
                    .filter(recipe -> recipe.value().matches(input, level));
            if (preferred.isPresent()) {
                return preferred;
            }
        }

        return CERecipeLookup.candidatesByTypes(level.getRecipeManager(), definition.recipeTypes(), input)
                .stream()
                .filter(recipe -> recipe.value().matches(input, level))
                .sorted(Comparator.comparing(recipe -> recipe.id().toString()))
                .findFirst();
    }

    private Optional<MachineTier> highestPortTier(MultiblockAbility ability) {
        if (level == null) {
            return Optional.empty();
        }

        MachineTier best = null;
        Set<BlockPos> seen = new HashSet<>();
        for (BlockPos pos : sortedPositions(abilityPositions(ability))) {
            if (!seen.add(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof MachinePortBlockEntity port) {
                best = best == null ? port.tier() : MachineTierStats.max(best, port.tier());
            }
        }
        return Optional.ofNullable(best);
    }

    private EnergyRuntime energyRuntime() {
        Optional<MachineTier> highest = highestPortTier(MultiblockAbility.ENERGY_INPUT);
        if (highest.isEmpty()) {
            return new EnergyRuntime(Optional.empty(), Optional.empty());
        }

        MachineTier baseTier = highest.get();
        long baseVoltage = Math.max(1L, MachineTierStats.ceTier(baseTier));
        long equivalentBaseAmps = energyCapacity(MultiblockAbility.ENERGY_INPUT) / baseVoltage;
        int overclockSteps = 0;
        while (equivalentBaseAmps >= 4 && overclockSteps < MachineTier.ALL.size()) {
            equivalentBaseAmps /= 4;
            overclockSteps++;
        }

        return new EnergyRuntime(
                Optional.of(MachineTierStats.next(baseTier)),
                Optional.of(MachineTierStats.offset(baseTier, overclockSteps))
        );
    }

    private int outputParallel(CERecipe recipe, MachineTier runtimeTier) {
        int baseCEt = recipe.runtimeCEt(runtimeTier);
        if (baseCEt <= 0) {
            return 1;
        }
        int capacity = energyCapacity(MultiblockAbility.ENERGY_OUTPUT);
        return Math.max(1, capacity / baseCEt);
    }

    private int energyCapacity(MultiblockAbility ability) {
        if (level == null) {
            return 0;
        }

        long capacity = 0;
        Set<BlockPos> seen = new HashSet<>();
        for (BlockPos pos : sortedPositions(abilityPositions(ability))) {
            if (!seen.add(pos) || !(level.getBlockEntity(pos) instanceof MachinePortBlockEntity port)) {
                continue;
            }

            CEEnergyContainer container = port.ceContainer();
            if (container == null) {
                continue;
            }

            if (ability == MultiblockAbility.ENERGY_INPUT) {
                capacity += saturatedMultiply(port.displayInputVoltage(), container.getInputAmperage());
            } else if (ability == MultiblockAbility.ENERGY_OUTPUT) {
                capacity += saturatedMultiply(container.getOutputVoltage(), container.getOutputAmperage());
            }
            if (capacity >= Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
        }
        return (int) capacity;
    }

    private int maxKineticRpm() {
        if (level == null) {
            return 0;
        }

        int rpm = 0;
        Set<BlockPos> seen = new HashSet<>();
        for (BlockPos pos : sortedPositions(abilityPositions(MultiblockAbility.KINETIC_INPUT))) {
            if (!seen.add(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof MachinePortBlockEntity port) {
                rpm = Math.max(rpm, port.kineticRpm());
            }
        }
        return rpm;
    }

    private static MachineTier runtimeTier(CERecipe recipe, CERecipeInput input, EnergyRuntime energyRuntime) {
        if (!recipe.generatesEnergy() && recipe.requiredEnergyTier().isPresent()) {
            return energyRuntime.overclockTier().orElse(input.energyTier().orElse(recipe.requiredEnergyTier().get()));
        }
        if (recipe.requiredKineticTier().isPresent()) {
            return input.kineticTier().orElse(recipe.requiredKineticTier().get());
        }
        if (recipe.requiredEnergyTier().isPresent()) {
            return input.energyTier().orElse(recipe.requiredEnergyTier().get());
        }
        if (recipe.requiredTier().isPresent()) {
            return input.machineTier().orElse(recipe.requiredTier().get());
        }
        if (input.machineTier().isPresent()) {
            return input.machineTier().get();
        }
        if (input.kineticTier().isPresent()) {
            return input.kineticTier().get();
        }
        return input.energyTier().orElse(MachineTier.ULV);
    }

    private static int signedRuntimeCEt(CERecipe recipe, MachineTier runtimeTier, int parallel) {
        int runtimeCEt = recipe.runtimeCEt(runtimeTier);
        runtimeCEt = multiplyClamped(runtimeCEt, parallel);
        return recipe.generatesEnergy() ? -runtimeCEt : runtimeCEt;
    }

    private boolean canProcessEnergy(int signedCEt) {
        return transferEnergy(signedCEt, true);
    }

    private boolean processEnergy(int signedCEt) {
        return transferEnergy(signedCEt, false);
    }

    private boolean transferEnergy(int signedCEt, boolean simulate) {
        long profileStart = CEPerformanceProfiler.begin(level);
        try {
            return transferEnergyInner(signedCEt, simulate);
        } finally {
            CEPerformanceProfiler.record(CEPerformanceProfiler.Metric.ENERGY_TRANSFER, profileStart);
        }
    }

    private boolean transferEnergyInner(int signedCEt, boolean simulate) {
        if (signedCEt == 0) {
            return true;
        }

        MultiblockAbility ability = signedCEt > 0 ? MultiblockAbility.ENERGY_INPUT : MultiblockAbility.ENERGY_OUTPUT;
        int remaining = Math.abs(signedCEt);
        for (BlockPos pos : abilityPositions(ability)) {
            if (level == null || !(level.getBlockEntity(pos) instanceof MachinePortBlockEntity port)) {
                continue;
            }

            CEEnergyContainer container = port.ceContainer();
            if (container == null) {
                continue;
            }

            long moved;
            if (signedCEt > 0) {
                long inputVoltage = port.displayInputVoltage();
                long portLimit = Math.max(0L, saturatedMultiply(inputVoltage, container.getInputAmperage()));
                if (portLimit <= 0) {
                    continue;
                }
                moved = container.extract(Math.min(remaining, portLimit), simulate);
                if (!simulate && moved > 0) {
                    port.recordEnergyInputLoad(moved, inputVoltage);
                }
            } else {
                moved = container.insert(remaining, simulate);
            }
            remaining -= (int) moved;
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    private List<MachinePortBlockEntity> itemPorts(MultiblockAbility ability) {
        if (level == null) {
            return List.of();
        }

        List<MachinePortBlockEntity> ports = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        for (BlockPos pos : sortedPositions(abilityPositions(ability))) {
            if (!seen.add(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof MachinePortBlockEntity port && port.items().getSlots() > 0) {
                ports.add(port);
            }
        }
        return ports;
    }

    private List<MachinePortBlockEntity> fluidPorts(MultiblockAbility ability) {
        if (level == null) {
            return List.of();
        }

        List<MachinePortBlockEntity> ports = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        for (BlockPos pos : sortedPositions(abilityPositions(ability))) {
            if (!seen.add(pos)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof MachinePortBlockEntity port && !port.fluidTanks().isEmpty()) {
                ports.add(port);
            }
        }
        return ports;
    }

    private List<MachinePortBlockEntity> outputPorts(MultiblockAbility ability, InputRoute route) {
        List<MachinePortBlockEntity> ports = ability == MultiblockAbility.ITEM_OUTPUT ? itemPorts(ability) : fluidPorts(ability);
        if (!route.usesColor()) {
            return ports;
        }
        return ports.stream()
                .filter(port -> MachinePortBlockEntity.colorsConnect(routeColor(port), route.color()))
                .toList();
    }

    private static List<InputRoute> inputRoutes(List<MachinePortBlockEntity> itemPorts, List<MachinePortBlockEntity> fluidPorts) {
        List<InputRoute> routes = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        List<MachinePortBlockEntity> allPorts = new ArrayList<>();
        allPorts.addAll(itemPorts);
        allPorts.addAll(fluidPorts);
        if (allPorts.isEmpty()) {
            routes.add(new InputRoute(Optional.empty(), DyeColor.GRAY, false, itemPorts, fluidPorts));
            return routes;
        }

        for (MachinePortBlockEntity port : allPorts) {
            int circuit = port.circuit();
            DyeColor color = routeColor(port);
            String key = circuit + ":" + color.getId();
            if (!seen.add(key)) {
                continue;
            }

            List<MachinePortBlockEntity> routeItemPorts = itemPorts.stream()
                    .filter(candidate -> candidate.circuit() == circuit && inputColorsConnect(routeColor(candidate), color))
                    .toList();
            List<MachinePortBlockEntity> routeFluidPorts = fluidPorts.stream()
                    .filter(candidate -> candidate.circuit() == circuit && inputColorsConnect(routeColor(candidate), color))
                    .toList();
            routes.add(new InputRoute(circuit > 0 ? Optional.of(circuit) : Optional.empty(), color, true, routeItemPorts, routeFluidPorts));
        }
        return routes;
    }

    private static boolean inputColorsConnect(DyeColor inputColor, DyeColor routeColor) {
        return routeColor == DyeColor.GRAY ? inputColor == DyeColor.GRAY : MachinePortBlockEntity.colorsConnect(inputColor, routeColor);
    }

    private static DyeColor routeColor(MachinePortBlockEntity port) {
        return port.supportsIoColor() ? port.ioColor() : DyeColor.GRAY;
    }

    private static List<BlockPos> sortedPositions(List<BlockPos> positions) {
        return positions.stream()
                .sorted(Comparator.comparingInt((BlockPos pos) -> pos.getX())
                        .thenComparingInt(pos -> pos.getY())
                        .thenComparingInt(pos -> pos.getZ()))
                .toList();
    }

    private static List<ItemStack> itemStacks(List<MachinePortBlockEntity> ports) {
        List<ItemStack> stacks = new ArrayList<>();
        for (MachinePortBlockEntity port : ports) {
            ItemStackHandler handler = port.items();
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    stacks.add(stack.copy());
                }
            }
        }
        return stacks;
    }

    private static List<FluidStack> fluidStacks(List<MachinePortBlockEntity> ports) {
        List<FluidStack> stacks = new ArrayList<>();
        for (MachinePortBlockEntity port : ports) {
            for (FluidTank tank : port.fluidTanks()) {
                FluidStack stack = tank.getFluid();
                if (!stack.isEmpty()) {
                    stacks.add(stack.copy());
                }
            }
        }
        return stacks;
    }

    private List<ItemEntity> worldInputEntities(MultiblockDefinition definition) {
        if (level == null) {
            return List.of();
        }

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof MultiblockControllerBlock)
                || !state.hasProperty(MultiblockControllerBlock.FACING)) {
            return List.of();
        }

        Direction facing = state.getValue(MultiblockControllerBlock.FACING);
        List<ItemEntity> entities = new ArrayList<>();
        Set<Integer> seenEntityIds = new HashSet<>();

        for (BlockPos inputPos : definition.worldInteractionPositions(
                worldPosition,
                facing,
                MultiblockDefinition.WorldInteractionType.INPUT
        )) {
            for (ItemEntity entity : level.getEntitiesOfClass(
                    ItemEntity.class,
                    new AABB(inputPos),
                    entity -> entity.isAlive() && !entity.getItem().isEmpty()
            )) {
                if (seenEntityIds.add(entity.getId())) {
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    private static List<ItemStack> worldItemStacks(List<ItemEntity> entities) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemEntity entity : entities) {
            ItemStack stack = entity.getItem();
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
        return stacks;
    }

    private static boolean consumeInputs(
            List<MachinePortBlockEntity> ports,
            List<ItemEntity> worldEntities,
            CERecipe recipe,
            int multiplier
    ) {
        return consumeInputs(ports, worldEntities, recipe, multiplier, false);
    }

    private static boolean canConsumeInputs(
            List<MachinePortBlockEntity> ports,
            List<ItemEntity> worldEntities,
            CERecipe recipe,
            int multiplier
    ) {
        return consumeInputs(ports, worldEntities, recipe, multiplier, true);
    }

    private static boolean consumeInputs(
            List<MachinePortBlockEntity> ports,
            List<ItemEntity> worldEntities,
            CERecipe recipe,
            int multiplier,
            boolean simulate
    ) {
        List<ItemStack> simulatedPortStacks = new ArrayList<>();
        List<ItemStack> simulatedWorldStacks = new ArrayList<>();

        if (simulate) {
            for (MachinePortBlockEntity port : ports) {
                ItemStackHandler handler = port.items();
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    simulatedPortStacks.add(handler.getStackInSlot(slot).copy());
                }
            }
            for (ItemEntity entity : worldEntities) {
                simulatedWorldStacks.add(entity.getItem().copy());
            }
        }

        for (SizedIngredient input : recipe.itemInputs()) {
            int remaining = multiplyClamped(input.count(), multiplier);

            if (simulate) {
                remaining = consumeFromStacks(simulatedPortStacks, input, remaining);
                remaining = consumeFromStacks(simulatedWorldStacks, input, remaining);
            } else {
                remaining = consumeFromPorts(ports, input, remaining);
                remaining = consumeFromWorldEntities(worldEntities, input, remaining);
            }

            if (remaining > 0) {
                return false;
            }
        }

        return true;
    }

    private static int consumeFromStacks(
            List<ItemStack> stacks,
            SizedIngredient input,
            int remaining
    ) {
        for (ItemStack stack : stacks) {
            if (remaining <= 0) {
                break;
            }
            if (stack.isEmpty() || !input.ingredient().test(stack)) {
                continue;
            }

            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            remaining -= taken;
        }
        return remaining;
    }

    private static int consumeFromPorts(
            List<MachinePortBlockEntity> ports,
            SizedIngredient input,
            int remaining
    ) {
        for (MachinePortBlockEntity port : ports) {
            ItemStackHandler handler = port.items();
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (remaining <= 0) {
                    return 0;
                }

                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.isEmpty() || !input.ingredient().test(stack)) {
                    continue;
                }

                int taken = Math.min(remaining, stack.getCount());
                stack.shrink(taken);
                handler.setStackInSlot(slot, stack);
                remaining -= taken;
            }
        }
        return remaining;
    }

    private static int consumeFromWorldEntities(
            List<ItemEntity> entities,
            SizedIngredient input,
            int remaining
    ) {
        for (ItemEntity entity : entities) {
            if (remaining <= 0) {
                return 0;
            }

            ItemStack stack = entity.getItem();
            if (stack.isEmpty() || !input.ingredient().test(stack)) {
                continue;
            }

            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            remaining -= taken;

            if (stack.isEmpty()) {
                entity.discard();
            } else {
                entity.setItem(stack);
            }
        }
        return remaining;
    }

    private static boolean consumeFluidInputs(List<MachinePortBlockEntity> ports, CERecipe recipe, int multiplier) {
        return consumeFluidInputs(ports, recipe, multiplier, FluidAction.EXECUTE);
    }

    private static boolean canConsumeFluidInputs(List<MachinePortBlockEntity> ports, CERecipe recipe, int multiplier) {
        return consumeFluidInputs(ports, recipe, multiplier, FluidAction.SIMULATE);
    }

    private static boolean consumeFluidInputs(List<MachinePortBlockEntity> ports, CERecipe recipe, int multiplier, FluidAction action) {
        for (SizedFluidIngredient input : recipe.fluidInputs()) {
            int remaining = multiplyClamped(input.amount(), multiplier);
            for (MachinePortBlockEntity port : ports) {
                for (FluidTank tank : port.fluidTanks()) {
                    FluidStack stack = tank.getFluid();
                    if (stack.isEmpty() || !input.ingredient().test(stack)) {
                        continue;
                    }

                    FluidStack drained = tank.drain(remaining, action);
                    remaining -= drained.getAmount();
                    if (remaining <= 0) {
                        break;
                    }
                }
                if (remaining <= 0) {
                    break;
                }
            }

            if (remaining > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canRouteItemOutputs(
            MultiblockDefinition definition,
            List<MachinePortBlockEntity> outputPorts,
            List<ItemStack> outputs
    ) {
        if (outputs.isEmpty()) {
            return true;
        }

        /*
         * En eksisterende output bus har absolutt prioritet.
         * Hvis den finnes, må alle outputs få plass der.
         */
        if (!outputPorts.isEmpty()) {
            return canFitItemOutputs(outputPorts, outputs);
        }

        /*
         * World output brukes bare når multiblocken ikke har
         * noen gyldig output bus for denne ruten.
         */
        return !worldOutputPositions(definition).isEmpty();
    }

    private List<BlockPos> worldOutputPositions(MultiblockDefinition definition) {
        BlockState state = getBlockState();
        if (!state.hasProperty(MultiblockControllerBlock.FACING)) {
            return List.of();
        }

        Direction facing = state.getValue(MultiblockControllerBlock.FACING);
        return definition.worldInteractionPositions(
                worldPosition,
                facing,
                MultiblockDefinition.WorldInteractionType.OUTPUT
        );
    }

    private void dropItemOutputsInWorld(
            MultiblockDefinition definition,
            List<ItemStack> outputs
    ) {
        if (level == null || outputs.isEmpty()) {
            return;
        }

        List<BlockPos> positions = worldOutputPositions(definition);
        if (positions.isEmpty()) {
            return;
        }

        int index = 0;
        for (ItemStack output : outputs) {
            if (output.isEmpty()) {
                continue;
            }

            BlockPos pos = positions.get(index % positions.size());
            ItemEntity entity = new ItemEntity(
                    level,
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    output.copy()
            );
            entity.setDefaultPickUpDelay();
            entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            level.addFreshEntity(entity);
            index++;
        }
    }

    private static boolean canFitItemOutputs(List<MachinePortBlockEntity> ports, List<ItemStack> outputs) {
        return insertItemOutputs(ports, outputs, true);
    }

    private static boolean insertItemOutputs(List<MachinePortBlockEntity> ports, List<ItemStack> outputs, boolean simulate) {
        for (ItemStack output : outputs) {
            ItemStack remaining = output.copy();
            for (MachinePortBlockEntity port : ports) {
                ItemStackHandler handler = port.items();
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    remaining = handler.insertItem(slot, remaining, simulate);
                    if (remaining.isEmpty()) {
                        break;
                    }
                }
                if (remaining.isEmpty()) {
                    break;
                }
            }

            if (!remaining.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean canFitFluidOutputs(List<MachinePortBlockEntity> ports, List<FluidStack> outputs) {
        if (outputs.isEmpty()) {
            return true;
        }
        if (ports.isEmpty()) {
            return false;
        }

        List<SimulatedTank> tanks = new ArrayList<>();
        for (MachinePortBlockEntity port : ports) {
            for (FluidTank tank : port.fluidTanks()) {
                tanks.add(new SimulatedTank(tank.getFluid().copy(), tank.getCapacity()));
            }
        }

        for (FluidStack output : outputs) {
            FluidStack remaining = output.copy();
            for (SimulatedTank tank : tanks) {
                remaining = tank.fill(remaining);
                if (remaining.isEmpty()) {
                    break;
                }
            }
            if (!remaining.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean insertFluidOutputs(List<MachinePortBlockEntity> ports, List<FluidStack> outputs, boolean simulate) {
        for (FluidStack output : outputs) {
            FluidStack remaining = output.copy();
            for (MachinePortBlockEntity port : ports) {
                for (FluidTank tank : port.fluidTanks()) {
                    int filled = tank.fill(remaining, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
                    remaining.shrink(filled);
                    if (remaining.isEmpty()) {
                        break;
                    }
                }
                if (remaining.isEmpty()) {
                    break;
                }
            }
            if (!remaining.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<ItemStack> guaranteedItemOutputs(CERecipe recipe) {
        return recipe.itemOutputs().stream()
                .filter(output -> output.guaranteed() && !output.stack().isEmpty())
                .map(output -> output.stack().copy())
                .toList();
    }

    private static List<FluidStack> fluidOutputs(CERecipe recipe) {
        return recipe.fluidOutputs().stream()
                .filter(output -> !output.isEmpty())
                .map(FluidStack::copy)
                .toList();
    }

    private static List<ItemStack> displayItemInputs(CERecipe recipe) {
        List<ItemStack> stacks = new ArrayList<>();
        for (SizedIngredient input : recipe.itemInputs()) {
            ItemStack[] candidates = input.getItems();
            if (candidates.length == 0) {
                continue;
            }
            ItemStack stack = candidates[0].copy();
            stacks.add(stack);
        }
        return stacks;
    }

    private static List<FluidStack> displayFluidInputs(CERecipe recipe) {
        List<FluidStack> stacks = new ArrayList<>();
        for (SizedFluidIngredient input : recipe.fluidInputs()) {
            FluidStack[] candidates = input.getFluids();
            if (candidates.length == 0) {
                continue;
            }
            stacks.add(candidates[0].copy());
        }
        return stacks;
    }

    private static List<ItemStack> copyItems(List<ItemStack> stacks) {
        return stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
    }

    private static List<FluidStack> copyFluids(List<FluidStack> stacks) {
        return stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(FluidStack::copy)
                .toList();
    }

    private static List<ItemStack> multiplyItems(List<ItemStack> stacks, int multiplier) {
        int safeMultiplier = Math.max(1, multiplier);
        return stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(stack -> {
                    ItemStack copy = stack.copy();
                    copy.setCount(multiplyClamped(copy.getCount(), safeMultiplier));
                    return copy;
                })
                .toList();
    }

    private static List<FluidStack> multiplyFluids(List<FluidStack> stacks, int multiplier) {
        int safeMultiplier = Math.max(1, multiplier);
        return stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(stack -> stack.copyWithAmount(multiplyClamped(stack.getAmount(), safeMultiplier)))
                .toList();
    }

    private static int multiplyClamped(int value, int multiplier) {
        long result = (long) Math.max(0, value) * Math.max(1, multiplier);
        return (int) Math.min(Integer.MAX_VALUE, result);
    }

    private void clearActiveRecipe() {
        recipeProgress = 0;
        recipeDuration = 0;
        activeCEt = 0;
        activeSyncCooldown = 0;
    }

    private void detachParts() {
        if (level == null) {
            return;
        }

        for (BlockPos partPos : formedPositions) {
            if (partPos.equals(worldPosition)) {
                continue;
            }

            if (level.getBlockEntity(partPos) instanceof MultiblockPart part && worldPosition.equals(part.controllerPos())) {
                if (part instanceof MachinePortBlockEntity port) {
                    port.setAssembledOverlayTexture(null);
                }
                part.detachFromMultiblock();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Formed", formed);
        tag.putString("FormedVariant", formedVariant);
        tag.putInt("VariantLevel", variantLevel);
        if (formedTier != null) {
            tag.putString("FormedTier", formedTier.id());
        }
        tag.putInt("FormedCoilHeat", formedCoilHeat);
        tag.putInt("FormedCoilCount", formedCoilCount);
        tag.putInt("RecipeProgress", recipeProgress);
        tag.putInt("RecipeDuration", recipeDuration);
        tag.putInt("ActiveCEt", activeCEt);
        tag.putInt("ExternalCEt", externalCEt);
        if (preferredRecipeId != null) {
            tag.putString("PreferredRecipe", preferredRecipeId.toString());
        }
        interactionWear.save(tag);
        recipeLogic.save(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        formed = tag.getBoolean("Formed");
        formedVariant = tag.getString("FormedVariant");
        variantLevel = tag.getInt("VariantLevel");
        formedTier = null;
        if (tag.contains("FormedTier")) {
            String tierId = tag.getString("FormedTier");
            for (MachineTier tier : MachineTier.ALL) {
                if (tier.id().equals(tierId)) {
                    formedTier = tier;
                    break;
                }
            }
        }
        formedCoilHeat = tag.getInt("FormedCoilHeat");
        formedCoilCount = tag.getInt("FormedCoilCount");
        recipeProgress = tag.getInt("RecipeProgress");
        recipeDuration = tag.getInt("RecipeDuration");
        activeCEt = tag.getInt("ActiveCEt");
        externalCEt = tag.getInt("ExternalCEt");
        preferredRecipeId = tag.contains("PreferredRecipe") ? ResourceLocation.parse(tag.getString("PreferredRecipe")) : null;
        interactionWear.load(tag);
        recipeLogic.load(tag, registries);
        dirty = true;
    }

    private static long saturatedMultiply(long first, long second) {
        if (first > 0L && second > Long.MAX_VALUE / first) {
            return Long.MAX_VALUE;
        }
        return first * second;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setActive(boolean active) {
        boolean targetActive = active && formed;
        boolean changed = updateBlockActiveState(targetActive);
        boolean coilChanged = setCoilsActive(targetActive);
        boolean fireboxChanged = setFireboxesActive(targetActive);
        if (changed || coilChanged || fireboxChanged) {
            setChanged();
            syncToClient();
        }
    }

    private void updateBlockFormedState(boolean formed) {
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();
        if (!state.hasProperty(MultiblockControllerBlock.FORMED) || state.getValue(MultiblockControllerBlock.FORMED) == formed) {
            return;
        }

        level.setBlock(worldPosition, state.setValue(MultiblockControllerBlock.FORMED, formed), 3);
    }

    private boolean updateBlockActiveState(boolean active) {
        if (level == null) {
            return false;
        }

        BlockState state = getBlockState();
        if (!state.hasProperty(MultiblockControllerBlock.ACTIVE) || state.getValue(MultiblockControllerBlock.ACTIVE) == active) {
            return false;
        }

        level.setBlock(worldPosition, state.setValue(MultiblockControllerBlock.ACTIVE, active), 3);
        return true;
    }

    private boolean setCoilsActive(boolean active) {
        if (level == null) {
            return false;
        }

        boolean changed = false;
        for (BlockPos partPos : formedPositions) {
            BlockState state = level.getBlockState(partPos);
            if (!(state.getBlock() instanceof CoilBlock) || !state.hasProperty(CoilBlock.ACTIVE) || state.getValue(CoilBlock.ACTIVE) == active) {
                continue;
            }

            level.setBlock(partPos, state.setValue(CoilBlock.ACTIVE, active), 3);
            changed = true;
        }
        return changed;
    }

    private boolean setFireboxesActive(boolean active) {
        if (level == null) {
            return false;
        }

        boolean changed = false;
        for (BlockPos partPos : formedPositions) {
            BlockState state = level.getBlockState(partPos);
            if (!(state.getBlock() instanceof FireboxBlock) || !state.hasProperty(FireboxBlock.ACTIVE) || state.getValue(FireboxBlock.ACTIVE) == active) {
                continue;
            }

            level.setBlock(partPos, state.setValue(FireboxBlock.ACTIVE, active), 3);
            changed = true;
        }
        return changed;
    }

    private void syncToClient() {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }

    public boolean isProcessing() {
        MultiblockDefinition definition = currentDefinition();
        return recipeLogic.isProcessing()
                || (definition != null && definition.externalHeatSource() && externallyActive());
    }

    @Nullable
    private MultiblockDefinition currentDefinition() {
        if (!(getBlockState().getBlock() instanceof MultiblockControllerBlock controllerBlock)) {
            return null;
        }
        return currentDefinition(controllerBlock);
    }

    @Nullable
    private static MultiblockDefinition currentDefinition(MultiblockControllerBlock controllerBlock) {
        return MultiblockRegistry.byController(controllerBlock.controllerId()).orElse(null);
    }

    public int recipeProgress() {
        return recipeLogic.isProcessing() ? recipeLogic.progress() : recipeProgress;
    }

    public int recipeDuration() {
        return recipeLogic.isProcessing() ? recipeLogic.duration() : recipeDuration;
    }

    public int recipeRemaining() {
        return recipeLogic.isProcessing()
                ? recipeLogic.remaining()
                : Math.max(0, recipeDuration - recipeProgress);
    }

    public int activeCEt() {
        return recipeLogic.isProcessing() ? recipeLogic.resourcePerTick() : activeCEt;
    }

    public int activeParallel() {
        return recipeLogic.parallel();
    }

    @Nullable
    public ResourceLocation activeRecipeId() {
        CERecipeExecution execution = recipeLogic.execution();
        return execution == null ? null : execution.recipeId();
    }

    public List<ItemStack> activeItemInputs() {
        CERecipeExecution execution = recipeLogic.execution();
        return execution == null ? List.of() : execution.itemInputs();
    }

    public List<FluidStack> activeFluidInputs() {
        CERecipeExecution execution = recipeLogic.execution();
        return execution == null ? List.of() : execution.fluidInputs();
    }

    public List<ItemStack> activeItemOutputs() {
        CERecipeExecution execution = recipeLogic.execution();
        return execution == null ? List.of() : execution.itemOutputs();
    }

    public List<FluidStack> activeFluidOutputs() {
        CERecipeExecution execution = recipeLogic.execution();
        return execution == null ? List.of() : execution.fluidOutputs();
    }

    @Override
    public Component getDisplayName() {
        if (getBlockState().getBlock() instanceof MultiblockControllerBlock controller) {
            return Component.translatable(controller.getDescriptionId());
        }
        return Component.literal("Multiblock Controller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MultiblockControllerMenu(containerId, playerInventory, this);
    }

    @Override
    public CERecipeLogic recipeLogic() {
        return recipeLogic;
    }

    public ProgressBar progressBar() {
        MultiblockDefinition definition = currentDefinition();
        return definition == null ? ProgressBar.ARROW : definition.progressBar();
    }

    private Optional<CERecipe> recipeById(ResourceLocation recipeId) {
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager()
                .byKey(recipeId)
                .filter(holder -> holder.value() instanceof CERecipe)
                .map(holder -> (CERecipe) holder.value());
    }

    private InteractionContext interactionContext(List<ItemStack> items, List<FluidStack> fluids) {
        Direction facing = getBlockState().hasProperty(MultiblockControllerBlock.FACING)
                ? getBlockState().getValue(MultiblockControllerBlock.FACING)
                : Direction.NORTH;
        return new InteractionContext() {
            @Override
            public Level level() {
                return level;
            }

            @Override
            public BlockPos origin() {
                return worldPosition;
            }

            @Override
            public Direction facing() {
                return facing;
            }

            @Override
            public List<ItemStack> itemInputs() {
                return items;
            }

            @Override
            public List<FluidStack> fluidInputs() {
                return fluids;
            }

            @Override
            public InteractionWearStore wearStore() {
                return interactionWear;
            }
        };
    }

    private Optional<CERecipeExecution> prepareRecipeExecution(MultiblockDefinition definition) {
        if (level == null) {
            return Optional.empty();
        }

        List<MachinePortBlockEntity> inputPorts = itemPorts(MultiblockAbility.ITEM_INPUT);
        List<MachinePortBlockEntity> fluidInputPorts = fluidPorts(MultiblockAbility.FLUID_INPUT);
        Optional<MachineTier> kineticTier = highestPortTier(MultiblockAbility.KINETIC_INPUT);
        EnergyRuntime energyRuntime = energyRuntime();
        int rpm = maxKineticRpm();

        for (InputRoute route : inputRoutes(inputPorts, fluidInputPorts)) {
            List<ItemEntity> worldInputEntities = worldInputEntities(definition);
            List<ItemStack> availableItems = new ArrayList<>(itemStacks(route.itemPorts()));
            availableItems.addAll(worldItemStacks(worldInputEntities));
            List<FluidStack> availableFluids = fluidStacks(route.fluidPorts());
            InteractionContext interactionContext = interactionContext(availableItems, availableFluids);
            CERecipeInput input = new CERecipeInput(
                    availableItems,
                    availableFluids,
                    route.circuit(),
                    Set.copyOf(definition.logicIds()),
                    energyRuntime.recipeAccessTier().or(() -> Optional.ofNullable(formedTier)),
                    kineticTier,
                    energyRuntime.recipeAccessTier(),
                    rpm,
                    formedCoilHeat
            );

            Optional<RecipeHolder<CERecipe>> match = matchingRecipe(definition, input);
            if (match.isEmpty()) {
                continue;
            }

            CERecipe recipe = match.get().value();
            if (!InteractionRuntime.conditionsMatch(definition.conditions(), interactionContext, InteractionPhase.ON_START)
                    || !InteractionRuntime.conditionsMatch(recipe.conditions(), interactionContext, InteractionPhase.ON_START)
                    || !InteractionRuntime.interactionsMatch(definition.blockInteractions(), interactionContext, InteractionPhase.ON_START)
                    || !InteractionRuntime.interactionsMatch(recipe.blockInteractions(), interactionContext, InteractionPhase.ON_START)
                    || !InteractionRuntime.interactionsMatch(definition.blockInteractions(), interactionContext, InteractionPhase.ON_COMPLETE)
                    || !InteractionRuntime.interactionsMatch(recipe.blockInteractions(), interactionContext, InteractionPhase.ON_COMPLETE)) {
                continue;
            }
            List<MachinePortBlockEntity> outputPorts = outputPorts(MultiblockAbility.ITEM_OUTPUT, route);
            List<MachinePortBlockEntity> fluidOutputPorts = outputPorts(MultiblockAbility.FLUID_OUTPUT, route);
            MachineTier runtimeTier = runtimeTier(recipe, input, energyRuntime);
            int parallel = recipe.generatesEnergy() ? outputParallel(recipe, runtimeTier) : 1;
            Optional<MachineModifier> machineModifier =
                    InteractionRuntime.firstMatchingModifier(definition.modifiers(), interactionContext);
            Optional<MachineModifier> recipeModifier =
                    InteractionRuntime.firstMatchingModifier(recipe.modifiers(), interactionContext);
            List<ItemStack> possibleItemOutputs = new ArrayList<>();
            for (int run = 0; run < parallel; run++) {
                recipe.itemOutputs().forEach(output -> possibleItemOutputs.add(output.stack().copy()));
            }
            List<FluidStack> plannedFluidOutputs = multiplyFluids(fluidOutputs(recipe), parallel);
            int signedCEt = signedRuntimeCEt(recipe, runtimeTier, parallel);
            signedCEt = recipe.generatesEnergy()
                    ? signedCEt
                    : InteractionRuntime.adjustedResource(signedCEt, false, machineModifier, recipeModifier);

            if (!canRouteItemOutputs(definition, outputPorts, possibleItemOutputs)
                    || !canFitFluidOutputs(fluidOutputPorts, plannedFluidOutputs)
                    || !canProcessEnergy(signedCEt)
                    || !canConsumeInputs(route.itemPorts(), worldInputEntities, recipe, parallel)
                    || !canConsumeFluidInputs(route.fluidPorts(), recipe, parallel)) {
                continue;
            }
            if (!consumeInputs(route.itemPorts(), worldInputEntities, recipe, parallel)
                    || !consumeFluidInputs(route.fluidPorts(), recipe, parallel)) {
                continue;
            }

            List<ItemStack> plannedItemOutputs = rollItemOutputs(recipe, parallel);
            InteractionRuntime.applyInteractions(definition.blockInteractions(), interactionContext, InteractionPhase.ON_START);
            InteractionRuntime.applyInteractions(recipe.blockInteractions(), interactionContext, InteractionPhase.ON_START);
            CompoundTag machineData = new CompoundTag();
            machineData.putBoolean("UsesIoColor", route.usesColor());
            machineData.putInt("IoColor", route.color().getId());
            preferredRecipeId = match.get().id();
            return Optional.of(new CERecipeExecution(
                    match.get().id(),
                    recipe.recipeType(),
                    InteractionRuntime.adjustedDuration(recipe.runtimeDuration(runtimeTier, rpm), machineModifier, recipeModifier),
                    signedCEt,
                    parallel,
                    multiplyItems(displayItemInputs(recipe), parallel),
                    multiplyFluids(displayFluidInputs(recipe), parallel),
                    plannedItemOutputs,
                    plannedFluidOutputs,
                    machineData
            ));
        }
        return Optional.empty();
    }

    private InputRoute executionRoute(CERecipeExecution execution) {
        CompoundTag data = execution.machineData();
        return data.getBoolean("UsesIoColor")
                ? InputRoute.outputOnly(DyeColor.byId(data.getInt("IoColor")))
                : InputRoute.uncolored();
    }

    private List<ItemStack> rollItemOutputs(CERecipe recipe, int parallel) {
        if (level == null) {
            return List.of();
        }
        List<ItemStack> outputs = new ArrayList<>();
        for (int run = 0; run < Math.max(1, parallel); run++) {
            for (net.mads.createexpansion.recipe.CEChancedItemOutput output : recipe.itemOutputs()) {
                if (output.chance() >= net.mads.createexpansion.recipe.CEChancedItemOutput.MAX_CHANCE
                        || level.random.nextInt(net.mads.createexpansion.recipe.CEChancedItemOutput.MAX_CHANCE)
                        < output.chance()) {
                    outputs.add(output.stack().copy());
                }
            }
        }
        return outputs;
    }

    private final class RecipeHost implements CERecipeLogicHost {
        @Override
        public boolean recipeMachineReady() {
            MultiblockDefinition definition = currentDefinition();
            return level != null && formed && definition != null && !definition.recipeTypes().isEmpty();
        }

        @Override
        public Optional<CERecipeExecution> findAndConsumeRecipeInputs() {
            MultiblockDefinition definition = currentDefinition();
            return definition == null ? Optional.empty() : prepareRecipeExecution(definition);
        }

        @Override
        public CERecipeTickResult consumeRecipeTick(CERecipeExecution execution) {
            MultiblockDefinition definition = currentDefinition();
            Optional<CERecipe> recipe = recipeById(execution.recipeId());
            InteractionContext context = interactionContext(execution.itemInputs(), execution.fluidInputs());
            if (definition == null) {
                return CERecipeTickResult.WAIT_FOR_RESOURCE;
            }
            ConditionFailure machineFailure = InteractionRuntime.failedConditionBehavior(definition.conditions(), context, InteractionPhase.WHILE_PROCESSING);
            ConditionFailure recipeFailure = recipe.map(value -> InteractionRuntime.failedConditionBehavior(value.conditions(), context, InteractionPhase.WHILE_PROCESSING)).orElse(null);
            ConditionFailure failure = machineFailure != null ? machineFailure : recipeFailure;
            if (failure != null) {
                return failure == ConditionFailure.CANCEL
                        ? CERecipeTickResult.CANCEL
                        : failure == ConditionFailure.RESET ? CERecipeTickResult.WAIT_FOR_RESOURCE : CERecipeTickResult.PAUSE;
            }
            return processEnergy(execution.resourcePerTick())
                    ? CERecipeTickResult.CONTINUE
                    : CERecipeTickResult.WAIT_FOR_RESOURCE;
        }

        @Override
        public boolean canCompleteRecipe(CERecipeExecution execution) {
            MultiblockDefinition definition = currentDefinition();
            if (definition == null) {
                return false;
            }
            Optional<CERecipe> recipe = recipeById(execution.recipeId());
            if (recipe.isEmpty()) {
                return false;
            }
            InteractionContext context = interactionContext(execution.itemInputs(), execution.fluidInputs());
            InputRoute route = executionRoute(execution);
            return canRouteItemOutputs(
                    definition,
                    outputPorts(MultiblockAbility.ITEM_OUTPUT, route),
                    execution.itemOutputs()
            ) && canFitFluidOutputs(
                    outputPorts(MultiblockAbility.FLUID_OUTPUT, route),
                    execution.fluidOutputs()
            ) && InteractionRuntime.conditionsMatch(definition.conditions(), context, InteractionPhase.ON_COMPLETE)
                    && InteractionRuntime.conditionsMatch(recipe.get().conditions(), context, InteractionPhase.ON_COMPLETE)
                    && InteractionRuntime.interactionsMatch(definition.blockInteractions(), context, InteractionPhase.ON_COMPLETE)
                    && InteractionRuntime.interactionsMatch(recipe.get().blockInteractions(), context, InteractionPhase.ON_COMPLETE);
        }

        @Override
        public void completeRecipe(CERecipeExecution execution) {
            MultiblockDefinition definition = currentDefinition();
            if (definition == null) {
                return;
            }
            Optional<CERecipe> recipe = recipeById(execution.recipeId());
            if (recipe.isPresent()) {
                InteractionContext context = interactionContext(execution.itemInputs(), execution.fluidInputs());
                InteractionRuntime.applyInteractions(definition.blockInteractions(), context, InteractionPhase.ON_COMPLETE);
                InteractionRuntime.applyInteractions(recipe.get().blockInteractions(), context, InteractionPhase.ON_COMPLETE);
            }
            InputRoute route = executionRoute(execution);
            List<MachinePortBlockEntity> itemOutputPorts =
                    outputPorts(MultiblockAbility.ITEM_OUTPUT, route);
            if (itemOutputPorts.isEmpty()) {
                dropItemOutputsInWorld(definition, execution.itemOutputs());
            } else {
                insertItemOutputs(itemOutputPorts, execution.itemOutputs(), false);
            }
            insertFluidOutputs(
                    outputPorts(MultiblockAbility.FLUID_OUTPUT, route),
                    execution.fluidOutputs(),
                    false
            );
        }

        @Override
        public void onRecipeLogicChanged(boolean activeChanged) {
            setActive(recipeLogic.isActive());
            setChanged();
            if (activeChanged) {
                syncToClient();
            } else {
                syncActiveProgress();
            }
        }
    }

    private static final class SimulatedTank {
        private FluidStack fluid;
        private final int capacity;

        private SimulatedTank(FluidStack fluid, int capacity) {
            this.fluid = fluid;
            this.capacity = capacity;
        }

        FluidStack fill(FluidStack stack) {
            if (stack.isEmpty()) {
                return FluidStack.EMPTY;
            }
            if (!fluid.isEmpty() && !FluidStack.isSameFluidSameComponents(fluid, stack)) {
                return stack;
            }

            int space = capacity - fluid.getAmount();
            if (space <= 0) {
                return stack;
            }

            int filled = Math.min(space, stack.getAmount());
            FluidStack remaining = stack.copyWithAmount(stack.getAmount() - filled);
            if (fluid.isEmpty()) {
                fluid = stack.copyWithAmount(filled);
            } else {
                fluid.grow(filled);
            }
            return remaining.isEmpty() ? FluidStack.EMPTY : remaining;
        }
    }

    private record InputRoute(Optional<Integer> circuit, DyeColor color, boolean usesColor, List<MachinePortBlockEntity> itemPorts, List<MachinePortBlockEntity> fluidPorts) {
        private static InputRoute uncolored() {
            return new InputRoute(Optional.empty(), DyeColor.GRAY, false, List.of(), List.of());
        }

        private static InputRoute outputOnly(DyeColor color) {
            return new InputRoute(Optional.empty(), color, true, List.of(), List.of());
        }
    }

    private record EnergyRuntime(Optional<MachineTier> recipeAccessTier, Optional<MachineTier> overclockTier) {
    }
}
