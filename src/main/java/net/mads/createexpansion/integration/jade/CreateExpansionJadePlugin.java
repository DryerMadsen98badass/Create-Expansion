package net.mads.createexpansion.integration.jade;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.energy.CEEnergyContainer;
import net.mads.createexpansion.energy.CreativeEnergyBlock;
import net.mads.createexpansion.energy.CreativeEnergyBlockEntity;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.EnergyWireBlockEntity;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.machine.SingleBlockMachineBlock;
import net.mads.createexpansion.machine.SingleBlockMachineBlockEntity;
import net.mads.createexpansion.machine.runtime.CERecipeExecution;
import net.mads.createexpansion.machine.runtime.CERecipeLogic;
import net.mads.createexpansion.machine.runtime.CERecipeLogicMachine;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyRecipe;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyWorldProgress;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

import java.util.Locale;

@WailaPlugin(CreateExpansion.MOD_ID)
public class CreateExpansionJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EnergyStorageProvider.INSTANCE, MachinePortBlockEntity.class);
        registration.registerBlockDataProvider(EnergyStorageProvider.INSTANCE, CreativeEnergyBlockEntity.class);
        registration.registerBlockDataProvider(MachineInfoProvider.INSTANCE, BlockEntity.class);
        registration.registerBlockDataProvider(WireProvider.INSTANCE, BlockEntity.class);
        registration.registerBlockDataProvider(AssemblyBlockProvider.INSTANCE, Block.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(MultiblockStatusProvider.UID, true);
        registration.addConfig(EnergyStorageProvider.UID, true);
        registration.addConfig(MachineInfoProvider.UID, true);
        registration.addConfig(WireProvider.UID, true);
        registration.addConfig(AssemblyBlockProvider.UID, true);
        registration.registerBlockComponent(MultiblockStatusProvider.INSTANCE, MultiblockControllerBlock.class);
        registration.registerBlockComponent(EnergyStorageProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(MachineInfoProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(WireProvider.INSTANCE, EnergyWireBlock.class);
        registration.registerBlockComponent(AssemblyBlockProvider.INSTANCE, Block.class);
    }

    private enum EnergyStorageProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "ce_energy_storage");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getBlock() instanceof SingleBlockMachineBlock) {
                return;
            }

            CompoundTag data = accessor.getServerData().getCompound(UID.toString());
            if (data.contains("Capacity")) {
                appendEnergyTooltip(tooltip, data);
                return;
            }

            CEEnergyContainer energy = energyContainer(accessor);
            if (energy == null || energy.capacity() <= 0) {
                return;
            }

            CompoundTag fallback = new CompoundTag();
            writeEnergy(fallback, energy);
            appendEnergyTooltip(tooltip, fallback);
        }

        private static void appendEnergyTooltip(ITooltip tooltip, CompoundTag data) {
            appendEnergyTooltip(tooltip, data, 0xFFFFFFFF);
        }

        private static void appendEnergyTooltip(ITooltip tooltip, CompoundTag data, int textColor) {
            long stored = data.getLong("Stored");
            long capacity = data.getLong("Capacity");
            float progress = capacity <= 0L ? 0.0F : (float) Math.min(1.0D, stored / (double) capacity);
            tooltip.add(IElementHelper.get().progress(
                    progress,
                    Component.literal("Energy: " + stored + " / " + capacity + " CE"),
                    IElementHelper.get().progressStyle().color(0xFFEEE600, 0xFFEEE600).textColor(textColor),
                    BoxStyle.getNestedBox(),
                    true
            ));
        }

        @Override
        public void appendServerData(CompoundTag root, BlockAccessor accessor) {
            CEEnergyContainer energy = energyContainer(accessor);
            if (energy == null || energy.capacity() <= 0) {
                return;
            }

            CompoundTag data = root.getCompound(UID.toString());
            writeEnergy(data, energy);
            root.put(UID.toString(), data);
        }

        private static CEEnergyContainer energyContainer(BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof MachinePortBlockEntity port) {
                return port.ceContainer();
            }
            if (accessor.getBlockEntity() instanceof CreativeEnergyBlockEntity creative) {
                return creative.ceContainer();
            }
            return null;
        }

        private static void writeEnergy(CompoundTag data, CEEnergyContainer energy) {
            data.putLong("Stored", energy.stored());
            data.putLong("Capacity", energy.capacity());
            data.putString("Tier", energy.tier().displayName());
            data.putLong("Voltage", energy.voltage());
            data.putLong("InputAmps", energy.maxInputAmps());
            data.putLong("OutputAmps", energy.maxOutputAmps());
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    private enum MachineInfoProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "machine_info");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!(accessor.getBlock() instanceof SingleBlockMachineBlock)
                    && !(accessor.getBlock() instanceof MultiblockControllerBlock)) {
                return;
            }
            CompoundTag data = accessor.getServerData().getCompound(UID.toString());
            if (accessor.getBlockEntity() instanceof CERecipeLogicMachine machine) {
                writeMachineData(data, machine);
            }

            String input = data.getString("MachineInput");
            if (!input.isBlank()) {
                tooltip.add(Component.literal("Input: " + input).withStyle(ChatFormatting.AQUA));
            }
            String output = data.getString("MachineOutput");
            if (!output.isBlank()) {
                tooltip.add(Component.literal("Output: " + output).withStyle(ChatFormatting.GOLD));
            }

            int resourcePerTick = data.getInt("MachineResourcePerTick");
            long voltage = data.getLong("MachineEnergyVoltage");
            if (resourcePerTick != 0 && voltage > 0L) {
                tooltip.add(Component.literal("CE/t: " + Math.abs(resourcePerTick))
                        .withStyle(resourcePerTick > 0 ? ChatFormatting.RED : ChatFormatting.GREEN));
            } else if (resourcePerTick != 0) {
                tooltip.add(Component.literal(
                        (resourcePerTick > 0 ? "Steam Consumption: " : "Steam Production: ")
                                + Math.abs(resourcePerTick) + " mB/t"
                ).withStyle(resourcePerTick > 0 ? ChatFormatting.RED : ChatFormatting.GREEN));
            }

            int total = data.getInt("MachineProgressTotal");
            if (total > 0) {
                int done = Math.min(total, Math.max(0, data.getInt("MachineProgress")));
                float progress = Math.max(0.0F, Math.min(1.0F, (float) done / total));
                tooltip.add(IElementHelper.get().progress(
                        progress,
                        Component.literal("Duration: " + done + " / " + total + " ticks"),
                        IElementHelper.get().progressStyle().color(0xFFCC3333, 0xFF8B1A1A).textColor(0xFFFFFFFF),
                        BoxStyle.getNestedBox(),
                        true
                ));
            }

            String status = data.getString("MachineStatus");
            if (!status.isBlank() && !"IDLE".equals(status) && !"WORKING".equals(status)) {
                tooltip.add(Component.literal(formatStatus(status)).withStyle(ChatFormatting.RED));
            }

            long energyCapacity = data.getLong("MachineEnergyCapacity");
            if (energyCapacity > 0) {
                CompoundTag energyData = new CompoundTag();
                energyData.putLong("Stored", data.getLong("MachineEnergyStored"));
                energyData.putLong("Capacity", energyCapacity);
                EnergyStorageProvider.appendEnergyTooltip(tooltip, energyData, 0xFFFFFFFF);
            }
        }

        @Override
        public void appendServerData(CompoundTag root, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof CERecipeLogicMachine machine)) {
                return;
            }

            CompoundTag data = root.getCompound(UID.toString());
            writeMachineData(data, machine);
            root.put(UID.toString(), data);
        }

        private static void writeMachineData(CompoundTag data, CERecipeLogicMachine machine) {
            CERecipeLogic logic = machine.recipeLogic();
            CERecipeExecution execution = logic.execution();
            data.putInt("MachineProgress", logic.progress());
            data.putInt("MachineProgressTotal", logic.duration());
            data.putInt("MachineResourcePerTick", logic.resourcePerTick());
            data.putString("MachineInput", execution == null
                    ? ""
                    : join(formatItems(execution.itemInputs()), formatFluids(execution.fluidInputs())));
            data.putString("MachineOutput", execution == null
                    ? ""
                    : join(formatItems(execution.itemOutputs()), formatFluids(execution.fluidOutputs())));
            data.putString("MachineStatus", logic.status().name());

            if (machine instanceof SingleBlockMachineBlockEntity singleBlock
                    && singleBlock.energyCapacity() > 0) {
                CEEnergyContainer energy = singleBlock.ceContainer();
                data.putLong("MachineEnergyStored", singleBlock.energyStored());
                data.putLong("MachineEnergyCapacity", singleBlock.energyCapacity());
                data.putString("MachineEnergyTier", energy.tier().displayName());
                data.putLong("MachineEnergyVoltage", energy.voltage());
                data.putLong("MachineEnergyInputAmps", energy.maxInputAmps());
            } else {
                data.remove("MachineEnergyStored");
                data.remove("MachineEnergyCapacity");
                data.remove("MachineEnergyTier");
                data.remove("MachineEnergyVoltage");
                data.remove("MachineEnergyInputAmps");
            }
        }

        private static String formatStatus(String status) {
            return switch (status) {
                case "WAITING_FOR_RESOURCE" -> "Waiting for power or steam";
                case "WAITING_FOR_OUTPUT" -> "Output full";
                case "MACHINE_INVALID" -> "Machine invalid";
                default -> status;
            };
        }

        private static String formatItems(java.util.List<ItemStack> stacks) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Math.min(3, stacks.size()); i++) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }
                ItemStack stack = stacks.get(i);
                builder.append(stack.getCount()).append("x ").append(stack.getHoverName().getString());
            }
            if (stacks.size() > 3) {
                builder.append(", +").append(stacks.size() - 3);
            }
            return builder.toString();
        }

        private static String formatFluids(java.util.List<FluidStack> stacks) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Math.min(3, stacks.size()); i++) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }
                FluidStack stack = stacks.get(i);
                builder.append(stack.getAmount()).append("mB ").append(stack.getHoverName().getString());
            }
            if (stacks.size() > 3) {
                builder.append(", +").append(stacks.size() - 3);
            }
            return builder.toString();
        }

        private static String join(String first, String second) {
            if (first.isBlank()) {
                return second;
            }
            if (second.isBlank()) {
                return first;
            }
            return first + ", " + second;
        }

        private static String voltageName(long voltage) {
            return MachineTierStats.tierForVoltage(voltage).displayName();
        }

        private static String oneDecimal(double value) {
            return String.format(Locale.ROOT, "%.1f", value);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    private enum WireProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "ce_wire");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!(accessor.getBlock() instanceof EnergyWireBlock)) {
                return;
            }

            CompoundTag data = accessor.getServerData().getCompound(UID.toString());
            CompoundTag cableData = data.contains("cableData", Tag.TAG_COMPOUND)
                    ? data.getCompound("cableData")
                    : new CompoundTag();
            EnergyWireBlock wireBlock = (EnergyWireBlock) accessor.getBlock();
            EnergyWireBlockEntity wireEntity = accessor.getBlockEntity() instanceof EnergyWireBlockEntity wire
                    ? wire
                    : null;
            long currentVoltage = cableData.contains("currentVoltage")
                    ? cableData.getLong("currentVoltage")
                    : wireEntity == null ? 0L : wireEntity.currentVoltage();
            double currentAmperage = cableData.contains("currentAmperage")
                    ? cableData.getDouble("currentAmperage")
                    : wireEntity == null ? 0.0D : wireEntity.averageAmperage();
            double maxAmperage = cableData.contains("maxAmperage")
                    ? cableData.getDouble("maxAmperage")
                    : wireBlock.maxAmps();

            String voltage = currentVoltage > 0L ? voltageName(currentVoltage) : "None";
            String amperage = oneDecimal(currentAmperage) + "A / " + oneDecimal(maxAmperage) + "A";

            tooltip.add(Component.literal("Voltage: " + voltage).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Amperage: " + amperage).withStyle(ChatFormatting.AQUA));
        }

        @Override
        public void appendServerData(CompoundTag root, BlockAccessor accessor) {
            if (!(accessor.getBlock() instanceof EnergyWireBlock)) {
                return;
            }
            CompoundTag data = root.getCompound(UID.toString());
            BlockEntity blockEntity = accessor.getLevel().getBlockEntity(accessor.getPosition());
            if (blockEntity instanceof EnergyWireBlockEntity wire) {
                CompoundTag cableData = new CompoundTag();
                cableData.putLong("currentVoltage", wire.currentVoltage());
                cableData.putLong("maxVoltage", wire.maxVoltage());
                cableData.putDouble("currentAmperage", wire.averageAmperage());
                cableData.putDouble("maxAmperage", wire.maxAmperage());
                cableData.putInt("temperature", wire.temperature());
                data.put("cableData", cableData);
            }
            root.put(UID.toString(), data);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        private static String voltageName(long voltage) {
            return MachineTierStats.tierForVoltage(voltage).displayName();
        }

        private static String oneDecimal(double value) {
            return String.format(Locale.ROOT, "%.1f", value);
        }

    }

    private enum MultiblockStatusProvider implements IBlockComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "multiblock_status");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            boolean formed = accessor.getBlockState().hasProperty(MultiblockControllerBlock.FORMED)
                    && accessor.getBlockState().getValue(MultiblockControllerBlock.FORMED);

            tooltip.add(Component.literal(formed ? "Formed" : "Unformed")
                    .withStyle(formed ? ChatFormatting.GREEN : ChatFormatting.RED));

            if (!(accessor.getBlockEntity() instanceof MultiblockControllerBlockEntity controller) || !controller.isProcessing()) {
                return;
            }

            int remaining = controller.recipeRemaining();
            tooltip.add(Component.literal("Time: " + remaining + " ticks").withStyle(ChatFormatting.GRAY));
            if (!controller.activeItemInputs().isEmpty() || !controller.activeFluidInputs().isEmpty()) {
                tooltip.add(Component.literal("Input: " + join(formatItems(controller.activeItemInputs()), formatFluids(controller.activeFluidInputs())))
                        .withStyle(ChatFormatting.AQUA));
            }
            if (!controller.activeItemOutputs().isEmpty() || !controller.activeFluidOutputs().isEmpty()) {
                tooltip.add(Component.literal("Output: " + join(formatItems(controller.activeItemOutputs()), formatFluids(controller.activeFluidOutputs())))
                        .withStyle(ChatFormatting.GOLD));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        private static String formatItems(java.util.List<ItemStack> stacks) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Math.min(3, stacks.size()); i++) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }
                ItemStack stack = stacks.get(i);
                builder.append(stack.getCount()).append("x ").append(stack.getHoverName().getString());
            }
            if (stacks.size() > 3) {
                builder.append(", +").append(stacks.size() - 3);
            }
            return builder.toString();
        }

        private static String formatFluids(java.util.List<FluidStack> stacks) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Math.min(3, stacks.size()); i++) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }
                FluidStack stack = stacks.get(i);
                builder.append(stack.getAmount()).append("mB ").append(stack.getHoverName().getString());
            }
            if (stacks.size() > 3) {
                builder.append(", +").append(stacks.size() - 3);
            }
            return builder.toString();
        }

        private static String join(String first, String second) {
            if (first.isBlank()) {
                return second;
            }
            if (second.isBlank()) {
                return first;
            }
            return first + ", " + second;
        }
    }

    private enum AssemblyBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "assembly_block");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData().getCompound(UID.toString());
            if (data.contains("Output")) {
                tooltip.add(Component.literal("Output: " + data.getString("Output")).withStyle(ChatFormatting.GOLD));
                return;
            }
            if (!data.contains("Next")) {
                return;
            }
            tooltip.add(Component.literal("Step: " + (data.getInt("Step") + 1) + " / " + data.getInt("Total")).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Next: " + data.getString("Next")).withStyle(ChatFormatting.AQUA));
            for (int i = 1; i <= 2; i++) {
                String upcoming = data.getString("Upcoming" + i);
                if (!upcoming.isBlank()) {
                    tooltip.add(Component.literal("-> " + upcoming).withStyle(ChatFormatting.DARK_AQUA));
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag root, BlockAccessor accessor) {
            if (accessor.getLevel() == null) {
                return;
            }

            AssemblyWorldProgress.Entry progress = AssemblyWorldProgress.get(accessor.getLevel(), accessor.getPosition());
            if (progress == null) {
                return;
            }

            AssemblyRecipe recipe = accessor.getLevel().getRecipeManager().byKey(progress.recipeId())
                    .map(holder -> holder.value())
                    .filter(AssemblyRecipe.class::isInstance)
                    .map(AssemblyRecipe.class::cast)
                    .orElse(null);
            if (recipe == null) {
                return;
            }

            CompoundTag data = root.getCompound(UID.toString());
            data.putInt("Step", progress.action());
            data.putInt("Total", recipe.totalActions());
            int group = groupForAction(recipe, progress.action());
            for (int i = 0; i < 3; i++) {
                SizedIngredient input = groupInput(recipe, group + i);
                if (input == null) {
                    break;
                }
                ItemStack[] stacks = input.ingredient().getItems();
                if (stacks.length == 0) {
                    continue;
                }
                String formatted = formatItem(stacks[0].copyWithCount(input.count()));
                if (i == 0) {
                    data.putString("Next", formatted);
                } else {
                    data.putString("Upcoming" + i, formatted);
                }
            }
            root.put(UID.toString(), data);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        private static String formatItem(ItemStack stack) {
            return stack.getCount() + "x " + stack.getHoverName().getString();
        }

        private static int groupForAction(AssemblyRecipe recipe, int action) {
            int actionInLoop = action % recipe.actionsPerLoop();
            int passed = 0;
            for (int i = 0; i < recipe.inputs().size(); i++) {
                passed += recipe.inputs().get(i).count();
                if (actionInLoop < passed) {
                    return (action / recipe.actionsPerLoop()) * recipe.inputs().size() + i;
                }
            }
            return 0;
        }

        private static SizedIngredient groupInput(AssemblyRecipe recipe, int group) {
            int totalGroups = recipe.inputs().size() * recipe.loops();
            if (group < 0 || group >= totalGroups || recipe.inputs().isEmpty()) {
                return null;
            }
            return recipe.inputs().get(group % recipe.inputs().size());
        }
    }
}
