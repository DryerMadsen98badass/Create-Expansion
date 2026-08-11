package net.mads.createexpansion.recipe.recipes.shaped;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class crafting {

    private crafting() {
    }

    public static void build(RecipeOutput output) {
        // Test recipe: 4 gullstenger i firkant -> 1 diamant (bare eksempel/mal)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:firebricks"))
                .pattern("AA")
                .pattern("AA")
                .define('A', item("create_expansion:firebrick"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":compacting/firebrick_to_firebricks");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:silica_bricks"))
                .pattern("AA")
                .pattern("AA")
                .define('A', item("create_expansion:silica_brick"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":compacting/silica_brick_to_silica_bricks");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:wood_plate"))
                .pattern("AAA")
                .define('A', tag("minecraft:wooden_slabs"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":crafting/materials/wood_plate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:treated_wood_plate"))
                .pattern("AAA")
                .define('A', item("create_expansion:treated_wood_slab"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":crafting/materials/treated_wood_plate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:concrete_bucket"))
                .pattern("AAA")
                .pattern("ABA")
                .pattern("ACA")
                .define('A', item("minecraft:clay_ball"))
                .define('B', item("minecraft:water_bucket"))
                .define('C', item("minecraft:bucket"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/concrete_bucket");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:firebrick_firebox"))
                .pattern(" B ")
                .pattern("BAB")
                .pattern(" B ")
                .define('A', item("create_expansion:firebricks"))
                .define('B', item("minecraft:iron_bars"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/firebrick_firebox");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:unfired_silica_brick"))
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', item("create_expansion:tuff_dust"))
                .define('B', item("create_expansion:concrete_bucket"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/unfired_silica_brick");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:unfired_firebrick"))
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', item("minecraft:clay_ball"))
                .define('B', item("create_expansion:concrete_bucket"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/unfired_firebrick");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:blast_furnace"))
                .pattern("AAA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', item("create_expansion:firebricks"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/blast_furnace");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:coke_oven"))
                .pattern("AAA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', item("create_expansion:silica_bricks"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/coke_oven");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:foundry_casing"))
                .pattern("AA")
                .pattern("AA")
                .define('A', item("create_expansion:seared_brick"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/foundry_casing");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:foundry_drain"))
                .pattern("A A")
                .pattern(" A ")
                .define('A', item("create_expansion:seared_brick"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/foundry_drain");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:foundry_mold_caster"))
                .pattern("AAA")
                .pattern("A A")
                .pattern("A A")
                .define('A', item("create_expansion:seared_brick"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/foundry_mold_caster");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:foundry_controller"))
                .pattern("AAA")
                .pattern("A A")
                .pattern("AAA")
                .define('A', item("create_expansion:seared_brick"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/foundry_controller");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:spool"))
                .pattern("AAA")
                .pattern(" A ")
                .pattern("AAA")
                .define('A', item("minecraft:string"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/spool");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:treated_wood_rod"), 4)
                .pattern("A")
                .pattern("A")
                .define('A', item("create_expansion:treated_wood"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/treated_wood_rod");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:bucket_clay"))
                .pattern("A A")
                .pattern(" A ")
                .define('A', Items.CLAY_BALL)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/bucket_clay");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.IRON_BARS)
                .pattern("AAA")
                .pattern("AAA")
                .define('A', Items.IRON_NUGGET)
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/iron_nugget_to_iron_bars");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create:propeller"))
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.IRON_INGOT)
                .define('B', item("create:andesite_alloy"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/propeller");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:stainless_bronze_fluid_pipe"))
                .pattern("A")
                .pattern("B")
                .pattern("A")
                .define('A', item("create_expansion:stainless_bronze_plate"))
                .define('B', item("create_expansion:stainless_bronze_ingot"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/stainless_bronze_fluid_pipe_vertical");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:stainless_bronze_fluid_pipe"))
                .pattern("ABA")
                .define('A', item("create_expansion:stainless_bronze_plate"))
                .define('B', item("create_expansion:stainless_bronze_ingot"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/stainless_bronze_fluid_pipe");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:stainless_bronze_fluid_tank"))
                .pattern("A")
                .pattern("B")
                .pattern("A")
                .define('A', item("create_expansion:stainless_bronze_plate"))
                .define('B', item("minecraft:barrel"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/stainless_bronze_fluid_tank");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:drill_head"))
                .pattern(" A ")
                .pattern("ABA")
                .pattern("ABA")
                .define('A', item("create:iron_sheet"))
                .define('B', item("create:andesite_alloy"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/drill_head");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item("create_expansion:stainless_bronze_hand"))
                .pattern(" A ")
                .pattern("AAA")
                .pattern(" B ")
                .define('A', item("create_expansion:stainless_bronze_plate"))
                .define('B', item("create_expansion:wrought_iron_ingot"))
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, CreateExpansion.MOD_ID + ":shaped/stainless_bronze_hand");


    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }

    private static Criterion<PlayerTrigger.TriggerInstance> alwaysTrue() {
        return CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}