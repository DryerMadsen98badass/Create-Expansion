package net.mads.createexpansion.integration.jei;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockPattern;
import net.mads.createexpansion.machine.machines.electric.multiblock.PatternVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiblockStructureCategory implements IRecipeCategory<MultiblockJeiRecipe> {
    public static final RecipeType<MultiblockJeiRecipe> TYPE = RecipeType.create(CreateExpansion.MOD_ID, "multiblock_structure", MultiblockJeiRecipe.class);
    private static final int WIDTH = 280;
    private static final int HEIGHT = 230;
    private static final int VIEW_X = 8;
    private static final int VIEW_Y = 38;
    private static final int VIEW_WIDTH = 150;
    private static final int VIEW_HEIGHT = 122;
    private static final int PANEL_X = 166;
    private static final int PANEL_Y = 38;
    private static final int PANEL_WIDTH = 106;
    private static final int PANEL_HEIGHT = 122;
    private static final int MATERIAL_X = 8;
    private static final int MATERIAL_Y = 180;
    private static final int MATERIAL_WIDTH = 264;
    private static final int MATERIAL_HEIGHT = 44;
    private static final int HIT_SIZE = 12;
    private static final int STACK_COLUMNS = 5;
    private static final int STACK_ROWS = 3;
    private static final int STACK_PAGE_SIZE = STACK_COLUMNS * STACK_ROWS;
    private static final int MATERIAL_COLUMNS = 13;
    private static final int MATERIAL_PAGE_SIZE = MATERIAL_COLUMNS;

    private final IDrawable icon;

    public MultiblockStructureCategory(IGuiHelper guiHelper, ItemStack iconStack) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
    }

    @Override
    public RecipeType<MultiblockJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Multiblock Structures");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MultiblockJeiRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.validStacks(MultiblockPattern.controller));
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.allValidStacks());

        List<MultiblockJeiRecipe.MaterialEntry> materials = recipe.materialEntries();
        int end = Math.min(materials.size(), MATERIAL_PAGE_SIZE);
        for (int index = 0; index < end; index++) {
            MultiblockJeiRecipe.MaterialEntry entry = materials.get(index);
            builder.addSlot(RecipeIngredientRole.INPUT, materialStackX(index), materialStackY())
                    .addIngredients(VanillaTypes.ITEM_STACK, entry.stacks())
                    .addTooltipCallback((slotView, tooltip) -> {
                        for (String line : entry.tooltip()) {
                            tooltip.add(Component.literal(line));
                        }
                    })
                    .setStandardSlotBackground();
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, MultiblockJeiRecipe recipe, IFocusGroup focuses) {
        builder.addInputHandler(new StructureInputHandler(recipe));
    }

    @Override
    public void draw(MultiblockJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        button(guiGraphics, font, "Layer", 8, 16, 44, 14);
        button(guiGraphics, font, "Variant", 56, 16, 52, 14);
        button(guiGraphics, font, recipe.tier().displayName(), 112, 16, 40, 14);
        button(guiGraphics, font, "Reset", 206, 16, 42, 14);

        guiGraphics.drawString(font, Component.literal(recipe.definition().displayName()), 8, 4, 0xFF303030, false);
        guiGraphics.drawString(font, Component.literal(recipe.layerDisplay() + "  Variant " + recipe.variant().id()), 8, 32, 0xFF555555, false);

        drawStructure3D(recipe, guiGraphics, font, mouseX, mouseY);
        drawSelection(recipe, guiGraphics, font);
        drawMaterialList(recipe, guiGraphics, font);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MultiblockJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (inside(mouseX, mouseY, 8, 16, 44, 14)) {
            tooltip.add(Component.literal("Toggle all layers / next layer"));
            return;
        }
        if (inside(mouseX, mouseY, 56, 16, 52, 14)) {
            tooltip.add(Component.literal("Next structure variant"));
            return;
        }
        if (inside(mouseX, mouseY, 112, 16, 40, 14)) {
            tooltip.add(Component.literal("Next machine tier"));
            return;
        }
        if (inside(mouseX, mouseY, 206, 16, 42, 14)) {
            tooltip.add(Component.literal("Reset 3D view"));
            return;
        }
        if (insideSelectedPageButton(mouseX, mouseY, recipe)) {
            tooltip.add(Component.literal("Next valid block page"));
            return;
        }
        if (insideMaterialPageButton(mouseX, mouseY, recipe)) {
            tooltip.add(Component.literal("Next material page"));
            return;
        }

        ItemStack selectedStack = selectedStackAtMouse(recipe, mouseX, mouseY);
        if (!selectedStack.isEmpty()) {
            tooltip.add(selectedStack.getHoverName());
            return;
        }

        MultiblockJeiRecipe.MaterialEntry materialEntry = materialEntryAtMouse(recipe, mouseX, mouseY);
        if (materialEntry != null) {
            tooltip.add(materialEntry.stack().getHoverName());
            for (String line : materialEntry.tooltip()) {
                tooltip.add(Component.literal(line));
            }
            return;
        }

        ProjectedBlock block = blockAtMouse(recipe, mouseX, mouseY);
        if (block == null) {
            if (inside(mouseX, mouseY, VIEW_X, VIEW_Y, VIEW_WIDTH, VIEW_HEIGHT)) {
                tooltip.add(Component.literal("Drag to rotate"));
                tooltip.add(Component.literal("Scroll to zoom"));
            }
            return;
        }

        tooltip.add(Component.literal("Position: " + block.x() + ", " + block.y() + ", " + block.z()));
        List<ItemStack> stacks = recipe.validStacks(block.symbol());
        tooltip.add(Component.literal(stacks.size() + " valid block" + (stacks.size() == 1 ? "" : "s")));
        tooltip.add(Component.literal("Click to inspect"));
    }

    private static void drawStructure3D(MultiblockJeiRecipe recipe, GuiGraphics guiGraphics, Font font, double mouseX, double mouseY) {
        PatternVariant variant = recipe.variant();
        int width = variant.width();
        int rows = variant.height();
        int cols = variant.length();

        guiGraphics.fill(VIEW_X, VIEW_Y, VIEW_X + VIEW_WIDTH, VIEW_Y + VIEW_HEIGHT, 0xFFE0E4EA);
        guiGraphics.fill(VIEW_X + 1, VIEW_Y + 1, VIEW_X + VIEW_WIDTH - 1, VIEW_Y + VIEW_HEIGHT - 1, 0xFFF7F8FA);

        PoseStack pose = guiGraphics.pose();
        guiGraphics.flush();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();

        pose.pushPose();
        pose.translate(viewCenterX(), viewCenterY(), 260.0F);
        float scale = viewScale(recipe);
        pose.scale(scale, -scale, scale);
        pose.mulPose(Axis.XP.rotationDegrees(recipe.viewPitch()));
        pose.mulPose(Axis.YP.rotationDegrees(recipe.viewYaw()));
        pose.translate(-width / 2.0F, -rows / 2.0F, -cols / 2.0F);

        for (int x = 0; x < width; x++) {
            if (!recipe.isLayerVisible(x)) {
                continue;
            }

            for (int y = 0; y < rows; y++) {
                for (int z = 0; z < cols; z++) {
                    char symbol = variant.symbolAt(x, y, z);
                    if (symbol == MultiblockPattern.air) {
                        continue;
                    }

                    BlockState state = renderState(recipe, symbol);
                    if (state == null) {
                        continue;
                    }

                    pose.pushPose();
                    pose.translate(x, y, z);
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                            state,
                            pose,
                            guiGraphics.bufferSource(),
                            LightTexture.FULL_BRIGHT,
                            OverlayTexture.NO_OVERLAY
                    );
                    pose.popPose();
                }
            }
        }

        pose.popPose();
        guiGraphics.flush();
        Lighting.setupForFlatItems();
        RenderSystem.disableDepthTest();

        ProjectedBlock hovered = blockAtMouse(recipe, mouseX, mouseY);
        if (hovered != null) {
            drawProjectedHighlight(guiGraphics, hovered);
            drawProjectedOutline(guiGraphics, hovered, 0xFF4D8DFF);
        }

        MultiblockJeiRecipe.SelectedBlock selected = recipe.selectedBlock();
        if (selected != null && recipe.isLayerVisible(selected.x())) {
            ProjectedBlock selectedProjection = projectBlock(recipe, selected.x(), selected.y(), selected.z(), selected.symbol());
            drawProjectedOutline(guiGraphics, selectedProjection, 0xFFFFC857);
        }

        guiGraphics.drawString(font, Component.literal("Drag view  Scroll zoom"), VIEW_X + 4, VIEW_Y + VIEW_HEIGHT + 4, 0xFF555555, false);
    }

    @Nullable
    private static BlockState renderState(MultiblockJeiRecipe recipe, char symbol) {
        List<ItemStack> stacks = recipe.validStacks(symbol);
        if (stacks.isEmpty() || !(stacks.getFirst().getItem() instanceof BlockItem blockItem)) {
            return null;
        }
        return blockItem.getBlock().defaultBlockState();
    }

    private static void drawSelection(MultiblockJeiRecipe recipe, GuiGraphics guiGraphics, Font font) {
        guiGraphics.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEIGHT, 0xFFE5E8EC);
        guiGraphics.fill(PANEL_X + 1, PANEL_Y + 1, PANEL_X + PANEL_WIDTH - 1, PANEL_Y + PANEL_HEIGHT - 1, 0xFFF8F9FA);
        guiGraphics.drawString(font, Component.literal("Valid Blocks"), PANEL_X + 5, PANEL_Y + 5, 0xFF303030, false);

        MultiblockJeiRecipe.SelectedBlock selected = recipe.selectedBlock();
        if (selected == null) {
            guiGraphics.drawString(font, Component.literal("Click a block"), PANEL_X + 5, PANEL_Y + 23, 0xFF777777, false);
            return;
        }

        guiGraphics.drawString(font, Component.literal("Position"), PANEL_X + 5, PANEL_Y + 19, 0xFF555555, false);
        guiGraphics.drawString(font, Component.literal(selected.x() + "," + selected.y() + "," + selected.z()), PANEL_X + 50, PANEL_Y + 19, 0xFF777777, false);

        List<ItemStack> stacks = recipe.validStacks(selected.symbol());
        int pages = Math.max(1, (stacks.size() + STACK_PAGE_SIZE - 1) / STACK_PAGE_SIZE);
        int page = Math.min(recipe.selectedStackPage(), pages - 1);
        int start = page * STACK_PAGE_SIZE;
        int end = Math.min(stacks.size(), start + STACK_PAGE_SIZE);

        for (int index = start; index < end; index++) {
            int local = index - start;
            int x = stackX(local);
            int y = stackY(local);
            guiGraphics.renderItem(stacks.get(index), x, y);
            guiGraphics.renderItemDecorations(font, stacks.get(index), x, y);
        }

        if (pages > 1) {
            guiGraphics.drawString(font, Component.literal((page + 1) + "/" + pages), PANEL_X + 5, PANEL_Y + PANEL_HEIGHT - 13, 0xFF555555, false);
            button(guiGraphics, font, "More", PANEL_X + PANEL_WIDTH - 35, PANEL_Y + PANEL_HEIGHT - 16, 30, 12);
        }
    }

    private static void drawMaterialList(MultiblockJeiRecipe recipe, GuiGraphics guiGraphics, Font font) {
        guiGraphics.fill(MATERIAL_X, MATERIAL_Y, MATERIAL_X + MATERIAL_WIDTH, MATERIAL_Y + MATERIAL_HEIGHT, 0xFFE5E8EC);
        guiGraphics.fill(MATERIAL_X + 1, MATERIAL_Y + 1, MATERIAL_X + MATERIAL_WIDTH - 1, MATERIAL_Y + MATERIAL_HEIGHT - 1, 0xFFF8F9FA);
        guiGraphics.drawString(font, Component.literal("Materials"), MATERIAL_X + 5, MATERIAL_Y + 5, 0xFF303030, false);
        List<MultiblockJeiRecipe.MaterialEntry> materials = recipe.materialEntries();
        if (materials.isEmpty()) {
            guiGraphics.drawString(font, Component.literal("No material entries"), MATERIAL_X + 5, MATERIAL_Y + 24, 0xFF777777, false);
            return;
        }

    }

    private static void button(GuiGraphics guiGraphics, Font font, String label, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0xFF606A78);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFFECEFF3);
        int textX = x + Math.max(2, (width - font.width(label)) / 2);
        guiGraphics.drawString(font, label, textX, y + Math.max(2, (height - 8) / 2), 0xFF22262C, false);
    }

    @Nullable
    private static ProjectedBlock blockAtMouse(MultiblockJeiRecipe recipe, double mouseX, double mouseY) {
        if (!inside(mouseX, mouseY, VIEW_X, VIEW_Y, VIEW_WIDTH, VIEW_HEIGHT)) {
            return null;
        }

        PatternVariant variant = recipe.variant();
        ProjectedBlock best = null;
        double bestScore = Double.MAX_VALUE;

        for (int x = 0; x < variant.width(); x++) {
            if (!recipe.isLayerVisible(x)) {
                continue;
            }

            for (int y = 0; y < variant.height(); y++) {
                for (int z = 0; z < variant.length(); z++) {
                    char symbol = variant.symbolAt(x, y, z);
                    if (symbol == MultiblockPattern.air) {
                        continue;
                    }

                    ProjectedBlock projected = projectBlock(recipe, x, y, z, symbol);
                    double dx = mouseX - projected.screenX();
                    double dy = mouseY - projected.screenY();
                    if (Math.abs(dx) > HIT_SIZE || Math.abs(dy) > HIT_SIZE) {
                        continue;
                    }

                    double score = dx * dx + dy * dy - projected.depth() * 0.01D;
                    if (score < bestScore) {
                        best = projected;
                        bestScore = score;
                    }
                }
            }
        }

        return best;
    }

    private static ProjectedBlock projectBlock(MultiblockJeiRecipe recipe, int x, int y, int z, char symbol) {
        PatternVariant variant = recipe.variant();
        double localX = x + 0.5D - variant.width() / 2.0D;
        double localY = y + 0.5D - variant.height() / 2.0D;
        double localZ = z + 0.5D - variant.length() / 2.0D;

        double yaw = Math.toRadians(recipe.viewYaw());
        double pitch = Math.toRadians(recipe.viewPitch());
        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);

        double rotatedX = localX * cosYaw + localZ * sinYaw;
        double rotatedZ = -localX * sinYaw + localZ * cosYaw;
        double rotatedY = localY * cosPitch - rotatedZ * sinPitch;
        double depth = localY * sinPitch + rotatedZ * cosPitch;

        float scale = viewScale(recipe);
        int screenX = (int) Math.round(viewCenterX() + rotatedX * scale);
        int screenY = (int) Math.round(viewCenterY() - rotatedY * scale);
        return new ProjectedBlock(x, y, z, symbol, screenX, screenY, depth);
    }

    private static float viewScale(MultiblockJeiRecipe recipe) {
        PatternVariant variant = recipe.variant();
        double baseSize = Math.max(1.0D, Math.max(variant.width(), variant.length()) * 0.95D + variant.height() * 0.55D);
        float fitted = (float) Math.min(VIEW_WIDTH / (baseSize * 1.45D), VIEW_HEIGHT / (baseSize * 1.35D));
        return Math.max(7.0F, fitted) * recipe.zoom();
    }

    private static float viewCenterX() {
        return VIEW_X + VIEW_WIDTH / 2.0F;
    }

    private static float viewCenterY() {
        return VIEW_Y + VIEW_HEIGHT / 2.0F + 10.0F;
    }

    private static void drawProjectedOutline(GuiGraphics guiGraphics, ProjectedBlock block, int color) {
        int half = 7;
        int x = block.screenX();
        int y = block.screenY();
        guiGraphics.fill(x - half, y - half, x + half + 1, y - half + 1, color);
        guiGraphics.fill(x - half, y + half, x + half + 1, y + half + 1, color);
        guiGraphics.fill(x - half, y - half, x - half + 1, y + half + 1, color);
        guiGraphics.fill(x + half, y - half, x + half + 1, y + half + 1, color);
    }

    private static void drawProjectedHighlight(GuiGraphics guiGraphics, ProjectedBlock block) {
        int half = 7;
        int x = block.screenX();
        int y = block.screenY();
        guiGraphics.fill(x - half, y - half, x + half + 1, y + half + 1, 0x55FFFFFF);
    }

    private static ItemStack selectedStackAtMouse(MultiblockJeiRecipe recipe, double mouseX, double mouseY) {
        MultiblockJeiRecipe.SelectedBlock selected = recipe.selectedBlock();
        if (selected == null) {
            return ItemStack.EMPTY;
        }

        List<ItemStack> stacks = recipe.validStacks(selected.symbol());
        int page = Math.min(recipe.selectedStackPage(), Math.max(0, (stacks.size() + STACK_PAGE_SIZE - 1) / STACK_PAGE_SIZE - 1));
        int start = page * STACK_PAGE_SIZE;
        int end = Math.min(stacks.size(), start + STACK_PAGE_SIZE);
        for (int index = start; index < end; index++) {
            int local = index - start;
            if (inside(mouseX, mouseY, stackX(local), stackY(local), 16, 16)) {
                return stacks.get(index);
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    private static MultiblockJeiRecipe.MaterialEntry materialEntryAtMouse(MultiblockJeiRecipe recipe, double mouseX, double mouseY) {
        if (recipe.selectedBlock() != null) {
            return null;
        }

        List<MultiblockJeiRecipe.MaterialEntry> materials = recipe.materialEntries();
        int end = Math.min(materials.size(), MATERIAL_PAGE_SIZE);
        for (int index = 0; index < end; index++) {
            if (inside(mouseX, mouseY, materialStackX(index), materialStackY(), 16, 16)) {
                return materials.get(index);
            }
        }
        return null;
    }

    private static int stackX(int localIndex) {
        return PANEL_X + 5 + (localIndex % STACK_COLUMNS) * 18;
    }

    private static int stackY(int localIndex) {
        return PANEL_Y + 43 + (localIndex / STACK_COLUMNS) * 18;
    }

    private static int materialStackX(int localIndex) {
        return MATERIAL_X + 5 + localIndex * 18;
    }

    private static int materialStackY() {
        return MATERIAL_Y + 20;
    }

    private static boolean insideSelectedPageButton(double mouseX, double mouseY, MultiblockJeiRecipe recipe) {
        MultiblockJeiRecipe.SelectedBlock selected = recipe.selectedBlock();
        if (selected == null || recipe.validStacks(selected.symbol()).size() <= STACK_PAGE_SIZE) {
            return false;
        }
        return inside(mouseX, mouseY, PANEL_X + PANEL_WIDTH - 35, PANEL_Y + PANEL_HEIGHT - 16, 30, 12);
    }

    private static boolean insideMaterialPageButton(double mouseX, double mouseY, MultiblockJeiRecipe recipe) {
        return false;
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private record ProjectedBlock(int x, int y, int z, char symbol, int screenX, int screenY, double depth) {
    }

    private static final class StructureInputHandler implements IJeiInputHandler {
        private final MultiblockJeiRecipe recipe;
        private boolean dragged;

        private StructureInputHandler(MultiblockJeiRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public ScreenRectangle getArea() {
            return new ScreenRectangle(0, 0, WIDTH, HEIGHT);
        }

        @Override
        public boolean handleInput(double mouseX, double mouseY, IJeiUserInput input) {
            InputConstants.Key key = input.getKey();
            if (key.getType() != InputConstants.Type.MOUSE || key.getValue() != 0) {
                return false;
            }

            boolean canHandle = clickable(mouseX, mouseY);
            if (input.isSimulate() || !canHandle) {
                return canHandle;
            }

            if (inside(mouseX, mouseY, 8, 16, 44, 14)) {
                recipe.nextLayer();
                dragged = false;
                return true;
            }
            if (inside(mouseX, mouseY, 56, 16, 52, 14)) {
                recipe.nextVariant();
                dragged = false;
                return true;
            }
            if (inside(mouseX, mouseY, 112, 16, 40, 14)) {
                recipe.nextTier();
                dragged = false;
                return true;
            }
            if (inside(mouseX, mouseY, 206, 16, 42, 14)) {
                recipe.resetView();
                dragged = false;
                return true;
            }
            if (insideSelectedPageButton(mouseX, mouseY, recipe)) {
                recipe.nextSelectedStackPage(STACK_PAGE_SIZE);
                dragged = false;
                return true;
            }
            if (insideMaterialPageButton(mouseX, mouseY, recipe)) {
                recipe.nextMaterialPage(MATERIAL_PAGE_SIZE);
                dragged = false;
                return true;
            }
            if (dragged) {
                dragged = false;
                return true;
            }

            ProjectedBlock block = blockAtMouse(recipe, mouseX, mouseY);
            if (block != null) {
                recipe.selectBlock(block.x(), block.y(), block.z(), block.symbol());
                return true;
            }
            return false;
        }

        @Override
        public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
            if (!inside(mouseX, mouseY, VIEW_X, VIEW_Y, VIEW_WIDTH, VIEW_HEIGHT)) {
                return false;
            }

            recipe.zoom(scrollDeltaY);
            return true;
        }

        @Override
        public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
            if (mouseKey.getType() != InputConstants.Type.MOUSE || mouseKey.getValue() != 0) {
                return false;
            }
            if (!inside(mouseX, mouseY, VIEW_X, VIEW_Y, VIEW_WIDTH, VIEW_HEIGHT)) {
                return false;
            }

            recipe.dragView(dragX, dragY);
            dragged = true;
            return true;
        }

        private boolean clickable(double mouseX, double mouseY) {
            return inside(mouseX, mouseY, 8, 16, 44, 14)
                    || inside(mouseX, mouseY, 56, 16, 52, 14)
                    || inside(mouseX, mouseY, 112, 16, 40, 14)
                    || inside(mouseX, mouseY, 206, 16, 42, 14)
                    || insideSelectedPageButton(mouseX, mouseY, recipe)
                    || insideMaterialPageButton(mouseX, mouseY, recipe)
                    || inside(mouseX, mouseY, VIEW_X, VIEW_Y, VIEW_WIDTH, VIEW_HEIGHT);
        }
    }
}