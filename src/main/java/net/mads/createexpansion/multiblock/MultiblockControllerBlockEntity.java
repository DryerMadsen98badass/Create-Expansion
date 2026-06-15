package net.mads.createexpansion.multiblock;

import net.mads.createexpansion.energy.CEEnergyContainer;
import net.mads.createexpansion.debug.CEPerformanceProfiler;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeInput;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

public class MultiblockControllerBlockEntity extends BlockEntity implements MenuProvider {
    private static final int VALIDATION_INTERVAL = 40;
    private static final int IDLE_RECIPE_CHECK_INTERVAL = 5;
    private static final int ACTIVE_SYNC_INTERVAL = 20;

    private boolean formed;
    private boolean dirty = true;
    private int validationCooldown;
    private int recipeCheckCooldown;
    private String formedVariant = "";
    private int variantLevel;
    private MachineTier formedTier;
    private List<BlockPos> formedPositions = List.of();
    private Map<MultiblockAbility, List<BlockPos>> abilityPositions = new EnumMap<>(MultiblockAbility.class);
    private int recipeProgress;
    private int recipeDuration;
    private int activeCEt;
    private int activeSyncCooldown;
    private ResourceLocation activeRecipeId;
    private ResourceLocation preferredRecipeId;
    private boolean activeUsesIoColor;
    private DyeColor activeIoColor = DyeColor.GRAY;
    private List<ItemStack> activeItemInputs = List.of();
    private List<FluidStack> activeFluidInputs = List.of();
    private List<ItemStack> activeItemOutputs = List.of();
    private List<FluidStack> activeFluidOutputs = List.of();

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
        detachParts();
        formed = false;
        formedVariant = "";
        variantLevel = 0;
        formedTier = null;
        formedPositions = List.of();
        abilityPositions = new EnumMap<>(MultiblockAbility.class);
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
        MultiblockDefinition definition = MultiblockRegistry.byController(controllerBlock.controllerId()).orElse(null);
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
        detachParts();
        formed = true;
        formedVariant = result.variant();
        variantLevel = result.variantLevel();
        formedTier = result.tier();
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
            clearActiveRecipe();
            setActive(false);
            return;
        }

        if (activeRecipeId != null) {
            tickActiveRecipe();
            return;
        }

        if (recipeCheckCooldown > 0) {
            recipeCheckCooldown--;
            return;
        }

        MultiblockDefinition definition = MultiblockRegistry.byController(controllerBlock.controllerId()).orElse(null);
        if (definition == null || definition.recipeTypes().isEmpty()) {
            clearActiveRecipe();
            setActive(false);
            return;
        }

        if (tryStartRecipe(definition)) {
            return;
        }

        clearActiveRecipe();
        setActive(false);
        recipeCheckCooldown = IDLE_RECIPE_CHECK_INTERVAL;
    }

    private boolean tryStartRecipe(MultiblockDefinition definition) {
        if (level == null) {
            return false;
        }

        List<MachinePortBlockEntity> inputPorts = itemPorts(MultiblockAbility.ITEM_INPUT);
        List<MachinePortBlockEntity> fluidInputPorts = fluidPorts(MultiblockAbility.FLUID_INPUT);
        Optional<MachineTier> kineticTier = highestPortTier(MultiblockAbility.KINETIC_INPUT);
        Optional<MachineTier> energyTier = highestPortTier(MultiblockAbility.ENERGY_INPUT);
        int rpm = maxKineticRpm();

        for (InputRoute route : inputRoutes(inputPorts, fluidInputPorts)) {
            CERecipeInput input = new CERecipeInput(
                    itemStacks(route.itemPorts()),
                    fluidStacks(route.fluidPorts()),
                    route.circuit(),
                    Set.copyOf(definition.logicIds()),
                    Optional.ofNullable(formedTier),
                    kineticTier,
                    energyTier,
                    rpm
            );

            Optional<RecipeHolder<CERecipe>> match = matchingRecipe(definition, input);
            if (match.isEmpty()) {
                continue;
            }

            CERecipe recipe = match.get().value();
            List<MachinePortBlockEntity> outputPorts = outputPorts(MultiblockAbility.ITEM_OUTPUT, route);
            List<MachinePortBlockEntity> fluidOutputPorts = outputPorts(MultiblockAbility.FLUID_OUTPUT, route);
            List<ItemStack> plannedItemOutputs = guaranteedItemOutputs(recipe);
            List<FluidStack> plannedFluidOutputs = fluidOutputs(recipe);
            if (!canFitItemOutputs(outputPorts, plannedItemOutputs) || !canFitFluidOutputs(fluidOutputPorts, plannedFluidOutputs)) {
                continue;
            }

            MachineTier runtimeTier = runtimeTier(recipe, input);
            int signedCEt = signedRuntimeCEt(recipe, runtimeTier);
            if (!canProcessEnergy(signedCEt)) {
                continue;
            }

            if (!canConsumeInputs(route.itemPorts(), recipe) || !canConsumeFluidInputs(route.fluidPorts(), recipe)) {
                continue;
            }

            if (!consumeInputs(route.itemPorts(), recipe) || !consumeFluidInputs(route.fluidPorts(), recipe)) {
                continue;
            }

            startRecipe(match.get().id(), recipe, plannedItemOutputs, plannedFluidOutputs, route, recipe.runtimeDuration(runtimeTier, rpm), signedCEt);
            return true;
        }

        return false;
    }

    private void tickActiveRecipe() {
        if (level == null) {
            return;
        }

        recipeCheckCooldown = 0;

        if (recipeDuration <= 0) {
            recipeDuration = 1;
        }

        if (recipeProgress < recipeDuration) {
            if (!processEnergy(activeCEt)) {
                setActive(false);
                return;
            }
            setActive(true);
            recipeProgress++;
            setChanged();
            syncActiveProgress();
            return;
        }

        InputRoute route = activeUsesIoColor ? InputRoute.outputOnly(activeIoColor) : InputRoute.uncolored();
        List<MachinePortBlockEntity> outputPorts = outputPorts(MultiblockAbility.ITEM_OUTPUT, route);
        List<MachinePortBlockEntity> fluidOutputPorts = outputPorts(MultiblockAbility.FLUID_OUTPUT, route);
        if (!canFitItemOutputs(outputPorts, activeItemOutputs) || !canFitFluidOutputs(fluidOutputPorts, activeFluidOutputs)) {
            setChanged();
            syncActiveProgress();
            return;
        }

        insertItemOutputs(outputPorts, activeItemOutputs, false);
        insertFluidOutputs(fluidOutputPorts, activeFluidOutputs, false);
        clearActiveRecipe();
        recipeCheckCooldown = 0;

        MultiblockDefinition definition = null;
        if (getBlockState().getBlock() instanceof MultiblockControllerBlock controllerBlock) {
            definition = MultiblockRegistry.byController(controllerBlock.controllerId()).orElse(null);
        }
        if (definition != null && !definition.recipeTypes().isEmpty() && tryStartRecipe(definition)) {
            setChanged();
            return;
        }

        setActive(false);
        setChanged();
        syncToClient();
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

    private void startRecipe(ResourceLocation recipeId, CERecipe recipe, List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs, InputRoute route, int duration, int signedCEt) {
        activeRecipeId = recipeId;
        preferredRecipeId = recipeId;
        recipeProgress = 0;
        recipeDuration = Math.max(1, duration);
        activeCEt = signedCEt;
        activeSyncCooldown = 0;
        activeUsesIoColor = route.usesColor();
        activeIoColor = route.color();
        activeItemInputs = displayItemInputs(recipe);
        activeFluidInputs = displayFluidInputs(recipe);
        activeItemOutputs = copyItems(itemOutputs);
        activeFluidOutputs = copyFluids(fluidOutputs);
        setActive(true);
        setChanged();
        syncToClient();
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

        List<RecipeHolder<CERecipe>> recipes = level.getRecipeManager().getAllRecipesFor(RecipeRegistry.MACHINE_RECIPE_TYPE.get());
        if (preferredRecipeId != null) {
            Optional<RecipeHolder<CERecipe>> preferred = recipes.stream()
                    .filter(recipe -> recipe.id().equals(preferredRecipeId))
                    .filter(recipe -> definition.recipeTypes().contains(recipe.value().recipeType()))
                    .filter(recipe -> recipe.value().matches(input, level))
                    .findFirst();
            if (preferred.isPresent()) {
                return preferred;
            }
        }

        return recipes.stream()
                .filter(recipe -> definition.recipeTypes().contains(recipe.value().recipeType()))
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

    private static MachineTier runtimeTier(CERecipe recipe, CERecipeInput input) {
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

    private static int signedRuntimeCEt(CERecipe recipe, MachineTier runtimeTier) {
        int runtimeCEt = recipe.runtimeCEt(runtimeTier);
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

            int moved = signedCEt > 0 ? container.extract(remaining, simulate) : container.insert(remaining, simulate);
            remaining -= moved;
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

    private static boolean consumeInputs(List<MachinePortBlockEntity> ports, CERecipe recipe) {
        return consumeInputs(ports, recipe, false);
    }

    private static boolean canConsumeInputs(List<MachinePortBlockEntity> ports, CERecipe recipe) {
        return consumeInputs(ports, recipe, true);
    }

    private static boolean consumeInputs(List<MachinePortBlockEntity> ports, CERecipe recipe, boolean simulate) {
        for (SizedIngredient input : recipe.itemInputs()) {
            int remaining = input.count();
            for (MachinePortBlockEntity port : ports) {
                ItemStackHandler handler = port.items();
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    ItemStack stack = handler.getStackInSlot(slot);
                    if (stack.isEmpty() || !input.ingredient().test(stack)) {
                        continue;
                    }

                    int taken = Math.min(remaining, stack.getCount());
                    if (!simulate) {
                        stack.shrink(taken);
                        handler.setStackInSlot(slot, stack);
                    }
                    remaining -= taken;
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

    private static boolean consumeFluidInputs(List<MachinePortBlockEntity> ports, CERecipe recipe) {
        return consumeFluidInputs(ports, recipe, FluidAction.EXECUTE);
    }

    private static boolean canConsumeFluidInputs(List<MachinePortBlockEntity> ports, CERecipe recipe) {
        return consumeFluidInputs(ports, recipe, FluidAction.SIMULATE);
    }

    private static boolean consumeFluidInputs(List<MachinePortBlockEntity> ports, CERecipe recipe, FluidAction action) {
        for (SizedFluidIngredient input : recipe.fluidInputs()) {
            int remaining = input.amount();
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

    private void clearActiveRecipe() {
        recipeProgress = 0;
        recipeDuration = 0;
        activeCEt = 0;
        activeSyncCooldown = 0;
        activeRecipeId = null;
        activeUsesIoColor = false;
        activeIoColor = DyeColor.GRAY;
        activeItemInputs = List.of();
        activeFluidInputs = List.of();
        activeItemOutputs = List.of();
        activeFluidOutputs = List.of();
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
        tag.putInt("RecipeProgress", recipeProgress);
        tag.putInt("RecipeDuration", recipeDuration);
        tag.putInt("ActiveCEt", activeCEt);
        if (activeRecipeId != null) {
            tag.putString("ActiveRecipe", activeRecipeId.toString());
        }
        if (preferredRecipeId != null) {
            tag.putString("PreferredRecipe", preferredRecipeId.toString());
        }
        tag.putBoolean("ActiveUsesIoColor", activeUsesIoColor);
        tag.putInt("ActiveIoColor", activeIoColor.getId());
        tag.put("ActiveItemInputs", saveItems(activeItemInputs, registries));
        tag.put("ActiveFluidInputs", saveFluids(activeFluidInputs, registries));
        tag.put("ActiveItemOutputs", saveItems(activeItemOutputs, registries));
        tag.put("ActiveFluidOutputs", saveFluids(activeFluidOutputs, registries));
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
        recipeProgress = tag.getInt("RecipeProgress");
        recipeDuration = tag.getInt("RecipeDuration");
        activeCEt = tag.getInt("ActiveCEt");
        activeRecipeId = tag.contains("ActiveRecipe") ? ResourceLocation.parse(tag.getString("ActiveRecipe")) : null;
        preferredRecipeId = tag.contains("PreferredRecipe") ? ResourceLocation.parse(tag.getString("PreferredRecipe")) : null;
        activeUsesIoColor = tag.getBoolean("ActiveUsesIoColor");
        activeIoColor = tag.contains("ActiveIoColor") ? DyeColor.byId(tag.getInt("ActiveIoColor")) : DyeColor.GRAY;
        activeItemInputs = loadItems(tag.getList("ActiveItemInputs", Tag.TAG_COMPOUND), registries);
        activeFluidInputs = loadFluids(tag.getList("ActiveFluidInputs", Tag.TAG_COMPOUND), registries);
        activeItemOutputs = loadItems(tag.getList("ActiveItemOutputs", Tag.TAG_COMPOUND), registries);
        activeFluidOutputs = loadFluids(tag.getList("ActiveFluidOutputs", Tag.TAG_COMPOUND), registries);
        dirty = true;
    }

    private static ListTag saveItems(List<ItemStack> stacks, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (ItemStack stack : stacks) {
            list.add(stack.saveOptional(registries));
        }
        return list;
    }

    private static List<ItemStack> loadItems(ListTag list, HolderLookup.Provider registries) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.parseOptional(registries, list.getCompound(i));
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    private static ListTag saveFluids(List<FluidStack> stacks, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (FluidStack stack : stacks) {
            list.add(stack.saveOptional(registries));
        }
        return list;
    }

    private static List<FluidStack> loadFluids(ListTag list, HolderLookup.Provider registries) {
        List<FluidStack> stacks = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FluidStack stack = FluidStack.parseOptional(registries, list.getCompound(i));
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
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
        if (changed) {
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

    private void syncToClient() {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }

    public boolean isProcessing() {
        return activeRecipeId != null;
    }

    public int recipeProgress() {
        return recipeProgress;
    }

    public int recipeDuration() {
        return recipeDuration;
    }

    public int recipeRemaining() {
        return Math.max(0, recipeDuration - recipeProgress);
    }

    @Nullable
    public ResourceLocation activeRecipeId() {
        return activeRecipeId;
    }

    public List<ItemStack> activeItemInputs() {
        return activeItemInputs;
    }

    public List<FluidStack> activeFluidInputs() {
        return activeFluidInputs;
    }

    public List<ItemStack> activeItemOutputs() {
        return activeItemOutputs;
    }

    public List<FluidStack> activeFluidOutputs() {
        return activeFluidOutputs;
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
}
