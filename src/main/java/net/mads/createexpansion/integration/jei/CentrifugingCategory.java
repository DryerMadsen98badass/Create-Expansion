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
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugingRecipe;
import net.mads.createexpansion.recipe.recipetypes.CentrifugingRecipeType;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class CentrifugingCategory implements IRecipeCategory<RecipeHolder<CentrifugingRecipe>> {
    public static final RecipeType<RecipeHolder<CentrifugingRecipe>> TYPE = createRecipeHolderType(CentrifugingRecipeType.ID);

    private static final int WIDTH = 177;
    private static final int HEIGHT = 104;

    private final IDrawable icon;
    private final AnimatedCentrifuge centrifuge = new AnimatedCentrifuge();

    public CentrifugingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.KINETIC_CENTRIFUGE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<CentrifugingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal(KineticCentrifugeRegistration.RECIPE_DISPLAY_NAME);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CentrifugingRecipe> holder, IFocusGroup focuses) {
        CentrifugingRecipe recipe = holder.value();

        if (!recipe.itemIngredients().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 15, 14)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.itemIngredients().getFirst());
        }

        if (!recipe.fluidIngredients().isEmpty()) {
            CreateRecipeCategory.addFluidSlot(builder, 15, 58, recipe.fluidIngredients().getFirst());
        }

        List<ProcessingOutput> results = recipe.itemResults();
        boolean single = results.size() == 1;
        for (int i = 0; i < results.size(); i++) {
            ProcessingOutput output = results.get(i);
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;

            builder.addSlot(RecipeIngredientRole.OUTPUT, single ? 139 : 133 + xOffset, 32 + yOffset)
                    .setBackground(CreateRecipeCategory.getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(CreateRecipeCategory.addStochasticTooltip(output));
        }

        List<FluidStack> fluidResults = recipe.fluidResults();
        if (!fluidResults.isEmpty()) {
            CreateRecipeCategory.addFluidSlot(builder, 139, 58, fluidResults.getFirst());
        }
    }

    @Override
    public void draw(RecipeHolder<CentrifugingRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 85, 37);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 8);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 43, 52);
        centrifuge.draw(graphics, 52, 35);
    }

    private static class AnimatedCentrifuge extends AnimatedKinetics {
        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(xOffset, yOffset, 200);

            int scale = 17;
            AllGuiTextures.JEI_SHADOW.render(graphics, -18, 26);

            blockElement(shaft(Direction.Axis.Y))
                    .rotateBlock(0, getCurrentAngle() * 2, 0)
                    .atLocal(0, 1.1, 0)
                    .scale(scale)
                    .render(graphics);

            blockElement(BlockRegistry.KINETIC_CENTRIFUGE.get().defaultBlockState())
                    .scale(scale)
                    .render(graphics);

            blockElement(CreateExpansionPartialModels.CENTRIFUGE_ROTOR)
                    .rotateBlock(0, getCurrentAngle() * 4, 0)
                    .scale(scale)
                    .render(graphics);

            blockElement(CreateExpansionPartialModels.CENTRIFUGE_BASIN)
                    .rotateBlock(0, getCurrentAngle() * 4, 0)
                    .atLocal(28 / 16f, 0, 0)
                    .scale(scale)
                    .render(graphics);
            blockElement(CreateExpansionPartialModels.CENTRIFUGE_BASIN)
                    .rotateBlock(0, getCurrentAngle() * 4, 0)
                    .atLocal(-28 / 16f, 0, 0)
                    .scale(scale)
                    .render(graphics);
            blockElement(CreateExpansionPartialModels.CENTRIFUGE_BASIN)
                    .rotateBlock(0, getCurrentAngle() * 4, 0)
                    .atLocal(0, 0, 28 / 16f)
                    .scale(scale)
                    .render(graphics);
            blockElement(CreateExpansionPartialModels.CENTRIFUGE_BASIN)
                    .rotateBlock(0, getCurrentAngle() * 4, 0)
                    .atLocal(0, 0, -28 / 16f)
                    .scale(scale)
                    .render(graphics);

            poseStack.popPose();
        }
    }
}
