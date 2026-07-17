package net.mads.createexpansion.integration.jade;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.energy.CEEnergyContainer;
import net.mads.createexpansion.energy.CEEnergyNetwork;
import net.mads.createexpansion.energy.CreativeEnergyBlock;
import net.mads.createexpansion.energy.CreativeEnergyBlockEntity;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.EnergyWireBlockEntity;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockAbility;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@WailaPlugin(CreateExpansion.MOD_ID)
public class CreateExpansionJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EnergyStorageProvider.INSTANCE, MachinePortBlockEntity.class);
        registration.registerBlockDataProvider(EnergyStorageProvider.INSTANCE, CreativeEnergyBlockEntity.class);
        registration.registerBlockDataProvider(WireProvider.INSTANCE, EnergyWireBlock.class);
        registration.registerBlockDataProvider(WireProvider.INSTANCE, EnergyWireBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(MultiblockStatusProvider.UID, true);
        registration.addConfig(EnergyStorageProvider.UID, true);
        registration.addConfig(WireProvider.UID, true);
        registration.registerBlockComponent(MultiblockStatusProvider.INSTANCE, MultiblockControllerBlock.class);
        registration.registerBlockComponent(EnergyStorageProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(WireProvider.INSTANCE, Block.class);
    }

    private enum EnergyStorageProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "ce_energy_storage");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
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
            int outputAmps = data.getInt("OutputAmps");
            int inputAmps = data.getInt("InputAmps");
            String io = outputAmps > 0 ? "out " + outputAmps + "A" : "in " + inputAmps + "A";

            tooltip.add(Component.literal("CE: " + data.getInt("Stored") + " / " + data.getInt("Capacity")).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.literal(data.getString("Tier") + " " + data.getInt("Voltage") + " CE, " + io)
                    .withStyle(ChatFormatting.GRAY));
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
            data.putInt("Stored", energy.stored());
            data.putInt("Capacity", energy.capacity());
            data.putString("Tier", energy.tier().displayName());
            data.putInt("Voltage", energy.voltage());
            data.putInt("InputAmps", energy.maxInputAmps());
            data.putInt("OutputAmps", energy.maxOutputAmps());
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
            int cePerTick = readWireCEt(accessor.getServerData(), data);
            int voltage = readWireVoltage(accessor.getServerData(), data);
            if ((cePerTick <= 0 || voltage <= 0) && accessor.getBlockEntity() instanceof EnergyWireBlockEntity wire) {
                cePerTick = wire.currentCEt();
                voltage = wire.displayVoltage();
            }
            if (cePerTick <= 0 || voltage <= 0) {
                int[] networkLoad = networkInputLoad(accessor);
                cePerTick = networkLoad[0];
                voltage = networkLoad[1];
            }
            String amps = cePerTick > 0 && voltage > 0 ? formatAmps(cePerTick, voltage) : "0";
            String tier = voltage > 0 ? MachineTierStats.tierForVoltage(voltage).displayName() : "none";

            tooltip.add(Component.literal("Flow: " + amps + "A").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.literal("Tier: " + tier).withStyle(ChatFormatting.GRAY));
        }

        @Override
        public void appendServerData(CompoundTag root, BlockAccessor accessor) {
            if (!(accessor.getBlock() instanceof EnergyWireBlock)) {
                return;
            }
            CompoundTag data = root.getCompound(UID.toString());
            int cachedCEt = CEEnergyNetwork.currentWireCEt(accessor.getLevel(), accessor.getPosition());
            int cachedVoltage = CEEnergyNetwork.currentWireVoltage(accessor.getLevel(), accessor.getPosition());
            if (cachedCEt > 0 && cachedVoltage > 0) {
                writeWireData(root, data, cachedCEt, cachedVoltage);
            }
            if (accessor.getBlockEntity() instanceof EnergyWireBlockEntity wire) {
                data.putInt("Amps", wire.currentAmperage());
                if (data.getInt("Voltage") <= 0) {
                    writeWireVoltage(root, data, wire.displayVoltage());
                }
                if (data.getInt("CEt") <= 0) {
                    writeWireCEt(root, data, wire.currentCEt());
                }
            }
            if (data.getInt("CEt") <= 0 || data.getInt("Voltage") <= 0) {
                int[] networkLoad = networkInputLoad(accessor);
                writeWireData(root, data, networkLoad[0], networkLoad[1]);
            }
            root.put(UID.toString(), data);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        private static String formatAmps(int cePerTick, int voltage) {
            if (voltage <= 0) {
                return "0";
            }
            if (cePerTick % voltage == 0) {
                return Integer.toString(cePerTick / voltage);
            }
            return java.math.BigDecimal.valueOf(cePerTick)
                    .divide(java.math.BigDecimal.valueOf(voltage), 4, java.math.RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString();
        }

        private static int readWireCEt(CompoundTag root, CompoundTag data) {
            int cePerTick = data.getInt("CEt");
            return cePerTick > 0 ? cePerTick : root.getInt("CEWireCEt");
        }

        private static int readWireVoltage(CompoundTag root, CompoundTag data) {
            int voltage = data.getInt("Voltage");
            return voltage > 0 ? voltage : root.getInt("CEWireVoltage");
        }

        private static void writeWireData(CompoundTag root, CompoundTag data, int cePerTick, int voltage) {
            writeWireCEt(root, data, cePerTick);
            writeWireVoltage(root, data, voltage);
        }

        private static void writeWireCEt(CompoundTag root, CompoundTag data, int cePerTick) {
            if (cePerTick <= 0) {
                return;
            }
            data.putInt("CEt", cePerTick);
            root.putInt("CEWireCEt", cePerTick);
        }

        private static void writeWireVoltage(CompoundTag root, CompoundTag data, int voltage) {
            if (voltage <= 0) {
                return;
            }
            data.putInt("Voltage", voltage);
            root.putInt("CEWireVoltage", voltage);
        }

        private static int[] networkInputLoad(BlockAccessor accessor) {
            Level level = accessor.getLevel();
            if (level == null || !(accessor.getBlock() instanceof EnergyWireBlock)) {
                return new int[]{0, 0};
            }

            int cePerTick = 0;
            int voltage = 0;
            Queue<BlockPos> queue = new ArrayDeque<>();
            Set<BlockPos> seenWires = new HashSet<>();
            Set<BlockPos> seenPorts = new HashSet<>();
            BlockPos start = accessor.getPosition();
            queue.add(start);
            seenWires.add(start);

            while (!queue.isEmpty()) {
                BlockPos wirePos = queue.remove();
                BlockState wireState = level.getBlockState(wirePos);
                if (!(wireState.getBlock() instanceof EnergyWireBlock)) {
                    continue;
                }

                for (Direction direction : Direction.values()) {
                    if (!EnergyWireBlock.hasEnabledConnection(wireState, direction)) {
                        continue;
                    }

                    BlockPos nextPos = wirePos.relative(direction);
                    BlockState nextState = level.getBlockState(nextPos);
                    if (nextState.getBlock() instanceof EnergyWireBlock && EnergyWireBlock.wiresConnect(wireState, direction, nextState)) {
                        if (seenWires.add(nextPos)) {
                            queue.add(nextPos);
                        }
                        continue;
                    }

                    if (seenPorts.add(nextPos)
                            && level.getBlockEntity(nextPos) instanceof MachinePortBlockEntity port
                            && port.abilities().contains(MultiblockAbility.ENERGY_INPUT)
                            && port.lastInputCEt() > 0
                            && port.lastInputVoltage() > 0) {
                        cePerTick += port.lastInputCEt();
                        voltage = Math.max(voltage, port.lastInputVoltage());
                    }
                }
            }
            return new int[]{cePerTick, voltage};
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
}
