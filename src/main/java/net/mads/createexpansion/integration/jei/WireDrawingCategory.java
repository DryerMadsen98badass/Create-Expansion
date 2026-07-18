package net.mads.createexpansion.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipe;
import net.mads.createexpansion.recipe.recipetypes.WireDrawingRecipeType;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class WireDrawingCategory implements IRecipeCategory<RecipeHolder<WireDrawingRecipe>> {
    public static final RecipeType<RecipeHolder<WireDrawingRecipe>> TYPE = createRecipeHolderType(WireDrawingRecipeType.ID);
    private final IDrawable icon;
    private final MachinePreview preview = new MachinePreview();

    public WireDrawingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.KINETIC_WIRE_DRAWER.get()));
    }

    @Override public RecipeType<RecipeHolder<WireDrawingRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.literal(KineticWireDrawerRegistration.RECIPE_DISPLAY_NAME); }
    @Override public @Nullable IDrawable getBackground() { return null; }
    @Override public int getWidth() { return 177; }
    @Override public int getHeight() { return 86; }
    @Override public @Nullable IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<WireDrawingRecipe> holder, IFocusGroup focuses) {
        WireDrawingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 9)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.ingredients().getFirst());
        if (recipe.ingredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 15, 31)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.ingredients().get(1));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 27)
                .setBackground(CreateRecipeCategory.getRenderedSlot(recipe.results().getFirst()), -1, -1)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<WireDrawingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);
        preview.draw(graphics, 48, 27);
        RpmJeiHelper.draw(graphics, 8, 72, holder.value().minRpm(), holder.value().maxRpm());
    }

    private static class MachinePreview extends AnimatedKinetics {
        @Override public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(xOffset - 2, yOffset + 18, 0);
            AllGuiTextures.JEI_SHADOW.render(graphics, -16, 13);
            blockElement(BlockRegistry.KINETIC_WIRE_DRAWER.get().defaultBlockState())
                    .rotateBlock(22.5, 22.5, 0).scale(22).render(graphics);
            pose.popPose();
        }
    }
}
