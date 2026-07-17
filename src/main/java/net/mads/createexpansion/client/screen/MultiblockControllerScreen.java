package net.mads.createexpansion.client.screen;

import net.mads.createexpansion.menu.MultiblockControllerMenu;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlock;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockControllerBlockEntity;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class MultiblockControllerScreen extends AbstractContainerScreen<MultiblockControllerMenu> {
    private static final int BACKGROUND = 0xF0111418;
    private static final int PANEL = 0xFF1A2027;
    private static final int PANEL_EDGE = 0xFF3E4A55;
    private static final int SLOT = 0xFF20262D;
    private static final int SLOT_DARK = 0xFF15191F;
    private static final int SLOT_EDGE = 0xFF59636F;
    private static final int TEXT = 0xFFE6EDF3;
    private static final int MUTED = 0xFF9CA8B3;
    private static final int GREEN = 0xFF5DD97A;
    private static final int RED = 0xFFE85B5B;
    private static final int BLUE = 0xFF4E8FDC;

    public MultiblockControllerScreen(MultiblockControllerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 224;
        imageHeight = 204;
        titleLabelX = 10;
        titleLabelY = 8;
        inventoryLabelX = 31;
        inventoryLabelY = 106;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        drawPanel(graphics, x, y, imageWidth, imageHeight);
        drawStatus(graphics, x, y);
        drawProgress(graphics, x, y);
        drawRecipePreview(graphics, x, y);

        for (Slot slot : menu.slots) {
            drawSlot(graphics, x + slot.x, y + slot.y, false);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, TEXT, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, MUTED, false);
    }

    private void drawStatus(GuiGraphics graphics, int x, int y) {
        boolean formed = menu.formed();
        int color = formed ? GREEN : RED;
        String label = formed ? "Formed" : "Unformed";
        int labelWidth = font.width(label);
        int chipX = x + imageWidth - labelWidth - 19;
        graphics.fill(chipX, y + 7, x + imageWidth - 8, y + 18, 0xFF111820);
        graphics.fill(chipX + 3, y + 10, chipX + 7, y + 14, color);
        graphics.drawString(font, label, chipX + 10, y + 9, color, false);
    }

    private void drawProgress(GuiGraphics graphics, int x, int y) {
        int barX = x + 12;
        int barY = y + 29;
        int barWidth = imageWidth - 24;
        int duration = Math.max(1, menu.duration());
        int progress = Math.min(menu.progress(), duration);
        int fill = menu.processing() ? (int) ((barWidth - 2) * (progress / (float) duration)) : 0;

        graphics.fill(barX, barY, barX + barWidth, barY + 14, 0xFF0E1217);
        graphics.fill(barX + 1, barY + 1, barX + 1 + fill, barY + 13, BLUE);
        graphics.fill(barX, barY, barX + barWidth, barY + 1, PANEL_EDGE);
        graphics.fill(barX, barY + 13, barX + barWidth, barY + 14, PANEL_EDGE);

        String text = menu.processing()
                ? Math.max(0, duration - progress) + " ticks remaining"
                : "Idle";
        graphics.drawString(font, text, barX + barWidth / 2 - font.width(text) / 2, barY + 3, TEXT, false);
    }

    private void drawRecipePreview(GuiGraphics graphics, int x, int y) {
        MultiblockControllerBlockEntity controller = menu.blockEntity();
        MultiblockDefinition definition = definition(controller);
        if (definition != null && definition.inputOnlyDisplay() != null) {
            drawInputOnlyPreview(graphics, x, y, controller, definition.inputOnlyDisplay());
            return;
        }

        List<ItemStack> itemInputs = controller == null ? List.of() : controller.activeItemInputs();
        List<FluidStack> fluidInputs = controller == null ? List.of() : controller.activeFluidInputs();
        List<ItemStack> itemOutputs = controller == null ? List.of() : controller.activeItemOutputs();
        List<FluidStack> fluidOutputs = controller == null ? List.of() : controller.activeFluidOutputs();

        graphics.drawString(font, Component.literal("Inputs").withStyle(ChatFormatting.GRAY), x + 18, y + 50, MUTED, false);
        graphics.drawString(font, Component.literal("Outputs").withStyle(ChatFormatting.GRAY), x + 145, y + 50, MUTED, false);

        drawStacks(graphics, itemInputs, x + 18, y + 61, false);
        drawFluids(graphics, fluidInputs, x + 18, y + 83);
        drawStacks(graphics, itemOutputs, x + 145, y + 61, false);
        drawFluids(graphics, fluidOutputs, x + 145, y + 83);

        graphics.drawString(font, ">", x + imageWidth / 2 - 3, y + 70, BLUE, false);

        if (controller != null) {
            ResourceLocation recipeId = controller.activeRecipeId();
            if (recipeId != null) {
                String recipe = recipeId.getPath();
                graphics.drawString(font, recipe, x + imageWidth / 2 - font.width(recipe) / 2, y + 94, MUTED, false);
            }
        }
    }

    private void drawInputOnlyPreview(GuiGraphics graphics, int x, int y, MultiblockControllerBlockEntity controller, MultiblockDefinition.InputOnlyDisplay display) {
        graphics.drawString(font, Component.literal("Inputs").withStyle(ChatFormatting.GRAY), x + 18, y + 50, MUTED, false);

        int cePerTick = display.dynamicCePerTick() && controller != null ? controller.activeCEt() : display.cePerTick();
        String ceText = cePerTick < 0 ? "CE/t: -" : "CE/t: " + cePerTick;
        graphics.drawString(font, ceText, x + 18, y + 62, TEXT, false);

        int slotY = y + 78;
        if (!display.dynamicCePerTick()) {
            graphics.drawString(font, "Duration: " + display.durationTicks() + " ticks", x + 18, y + 74, MUTED, false);
            slotY = y + 88;
        }

        List<ItemStack> itemInputs = display.itemInputs().stream()
                .map(input -> new ItemStack(BuiltInRegistries.ITEM.get(input.itemId()), input.amount()))
                .filter(stack -> !stack.isEmpty() && stack.getItem() != Items.AIR)
                .toList();
        drawStacks(graphics, itemInputs, x + 18, slotY, false);

        List<FluidStack> fluidInputs = display.fluidInputs().stream()
                .map(input -> new FluidStack(BuiltInRegistries.FLUID.get(input.fluidId()), input.amount()))
                .filter(stack -> !stack.isEmpty())
                .toList();
        drawFluids(graphics, fluidInputs, x + 92, slotY);
    }

    private static MultiblockDefinition definition(MultiblockControllerBlockEntity controller) {
        if (controller == null || !(controller.getBlockState().getBlock() instanceof MultiblockControllerBlock block)) {
            return null;
        }
        return MultiblockRegistry.byController(block.controllerId()).orElse(null);
    }

    private void drawStacks(GuiGraphics graphics, List<ItemStack> stacks, int x, int y, boolean dark) {
        for (int i = 0; i < Math.min(4, stacks.size()); i++) {
            int slotX = x + (i % 2) * 18;
            int slotY = y + (i / 2) * 18;
            drawSlot(graphics, slotX, slotY, dark);
            graphics.renderItem(stacks.get(i), slotX + 1, slotY + 1);
            graphics.renderItemDecorations(font, stacks.get(i), slotX + 1, slotY + 1);
        }
    }

    private void drawFluids(GuiGraphics graphics, List<FluidStack> fluids, int x, int y) {
        for (int i = 0; i < Math.min(2, fluids.size()); i++) {
            int slotX = x + 45 + i * 18;
            drawSlot(graphics, slotX, y, true);
            graphics.fill(slotX + 3, y + 3, slotX + 15, y + 15, fluidColor(fluids.get(i)));
        }
    }

    private static void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND);
        graphics.fill(x, y, x + width, y + 1, PANEL_EDGE);
        graphics.fill(x, y + height - 1, x + width, y + height, PANEL_EDGE);
        graphics.fill(x, y, x + 1, y + height, PANEL_EDGE);
        graphics.fill(x + width - 1, y, x + width, y + height, PANEL_EDGE);
        graphics.fill(x + 5, y + 23, x + width - 5, y + height - 5, PANEL);
    }

    private static void drawSlot(GuiGraphics graphics, int x, int y, boolean fluid) {
        graphics.fill(x, y, x + 18, y + 18, SLOT_EDGE);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, fluid ? SLOT_DARK : SLOT);
        graphics.fill(x + 2, y + 2, x + 16, y + 16, fluid ? 0xFF10141A : 0xFF171D23);
    }

    private static int fluidColor(FluidStack stack) {
        int hash = stack.getFluid().builtInRegistryHolder().key().location().toString().hashCode();
        int red = 70 + Math.floorMod(hash, 110);
        int green = 90 + Math.floorMod(hash >> 8, 100);
        int blue = 130 + Math.floorMod(hash >> 16, 90);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }
}
