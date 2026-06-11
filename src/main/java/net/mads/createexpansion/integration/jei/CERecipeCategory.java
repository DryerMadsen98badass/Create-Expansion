package net.mads.createexpansion.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.recipe.CEChancedItemOutput;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeTypeDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CERecipeCategory implements IRecipeCategory<CERecipe> {
    private static final int WIDTH = 176;
    private static final int TOP = 22;
    private static final int LEFT_X = 8;
    private static final int RIGHT_X = 110;
    private static final int SLOT = 18;
    private static final int SLOT_STEP = 20;
    private static final int MAX_COLUMNS = 3;
    private static final int SECTION_GAP = 10;
    private static final int INFO_ROW_HEIGHT = 11;
    private static final int INFO_ROWS_RESERVED = 6;
    private static final int TIER_BUTTON_X = WIDTH - 47;
    private static final int TIER_BUTTON_WIDTH = 39;
    private static final int TIER_BUTTON_HEIGHT = 13;

    private final CERecipeTypeDefinition recipeType;
    private final RecipeType<CERecipe> jeiRecipeType;
    private final IDrawable icon;
    private final Map<CERecipe, Integer> selectedTierIndexes = new HashMap<>();

    public CERecipeCategory(CERecipeTypeDefinition recipeType, RecipeType<CERecipe> jeiRecipeType, IGuiHelper guiHelper, ItemStack icon) {
        this.recipeType = recipeType;
        this.jeiRecipeType = jeiRecipeType;
        this.icon = guiHelper.createDrawableItemStack(icon);
    }

    @Override
    public RecipeType<CERecipe> getRecipeType() {
        return jeiRecipeType;
    }

    @Override
    public Component getTitle() {
        return Component.literal(recipeType.displayName());
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return null;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        int inputHeight = stackHeight(itemInputSlotCount(), fluidInputSlotCount());
        int outputHeight = stackHeight(itemOutputSlotCount(), fluidOutputSlotCount());
        int contentHeight = Math.max(inputHeight, outputHeight);
        int infoHeight = INFO_ROWS_RESERVED * INFO_ROW_HEIGHT;
        return Math.max(104, TOP + contentHeight + 12 + infoHeight + 6);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CERecipe recipe, IFocusGroup focuses) {
        List<SizedIngredient> itemInputs = itemInputs(recipe);
        List<SizedFluidIngredient> fluidInputs = fluidInputs(recipe);
        List<CEChancedItemOutput> itemOutputs = recipe.itemOutputs();
        List<FluidStack> fluidOutputs = recipe.fluidOutputs();

        int itemInputSlots = itemInputSlotCount(itemInputs);
        int fluidInputSlots = fluidInputSlotCount(fluidInputs);
        int itemOutputSlots = itemOutputSlotCount(itemOutputs);
        int fluidOutputSlots = fluidOutputSlotCount(fluidOutputs);

        addItemInputs(builder, itemInputSlots, itemInputs);
        addFluidInputs(builder, itemInputSlots, fluidInputSlots, fluidInputs);
        addItemOutputs(builder, itemOutputSlots, itemOutputs);
        addFluidOutputs(builder, itemOutputSlots, fluidOutputSlots, fluidOutputs);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CERecipe recipe, IFocusGroup focuses) {
        builder.addGuiEventListener(new TierButtonListener(this, recipe));
    }

    @Override
    public void draw(CERecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int height = getHeight();
        MachineTier selectedTier = selectedTier(recipe);

        guiGraphics.fill(0, 0, WIDTH, height, 0xFF1B1D20);
        guiGraphics.fill(1, 1, WIDTH - 1, height - 1, 0xFF262A2F);
        drawTierButton(recipe, selectedTier, guiGraphics, font, height);

        List<SizedIngredient> itemInputs = itemInputs(recipe);
        List<SizedFluidIngredient> fluidInputs = fluidInputs(recipe);
        List<CEChancedItemOutput> itemOutputs = recipe.itemOutputs();
        List<FluidStack> fluidOutputs = recipe.fluidOutputs();

        int itemInputSlots = itemInputSlotCount(itemInputs);
        int fluidInputSlots = fluidInputSlotCount(fluidInputs);
        int itemOutputSlots = itemOutputSlotCount(itemOutputs);
        int fluidOutputSlots = fluidOutputSlotCount(fluidOutputs);

        drawSection(guiGraphics, font, "Item Inputs", LEFT_X, TOP, itemInputSlots, 0xFF353A40);
        drawSection(guiGraphics, font, "Fluid Inputs", LEFT_X, fluidY(TOP, itemInputSlots, fluidInputSlots), fluidInputSlots, 0xFF1D2C34);
        drawSection(guiGraphics, font, "Item Outputs", RIGHT_X, TOP, itemOutputSlots, 0xFF353A40);
        drawSection(guiGraphics, font, "Fluid Outputs", RIGHT_X, fluidY(TOP, itemOutputSlots, fluidOutputSlots), fluidOutputSlots, 0xFF1D2C34);

        int contentHeight = Math.max(stackHeight(itemInputSlots, fluidInputSlots), stackHeight(itemOutputSlots, fluidOutputSlots));
        drawArrow(guiGraphics, TOP + Math.max(SLOT, contentHeight) / 2);
        drawInfo(guiGraphics, font, recipe, selectedTier, TOP + contentHeight + 12);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CERecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int contentHeight = Math.max(
                stackHeight(itemInputSlotCount(itemInputs(recipe)), fluidInputSlotCount(fluidInputs(recipe))),
                stackHeight(itemOutputSlotCount(recipe.itemOutputs()), fluidOutputSlotCount(recipe.fluidOutputs()))
        );
        int infoY = TOP + contentHeight + 12;
        MachineTier selectedTier = selectedTier(recipe);

        if (hasTierButton(recipe) && inside(mouseX, mouseY, TIER_BUTTON_X, tierButtonY(getHeight()), TIER_BUTTON_WIDTH, TIER_BUTTON_HEIGHT)) {
            tooltip.add(Component.literal("Tier: " + selectedTier.displayName()));
            tooltip.add(Component.literal("Right click: higher tier"));
            tooltip.add(Component.literal("Left click: lower tier"));
            return;
        }

        if (mouseX >= 8 && mouseX <= WIDTH - 8 && mouseY >= infoY && mouseY <= getHeight() - 6) {
            tooltip.add(Component.literal("Duration: " + durationText(recipe, selectedTier) + " ticks"));
            if (recipe.cet() != 0) {
                tooltip.add(Component.literal((recipe.generatesEnergy() ? "Generates: " : "Consumes: ") + recipe.runtimeCEt(selectedTier) + " CE/t"));
            }
            recipe.minRpm().ifPresent(min -> tooltip.add(Component.literal("Minimum RPM: " + min)));
            recipe.effectiveMaxRpm().ifPresent(max -> tooltip.add(Component.literal("Maximum RPM: " + max)));
            recipe.circuit().ifPresent(circuit -> tooltip.add(Component.literal("Circuit: " + circuit)));
            recipe.minimumRuntimeTier().ifPresent(tier -> tooltip.add(Component.literal("Required Tier: " + tier.displayName() + "+")));
        }
    }

    private static void addItemInputs(IRecipeLayoutBuilder builder, int slotCount, List<SizedIngredient> inputs) {
        for (int i = 0; i < slotCount; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, slotX(LEFT_X, i), slotY(TOP, i))
                    .setStandardSlotBackground();
            if (i < inputs.size()) {
                List<ItemStack> stacks = Arrays.asList(inputs.get(i).ingredient().getItems());
                slot.addIngredients(VanillaTypes.ITEM_STACK, stacks);
            }
        }
    }

    private static void addFluidInputs(IRecipeLayoutBuilder builder, int itemInputCount, int slotCount, List<SizedFluidIngredient> inputs) {
        int y = fluidY(TOP, itemInputCount, slotCount);
        for (int i = 0; i < slotCount; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, slotX(LEFT_X, i), slotY(y, i))
                    .setStandardSlotBackground();
            if (i < inputs.size()) {
                addFluidIngredient(slot, inputs.get(i));
            }
        }
    }

    private static void addItemOutputs(IRecipeLayoutBuilder builder, int slotCount, List<CEChancedItemOutput> outputs) {
        for (int i = 0; i < slotCount; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, slotX(RIGHT_X, i), slotY(TOP, i))
                    .setStandardSlotBackground();
            if (i < outputs.size()) {
                slot.addItemStack(outputs.get(i).stack());
            }
        }
    }

    private static void addFluidOutputs(IRecipeLayoutBuilder builder, int itemOutputCount, int slotCount, List<FluidStack> outputs) {
        int y = fluidY(TOP, itemOutputCount, slotCount);
        for (int i = 0; i < slotCount; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, slotX(RIGHT_X, i), slotY(y, i))
                    .setStandardSlotBackground();
            if (i < outputs.size()) {
                addFluidStack(slot, outputs.get(i));
            }
        }
    }

    private static void addFluidIngredient(IRecipeSlotBuilder slot, SizedFluidIngredient ingredient) {
        slot.setFluidRenderer(Math.max(1, ingredient.amount()), false, 16, 16);
        for (FluidStack stack : ingredient.getFluids()) {
            addFluidStack(slot, stack);
        }
    }

    private static void addFluidStack(IRecipeSlotBuilder slot, FluidStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        slot.setFluidRenderer(Math.max(1, stack.getAmount()), false, 16, 16)
                .addFluidStack(stack.getFluid(), stack.getAmount(), stack.getComponentsPatch());
    }

    private static void drawSection(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, String label, int x, int y, int count, int fillColor) {
        if (count <= 0) {
            return;
        }

        int columns = Math.min(MAX_COLUMNS, count);
        int rows = rows(count);
        int width = columns * SLOT_STEP - 2;
        int height = rows * SLOT_STEP - 2;

        guiGraphics.drawString(font, label, x, y - 11, 0xFFB8C0C8, false);
        guiGraphics.fill(x - 3, y - 3, x + width + 3, y + height + 3, 0xFF17191C);
        guiGraphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, fillColor);
    }

    private static void drawArrow(GuiGraphics guiGraphics, int centerY) {
        int x = 77;
        int y = centerY - 1;
        guiGraphics.fill(x, y, x + 22, y + 2, 0xFF9EA6AF);
        guiGraphics.fill(x + 22, y - 4, x + 24, y + 6, 0xFF9EA6AF);
        guiGraphics.fill(x + 24, y - 3, x + 26, y + 5, 0xFF9EA6AF);
        guiGraphics.fill(x + 26, y - 2, x + 28, y + 4, 0xFF9EA6AF);
        guiGraphics.fill(x + 28, y - 1, x + 30, y + 3, 0xFF9EA6AF);
    }

    private static void drawInfo(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, CERecipe recipe, MachineTier selectedTier, int startY) {
        List<String> lines = new ArrayList<>();
        lines.add("Duration: " + durationText(recipe, selectedTier) + " t");
        if (recipe.cet() != 0) {
            lines.add((recipe.generatesEnergy() ? "Generation: " : "Energy: ") + recipe.runtimeCEt(selectedTier) + " CE/t");
        }
        if (recipe.minRpm().isPresent() || recipe.maxRpm().isPresent()) {
            lines.add("RPM: " + rpmText(recipe));
        }
        recipe.circuit().ifPresent(circuit -> lines.add("Circuit: " + circuit));
        recipe.requiredKineticTier().ifPresent(tier -> lines.add("Kinetic: " + tier.displayName() + "+"));
        if (recipe.requiredKineticTier().isEmpty()) {
            recipe.minimumRuntimeTier().ifPresent(tier -> lines.add("Tier: " + tier.displayName() + "+"));
        }

        for (int i = 0; i < lines.size(); i++) {
            int y = startY + i * INFO_ROW_HEIGHT;
            guiGraphics.fill(8, y - 1, WIDTH - 8, y + 9, 0xFF17191C);
            guiGraphics.fill(9, y, WIDTH - 9, y + 8, 0xFF31363D);
            guiGraphics.drawString(font, lines.get(i), 12, y, 0xFFE6EAEE, false);
        }
    }

    private static String durationText(CERecipe recipe, MachineTier selectedTier) {
        int slowRpm = recipe.minRpm().orElse(1);
        int slow = recipe.runtimeDuration(selectedTier, slowRpm);
        int base = recipe.runtimeDuration(selectedTier, recipe.baseRpm());
        int fast = recipe.effectiveMaxRpm()
                .map(max -> recipe.runtimeDuration(selectedTier, max))
                .orElse(base);
        return fast == slow ? Integer.toString(slow) : fast + "-" + slow;
    }

    private static String rpmText(CERecipe recipe) {
        if (recipe.minRpm().isPresent() && recipe.maxRpm().isPresent()) {
            return recipe.minRpm().get() + "-" + recipe.maxRpm().get();
        }
        if (recipe.minRpm().isPresent()) {
            return ">=" + recipe.minRpm().get();
        }
        return recipe.maxRpm().map(max -> "1-" + max).orElse(">=1");
    }

    private MachineTier selectedTier(CERecipe recipe) {
        int minIndex = minimumTierIndex(recipe);
        int index = selectedTierIndexes.getOrDefault(recipe, minIndex);
        index = Math.max(minIndex, Math.min(MachineTier.ALL.size() - 1, index));
        selectedTierIndexes.put(recipe, index);
        return MachineTier.ALL.get(index);
    }

    private void adjustSelectedTier(CERecipe recipe, int amount) {
        if (!hasTierButton(recipe)) {
            return;
        }
        int minIndex = minimumTierIndex(recipe);
        int current = selectedTierIndexes.getOrDefault(recipe, minIndex);
        int next = Math.max(minIndex, Math.min(MachineTier.ALL.size() - 1, current + amount));
        selectedTierIndexes.put(recipe, next);
    }

    private static boolean hasTierButton(CERecipe recipe) {
        return recipe.minimumRuntimeTier().isPresent();
    }

    private static int minimumTierIndex(CERecipe recipe) {
        return recipe.minimumRuntimeTier()
                .map(MachineTierStats::tierIndex)
                .orElse(0);
    }

    private static void drawTierButton(CERecipe recipe, MachineTier selectedTier, GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, int height) {
        if (!hasTierButton(recipe)) {
            return;
        }

        int color = 0xFF000000 | selectedTier.color();
        int y = tierButtonY(height);
        guiGraphics.fill(TIER_BUTTON_X, y, TIER_BUTTON_X + TIER_BUTTON_WIDTH, y + TIER_BUTTON_HEIGHT, 0xFF111315);
        guiGraphics.fill(TIER_BUTTON_X + 1, y + 1, TIER_BUTTON_X + TIER_BUTTON_WIDTH - 1, y + TIER_BUTTON_HEIGHT - 1, color);
        int textX = TIER_BUTTON_X + (TIER_BUTTON_WIDTH - font.width(selectedTier.displayName())) / 2;
        guiGraphics.drawString(font, selectedTier.displayName(), textX, y + 3, 0xFFFFFFFF, false);
    }

    private static List<SizedIngredient> itemInputs(CERecipe recipe) {
        List<SizedIngredient> inputs = new ArrayList<>(recipe.itemInputs());
        inputs.addAll(recipe.notConsumableItems());
        return inputs;
    }

    private static List<SizedFluidIngredient> fluidInputs(CERecipe recipe) {
        List<SizedFluidIngredient> inputs = new ArrayList<>(recipe.fluidInputs());
        inputs.addAll(recipe.notConsumableFluids());
        return inputs;
    }

    private int itemInputSlotCount() {
        return recipeType.maxItemInputs();
    }

    private int itemInputSlotCount(List<SizedIngredient> inputs) {
        return Math.max(itemInputSlotCount(), inputs.size());
    }

    private int itemOutputSlotCount() {
        return recipeType.maxItemOutputs();
    }

    private int itemOutputSlotCount(List<CEChancedItemOutput> outputs) {
        return Math.max(itemOutputSlotCount(), outputs.size());
    }

    private int fluidInputSlotCount() {
        return recipeType.maxFluidInputs();
    }

    private int fluidInputSlotCount(List<SizedFluidIngredient> inputs) {
        return Math.max(fluidInputSlotCount(), inputs.size());
    }

    private int fluidOutputSlotCount() {
        return recipeType.maxFluidOutputs();
    }

    private int fluidOutputSlotCount(List<FluidStack> outputs) {
        return Math.max(fluidOutputSlotCount(), outputs.size());
    }

    private static int slotX(int baseX, int index) {
        return baseX + (index % MAX_COLUMNS) * SLOT_STEP;
    }

    private static int slotY(int baseY, int index) {
        return baseY + (index / MAX_COLUMNS) * SLOT_STEP;
    }

    private static int fluidY(int baseY, int itemCount, int fluidCount) {
        if (fluidCount <= 0) {
            return baseY + gridHeight(itemCount);
        }
        return baseY + gridHeight(itemCount) + (itemCount > 0 ? SECTION_GAP : 0);
    }

    private static int stackHeight(int itemCount, int fluidCount) {
        int height = gridHeight(itemCount);
        if (fluidCount > 0) {
            height += (itemCount > 0 ? SECTION_GAP : 0) + gridHeight(fluidCount);
        }
        return height;
    }

    private static int gridHeight(int count) {
        return rows(count) * SLOT_STEP;
    }

    private static int rows(int count) {
        return count <= 0 ? 0 : (count + MAX_COLUMNS - 1) / MAX_COLUMNS;
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static int tierButtonY(int height) {
        return height - TIER_BUTTON_HEIGHT - 6;
    }

    private static final class TierButtonListener implements IJeiGuiEventListener {
        private final CERecipeCategory category;
        private final CERecipe recipe;

        private TierButtonListener(CERecipeCategory category, CERecipe recipe) {
            this.category = category;
            this.recipe = recipe;
        }

        @Override
        public ScreenRectangle getArea() {
            return new ScreenRectangle(0, 0, WIDTH, category.getHeight());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!hasTierButton(recipe) || !inside(mouseX, mouseY, TIER_BUTTON_X, tierButtonY(category.getHeight()), TIER_BUTTON_WIDTH, TIER_BUTTON_HEIGHT)) {
                return false;
            }

            if (button != 0 && button != 1) {
                return false;
            }

            category.adjustSelectedTier(recipe, button == 0 ? -1 : 1);
            return true;
        }
    }
}
