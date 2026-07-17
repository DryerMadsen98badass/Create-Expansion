package net.mads.createexpansion.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressBlockEntity;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRegistration;
import net.mads.createexpansion.recipe.recipetypes.HydraulicPressingRecipeType;
import net.mads.createexpansion.recipe.recipes.hydraulicpress.HydraulicPressingRecipe;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class HydraulicPressingCategory implements IRecipeCategory<RecipeHolder<HydraulicPressingRecipe>> {
    public static final RecipeType<RecipeHolder<HydraulicPressingRecipe>> TYPE =
            createRecipeHolderType(HydraulicPressingRecipeType.ID);
    private final IDrawable icon;
    private final MachinePreview preview = new MachinePreview();

    public HydraulicPressingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.HYDRAULIC_PRESS.get()));
    }

    @Override public RecipeType<RecipeHolder<HydraulicPressingRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.literal(HydraulicPressRegistration.RECIPE_DISPLAY_NAME); }
    @Override public @Nullable IDrawable getBackground() { return null; }
    @Override public int getWidth() { return 177; }
    @Override public int getHeight() { return 82; }
    @Override public @Nullable IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HydraulicPressingRecipe> holder, IFocusGroup focuses) {
        HydraulicPressingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 10)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.ingredients().getFirst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 28)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.results().getFirst()), -1, -1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<HydraulicPressingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 34);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 5);
        preview.draw(graphics, 51, 29);
        int blows = holder.value().blows();
        graphics.drawString(Minecraft.getInstance().font, "Blows: " + blows, 8, 72, 0xFF404040, false);
    }

    private static class MachinePreview extends AnimatedKinetics {
        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(xOffset, yOffset, 200);
            AllGuiTextures.JEI_SHADOW.render(graphics, -18, 27);
            pose.mulPose(Axis.XP.rotationDegrees(-15.5F));
            pose.mulPose(Axis.YP.rotationDegrees(22.5F));
            int scale = 22;
            blockElement(CreateExpansionPartialModels.HYDRAULIC_PRESS_HEAD)
                    .atLocal(0, headOffset(), 0)
                    .scale(scale).render(graphics);
            blockElement(BlockRegistry.HYDRAULIC_PRESS.get().defaultBlockState())
                    .scale(scale).render(graphics);
            pose.popPose();
        }

        private float headOffset() {
            float tick = AnimationTickHolder.getRenderTime() % HydraulicPressBlockEntity.CYCLE_TICKS;
            if (tick <= HydraulicPressBlockEntity.DOWN_TICKS) {
                float progress = tick / HydraulicPressBlockEntity.DOWN_TICKS;
                return progress * progress * progress * HydraulicPressBlockEntity.HEAD_TRAVEL;
            }
            float progress = (tick - HydraulicPressBlockEntity.DOWN_TICKS) / HydraulicPressBlockEntity.UP_TICKS;
            return (1 - progress) * HydraulicPressBlockEntity.HEAD_TRAVEL;
        }
    }
}
