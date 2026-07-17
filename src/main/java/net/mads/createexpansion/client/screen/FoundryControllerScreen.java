package net.mads.createexpansion.client.screen;

import net.mads.createexpansion.menu.FoundryControllerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FoundryControllerScreen extends AbstractContainerScreen<FoundryControllerMenu> {
    private static final int BG = 0xFFC6C6C6;
    private static final int LIGHT = 0xFFFFFFFF;
    private static final int DARK = 0xFF555555;
    private static final int INNER = 0xFF8B8B8B;
    private static final int SLOT = 0xFF8B8B8B;
    private static final int TEXT = 0xFF404040;

    private static final int MAX_TEMPERATURE = 10000;
    private static final int TANK_X = 70;
    private static final int TANK_Y = 30;
    private static final int TANK_W = 45;
    private static final int TANK_H = 90;
    private static final int BUCKET_INPUT_X = 120;
    private static final int BUCKET_INPUT_Y = 43;
    private static final int BUCKET_OUTPUT_X = 120;
    private static final int BUCKET_OUTPUT_Y = 101;
    private static final int MODE_BUTTON_X = 120;
    private static final int MODE_BUTTON_Y = 70;
    private static final int TEMP_X = 150;
    private static final int TEMP_Y = 31;
    private static final int TEMP_W = 14;
    private static final int TEMP_H = 91;
    private EditBox temperatureBox;

    public FoundryControllerScreen(FoundryControllerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 220;
        titleLabelX = 10;
        titleLabelY = 5;
        inventoryLabelX = 10;
        inventoryLabelY = 126;
    }

    @Override
    protected void init() {
        super.init();
        if (!menu.creativeTemperatureController()) {
            temperatureBox = null;
            return;
        }

        temperatureBox = new EditBox(font, leftPos + 116, topPos + 124, 50, 12, Component.literal("Temperature"));
        temperatureBox.setMaxLength(5);
        temperatureBox.setValue(String.valueOf(menu.temperature()));
        temperatureBox.setFilter(value -> value.isEmpty() || value.chars().allMatch(Character::isDigit));
        temperatureBox.setResponder(value -> {
            if (value.isBlank()) {
                return;
            }
            int temperature = Math.max(0, Math.min(MAX_TEMPERATURE, Integer.parseInt(value)));
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 10000 + temperature);
            }
        });
        addRenderableWidget(temperatureBox);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderFluidTooltip(graphics, mouseX, mouseY);
        renderTemperatureTooltip(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        drawPanel(graphics, x, y, imageWidth, imageHeight);
        drawFluidTank(graphics, x, y);
        drawBucketArea(graphics, x, y, mouseX, mouseY);
        drawTemperatureGauge(graphics, x, y);
        drawFoundryInfo(graphics, x, y);

        for (Slot slot : menu.slots) {
            if (slot.isActive()) {
                drawSlot(graphics, x + slot.x - 1, y + slot.y - 1);
            }
        }
        drawMeltingProgress(graphics, x, y);
        drawMeltingScroll(graphics, x, y);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isHovering((int) mouseX, (int) mouseY, leftPos + 4, topPos + 28, 62, 94) && menu.scrollMeltingSlots(scrollY)) {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, scrollY < 0 ? 0 : 1);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, TEXT, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }

    private void drawFoundryInfo(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, Component.literal(menu.formed() ? "Formed" : "Unformed"), x + 119, y + 17, TEXT, false);
        if (!menu.creativeTemperatureController()) {
            graphics.drawString(font, Component.literal(menu.fluidAmount() + " mB"), x + 119, y + 124, TEXT, false);
        } else {
            graphics.drawString(font, Component.literal("Temp"), x + 119, y + 113, TEXT, false);
        }
    }

    private void drawMeltingProgress(GuiGraphics graphics, int x, int y) {
        int count = Math.min(FoundryControllerMenu.VISIBLE_MELTING_SLOTS, menu.slots.size());
        for (int i = 0; i < count; i++) {
            Slot slot = menu.slots.get(i);
            if (!slot.isActive()) {
                continue;
            }

            int barX = x + slot.x - 5;
            int barY = y + slot.y;
            graphics.fill(barX, barY, barX + 3, barY + 16, 0xFF4A6FA5);
            int fill = Math.round(16 * menu.meltingProgress(i));
            if (fill > 0) {
                graphics.fill(barX, barY + 16 - fill, barX + 3, barY + 16, 0xFFE25822);
            }
        }
    }

    private void drawMeltingScroll(GuiGraphics graphics, int x, int y) {
        int totalRows = Math.max(1, (menu.meltingSlotCount() + FoundryControllerMenu.VISIBLE_MELTING_COLUMNS - 1) / FoundryControllerMenu.VISIBLE_MELTING_COLUMNS);
        if (totalRows <= FoundryControllerMenu.VISIBLE_MELTING_ROWS) {
            return;
        }

        int trackX = x + 63;
        int trackY = y + 30;
        int trackH = 88;
        int maxRow = totalRows - FoundryControllerMenu.VISIBLE_MELTING_ROWS;
        int thumbH = Math.max(10, trackH * FoundryControllerMenu.VISIBLE_MELTING_ROWS / totalRows);
        int thumbY = trackY + (trackH - thumbH) * menu.meltingScrollRow() / Math.max(1, maxRow);
        graphics.fill(trackX, trackY, trackX + 3, trackY + trackH, 0xFF777777);
        graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbH, 0xFFFFFFFF);
    }

    private void drawFluidTank(GuiGraphics graphics, int x, int y) {
        List<FluidStack> fluids = menu.fluids();
        int capacity = Math.max(1, menu.capacityMb());
        int tankX = x + TANK_X;
        int tankY = y + TANK_Y;

        drawInset(graphics, tankX - 1, tankY - 1, TANK_W + 2, TANK_H + 2);
        graphics.fill(tankX + 2, tankY + 2, tankX + TANK_W - 2, tankY + TANK_H - 2, 0xFF2A2A2A);

        int drawnHeight = 0;
        for (FluidStack stack : fluids) {
            if (stack.isEmpty()) {
                continue;
            }

            int layerHeight = Math.max(3, (TANK_H - 4) * stack.getAmount() / capacity);
            layerHeight = Math.min(layerHeight, TANK_H - 4 - drawnHeight);
            if (layerHeight <= 0) {
                continue;
            }

            int layerTop = tankY + TANK_H - 2 - drawnHeight - layerHeight;
            int layerBottom = tankY + TANK_H - 2 - drawnHeight;
            drawFluidLayer(graphics, stack, tankX + 2, layerTop, TANK_W - 4, layerBottom - layerTop);
            drawnHeight += layerHeight;
        }
    }

    private static void drawFluidLayer(GuiGraphics graphics, FluidStack stack, int x, int y, int width, int height) {
        if (stack.isEmpty() || width <= 0 || height <= 0) {
            return;
        }

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation texture = extensions.getStillTexture(stack);
        if (texture == null) {
            graphics.fill(x, y, x + width, y + height, 0xFFFFFFFF);
            return;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);
        int tint = extensions.getTintColor(stack);
        float alpha = ((tint >> 24) & 0xFF) / 255.0F;
        float red = ((tint >> 16) & 0xFF) / 255.0F;
        float green = ((tint >> 8) & 0xFF) / 255.0F;
        float blue = (tint & 0xFF) / 255.0F;
        if (alpha <= 0.0F) {
            alpha = 1.0F;
        }

        for (int tileX = 0; tileX < width; tileX += 16) {
            for (int tileY = 0; tileY < height; tileY += 16) {
                int tileWidth = Math.min(16, width - tileX);
                int tileHeight = Math.min(16, height - tileY);
                graphics.blit(x + tileX, y + tileY, 0, tileWidth, tileHeight, sprite, red, green, blue, alpha);
            }
        }
    }

    private void drawBucketArea(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        drawSlot(graphics, x + BUCKET_INPUT_X, y + BUCKET_INPUT_Y);
        drawSlot(graphics, x + BUCKET_OUTPUT_X, y + BUCKET_OUTPUT_Y);

        boolean hovered = isHovering(mouseX, mouseY, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, 18, 18);
        if (hovered) {
            drawRaised(graphics, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, 18, 18);
        } else {
            drawInset(graphics, x + MODE_BUTTON_X, y + MODE_BUTTON_Y, 18, 18);
        }
        graphics.drawString(font, "A", x + MODE_BUTTON_X + 6, y + MODE_BUTTON_Y + 5, TEXT, false);
    }

    private void drawTemperatureGauge(GuiGraphics graphics, int x, int y) {
        int gaugeX = x + TEMP_X;
        int gaugeY = y + TEMP_Y;
        drawInset(graphics, gaugeX - 1, gaugeY - 1, TEMP_W + 2, TEMP_H + 2);
        graphics.fill(gaugeX + 2, gaugeY + 2, gaugeX + TEMP_W - 2, gaugeY + TEMP_H - 2, 0xFF2A2A2A);

        int fill = Math.min(TEMP_H - 4, Math.max(0, (TEMP_H - 4) * menu.temperature() / MAX_TEMPERATURE));
        if (fill > 0) {
            int top = gaugeY + TEMP_H - 2 - fill;
            graphics.fill(gaugeX + 3, top, gaugeX + TEMP_W - 3, gaugeY + TEMP_H - 2, 0xFFE25822);
        }
    }

    private void renderTemperatureTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isHovering(mouseX, mouseY, leftPos + TEMP_X - 1, topPos + TEMP_Y - 1, TEMP_W + 2, TEMP_H + 2)) {
            return;
        }
        graphics.renderComponentTooltip(font, List.of(
                Component.literal("Temperature"),
                Component.literal(menu.temperature() + " / " + MAX_TEMPERATURE).withStyle(ChatFormatting.GRAY)
        ), mouseX, mouseY);
    }

    private void renderFluidTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isHovering(mouseX, mouseY, leftPos + TANK_X, topPos + TANK_Y, TANK_W, TANK_H)) {
            return;
        }

        HoveredFluid hovered = hoveredFluid(mouseX, mouseY);
        List<Component> tooltip = new ArrayList<>();
        if (hovered.stack().isEmpty()) {
            tooltip.add(Component.literal("Empty"));
        } else {
            drawFluidHighlight(graphics, hovered.top(), hovered.bottom());
            tooltip.add(hovered.stack().getHoverName());
            tooltip.add(Component.literal(hovered.stack().getAmount() + " mB").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Total: " + menu.fluidAmount() + " / " + menu.capacityMb() + " mB").withStyle(ChatFormatting.GRAY));
        }
        graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
    }

    private HoveredFluid hoveredFluid(int mouseX, int mouseY) {
        List<FluidStack> fluids = menu.fluids();
        int capacity = Math.max(1, menu.capacityMb());
        int drawnHeight = 0;
        for (FluidStack stack : fluids) {
            if (stack.isEmpty()) {
                continue;
            }
            int layerHeight = Math.max(3, (TANK_H - 4) * stack.getAmount() / capacity);
            layerHeight = Math.min(layerHeight, TANK_H - 4 - drawnHeight);
            if (layerHeight <= 0) {
                continue;
            }
            int layerTop = topPos + TANK_Y + TANK_H - 2 - drawnHeight - layerHeight;
            int layerBottom = topPos + TANK_Y + TANK_H - 2 - drawnHeight;
            if (isHovering(mouseX, mouseY, leftPos + TANK_X + 2, layerTop, TANK_W - 4, layerBottom - layerTop)) {
                return new HoveredFluid(stack, layerTop, layerBottom);
            }
            drawnHeight += layerHeight;
        }
        return new HoveredFluid(FluidStack.EMPTY, 0, 0);
    }

    private void drawFluidHighlight(GuiGraphics graphics, int layerTop, int layerBottom) {
        graphics.fill(leftPos + TANK_X + 2, layerTop, leftPos + TANK_X + TANK_W - 2, layerBottom, 0x44FFFFFF);
    }

    private static boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private static void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        drawRaised(graphics, x, y, width, height);
        graphics.fill(x + 4, y + 13, x + width - 4, y + height - 4, BG);
    }

    private static void drawSlot(GuiGraphics graphics, int x, int y) {
        drawInset(graphics, x, y, 18, 18);
        graphics.fill(x + 2, y + 2, x + 16, y + 16, SLOT);
    }

    private static void drawInset(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, INNER);
        graphics.fill(x, y, x + width, y + 1, DARK);
        graphics.fill(x, y, x + 1, y + height, DARK);
        graphics.fill(x + width - 1, y, x + width, y + height, LIGHT);
        graphics.fill(x, y + height - 1, x + width, y + height, LIGHT);
    }

    private static void drawRaised(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, BG);
        graphics.fill(x, y, x + width, y + 1, LIGHT);
        graphics.fill(x, y, x + 1, y + height, LIGHT);
        graphics.fill(x + width - 1, y, x + width, y + height, DARK);
        graphics.fill(x, y + height - 1, x + width, y + height, DARK);
    }

    private record HoveredFluid(FluidStack stack, int top, int bottom) {
    }
}
