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
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.recipe.recipes.rolling.RollingRecipe;
import net.mads.createexpansion.recipe.recipetypes.RollingRecipeType;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class RollingCategory implements IRecipeCategory<RecipeHolder<RollingRecipe>> {
    public static final RecipeType<RecipeHolder<RollingRecipe>> TYPE = createRecipeHolderType(RollingRecipeType.ID);
    private final IDrawable icon;
    private final MachinePreview preview = new MachinePreview();

    public RollingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.KINETIC_ROLLING_MILL.get()));
    }

    @Override public RecipeType<RecipeHolder<RollingRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.literal(KineticRollingMillRegistration.RECIPE_DISPLAY_NAME); }
    @Override public @Nullable IDrawable getBackground() { return null; }
    @Override public int getWidth() { return 177; }
    @Override public int getHeight() { return 86; }
    @Override public @Nullable IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<RollingRecipe> holder, IFocusGroup focuses) {
        RollingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 9)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.ingredients().getFirst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 27)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.results().getFirst()), -1, -1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<RollingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);
        preview.draw(graphics, 48, 27);
        RpmJeiHelper.draw(graphics, 8, 72, holder.value().minRpm(), holder.value().maxRpm());
    }

    private static class MachinePreview extends AnimatedKinetics {
        @Override public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(xOffset, yOffset, 200);
            AllGuiTextures.JEI_SHADOW.render(graphics, -18, 26);
            pose.mulPose(Axis.XP.rotationDegrees(-15.5F));
            pose.mulPose(Axis.YP.rotationDegrees(22.5F));

            int scale = 22;
            float angle = getCurrentAngle() * 2;
            blockElement(CreateExpansionPartialModels.ROLLING_MILL_ROTOR_1)
                    .rotate(angle, 0, 0)
                    .withRotationOffset(new Vec3(0.5, 8.414213F / 16F, 0.5))
                    .scale(scale).render(graphics);

            blockElement(CreateExpansionPartialModels.ROLLING_MILL_ROTOR_2)
                    .rotate(-angle, 0, 0)
                    .withRotationOffset(new Vec3(0.5, 11 / 16F, 8.114213F / 16F))
                    .scale(scale).render(graphics);

            blockElement(BlockRegistry.KINETIC_ROLLING_MILL.get().defaultBlockState())
                    .scale(scale).render(graphics);
            pose.popPose();
        }
    }
}
