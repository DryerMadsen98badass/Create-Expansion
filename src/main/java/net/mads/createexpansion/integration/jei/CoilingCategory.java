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
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipe;
import net.mads.createexpansion.recipe.recipetypes.CoilingRecipeType;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class CoilingCategory implements IRecipeCategory<RecipeHolder<CoilingRecipe>> {
    public static final RecipeType<RecipeHolder<CoilingRecipe>> TYPE = createRecipeHolderType(CoilingRecipeType.ID);
    private final IDrawable icon;
    private final MachinePreview preview = new MachinePreview();

    public CoilingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.SPRING_COILING_MACHINE.get()));
    }

    @Override public RecipeType<RecipeHolder<CoilingRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.literal(KineticCoilingMachineRegistration.RECIPE_DISPLAY_NAME); }
    @Override public @Nullable IDrawable getBackground() { return null; }
    @Override public int getWidth() { return 177; }
    @Override public int getHeight() { return 86; }
    @Override public @Nullable IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CoilingRecipe> holder, IFocusGroup focuses) {
        CoilingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 9)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.ingredients().getFirst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 27)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.results().getFirst()), -1, -1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<CoilingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);
        preview.draw(graphics, 48, 27);
        graphics.drawString(Minecraft.getInstance().font, "Min RPM: " + holder.value().minRpm(), 8, 72, 0xFF404040, false);
    }

    private static class MachinePreview extends AnimatedKinetics {
        @Override public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(xOffset + 2, yOffset + 22, 200);
            AllGuiTextures.JEI_SHADOW.render(graphics, -18, 12);
            pose.mulPose(Axis.XP.rotationDegrees(-15.5F));
            pose.mulPose(Axis.YP.rotationDegrees(112.5F));

            int scale = 25;
            blockElement(BlockRegistry.SPRING_COILING_MACHINE.get().defaultBlockState()
                    .setValue(HorizontalDirectionalBlock.FACING, Direction.WEST))
                    .scale(scale).render(graphics);

            blockElement(CreateExpansionPartialModels.COILING_WHEEL)
                    .rotateBlock(-getCurrentAngle(), 0, 0)
                    .withRotationOffset(new Vec3(5 / 16F, 10.5F / 16F, 11.5F / 16F))
                    .scale(scale).render(graphics);
            pose.popPose();
        }
    }
}
