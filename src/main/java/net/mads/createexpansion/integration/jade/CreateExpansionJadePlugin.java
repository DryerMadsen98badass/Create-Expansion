package net.mads.createexpansion.integration.jade;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.multiblock.MultiblockControllerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(CreateExpansion.MOD_ID)
public class CreateExpansionJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(MultiblockStatusProvider.UID, true);
        registration.registerBlockComponent(MultiblockStatusProvider.INSTANCE, MultiblockControllerBlock.class);
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
