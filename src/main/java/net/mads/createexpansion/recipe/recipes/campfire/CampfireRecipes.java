package net.mads.createexpansion.recipe.recipes.campfire;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public final class CampfireRecipes {

    private static final List<CampfireRecipe> RECIPES = List.of(
            new CampfireRecipe("create_expansion:unfired_firebrick", "create_expansion:firebrick", 600),
            new CampfireRecipe("create_expansion:unfired_silica_brick", "create_expansion:silica_brick", 600),
            new CampfireRecipe("create_expansion:bucket_clay", "create_expansion:fired_bucket", 600)
    );

    private CampfireRecipes() {
    }

    public static void build(RecipeOutput output) {
        for (CampfireRecipe recipe : RECIPES) {
            SimpleCookingRecipeBuilder.campfireCooking(
                            Ingredient.of(item(recipe.input())),
                            RecipeCategory.MISC,
                            item(recipe.output()),
                            0.0F,
                            recipe.duration()
                    )
                    .unlockedBy("always_unlocked", alwaysTrue())
                    .save(
                            output,
                            CreateExpansion.MOD_ID
                                    + ":campfire/"
                                    + path(recipe.output())
                                    + "_from_"
                                    + path(recipe.input())
                    );
        }
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(
                ResourceLocation.parse(id)
        );
    }

    private static String path(String id) {
        return ResourceLocation.parse(id).getPath();
    }

    private static Criterion<PlayerTrigger.TriggerInstance> alwaysTrue() {
        return CriteriaTriggers.TICK.createCriterion(
                new PlayerTrigger.TriggerInstance(Optional.empty())
        );
    }

    private record CampfireRecipe(
            String input,
            String output,
            int duration
    ) {
    }
}