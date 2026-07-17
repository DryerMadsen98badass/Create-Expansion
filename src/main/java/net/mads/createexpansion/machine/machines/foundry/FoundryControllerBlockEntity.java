package net.mads.createexpansion.machine.machines.foundry;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.mads.createexpansion.machine.machines.electric.multiblock.machines.Heater;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialLookup;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipe;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mads.createexpansion.menu.FoundryControllerMenu;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoundryControllerBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] VALID_WIDTHS = {3, 5, 7, 9};
    private static final int MB_PER_INSIDE_BLOCK = 144 * 9;
    private static final int VALIDATION_INTERVAL = 40;
    private static final int HEAT_INTERVAL = 20;
    private static final int BLAZE_BURNER_HEATED_TEMPERATURE = 750;
    private static final int BLAZE_BURNER_SUPERHEATED_TEMPERATURE = 2000;
    private static final int ACTIVE_OVERLAY_TEMPERATURE = 100;
    private static final int MAX_MELTING_SLOTS = 7 * 7 * 32;
    private static final int MAX_ALLOY_RESOLVE_PASSES = 32;
    private static final List<Set<IndustrialMaterial>> ALLOY_FAMILIES = buildAlloyFamilies();

    private boolean formed;
    private int outerWidth;
    private int outerHeight;
    private int capacityMb;
    private int validationCooldown;
    private int heatCooldown;
    private int temperature;
    private final List<FluidStack> fluids = new ArrayList<>();
    private final NonNullList<ItemStack> meltingItems = NonNullList.withSize(MAX_MELTING_SLOTS, ItemStack.EMPTY);
    private final int[] meltingProgress = new int[MAX_MELTING_SLOTS];
    private final Container meltingContainer = new FoundryMeltingContainer();
    private BlockPos bottomMin;
    private BlockPos bottomMax;
    private List<BlockPos> attachedHatches = List.of();

    public FoundryControllerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.FOUNDRY_CONTROLLER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FoundryControllerBlockEntity foundry) {
        if (level.isClientSide()) {
            return;
        }
        if (foundry.validationCooldown > 0) {
            foundry.validationCooldown--;
        } else {
            foundry.validationCooldown = VALIDATION_INTERVAL;
            foundry.tryUpdateStructure();
        }
        foundry.tickHeat();
        foundry.tickMelting();
    }

    public boolean isFormed() {
        return formed;
    }

    public int outerWidth() {
        return outerWidth;
    }

    public int outerHeight() {
        return outerHeight;
    }

    public int capacityMb() {
        return capacityMb;
    }

    public int temperature() {
        return temperature;
    }

    public boolean isCreativeTemperatureController() {
        return getBlockState().getBlock() == BlockRegistry.CREATIVE_FOUNDRY_CONTROLLER.get();
    }

    public void setCreativeTemperature(int temperature) {
        if (!isCreativeTemperatureController()) {
            return;
        }
        this.temperature = Math.max(0, Math.min(10000, temperature));
        updateBlockActiveState();
        contentChanged();
    }

    public int meltingSlotCount() {
        if (!formed || outerWidth <= 2) {
            return 0;
        }
        int insideWidth = outerWidth - 2;
        int insideHeight = Math.max(1, outerHeight - 1);
        return Math.min(MAX_MELTING_SLOTS, insideWidth * insideWidth * insideHeight);
    }

    public Container meltingContainer() {
        return meltingContainer;
    }

    public boolean canInsertMeltingItem(ItemStack stack) {
        MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
        return target != null
                && FoundryMeltingRecipes.canMelt(stack)
                && canAcceptMaterial(target.material());
    }

    public ItemStack insertMeltingItem(ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !canInsertMeltingItem(stack)) {
            return stack;
        }

        ItemStack remaining = stack.copy();
        int slots = meltingSlotCount();
        int emptySlots = 0;
        for (int slot = 0; slot < slots; slot++) {
            if (meltingItems.get(slot).isEmpty()) {
                emptySlots++;
            }
        }
        if (emptySlots <= 0) {
            return remaining;
        }

        int accepted = Math.min(remaining.getCount(), emptySlots);
        int inserted = 0;
        for (int slot = 0; slot < slots && !remaining.isEmpty(); slot++) {
            if (!meltingItems.get(slot).isEmpty()) {
                continue;
            }

            if (!simulate) {
                meltingItems.set(slot, remaining.copyWithCount(1));
                meltingProgress[slot] = 0;
            }
            remaining.shrink(1);
            inserted++;
            if (inserted >= accepted) {
                break;
            }
        }

        if (!simulate && remaining.getCount() != stack.getCount()) {
            contentChanged();
        }
        return remaining;
    }

    public int meltingProgress(int slot) {
        return slot >= 0 && slot < meltingProgress.length ? meltingProgress[slot] : 0;
    }

    public int meltingDuration(int slot) {
        FoundryMeltingRecipe recipe = slot >= 0 && slot < meltingSlotCount() ? meltingRecipe(meltingItems.get(slot)) : null;
        return recipe == null ? 0 : recipe.durationAt(temperature);
    }

    private void tickHeat() {
        if (isCreativeTemperatureController()) {
            return;
        }
        if (heatCooldown > 0) {
            heatCooldown--;
            return;
        }
        heatCooldown = HEAT_INTERVAL;

        int targetTemperature = heatTargetTemperature();
        if (targetTemperature == temperature) {
            return;
        }

        temperature += targetTemperature > temperature ? 1 : -1;
        updateBlockActiveState();
        contentChanged();
    }

    private int heatTargetTemperature() {
        if (!formed || level == null || bottomMin == null || bottomMax == null) {
            return 0;
        }

        ElectricHeaterSource electricSource = findElectricHeaterSource();
        if (electricSource != null) {
            return electricHeaterTargetTemperature(electricSource);
        }

        return blazeBurnerTargetTemperature();
    }

    private int blazeBurnerTargetTemperature() {
        int target = Integer.MAX_VALUE;
        int required = 0;
        int present = 0;
        int y = bottomMin.getY() - 1;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = bottomMin.getX() + 1; x <= bottomMax.getX() - 1; x++) {
            for (int z = bottomMin.getZ() + 1; z <= bottomMax.getZ() - 1; z++) {
                required++;
                cursor.set(x, y, z);
                BlockState state = level.getBlockState(cursor);
                if (!state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
                    continue;
                }

                BlazeBurnerBlock.HeatLevel heatLevel = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
                if (heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING) {
                    target = Math.min(target, BLAZE_BURNER_SUPERHEATED_TEMPERATURE);
                    present++;
                } else if (heatLevel == BlazeBurnerBlock.HeatLevel.KINDLED) {
                    target = Math.min(target, BLAZE_BURNER_HEATED_TEMPERATURE);
                    present++;
                }
            }
        }
        return present == required && required > 0 ? target : 0;
    }

    private int electricHeaterTargetTemperature(ElectricHeaterSource source) {
        int energyPerTick = Heater.energyPerTick(source.controller().formedCoilHeat(), source.controller().formedCoilCount(), insideVolume());
        if (!source.controller().consumeExternalHeatEnergy(energyPerTick, HEAT_INTERVAL)) {
            return 0;
        }
        if (!source.controller().externalHeatReady()) {
            return 0;
        }
        return source.controller().formedCoilHeat();
    }

    @Nullable
    private ElectricHeaterSource findElectricHeaterSource() {
        if (level == null || bottomMin == null || bottomMax == null) {
            return null;
        }

        int heatLayerY = bottomMin.getY() - 1;
        int controllerMinY = heatLayerY - 1;
        for (int y = heatLayerY; y >= controllerMinY; y--) {
            for (int x = bottomMin.getX(); x <= bottomMax.getX(); x++) {
                for (int z = bottomMin.getZ(); z <= bottomMax.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!(level.getBlockEntity(pos) instanceof MultiblockControllerBlockEntity controller) || !isLargeHeater(controller)) {
                        continue;
                    }

                    if (controller.formedCoilHeat() <= 0
                            || controller.formedCoilCount() <= 0
                            || controller.variantLevel() != requiredHeaterVariantLevel()
                            || !heaterCoversInnerFootprint(controller, heatLayerY)) {
                        continue;
                    }

                    return new ElectricHeaterSource(controller);
                }
            }
        }
        return null;
    }

    private int insideVolume() {
        int insideWidth = Math.max(1, outerWidth - 2);
        int insideHeight = Math.max(1, outerHeight - 1);
        return insideWidth * insideWidth * insideHeight;
    }

    private int requiredHeaterVariantLevel() {
        return Math.max(1, (outerWidth - 1) / 2);
    }

    private boolean heaterCoversInnerFootprint(MultiblockControllerBlockEntity controller, int y) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = bottomMin.getX() + 1; x <= bottomMax.getX() - 1; x++) {
            for (int z = bottomMin.getZ() + 1; z <= bottomMax.getZ() - 1; z++) {
                cursor.set(x, y, z);
                if (!controller.hasFormedPosition(cursor.immutable())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isLargeHeater(MultiblockControllerBlockEntity controller) {
        return controller.isFormed()
                && controller.getBlockState().getBlock() instanceof MultiblockControllerBlock controllerBlock
                && controllerBlock.controllerId().equals(Heater.CONTROLLER.id());
    }

    public FluidStack fluidInTank() {
        return fluids.isEmpty() ? FluidStack.EMPTY : fluids.getFirst().copy();
    }

    public List<FluidStack> fluids() {
        return fluids.stream()
                .filter(stack -> !stack.isEmpty())
                .map(FluidStack::copy)
                .toList();
    }

    public int fluidAmount() {
        return fluids.stream().mapToInt(FluidStack::getAmount).sum();
    }

    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || capacityMb <= 0) {
            return 0;
        }

        MaterialLookup.MaterialTarget target = MaterialLookup.find(resource);
        if (target == null || !canAcceptMaterial(target.material())) {
            return 0;
        }

        int filled = Math.min(resource.getAmount(), capacityMb - fluidAmount());
        if (filled <= 0) {
            return 0;
        }

        if (action.execute()) {
            FluidStack existing = matchingFluid(resource);
            if (existing == null) {
                fluids.add(resource.copyWithAmount(filled));
            } else {
                existing.grow(filled);
            }
            resolveAlloys();
            contentChanged();
        }
        return filled;
    }

    private void resolveAlloys() {
        boolean changed;
        int passes = 0;
        do {
            changed = false;
            for (IndustrialMaterial alloy : IndustrialMaterials.ALL) {
                if (!canAutoAlloy(alloy)) {
                    continue;
                }

                int batches = alloyBatchCount(alloy);
                if (batches <= 0) {
                    continue;
                }

                int outputAmount = 0;
                for (var component : alloy.components()) {
                    int consumed = component.amount() * batches;
                    outputAmount += consumed;
                    consumeMaterialFluid(component.material(), consumed);
                }

                addResolvedFluid(alloy, outputAmount);
                cleanupFluids();
                changed = true;
            }
            passes++;
        } while (changed && passes < MAX_ALLOY_RESOLVE_PASSES);
    }

    private static boolean canAutoAlloy(IndustrialMaterial alloy) {
        if (!alloy.has(net.mads.createexpansion.material.MaterialPart.MOLTEN_FLUID) || alloy.components().size() < 2) {
            return false;
        }

        for (var component : alloy.components()) {
            if (component.material() == alloy || FoundryMeltingRecipes.materialFluid(component.material()) == null) {
                return false;
            }
        }
        return FoundryMeltingRecipes.materialFluid(alloy) != null;
    }

    private int alloyBatchCount(IndustrialMaterial alloy) {
        int batches = Integer.MAX_VALUE;
        for (var component : alloy.components()) {
            int amount = materialFluidAmount(component.material());
            batches = Math.min(batches, amount / component.amount());
        }
        return batches == Integer.MAX_VALUE ? 0 : batches;
    }

    private int materialFluidAmount(IndustrialMaterial material) {
        int amount = 0;
        for (FluidStack stack : fluids) {
            MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
            if (target != null && target.material() == material) {
                amount += stack.getAmount();
            }
        }
        return amount;
    }

    private void consumeMaterialFluid(IndustrialMaterial material, int amount) {
        int remaining = amount;
        for (FluidStack stack : fluids) {
            if (remaining <= 0) {
                break;
            }

            MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
            if (target == null || target.material() != material) {
                continue;
            }

            int consumed = Math.min(remaining, stack.getAmount());
            stack.shrink(consumed);
            remaining -= consumed;
        }
    }

    private void addResolvedFluid(IndustrialMaterial material, int amount) {
        if (amount <= 0) {
            return;
        }

        var fluid = FoundryMeltingRecipes.materialFluid(material);
        if (fluid == null) {
            return;
        }

        FluidStack stack = new FluidStack(fluid.source().get(), amount);
        FluidStack existing = matchingFluid(stack);
        if (existing == null) {
            fluids.add(stack);
        } else {
            existing.grow(amount);
        }
    }

    private boolean canAcceptMaterial(IndustrialMaterial candidate) {
        Set<IndustrialMaterial> materials = containedMaterials();
        if (materials.isEmpty() || materials.contains(candidate)) {
            return true;
        }

        materials.add(candidate);
        return isValidMaterialSet(materials);
    }

    private Set<IndustrialMaterial> containedMaterials() {
        Set<IndustrialMaterial> materials = new HashSet<>();
        for (FluidStack stack : fluids) {
            MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
            if (target != null) {
                materials.add(target.material());
            }
        }

        for (int slot = 0; slot < meltingSlotCount(); slot++) {
            MaterialLookup.MaterialTarget target = MaterialLookup.find(meltingItems.get(slot));
            if (target != null) {
                materials.add(target.material());
            }
        }
        return materials;
    }

    private static boolean isValidMaterialSet(Set<IndustrialMaterial> materials) {
        if (materials.size() <= 1) {
            return true;
        }

        for (Set<IndustrialMaterial> family : ALLOY_FAMILIES) {
            if (family.containsAll(materials)) {
                return true;
            }
        }
        return false;
    }

    private static List<Set<IndustrialMaterial>> buildAlloyFamilies() {
        List<Set<IndustrialMaterial>> families = new ArrayList<>();
        for (IndustrialMaterial alloy : IndustrialMaterials.ALL) {
            if (canAutoAlloy(alloy)) {
                families.add(Set.copyOf(alloyFamily(alloy, new HashSet<>())));
            }
        }
        return List.copyOf(families);
    }

    private static Set<IndustrialMaterial> alloyFamily(IndustrialMaterial material, Set<IndustrialMaterial> seen) {
        if (!seen.add(material)) {
            return seen;
        }

        material.components().forEach(component -> alloyFamily(component.material(), seen));
        return seen;
    }

    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack existing = matchingFluid(resource);
        if (existing == null) {
            return FluidStack.EMPTY;
        }

        int drained = Math.min(resource.getAmount(), existing.getAmount());
        FluidStack result = existing.copyWithAmount(drained);
        if (action.execute()) {
            existing.shrink(drained);
            cleanupFluids();
            contentChanged();
        }
        return result;
    }

    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || fluids.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack existing = fluids.getFirst();
        int drained = Math.min(maxDrain, existing.getAmount());
        FluidStack result = existing.copyWithAmount(drained);
        if (action.execute()) {
            existing.shrink(drained);
            cleanupFluids();
            contentChanged();
        }
        return result;
    }

    public void tryUpdateStructure() {
        if (level == null || level.isClientSide()) {
            return;
        }

        FoundryMatch match = findMatch();
        if (match == null) {
            setFormed(false, 0, 0, 0);
            return;
        }

        int insideWidth = match.width() - 2;
        int insideHeight = match.height() - 1;
        int capacity = MB_PER_INSIDE_BLOCK * insideWidth * insideWidth * insideHeight;
        setFormed(true, match.width(), match.height(), capacity, match.hatches(), match.min(), match.max());
    }

    private void tickMelting() {
        if (!formed || level == null || temperature <= 0) {
            return;
        }

        boolean changed = false;
        for (int slot = 0; slot < meltingSlotCount(); slot++) {
            ItemStack stack = meltingItems.get(slot);
            if (stack.isEmpty()) {
                meltingProgress[slot] = 0;
                continue;
            }

            FoundryMeltingRecipe recipe = meltingRecipe(stack);
            if (recipe == null || temperature < recipe.temperature()) {
                meltingProgress[slot] = 0;
                continue;
            }

            FluidStack result = recipe.result();
            if (fill(result, FluidAction.SIMULATE) < result.getAmount()) {
                continue;
            }

            meltingProgress[slot]++;
            if (meltingProgress[slot] >= recipe.durationAt(temperature)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    meltingItems.set(slot, ItemStack.EMPTY);
                }
                fill(result, FluidAction.EXECUTE);
                meltingProgress[slot] = 0;
            }
            changed = true;
        }

        if (changed) {
            contentChanged();
        }
    }

    @Nullable
    private FoundryMeltingRecipe meltingRecipe(ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return null;
        }

        FoundryMeltingRecipe recipe = level.getRecipeManager()
                .getAllRecipesFor(net.mads.createexpansion.registry.RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(candidate -> candidate.matchesItem(stack))
                .findFirst()
                .orElse(null);
        return recipe != null ? recipe : FoundryMeltingRecipes.syntheticRecipeFor(stack);
    }

    private FoundryMatch findMatch() {
        if (level == null) {
            return null;
        }

        BlockPos base = worldPosition.below();
        for (int width : VALID_WIDTHS) {
            int half = width / 2;
            FoundryMatch north = tryMatch(width, base.offset(-half, 0, 0), base.offset(half, 0, width - 1));
            if (north != null) {
                return north;
            }
            FoundryMatch south = tryMatch(width, base.offset(-half, 0, -(width - 1)), base.offset(half, 0, 0));
            if (south != null) {
                return south;
            }
            FoundryMatch west = tryMatch(width, base.offset(0, 0, -half), base.offset(width - 1, 0, half));
            if (west != null) {
                return west;
            }
            FoundryMatch east = tryMatch(width, base.offset(-(width - 1), 0, -half), base.offset(0, 0, half));
            if (east != null) {
                return east;
            }
        }
        return null;
    }

    private FoundryMatch tryMatch(int width, BlockPos min, BlockPos max) {
        if (!isFullCasingPlate(min, max)) {
            return null;
        }

        int wallLayers = 0;
        for (int y = min.getY() + 1; y < level.getMaxBuildHeight(); y++) {
            if (!isWallLayer(min.getX(), max.getX(), y, min.getZ(), max.getZ())) {
                break;
            }
            wallLayers++;
        }

        if (wallLayers < 1) {
            return null;
        }

        FoundryLayerScan scan = scanLayers(min.getX(), max.getX(), min.getY(), max.getY(), min.getZ(), max.getZ(), wallLayers);
        int outerHeight = wallLayers + 1;
        if (scan == null || scan.inputHatches() > outerHeight * 2 || scan.outputHatches() > 1) {
            return null;
        }

        return new FoundryMatch(width, outerHeight, scan.hatches(), min.immutable(), max.immutable());
    }

    private boolean isFullCasingPlate(BlockPos min, BlockPos max) {
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                BlockPos pos = new BlockPos(x, min.getY(), z);
                BlockState state = level.getBlockState(pos);
                if (!isCasing(state) && !isOutputHatch(state)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWallLayer(int minX, int maxX, int y, int minZ, int maxZ) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos pos = new BlockPos(x, y, z);
                boolean boundary = x == minX || x == maxX || z == minZ || z == maxZ;
                if (boundary) {
                    if (pos.equals(worldPosition)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(pos);
                    if (!isCasing(state) && !isInputHatch(state) && !isInputBus(state)) {
                        return false;
                    }
                    continue;
                }

                if (!level.getBlockState(pos).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isCasing(BlockState state) {
        Block block = state.getBlock();
        return block == BlockRegistry.FOUNDRY_CASING.get();
    }

    private static boolean isInputHatch(BlockState state) {
        return state.getBlock() == BlockRegistry.FOUNDRY_INPUT_HATCH.get();
    }

    private static boolean isOutputHatch(BlockState state) {
        return state.getBlock() == BlockRegistry.FOUNDRY_OUTPUT_HATCH.get();
    }

    private static boolean isInputBus(BlockState state) {
        return state.getBlock() == BlockRegistry.FOUNDRY_INPUT_BUS.get();
    }

    @Nullable
    private FoundryLayerScan scanLayers(int minX, int maxX, int bottomY, int maxY, int minZ, int maxZ, int wallLayers) {
        List<BlockPos> hatches = new ArrayList<>();
        int inputs = 0;
        int outputs = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos pos = new BlockPos(x, bottomY, z);
                if (isOutputHatch(level.getBlockState(pos))) {
                    outputs++;
                    hatches.add(pos);
                }
            }
        }
        for (int y = bottomY + 1; y <= bottomY + wallLayers; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean boundary = x == minX || x == maxX || z == minZ || z == maxZ;
                    if (!boundary) {
                        continue;
                    }
                    BlockPos pos = new BlockPos(x, y, z);
                    if (isInputHatch(level.getBlockState(pos))) {
                        inputs++;
                        hatches.add(pos);
                    } else if (isInputBus(level.getBlockState(pos))) {
                        hatches.add(pos);
                    }
                }
            }
        }
        return new FoundryLayerScan(inputs, outputs, List.copyOf(hatches));
    }

    private void setFormed(boolean formed, int outerWidth, int outerHeight, int capacityMb) {
        setFormed(formed, outerWidth, outerHeight, capacityMb, List.of(), null, null);
    }

    private void setFormed(boolean formed, int outerWidth, int outerHeight, int capacityMb, List<BlockPos> hatches, @Nullable BlockPos bottomMin, @Nullable BlockPos bottomMax) {
        boolean changed = this.formed != formed
                || this.outerWidth != outerWidth
                || this.outerHeight != outerHeight
                || this.capacityMb != capacityMb
                || !this.attachedHatches.equals(hatches)
                || !java.util.Objects.equals(this.bottomMin, bottomMin)
                || !java.util.Objects.equals(this.bottomMax, bottomMax);
        if (!changed) {
            return;
        }

        detachHatches();
        this.formed = formed;
        this.outerWidth = outerWidth;
        this.outerHeight = outerHeight;
        this.capacityMb = capacityMb;
        this.attachedHatches = List.copyOf(hatches);
        this.bottomMin = bottomMin == null ? null : bottomMin.immutable();
        this.bottomMax = bottomMax == null ? null : bottomMax.immutable();
        trimFluidsToCapacity();
        if (formed) {
            attachHatches();
        }
        updateBlockFormedState(formed);
        updateBlockActiveState();
        setChanged();
        syncToClient();
    }

    private void attachHatches() {
        if (level == null) {
            return;
        }
        for (BlockPos hatchPos : attachedHatches) {
            if (level.getBlockEntity(hatchPos) instanceof FoundryHatchBlockEntity hatch) {
                hatch.attachToController(worldPosition);
            }
        }
    }

    private void detachHatches() {
        if (level == null) {
            return;
        }
        for (BlockPos hatchPos : attachedHatches) {
            if (level.getBlockEntity(hatchPos) instanceof FoundryHatchBlockEntity hatch) {
                hatch.clearCachedController();
            }
        }
    }

    private void updateBlockFormedState(boolean formed) {
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();
        if (!state.hasProperty(FoundryControllerBlock.FORMED) || state.getValue(FoundryControllerBlock.FORMED) == formed) {
            return;
        }

        level.setBlock(worldPosition, state.setValue(FoundryControllerBlock.FORMED, formed), 3);
    }

    private void updateBlockActiveState() {
        if (level == null) {
            return;
        }

        BlockState state = level.getBlockState(worldPosition);
        boolean active = formed && temperature > ACTIVE_OVERLAY_TEMPERATURE;
        if (!state.hasProperty(FoundryControllerBlock.ACTIVE) || state.getValue(FoundryControllerBlock.ACTIVE) == active) {
            return;
        }

        level.setBlock(worldPosition, state.setValue(FoundryControllerBlock.ACTIVE, active), 3);
    }

    private void syncToClient() {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Formed", formed);
        tag.putInt("OuterWidth", outerWidth);
        tag.putInt("OuterHeight", outerHeight);
        tag.putInt("CapacityMb", capacityMb);
        tag.putInt("Temperature", temperature);
        ContainerHelper.saveAllItems(tag, meltingItems, registries);
        tag.putIntArray("MeltingProgress", meltingProgress);
        ListTag fluidList = new ListTag();
        for (FluidStack stack : fluids) {
            if (!stack.isEmpty()) {
                fluidList.add(stack.saveOptional(registries));
            }
        }
        tag.put("Fluids", fluidList);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        formed = tag.getBoolean("Formed");
        outerWidth = tag.getInt("OuterWidth");
        outerHeight = tag.getInt("OuterHeight");
        capacityMb = tag.getInt("CapacityMb");
        temperature = tag.getInt("Temperature");
        meltingItems.replaceAll(stack -> ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, meltingItems, registries);
        int[] savedProgress = tag.getIntArray("MeltingProgress");
        System.arraycopy(savedProgress, 0, meltingProgress, 0, Math.min(savedProgress.length, meltingProgress.length));
        fluids.clear();
        if (tag.contains("Fluids")) {
            ListTag fluidList = tag.getList("Fluids", Tag.TAG_COMPOUND);
            for (int i = 0; i < fluidList.size(); i++) {
                FluidStack stack = FluidStack.parseOptional(registries, fluidList.getCompound(i));
                if (!stack.isEmpty()) {
                    fluids.add(stack);
                }
            }
        } else if (tag.contains("Tank")) {
            FluidStack stack = FluidStack.parseOptional(registries, tag.getCompound("Tank").getCompound("Fluid"));
            if (!stack.isEmpty()) {
                fluids.add(stack);
            }
        }
        trimFluidsToCapacity();
        trimMeltingSlots();
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

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FoundryControllerMenu(containerId, playerInventory, this);
    }

    private void contentChanged() {
        setChanged();
        syncToClient();
    }

    @Nullable
    private FluidStack matchingFluid(FluidStack resource) {
        for (FluidStack stack : fluids) {
            if (FluidStack.isSameFluidSameComponents(stack, resource)) {
                return stack;
            }
        }
        return null;
    }

    private void cleanupFluids() {
        fluids.removeIf(FluidStack::isEmpty);
    }

    private void trimFluidsToCapacity() {
        int remaining = Math.max(0, capacityMb);
        for (FluidStack stack : fluids) {
            int kept = Math.min(stack.getAmount(), remaining);
            stack.setAmount(kept);
            remaining -= kept;
        }
        cleanupFluids();
    }

    private void trimMeltingSlots() {
        int slots = meltingSlotCount();
        for (int slot = slots; slot < meltingItems.size(); slot++) {
            meltingItems.set(slot, ItemStack.EMPTY);
            meltingProgress[slot] = 0;
        }
    }

    private final class FoundryMeltingContainer implements Container {
        @Override
        public int getContainerSize() {
            return meltingSlotCount();
        }

        @Override
        public boolean isEmpty() {
            for (int slot = 0; slot < getContainerSize(); slot++) {
                if (!meltingItems.get(slot).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot >= 0 && slot < getContainerSize() ? meltingItems.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = ContainerHelper.removeItem(meltingItems, slot, amount);
            if (!stack.isEmpty()) {
                meltingProgress[slot] = 0;
                contentChanged();
            }
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot < 0 || slot >= getContainerSize()) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = meltingItems.get(slot);
            meltingItems.set(slot, ItemStack.EMPTY);
            meltingProgress[slot] = 0;
            return stack;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot < 0 || slot >= getContainerSize()) {
                return;
            }
            meltingItems.set(slot, stack.copyWithCount(Math.min(1, stack.getCount())));
            meltingProgress[slot] = 0;
            contentChanged();
        }

        @Override
        public void setChanged() {
            contentChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return player.level() == level && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return slot >= 0 && slot < getContainerSize() && canInsertMeltingItem(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void clearContent() {
            for (int slot = 0; slot < meltingItems.size(); slot++) {
                meltingItems.set(slot, ItemStack.EMPTY);
                meltingProgress[slot] = 0;
            }
            contentChanged();
        }
    }

    private record FoundryMatch(int width, int height, List<BlockPos> hatches, BlockPos min, BlockPos max) {
    }

    private record FoundryLayerScan(int inputHatches, int outputHatches, List<BlockPos> hatches) {
    }

    private record ElectricHeaterSource(MultiblockControllerBlockEntity controller) {
    }
}
