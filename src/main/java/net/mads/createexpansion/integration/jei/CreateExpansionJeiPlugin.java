package net.mads.createexpansion.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.multiblock.MultiblockDefinitions;
import net.mads.createexpansion.multiblock.MultiblockDefinition;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.registry.ItemRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class CreateExpansionJeiPlugin implements IModPlugin, IRecipeManagerPlugin {
    private static final Map<ResourceLocation, RecipeType<CERecipe>> RECIPE_TYPES = new HashMap<>();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MultiblockStructureCategory(registration.getJeiHelpers().getGuiHelper(), iconStack()));

        // Register categories for each CERecipeType
        for (var recipeType : CERecipeTypes.ALL) {
            RecipeType<CERecipe> jeiRecipeType = RecipeType.create(
                    CreateExpansion.MOD_ID,
                    recipeType.id().getPath(),
                    CERecipe.class
            );
            ItemStack icon = getCategoryIcon(recipeType.id());
            CERecipeCategory category = new CERecipeCategory(recipeType, jeiRecipeType, registration.getJeiHelpers().getGuiHelper(), icon);
            registration.addRecipeCategories(category);
            RECIPE_TYPES.put(recipeType.id(), jeiRecipeType);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(MultiblockStructureCategory.TYPE, MultiblockDefinitions.ALL.stream()
                .map(MultiblockJeiRecipe::new)
                .toList());

        // Register CERecipes grouped by type
        if (Minecraft.getInstance().level != null) {
            var recipeManager = Minecraft.getInstance().level.getRecipeManager();
            for (var recipeType : CERecipeTypes.ALL) {
                var recipes = recipeManager.getAllRecipesFor(RecipeRegistry.MACHINE_RECIPE_TYPE.get());
                var filtered = recipes.stream()
                        .map(r -> r.value())
                        .filter(r -> r.recipeType().equals(recipeType.id()))
                        .toList();

                if (!filtered.isEmpty()) {
                    RecipeType<CERecipe> jeiRecipeType = RECIPE_TYPES.get(recipeType.id());
                    if (jeiRecipeType != null) {
                        registration.addRecipes(jeiRecipeType, filtered);
                    }
                }
            }
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemRegistry.getAllMultiblockControllerItems().forEach(item ->
                registration.addRecipeCatalyst(item.get(), MultiblockStructureCategory.TYPE));

        ItemStack commandBlock = new ItemStack(Items.COMMAND_BLOCK);
        for (var recipeType : RECIPE_TYPES.values()) {
            registration.addRecipeCatalyst(commandBlock, recipeType);
        }

        for (MultiblockDefinition definition : MultiblockDefinitions.ALL) {
            DeferredHolder<net.minecraft.world.item.Item, net.minecraft.world.item.BlockItem> controllerItem =
                    ItemRegistry.MULTIBLOCK_CONTROLLERS.get(definition.controller().registryName());
            if (controllerItem == null) {
                continue;
            }

            for (ResourceLocation recipeTypeId : definition.recipeTypes()) {
                RecipeType<CERecipe> jeiRecipeType = RECIPE_TYPES.get(recipeTypeId);
                if (jeiRecipeType != null) {
                    registration.addRecipeCatalyst(controllerItem.get(), jeiRecipeType);
                }
            }
        }
    }

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        return new ArrayList<>(RECIPE_TYPES.values());
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> category) {
        return getRecipes(category, null);
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> category, IFocus<V> focus) {
        if (Minecraft.getInstance().level == null) {
            return List.of();
        }
        var recipeManager = Minecraft.getInstance().level.getRecipeManager();
        var recipeTypeUid = category.getRecipeType().getUid();
        var recipes = recipeManager.getAllRecipesFor(RecipeRegistry.MACHINE_RECIPE_TYPE.get()).stream()
                .map(r -> r.value())
                .filter(r -> r.recipeType().equals(recipeTypeUid))
                .map(r -> (T) r)
                .toList();
        return recipes;
    }

    private static ItemStack iconStack() {
        return new ItemStack(Items.COMMAND_BLOCK);
    }

    private ItemStack getCategoryIcon(ResourceLocation recipeTypeId) {
        return new ItemStack(Items.COMMAND_BLOCK);
    }
}
