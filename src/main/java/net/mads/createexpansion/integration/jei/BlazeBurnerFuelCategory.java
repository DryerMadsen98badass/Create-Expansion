package net.mads.createexpansion.integration.jei;

import com.simibubi.create.AllBlocks;
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
import net.mads.createexpansion.recipe.recipetypes.BlazeBurnerFuelRecipeType;
import net.mads.createexpansion.recipe.recipes.blazeburnerrecipes.BlazeBurnerFuelRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class BlazeBurnerFuelCategory implements IRecipeCategory<RecipeHolder<BlazeBurnerFuelRecipe>> {
    public static final RecipeType<RecipeHolder<BlazeBurnerFuelRecipe>> TYPE =
            createRecipeHolderType(BlazeBurnerFuelRecipeType.ID);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 58;

    private final IDrawable icon;

    public BlazeBurnerFuelCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(AllBlocks.BLAZE_BURNER.asStack());
    }

    @Override
    public RecipeType<RecipeHolder<BlazeBurnerFuelRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Blaze Burner Fuel");
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
        return HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BlazeBurnerFuelRecipe> holder, IFocusGroup focuses) {
        BlazeBurnerFuelRecipe recipe = holder.value();
        recipe.ingredient().ifPresent(ingredient -> builder.addSlot(RecipeIngredientRole.INPUT, 18, 21)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(ingredient));

        recipe.fluidIngredient().ifPresent(fluid -> builder.addSlot(RecipeIngredientRole.INPUT, 18, 21)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addFluidStack(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch())
                .addTooltipCallback((slotView, tooltip) ->
                        tooltip.add(Component.literal("Amount: " + fluid.getAmount() + " mB"))));

        builder.addSlot(RecipeIngredientRole.CATALYST, 112, 21)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemStack(AllBlocks.BLAZE_BURNER.asStack());
    }

    @Override
    public void draw(RecipeHolder<BlazeBurnerFuelRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        BlazeBurnerFuelRecipe recipe = holder.value();
        AllGuiTextures.JEI_ARROW.render(graphics, 63, 25);

        Minecraft minecraft = Minecraft.getInstance();
        int y = 7;
        if (recipe.superheated() > 0) {
            graphics.drawString(minecraft.font, Component.literal("Superheated: " + recipe.superheated() + "t"), 50, y, 0xFF404040, false);
            y += 12;
        }
        if (recipe.heated() > 0) {
            graphics.drawString(minecraft.font, Component.literal("Heated: " + recipe.heated() + "t"), 50, y, 0xFF404040, false);
        }
    }

    public List<Component> getTooltipStrings(RecipeHolder<BlazeBurnerFuelRecipe> holder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltip = new ArrayList<>();
        BlazeBurnerFuelRecipe recipe = holder.value();
        if (mouseX >= 50 && mouseX < 140 && mouseY >= 5 && mouseY < 32) {
            if (recipe.superheated() > 0 && recipe.heated() > 0) {
                tooltip.add(Component.literal("Runs superheated first, then heated."));
            }
        }
        return tooltip;
    }
}
