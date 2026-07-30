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

public class crafting {

    private crafting() {
    }

    public static void build(RecipeOutput output) {
        // Test recipe: 2 epler -> 1 gyllent eple (bare eksempel/mal)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:iron_andesite_compound_dust"))
                .requires(item("create_expansion:iron_dust"), 1)
                .requires(item("create_expansion:andesite_dust"), 8)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/unfired_firebrick");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:treated_wood"))
                .requires(item("create_expansion:creosote_oil_bucket"), 1)
                .requires(item("minecraft:spruce_planks"), 1)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/treated_wood_from_spruce");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:treated_wood"))
                .requires(item("create_expansion:creosote_oil_bucket"), 1)
                .requires(item("minecraft:oak_planks"), 1)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/treated_wood_from_oak");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:treated_wood"))
                .requires(item("create_expansion:creosote_oil_bucket"), 1)
                .requires(item("minecraft:birch_planks"), 1)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/treated_wood_from_birch");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:seared_dust"))
                .requires(item("create_expansion:tuff_dust"), 1)
                .requires(item("create_expansion:nether_brick_dust"), 1)
                .requires(item("create_expansion:clay_dust"), 1)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/seared_dust");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("create_expansion:mesh"))
                .requires(item("minecraft:string"), 9)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/mesh");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item("minecraft:flint_and_steel"))
                .requires(item("create_expansion:steel_ingot"))
                .requires(item("minecraft:flint"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shapeless/flint_and_steel");

    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static Criterion<PlayerTrigger.TriggerInstance> alwaysTrue() {
        return CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}