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
import net.mads.createexpansion.recipe.recipetypes.FoundryMeltingRecipeType;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipe;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static mezz.jei.api.recipe.RecipeType.createRecipeHolderType;

public class FoundryMeltingCategory implements IRecipeCategory<RecipeHolder<FoundryMeltingRecipe>> {
    public static final RecipeType<RecipeHolder<FoundryMeltingRecipe>> TYPE = createRecipeHolderType(FoundryMeltingRecipeType.ID);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 60;

    private final IDrawable icon;

    public FoundryMeltingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.FOUNDRY_CONTROLLER.get()));
    }

    @Override
    public RecipeType<RecipeHolder<FoundryMeltingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Foundry Melting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FoundryMeltingRecipe> holder, IFocusGroup focuses) {
        FoundryMeltingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 22)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.ingredient());

        FluidStack result = recipe.result();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 22)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addFluidStack(result.getFluid(), result.getAmount(), result.getComponentsPatch())
                .addTooltipCallback((slotView, tooltip) ->
                        tooltip.add(Component.literal("Amount: " + result.getAmount() + " mB")));
    }

    @Override
    public void draw(RecipeHolder<FoundryMeltingRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 63, 26);
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font,
                Component.literal(holder.value().temperature() + " C"),
                55,
                8,
                0xFF404040,
                false);
    }

    public List<Component> getTooltipStrings(RecipeHolder<FoundryMeltingRecipe> holder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 55 && mouseX < 105 && mouseY >= 6 && mouseY < 18) {
            return List.of(Component.literal("Required temperature: " + holder.value().temperature() + " C"));
        }
        return List.of();
    }
}
