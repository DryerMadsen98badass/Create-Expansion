package net.mads.createexpansion.recipe.recipes.shaped;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class crafting {

    private crafting() {
    }

    public static void build(RecipeOutput output) {
        // Test recipe: 4 gullstenger i firkant -> 1 diamant (bare eksempel/mal)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("minecraft:diamond"))
                .pattern("GG")
                .pattern("GG")
                .define('G', item("minecraft:gold_ingot"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":test/shaped_gold_to_diamond");
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static Criterion<PlayerTrigger.TriggerInstance> alwaysTrue() {
        return CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}