package net.mads.createexpansion.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.recipe.recipes.sifter.SiftingRecipe;
import net.mads.createexpansion.recipe.recipetypes.SiftingRecipeType;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class SiftingCategory implements IRecipeCategory<RecipeHolder<SiftingRecipe>> {
    public static final RecipeType<RecipeHolder<SiftingRecipe>> TYPE = createRecipeHolderType(SiftingRecipeType.ID);

    private static final int WIDTH = 177;
    private static final int HEIGHT = 86;

    private final IDrawable icon;
    private final AnimatedSifter sifter = new AnimatedSifter();

    public SiftingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.KINETIC_SIFTER.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SiftingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal(KineticSifterRegistration.RECIPE_DISPLAY_NAME);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SiftingRecipe> holder, IFocusGroup focuses) {
        SiftingRecipe recipe = holder.value();

        if (!recipe.getIngredients().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 15, 9)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.getIngredients().getFirst());
        }

        List<ProcessingOutput> results = recipe.getRollableResults();
        boolean single = results.size() == 1;
        for (int i = 0; i < results.size(); i++) {
            ProcessingOutput output = results.get(i);
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;

            builder.addSlot(RecipeIngredientRole.OUTPUT, single ? 139 : 133 + xOffset, 27 + yOffset)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(CreateRecipeCategory.addStochasticTooltip(output));
        }
    }

    @Override
    public void draw(RecipeHolder<SiftingRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 32);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 4);
        sifter.draw(graphics, 48, 27);
    }

    private static class AnimatedSifter extends AnimatedKinetics {
        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(xOffset, yOffset, 0);
            AllGuiTextures.JEI_SHADOW.render(graphics, -16, 13);
            poseStack.translate(-2, 18, 0);

            int scale = 22;
            blockElement(cogwheel())
                    .rotateBlock(22.5, getCurrentAngle() * 2, 0)
                    .scale(scale)
                    .render(graphics);

            blockElement(BlockRegistry.KINETIC_SIFTER.get().defaultBlockState())
                    .rotateBlock(22.5, 22.5, 0)
                    .scale(scale)
                    .render(graphics);

            poseStack.popPose();
        }
    }
}
