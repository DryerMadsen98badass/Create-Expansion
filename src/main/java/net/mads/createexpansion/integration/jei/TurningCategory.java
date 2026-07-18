package net.mads.createexpansion.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.recipe.recipes.lathe.TurningRecipe;
import net.mads.createexpansion.recipe.recipetypes.TurningRecipeType;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class TurningCategory implements IRecipeCategory<RecipeHolder<TurningRecipe>> {
    public static final RecipeType<RecipeHolder<TurningRecipe>> TYPE = createRecipeHolderType(TurningRecipeType.ID);

    private static final int WIDTH = 177;
    private static final int HEIGHT = 96;

    private final IDrawable icon;
    private final StaticLathe lathe = new StaticLathe();

    public TurningCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.KINETIC_LATHE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<TurningRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal(KineticLatheRegistration.RECIPE_DISPLAY_NAME);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TurningRecipe> holder, IFocusGroup focuses) {
        TurningRecipe recipe = holder.value();

        if (!recipe.itemIngredients().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 15, 16)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.itemIngredients().getFirst());
        }

        List<ProcessingOutput> results = recipe.itemResults();
        boolean single = results.size() == 1;
        for (int i = 0; i < results.size(); i++) {
            ProcessingOutput output = results.get(i);
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;

            builder.addSlot(RecipeIngredientRole.OUTPUT, single ? 139 : 133 + xOffset, 34 + yOffset)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(CreateRecipeCategory.addStochasticTooltip(output));
        }
    }

    @Override
    public void draw(RecipeHolder<TurningRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        TurningRecipe recipe = holder.value();
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 39);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 10);
        AllGuiTextures.JEI_SHADOW.render(graphics, 36, 57);
        lathe.draw(graphics, 52, 39);

        RpmJeiHelper.draw(graphics, 8, 78, recipe.minRpm(), recipe.maxRpm());
    }

    private static class StaticLathe extends AnimatedKinetics {
        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(xOffset, yOffset, 200);
            poseStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
            poseStack.mulPose(Axis.YP.rotationDegrees(22.5F));

            int scale = 20;
            blockElement(CreateExpansionPartialModels.LATHE_SIDE_SHAFT)
                    .rotateBlock(getCurrentAngle() * 2, 0, 0)
                    .scale(scale)
                    .render(graphics);
            blockElement(BlockRegistry.KINETIC_LATHE.get().defaultBlockState())
                    .scale(scale)
                    .render(graphics);

            poseStack.popPose();
        }
    }

}
