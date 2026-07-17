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
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinitions;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinition;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipes;
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
        registration.addRecipeCategories(new SiftingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CentrifugingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TurningCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RollingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new WireDrawingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new HydraulicPressingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CoilingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FoundryMeltingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FoundryCastingCategory(registration.getJeiHelpers().getGuiHelper()));

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
            registration.addRecipes(SiftingCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.SIFTING_RECIPE_TYPE.get()));
            registration.addRecipes(CentrifugingCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.CENTRIFUGING_RECIPE_TYPE.get()));
            registration.addRecipes(TurningCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.TURNING_RECIPE_TYPE.get()));
            registration.addRecipes(RollingCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.ROLLING_RECIPE_TYPE.get()));
            registration.addRecipes(WireDrawingCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.WIRE_DRAWING_RECIPE_TYPE.get()));
            registration.addRecipes(HydraulicPressingCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.HYDRAULIC_PRESSING_RECIPE_TYPE.get()));
            registration.addRecipes(CoilingCategory.TYPE, recipeManager.getAllRecipesFor(RecipeRegistry.COILING_RECIPE_TYPE.get()));
            var foundryMeltingRecipes = recipeManager.getAllRecipesFor(RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get());
            registration.addRecipes(FoundryMeltingCategory.TYPE, foundryMeltingRecipes.isEmpty() ? FoundryMeltingRecipes.syntheticRecipes() : foundryMeltingRecipes);
            registration.addRecipes(FoundryCastingCategory.TYPE, FoundryCastingJeiRecipe.all());

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
        } else {
            registration.addRecipes(FoundryMeltingCategory.TYPE, FoundryMeltingRecipes.syntheticRecipes());
            registration.addRecipes(FoundryCastingCategory.TYPE, FoundryCastingJeiRecipe.all());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ItemRegistry.getAllMultiblockControllerItems().forEach(item ->
                registration.addRecipeCatalyst(item.get(), MultiblockStructureCategory.TYPE));
        registration.addRecipeCatalyst(ItemRegistry.KINETIC_SIFTER.get(), SiftingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.KINETIC_CENTRIFUGE.get(), CentrifugingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.KINETIC_LATHE.get(), TurningCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.KINETIC_ROLLING_MILL.get(), RollingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.KINETIC_WIRE_DRAWER.get(), WireDrawingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.HYDRAULIC_PRESS.get(), HydraulicPressingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.SPRING_COILING_MACHINE.get(), CoilingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.FOUNDRY_CONTROLLER.get(), FoundryMeltingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.FOUNDRY_CONTROLLER.get(), FoundryCastingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.FOUNDRY_DRAIN.get(), FoundryCastingCategory.TYPE);
        registration.addRecipeCatalyst(ItemRegistry.FOUNDRY_MOLD_CASTER.get(), FoundryCastingCategory.TYPE);

        for (var recipeType : RECIPE_TYPES.values()) {
            registration.addRecipeCatalyst(getCategoryIcon(recipeType.getUid()), recipeType);
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
        List<RecipeType<?>> recipeTypes = new ArrayList<>(RECIPE_TYPES.values());
        recipeTypes.add(SiftingCategory.TYPE);
        recipeTypes.add(CentrifugingCategory.TYPE);
        recipeTypes.add(TurningCategory.TYPE);
        recipeTypes.add(RollingCategory.TYPE);
        recipeTypes.add(WireDrawingCategory.TYPE);
        recipeTypes.add(HydraulicPressingCategory.TYPE);
        recipeTypes.add(CoilingCategory.TYPE);
        recipeTypes.add(FoundryMeltingCategory.TYPE);
        recipeTypes.add(FoundryCastingCategory.TYPE);
        return recipeTypes;
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> category) {
        return getRecipes(category, null);
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> category, IFocus<V> focus) {
        var recipeTypeUid = category.getRecipeType().getUid();
        if (recipeTypeUid.equals(FoundryMeltingCategory.TYPE.getUid())) {
            if (Minecraft.getInstance().level == null) {
                return FoundryMeltingRecipes.syntheticRecipes().stream()
                        .map(recipe -> (T) recipe)
                        .toList();
            }

            var recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get());
            return (recipes.isEmpty() ? FoundryMeltingRecipes.syntheticRecipes() : recipes).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(FoundryCastingCategory.TYPE.getUid())) {
            return FoundryCastingJeiRecipe.all().stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }

        if (Minecraft.getInstance().level == null) {
            return List.of();
        }
        var recipeManager = Minecraft.getInstance().level.getRecipeManager();
        if (recipeTypeUid.equals(SiftingCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.SIFTING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(CentrifugingCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.CENTRIFUGING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(TurningCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.TURNING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(RollingCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.ROLLING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(WireDrawingCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.WIRE_DRAWING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(HydraulicPressingCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.HYDRAULIC_PRESSING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
        if (recipeTypeUid.equals(CoilingCategory.TYPE.getUid())) {
            return recipeManager.getAllRecipesFor(RecipeRegistry.COILING_RECIPE_TYPE.get()).stream()
                    .map(recipe -> (T) recipe)
                    .toList();
        }
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
