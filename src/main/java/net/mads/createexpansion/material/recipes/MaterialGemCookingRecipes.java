package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
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

public final class MaterialGemCookingRecipes {
    private static final float EXPERIENCE = 0.7F;
    private static final int SMELTING_TIME = 200;
    private static final int BLASTING_TIME = 100;

    private static final List<InputPart> INPUT_PARTS = List.of(
            new InputPart(MaterialPart.RAW_ORE, "raw_ore"),
            new InputPart(MaterialPart.CRUSHED_ORE, "crushed_ore"),
            new InputPart(MaterialPart.WASHED_CRUSHED_ORE, "washed_crushed_ore")
    );

    private MaterialGemCookingRecipes() {
    }

    public static void build(RecipeOutput output) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (!MaterialRecipeHelper.hasItems(material, MaterialPart.GEM)) {
                continue;
            }

            String gem = MaterialRecipeHelper.itemId(material, MaterialPart.GEM);
            for (InputPart inputPart : INPUT_PARTS) {
                if (!MaterialRecipeHelper.hasItems(material, inputPart.part())) {
                    continue;
                }

                String input = MaterialRecipeHelper.itemId(material, inputPart.part());
                saveSmelting(output, material, inputPart, input, gem);
                saveBlasting(output, material, inputPart, input, gem);
            }
        }
    }

    private static void saveSmelting(
            RecipeOutput output,
            IndustrialMaterial material,
            InputPart inputPart,
            String input,
            String gem
    ) {
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(item(input)),
                        RecipeCategory.MISC,
                        item(gem),
                        EXPERIENCE,
                        SMELTING_TIME
                )
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, recipeId("smelting", material, inputPart.name()));
    }

    private static void saveBlasting(
            RecipeOutput output,
            IndustrialMaterial material,
            InputPart inputPart,
            String input,
            String gem
    ) {
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(item(input)),
                        RecipeCategory.MISC,
                        item(gem),
                        EXPERIENCE,
                        BLASTING_TIME
                )
                .unlockedBy("always_unlocked", alwaysTrue())
                .save(output, recipeId("blasting", material, inputPart.name()));
    }

    private static String recipeId(String folder, IndustrialMaterial material, String inputName) {
        return CreateExpansion.MOD_ID
                + ":"
                + folder
                + "/materials/"
                + material.id()
                + "_"
                + inputName
                + "_to_gem";
    }

    private static Item item(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    private static Criterion<PlayerTrigger.TriggerInstance> alwaysTrue() {
        return CriteriaTriggers.TICK.createCriterion(
                new PlayerTrigger.TriggerInstance(Optional.empty())
        );
    }

    private record InputPart(MaterialPart part, String name) {
    }
}
