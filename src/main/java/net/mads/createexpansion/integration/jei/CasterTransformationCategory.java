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
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.recipes.foundry.CasterTransformationRecipe;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CasterTransformationCategory implements IRecipeCategory<CasterTransformationRecipe> {
    public static final RecipeType<CasterTransformationRecipe> TYPE = RecipeType.create(
            CreateExpansion.MOD_ID,
            "caster_transformation",
            CasterTransformationRecipe.class
    );
    public static final ResourceLocation ID = TYPE.getUid();

    private static final int WIDTH = 160;
    private static final int HEIGHT = 70;

    private final IDrawable icon;

    public CasterTransformationCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.FOUNDRY_MOLD_CASTER.get()));
    }

    @Override
    public RecipeType<CasterTransformationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Caster Transformation");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CasterTransformationRecipe recipe, IFocusGroup focuses) {
        FluidStack fluid = recipe.fluid();
        builder.addSlot(RecipeIngredientRole.INPUT, 14, 27)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.template());

        builder.addSlot(RecipeIngredientRole.INPUT, 54, 27)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addFluidStack(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch())
                .addTooltipCallback((slotView, tooltip) ->
                        tooltip.add(Component.literal("Amount: " + fluid.getAmount() + " mB")));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 122, 27)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(CasterTransformationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 86, 31);
    }
}
