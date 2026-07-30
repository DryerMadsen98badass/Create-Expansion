package net.mads.createexpansion.recipe.recipes.shapless;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class blocks {

    private blocks() {
    }

    public static void build(RecipeOutput output) {
        // Test recipe: 2 epler -> 1 gyllent eple (bare eksempel/mal)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:coal_coke_block"))
                .requires(item("create_expansion:coal_coke"), 9)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":block/coal_coke_block");
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static Criterion<PlayerTrigger.TriggerInstance> alwaysTrue() {
        return CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}