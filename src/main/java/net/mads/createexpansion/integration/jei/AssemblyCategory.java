package net.mads.createexpansion.integration.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mads.createexpansion.recipe.recipetypes.AssemblyRecipeType;
import net.mads.createexpansion.recipe.recipes.assembly.AssemblyRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class AssemblyCategory implements IRecipeCategory<RecipeHolder<AssemblyRecipe>> {
    public static final RecipeType<RecipeHolder<AssemblyRecipe>> TYPE = createRecipeHolderType(AssemblyRecipeType.ID);
    private static final int FIRST_STEP_X = 38;
    private static final int FIRST_STEP_Y = 8;
    private static final int STEP_SPACING = 18;
    private final IDrawable icon;

    public AssemblyCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(Items.CRAFTING_TABLE));
    }

    @Override public RecipeType<RecipeHolder<AssemblyRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.literal("Assembly"); }
    @Override public @Nullable IDrawable getBackground() { return null; }
    @Override public int getWidth() { return 177; }
    @Override public int getHeight() { return 82; }
    @Override public @Nullable IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AssemblyRecipe> holder, IFocusGroup focuses) {
        AssemblyRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 31)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.base());

        int shown = Math.min(recipe.inputs().size(), AssemblyRecipeType.MAX_JEI_STEPS);
        for (int i = 0; i < shown; i++) {
            SizedIngredient input = recipe.inputs().get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, stepX(i), stepY(i))
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addItemStacks(Arrays.stream(input.ingredient().getItems())
                            .map(stack -> stack.copyWithCount(input.count()))
                            .toList());
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 151, 31)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.resultOutput()), -1, -1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<AssemblyRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 28, 35);
        AllGuiTextures.JEI_ARROW.render(graphics, 129, 35);
        AssemblyRecipe recipe = holder.value();
        int shown = Math.min(recipe.inputs().size(), AssemblyRecipeType.MAX_JEI_STEPS);
        for (int i = 0; i < shown; i++) {
            int count = recipe.inputs().get(i).count();
            if (count > 1) {
                graphics.drawString(Minecraft.getInstance().font, Integer.toString(count), stepX(i) + 11, stepY(i) + 10, 0xFF404040, false);
            }
        }
        int hidden = recipe.inputs().size() - shown;
        if (hidden > 0) {
            graphics.drawString(Minecraft.getInstance().font, "+" + hidden, 113, 68, 0xFF404040, false);
        }
        if (recipe.loops() > 1) {
            graphics.drawString(Minecraft.getInstance().font, "Loops: " + recipe.loops(), 8, 72, 0xFF404040, false);
        }
    }

    private static int stepX(int index) {
        return FIRST_STEP_X + (index % 5) * STEP_SPACING;
    }

    private static int stepY(int index) {
        return FIRST_STEP_Y + (index / 5) * STEP_SPACING;
    }
}
