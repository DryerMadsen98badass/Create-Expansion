package net.mads.createexpansion.material.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.recipe.recipes.sifter.SifterRecipeBuilder;
import net.mads.createexpansion.recipe.recipes.sifter.SifterRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CreateMaterialRecipeProvider implements DataProvider {

    /*
     * ============================================================
     * VANILLA COOKING TIMES
     * ============================================================
     */

    private static final int FURNACE_COOKING_TIME = 200;
    private static final int BLAST_FURNACE_COOKING_TIME = 100;

    private static final float ORE_COOKING_EXPERIENCE = 0.7F;


    /*
     * ============================================================
     * CREATE CRUSHING TIMES
     * ============================================================
     */

    private static final int ORE_TO_RAW_ORE_CRUSHING_TIME = 250;

    private static final int RAW_ORE_TO_CRUSHED_ORE_CRUSHING_TIME = 200;

    private static final int CRUSHED_ORE_TO_IMPURE_DUST_CRUSHING_TIME = 250;

    private static final int WASHED_CRUSHED_ORE_TO_TINY_DUST_CRUSHING_TIME = 250;

    private static final int GEM_TO_DUST_CRUSHING_TIME = 250;


    /*
     * ============================================================
     * CREATE MILLING TIMES
     * ============================================================
     */

    private static final int INGOT_TO_DUST_MILLING_TIME = 250;

    private static final int NUGGET_TO_TINY_DUST_MILLING_TIME = 100;

    private static final int STONE_TO_DUST_MILLING_TIME = 250;


    /*
     * ============================================================
     * CREATE CUTTING TIMES
     * ============================================================
     */

    private static final int MATERIAL_CUTTING_TIME = 100;

    private static final int GEM_CUTTING_TIME = 100;


    /*
     * ============================================================
     * SIFTER SETTINGS
     * ============================================================
     */

    private static final int WASHED_CRUSHED_ORE_SIFTING_TIME = 200;

    private static final int WASHED_CRUSHED_ORE_SIFTING_MIN_RPM = 64;

    private static final float WASHED_CRUSHED_ORE_GEM_CHANCE = 0.75F;

    private static final float WASHED_CRUSHED_ORE_FLAWLESS_GEM_CHANCE = 0.25F;

    private static final float WASHED_CRUSHED_ORE_EXQUISITE_GEM_CHANCE = 0.05F;


    private final PackOutput.PathProvider recipes;

    private final CompletableFuture<HolderLookup.Provider> registries;


    public CreateMaterialRecipeProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        this.recipes = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                "recipe"
        );

        this.registries = registries;
    }


    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> {
            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                saveOreCookingRecipes(
                        futures,
                        output,
                        material
                );

                saveRawOreCrushing(
                        futures,
                        output,
                        material
                );

                saveGemCrushing(
                        futures,
                        output,
                        material
                );

                saveMaterialCompacting(
                        futures,
                        output,
                        material
                );

                saveDustCrafting(
                        futures,
                        output,
                        material
                );

                saveGemCutting(
                        futures,
                        output,
                        material
                );

                saveGemStorageCrafting(
                        futures,
                        output,
                        material
                );

                saveRawStorageCrafting(
                        futures,
                        output,
                        material
                );

                saveIngotMilling(
                        futures,
                        output,
                        material
                );

                saveNuggetMilling(
                        futures,
                        output,
                        material
                );

                saveStoneMilling(
                        futures,
                        output,
                        material
                );

                saveCutting(
                        futures,
                        output,
                        material,
                        MaterialPart.PLATE,
                        MaterialPart.LONG_ROD,
                        1,
                        "plate_to_long_rod"
                );

                saveCutting(
                        futures,
                        output,
                        material,
                        MaterialPart.LONG_ROD,
                        MaterialPart.ROD,
                        2,
                        "long_rod_to_rod"
                );

                saveCutting(
                        futures,
                        output,
                        material,
                        MaterialPart.GEAR,
                        MaterialPart.TOOL_HEAD_BUZZ_SAW,
                        1,
                        "gear_to_tool_head_buzz_saw"
                );

                savePressing(
                        futures,
                        output,
                        material,
                        MaterialPart.BOLT,
                        MaterialPart.SCREW,
                        "bolt_to_screw"
                );

                saveSequencedDoublePlate(
                        futures,
                        output,
                        material
                );

                saveSequencedBearing(
                        futures,
                        output,
                        material
                );

                saveSequencedFrame(
                        futures,
                        output,
                        material
                );

                saveOreCrushing(
                        futures,
                        output,
                        material
                );

                saveOreProcessingRecipes(
                        futures,
                        output,
                        material
                );
            }

            return CompletableFuture.allOf(
                    futures.toArray(CompletableFuture[]::new)
            );
        });
    }


    /*
     * ============================================================
     * SIFTER MATERIAL RECIPES
     * ============================================================
     */

    public static void buildSifterRecipes(
            RecipeOutput output,
            HolderLookup.Provider holderLookup
    ) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            saveWashedCrushedOreSifting(
                    output,
                    material
            );
        }
    }


    private static void saveWashedCrushedOreSifting(
            RecipeOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.WASHED_CRUSHED_ORE
        )) {
            return;
        }

        boolean hasGem = MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.GEM
        );

        boolean hasFlawlessGem = MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.FLAWLESS_GEM
        );

        boolean hasExquisiteGem = MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.EXQUISITE_GEM
        );

        if (!hasGem
                && !hasFlawlessGem
                && !hasExquisiteGem) {
            return;
        }

        String washedCrushedOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.WASHED_CRUSHED_ORE
        );

        SifterRecipeBuilder recipe = SifterRecipes.recipe(
                "ore_processing/materials/"
                        + material.id()
                        + "_washed_crushed_ore_to_gems"
        );

        recipe.inputItem(washedCrushedOre);

        if (hasGem) {
            recipe.chancedOutput(
                    MaterialRecipeHelper.itemId(
                            material,
                            MaterialPart.GEM
                    ),
                    WASHED_CRUSHED_ORE_GEM_CHANCE
            );
        }

        if (hasFlawlessGem) {
            recipe.chancedOutput(
                    MaterialRecipeHelper.itemId(
                            material,
                            MaterialPart.FLAWLESS_GEM
                    ),
                    WASHED_CRUSHED_ORE_FLAWLESS_GEM_CHANCE
            );
        }

        if (hasExquisiteGem) {
            recipe.chancedOutput(
                    MaterialRecipeHelper.itemId(
                            material,
                            MaterialPart.EXQUISITE_GEM
                    ),
                    WASHED_CRUSHED_ORE_EXQUISITE_GEM_CHANCE
            );
        }

        recipe.duration(
                WASHED_CRUSHED_ORE_SIFTING_TIME
        );

        recipe.minRpm(
                WASHED_CRUSHED_ORE_SIFTING_MIN_RPM
        );

        recipe.save(output);
    }


    /*
     * ============================================================
     * ORE BLOCK CRUSHING
     * ============================================================
     */

    private void saveOreCrushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.ORE,
                "minecraft:stone",
                "stone"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.DEEPSLATE_ORE,
                "minecraft:deepslate",
                "deepslate"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.DIORITE_ORE,
                "minecraft:diorite",
                "diorite"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.ANDESITE_ORE,
                "minecraft:andesite",
                "andesite"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.GRANITE_ORE,
                "minecraft:granite",
                "granite"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.TUFF_ORE,
                "minecraft:tuff",
                "tuff"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.NETHERRACK_ORE,
                "minecraft:netherrack",
                "netherrack"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.BLACKSTONE_ORE,
                "minecraft:blackstone",
                "blackstone"
        );

        saveOreCrushing(
                futures,
                output,
                material,
                MaterialPart.END_STONE_ORE,
                "minecraft:end_stone",
                "end_stone"
        );
    }


    private void saveOreCrushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material,
            MaterialPart orePart,
            String stoneItem,
            String stoneName
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                orePart,
                MaterialPart.RAW_ORE
        )) {
            return;
        }

        String ore = MaterialRecipeHelper.itemId(
                material,
                orePart
        );

        String rawOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.RAW_ORE
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:crushing"
        );

        json.add(
                "ingredients",
                ingredients(item(ore))
        );

        json.addProperty(
                "processing_time",
                ORE_TO_RAW_ORE_CRUSHING_TIME
        );

        JsonArray results = new JsonArray();

        JsonObject rawOreResult = new JsonObject();

        rawOreResult.addProperty(
                "id",
                rawOre
        );

        rawOreResult.addProperty(
                "count",
                2
        );

        results.add(rawOreResult);

        JsonObject experienceResult = new JsonObject();

        experienceResult.addProperty(
                "id",
                "create:experience_nugget"
        );

        experienceResult.addProperty(
                "count",
                2
        );

        results.add(experienceResult);

        JsonObject stoneResult = new JsonObject();

        stoneResult.addProperty(
                "id",
                stoneItem
        );

        results.add(stoneResult);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "crushing/ores/"
                        + material.id()
                        + "_"
                        + stoneName
                        + "_ore_to_raw_ore",
                json
        );
    }


    /*
     * ============================================================
     * ORE COOKING
     * ============================================================
     */

    private void saveOreCookingRecipes(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial inputMaterial
    ) {
        IndustrialMaterial resultMaterial;

        if (inputMaterial.smeltingResult().isPresent()) {
            resultMaterial = inputMaterial
                    .smeltingResult()
                    .get();
        } else if (inputMaterial.smeltingSelf()) {
            resultMaterial = inputMaterial;
        } else {
            return;
        }

        saveCookingForPart(
                futures,
                output,
                inputMaterial,
                resultMaterial,
                MaterialPart.RAW_ORE,
                "raw_ore"
        );

        saveCookingForPart(
                futures,
                output,
                inputMaterial,
                resultMaterial,
                MaterialPart.CRUSHED_ORE,
                "crushed_ore"
        );

        saveCookingForPart(
                futures,
                output,
                inputMaterial,
                resultMaterial,
                MaterialPart.REFINED_ORE,
                "refined_ore"
        );

        saveCookingForPart(
                futures,
                output,
                inputMaterial,
                resultMaterial,
                MaterialPart.PURIFIED_DUST,
                "purified_dust"
        );

        saveCookingForPart(
                futures,
                output,
                inputMaterial,
                resultMaterial,
                MaterialPart.WASHED_CRUSHED_ORE,
                "washed_crushed_ore"
        );

        saveCookingForPart(
                futures,
                output,
                inputMaterial,
                resultMaterial,
                MaterialPart.IMPURE_DUST,
                "impure_dust"
        );

        saveDustCookingRecipe(
                futures,
                output,
                inputMaterial,
                resultMaterial
        );
    }


    private void saveCookingForPart(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial inputMaterial,
            IndustrialMaterial resultMaterial,
            MaterialPart inputPart,
            String inputName
    ) {
        if (!MaterialRecipeHelper.hasItems(
                inputMaterial,
                inputPart
        )) {
            return;
        }

        if (!MaterialRecipeHelper.hasItems(
                resultMaterial,
                MaterialPart.NUGGET
        )) {
            return;
        }

        String inputItem = MaterialRecipeHelper.itemId(
                inputMaterial,
                inputPart
        );

        String resultItem = MaterialRecipeHelper.itemId(
                resultMaterial,
                MaterialPart.NUGGET
        );

        saveMinecraftCookingRecipe(
                futures,
                output,
                "minecraft:smelting",
                "smelting/materials/"
                        + inputMaterial.id()
                        + "_"
                        + inputName
                        + "_to_"
                        + resultMaterial.id()
                        + "_nugget",
                inputItem,
                resultItem,
                FURNACE_COOKING_TIME
        );

        saveMinecraftCookingRecipe(
                futures,
                output,
                "minecraft:blasting",
                "blasting/materials/"
                        + inputMaterial.id()
                        + "_"
                        + inputName
                        + "_to_"
                        + resultMaterial.id()
                        + "_nugget",
                inputItem,
                resultItem,
                BLAST_FURNACE_COOKING_TIME
        );
    }


    private void saveDustCookingRecipe(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial inputMaterial,
            IndustrialMaterial resultMaterial
    ) {
        if (!MaterialRecipeHelper.hasItems(
                inputMaterial,
                MaterialPart.DUST
        )) {
            return;
        }

        if (!MaterialRecipeHelper.hasItems(
                resultMaterial,
                MaterialPart.INGOT
        )) {
            return;
        }

        String inputItem = MaterialRecipeHelper.itemId(
                inputMaterial,
                MaterialPart.DUST
        );

        String resultItem = MaterialRecipeHelper.itemId(
                resultMaterial,
                MaterialPart.INGOT
        );

        saveMinecraftCookingRecipe(
                futures,
                output,
                "minecraft:smelting",
                "smelting/materials/"
                        + inputMaterial.id()
                        + "_dust_to_"
                        + resultMaterial.id()
                        + "_ingot",
                inputItem,
                resultItem,
                FURNACE_COOKING_TIME
        );

        saveMinecraftCookingRecipe(
                futures,
                output,
                "minecraft:blasting",
                "blasting/materials/"
                        + inputMaterial.id()
                        + "_dust_to_"
                        + resultMaterial.id()
                        + "_ingot",
                inputItem,
                resultItem,
                BLAST_FURNACE_COOKING_TIME
        );
    }


    private void saveMinecraftCookingRecipe(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            String recipeType,
            String recipeId,
            String inputItem,
            String resultItem,
            int cookingTime
    ) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                recipeType
        );

        json.add(
                "ingredient",
                item(inputItem)
        );

        JsonObject result = new JsonObject();

        result.addProperty(
                "id",
                resultItem
        );

        json.add(
                "result",
                result
        );

        json.addProperty(
                "experience",
                ORE_COOKING_EXPERIENCE
        );

        json.addProperty(
                "cookingtime",
                cookingTime
        );

        save(
                futures,
                output,
                recipeId,
                json
        );
    }


    /*
     * ============================================================
     * RAW ORE TO CRUSHED ORE
     * ============================================================
     */

    private void saveRawOreCrushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.RAW_ORE,
                MaterialPart.CRUSHED_ORE
        )) {
            return;
        }

        String rawOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.RAW_ORE
        );

        String crushedOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.CRUSHED_ORE
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:crushing"
        );

        json.add(
                "ingredients",
                ingredients(item(rawOre))
        );

        json.addProperty(
                "processing_time",
                RAW_ORE_TO_CRUSHED_ORE_CRUSHING_TIME
        );

        JsonArray results = new JsonArray();

        JsonObject guaranteedCrushedOre = new JsonObject();

        guaranteedCrushedOre.addProperty(
                "id",
                crushedOre
        );

        results.add(guaranteedCrushedOre);

        JsonObject bonusCrushedOre = new JsonObject();

        bonusCrushedOre.addProperty(
                "id",
                crushedOre
        );

        bonusCrushedOre.addProperty(
                "chance",
                0.5F
        );

        results.add(bonusCrushedOre);

        JsonObject experienceNugget = new JsonObject();

        experienceNugget.addProperty(
                "id",
                "create:experience_nugget"
        );

        results.add(experienceNugget);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "crushing/materials/"
                        + material.id()
                        + "_raw_ore_to_crushed_ore",
                json
        );
    }


    /*
     * ============================================================
     * GEM TO DUST
     * ============================================================
     */

    private void saveGemCrushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.GEM,
                MaterialPart.DUST
        )) {
            return;
        }

        String gem = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.GEM
        );

        String dust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.DUST
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:crushing"
        );

        json.add(
                "ingredients",
                ingredients(item(gem))
        );

        json.addProperty(
                "processing_time",
                GEM_TO_DUST_CRUSHING_TIME
        );

        json.add(
                "results",
                results(dust, 1)
        );

        save(
                futures,
                output,
                "crushing/materials/"
                        + material.id()
                        + "_gem_to_dust",
                json
        );
    }


    /*
     * ============================================================
     * ORE PROCESSING
     * ============================================================
     */

    private void saveOreProcessingRecipes(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        saveCrushedOreSplashing(
                futures,
                output,
                material
        );

        saveCrushedOreCrushing(
                futures,
                output,
                material
        );

        saveImpureDustSplashing(
                futures,
                output,
                material
        );

        saveWashedCrushedOreCrushing(
                futures,
                output,
                material
        );
    }


    private void saveCrushedOreSplashing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.CRUSHED_ORE,
                MaterialPart.WASHED_CRUSHED_ORE
        )) {
            return;
        }

        if (!MaterialRecipeHelper.hasItems(
                IndustrialMaterials.STONE,
                MaterialPart.DUST
        )) {
            return;
        }

        String crushedOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.CRUSHED_ORE
        );

        String washedCrushedOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.WASHED_CRUSHED_ORE
        );

        String stoneDust = MaterialRecipeHelper.itemId(
                IndustrialMaterials.STONE,
                MaterialPart.DUST
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:splashing"
        );

        json.add(
                "ingredients",
                ingredients(item(crushedOre))
        );

        JsonArray results = new JsonArray();

        JsonObject washedResult = new JsonObject();

        washedResult.addProperty(
                "id",
                washedCrushedOre
        );

        results.add(washedResult);

        JsonObject stoneDustResult = new JsonObject();

        stoneDustResult.addProperty(
                "id",
                stoneDust
        );

        results.add(stoneDustResult);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "splashing/materials/"
                        + material.id()
                        + "_crushed_ore_to_washed_crushed_ore",
                json
        );
    }


    private void saveCrushedOreCrushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.CRUSHED_ORE,
                MaterialPart.IMPURE_DUST
        )) {
            return;
        }

        String crushedOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.CRUSHED_ORE
        );

        String impureDust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.IMPURE_DUST
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:crushing"
        );

        json.add(
                "ingredients",
                ingredients(item(crushedOre))
        );

        json.addProperty(
                "processing_time",
                CRUSHED_ORE_TO_IMPURE_DUST_CRUSHING_TIME
        );

        JsonArray results = new JsonArray();

        JsonObject guaranteedResult = new JsonObject();

        guaranteedResult.addProperty(
                "id",
                impureDust
        );

        results.add(guaranteedResult);

        JsonObject chanceResult = new JsonObject();

        chanceResult.addProperty(
                "id",
                impureDust
        );

        chanceResult.addProperty(
                "chance",
                0.5F
        );

        results.add(chanceResult);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "crushing/materials/"
                        + material.id()
                        + "_crushed_ore_to_impure_dust",
                json
        );
    }


    private void saveImpureDustSplashing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.IMPURE_DUST,
                MaterialPart.TINY_DUST
        )) {
            return;
        }

        if (!MaterialRecipeHelper.hasItems(
                IndustrialMaterials.STONE,
                MaterialPart.DUST
        )) {
            return;
        }

        String impureDust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.IMPURE_DUST
        );

        String tinyDust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.TINY_DUST
        );

        String stoneDust = MaterialRecipeHelper.itemId(
                IndustrialMaterials.STONE,
                MaterialPart.DUST
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:splashing"
        );

        json.add(
                "ingredients",
                ingredients(item(impureDust))
        );

        JsonArray results = new JsonArray();

        JsonObject stoneDustResult = new JsonObject();

        stoneDustResult.addProperty(
                "id",
                stoneDust
        );

        results.add(stoneDustResult);

        JsonObject guaranteedTinyDust = new JsonObject();

        guaranteedTinyDust.addProperty(
                "id",
                tinyDust
        );

        guaranteedTinyDust.addProperty(
                "count",
                4
        );

        results.add(guaranteedTinyDust);

        JsonObject chanceTinyDust = new JsonObject();

        chanceTinyDust.addProperty(
                "id",
                tinyDust
        );

        chanceTinyDust.addProperty(
                "count",
                2
        );

        chanceTinyDust.addProperty(
                "chance",
                0.5F
        );

        results.add(chanceTinyDust);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "splashing/materials/"
                        + material.id()
                        + "_impure_dust_to_tiny_dust",
                json
        );
    }


    private void saveWashedCrushedOreCrushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.WASHED_CRUSHED_ORE,
                MaterialPart.TINY_DUST
        )) {
            return;
        }

        String washedCrushedOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.WASHED_CRUSHED_ORE
        );

        String tinyDust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.TINY_DUST
        );

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:crushing"
        );

        json.add(
                "ingredients",
                ingredients(item(washedCrushedOre))
        );

        json.addProperty(
                "processing_time",
                WASHED_CRUSHED_ORE_TO_TINY_DUST_CRUSHING_TIME
        );

        JsonArray results = new JsonArray();

        JsonObject tinyDustResult = new JsonObject();

        tinyDustResult.addProperty(
                "id",
                tinyDust
        );

        tinyDustResult.addProperty(
                "count",
                6
        );

        results.add(tinyDustResult);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "crushing/materials/"
                        + material.id()
                        + "_washed_crushed_ore_to_tiny_dust",
                json
        );
    }


    /*
     * ============================================================
     * COMPACTING
     * ============================================================
     */

    private void saveMaterialCompacting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        saveCompacting(
                futures,
                output,
                material,
                MaterialPart.NUGGET,
                MaterialPart.INGOT,
                9,
                "nugget_to_ingot"
        );

        saveCompacting(
                futures,
                output,
                material,
                MaterialPart.INGOT,
                MaterialPart.BLOCK,
                9,
                "ingot_to_block"
        );
    }


    private void saveCompacting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material,
            MaterialPart inputPart,
            MaterialPart resultPart,
            int inputCount,
            String name
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                inputPart,
                resultPart
        )) {
            return;
        }

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:compacting"
        );

        json.add(
                "ingredients",
                repeatedIngredients(
                        MaterialRecipeHelper.itemId(
                                material,
                                inputPart
                        ),
                        inputCount
                )
        );

        json.add(
                "results",
                results(
                        MaterialRecipeHelper.itemId(
                                material,
                                resultPart
                        ),
                        1
                )
        );

        save(
                futures,
                output,
                "compacting/materials/"
                        + material.id()
                        + "_"
                        + name,
                json
        );
    }


    /*
     * ============================================================
     * DUST CRAFTING
     * ============================================================
     */

    private void saveDustCrafting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.DUST,
                MaterialPart.SMALL_DUST,
                MaterialPart.TINY_DUST
        )) {
            return;
        }

        String dust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.DUST
        );

        String smallDust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.SMALL_DUST
        );

        String tinyDust = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.TINY_DUST
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_dust_to_small_dust",
                List.of(
                        "DDD",
                        "D  ",
                        "   "
                ),
                'D',
                smallDust,
                tinyDust,
                9
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_small_dust_to_tiny_dust",
                List.of("S"),
                'S',
                dust,
                smallDust,
                4
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_small_dust_to_dust",
                List.of(
                        "SS",
                        "SS"
                ),
                'S',
                smallDust,
                dust,
                1
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_tiny_dust_to_dust",
                List.of(
                        "TTT",
                        "TTT",
                        "TTT"
                ),
                'T',
                tinyDust,
                dust,
                1
        );
    }


    /*
     * ============================================================
     * GEM CUTTING
     * ============================================================
     */

    private void saveGemCutting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        saveChancedCutting(
                futures,
                output,
                material,
                MaterialPart.EXQUISITE_GEM,
                MaterialPart.FLAWLESS_GEM,
                2,
                0.5F,
                "exquisite_gem_to_flawless_gem"
        );

        saveChancedCutting(
                futures,
                output,
                material,
                MaterialPart.FLAWLESS_GEM,
                MaterialPart.GEM,
                2,
                0.5F,
                "flawless_gem_to_gem"
        );
    }


    private void saveChancedCutting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material,
            MaterialPart inputPart,
            MaterialPart resultPart,
            int count,
            float chance,
            String name
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                inputPart,
                resultPart
        )) {
            return;
        }

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:cutting"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(
                                MaterialRecipeHelper.itemId(
                                        material,
                                        inputPart
                                )
                        )
                )
        );

        json.addProperty(
                "processing_time",
                GEM_CUTTING_TIME
        );

        JsonArray results = new JsonArray();

        JsonObject result = new JsonObject();

        result.addProperty(
                "id",
                MaterialRecipeHelper.itemId(
                        material,
                        resultPart
                )
        );

        result.addProperty(
                "count",
                count
        );

        result.addProperty(
                "chance",
                chance
        );

        results.add(result);

        json.add(
                "results",
                results
        );

        save(
                futures,
                output,
                "cutting/materials/"
                        + material.id()
                        + "_"
                        + name,
                json
        );
    }


    /*
     * ============================================================
     * GEM STORAGE CRAFTING
     * ============================================================
     */

    private void saveGemStorageCrafting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (material.hasExistingRecipe(MaterialPart.BLOCK)
                || !MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.GEM,
                MaterialPart.BLOCK
        )) {
            return;
        }

        String gem = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.GEM
        );

        String block = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.BLOCK
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_gem_to_block",
                List.of(
                        "GGG",
                        "GGG",
                        "GGG"
                ),
                'G',
                gem,
                block,
                1
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_block_to_gem",
                List.of("B"),
                'B',
                block,
                gem,
                9
        );
    }


    /*
     * ============================================================
     * RAW STORAGE CRAFTING
     * ============================================================
     */

    private void saveRawStorageCrafting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!material.has(MaterialPart.GEM)
                || material.hasExistingRecipe(MaterialPart.RAW_BLOCK)
                || !MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.RAW_ORE,
                MaterialPart.RAW_BLOCK
        )) {
            return;
        }

        String rawOre = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.RAW_ORE
        );

        String rawBlock = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.RAW_BLOCK
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_raw_ore_to_raw_block",
                List.of(
                        "RRR",
                        "RRR",
                        "RRR"
                ),
                'R',
                rawOre,
                rawBlock,
                1
        );

        saveShaped(
                futures,
                output,
                "crafting/materials/"
                        + material.id()
                        + "_raw_block_to_raw_ore",
                List.of("B"),
                'B',
                rawBlock,
                rawOre,
                9
        );
    }


    /*
     * ============================================================
     * MILLING
     * ============================================================
     */

    private void saveIngotMilling(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.INGOT,
                MaterialPart.DUST
        )) {
            return;
        }

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:milling"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(
                                MaterialRecipeHelper.itemId(
                                        material,
                                        MaterialPart.INGOT
                                )
                        )
                )
        );

        json.addProperty(
                "processing_time",
                INGOT_TO_DUST_MILLING_TIME
        );

        json.add(
                "results",
                results(
                        MaterialRecipeHelper.itemId(
                                material,
                                MaterialPart.DUST
                        ),
                        1
                )
        );

        save(
                futures,
                output,
                "milling/materials/"
                        + material.id()
                        + "_ingot_to_dust",
                json
        );
    }


    private void saveNuggetMilling(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.NUGGET,
                MaterialPart.TINY_DUST
        )) {
            return;
        }

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:milling"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(
                                MaterialRecipeHelper.itemId(
                                        material,
                                        MaterialPart.NUGGET
                                )
                        )
                )
        );

        json.addProperty(
                "processing_time",
                NUGGET_TO_TINY_DUST_MILLING_TIME
        );

        json.add(
                "results",
                results(
                        MaterialRecipeHelper.itemId(
                                material,
                                MaterialPart.TINY_DUST
                        ),
                        1
                )
        );

        save(
                futures,
                output,
                "milling/materials/"
                        + material.id()
                        + "_nugget_to_tiny_dust",
                json
        );
    }


    private void saveStoneMilling(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.DUST
        )) {
            return;
        }

        for (var stoneSource : material.stoneSources()) {
            String input = stoneSource
                    .existingBlock()
                    .orElseGet(
                            () -> ResourceLocation.fromNamespaceAndPath(
                                    CreateExpansion.MOD_ID,
                                    stoneSource.registryName(material)
                            )
                    )
                    .toString();

            JsonObject json = new JsonObject();

            json.addProperty(
                    "type",
                    "create:milling"
            );

            json.add(
                    "ingredients",
                    ingredients(item(input))
            );

            json.addProperty(
                    "processing_time",
                    STONE_TO_DUST_MILLING_TIME
            );

            json.add(
                    "results",
                    results(
                            MaterialRecipeHelper.itemId(
                                    material,
                                    MaterialPart.DUST
                            ),
                            1
                    )
            );

            save(
                    futures,
                    output,
                    "milling/stones/"
                            + material.id()
                            + "_"
                            + stoneSource.id()
                            + "_to_dust",
                    json
            );
        }
    }


    /*
     * ============================================================
     * CUTTING
     * ============================================================
     */

    private void saveCutting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material,
            MaterialPart inputPart,
            MaterialPart resultPart,
            int count,
            String name
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                inputPart,
                resultPart
        )) {
            return;
        }

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:cutting"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(
                                MaterialRecipeHelper.itemId(
                                        material,
                                        inputPart
                                )
                        )
                )
        );

        json.addProperty(
                "processing_time",
                MATERIAL_CUTTING_TIME
        );

        json.add(
                "results",
                results(
                        MaterialRecipeHelper.itemId(
                                material,
                                resultPart
                        ),
                        count
                )
        );

        save(
                futures,
                output,
                "cutting/materials/"
                        + material.id()
                        + "_"
                        + name,
                json
        );
    }


    /*
     * ============================================================
     * PRESSING
     * ============================================================
     */

    private void savePressing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material,
            MaterialPart inputPart,
            MaterialPart resultPart,
            String name
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                inputPart,
                resultPart
        )) {
            return;
        }

        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:pressing"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(
                                MaterialRecipeHelper.itemId(
                                        material,
                                        inputPart
                                )
                        )
                )
        );

        json.add(
                "results",
                results(
                        MaterialRecipeHelper.itemId(
                                material,
                                resultPart
                        ),
                        1
                )
        );

        save(
                futures,
                output,
                "pressing/materials/"
                        + material.id()
                        + "_"
                        + name,
                json
        );
    }


    /*
     * ============================================================
     * SEQUENCED ASSEMBLY
     * ============================================================
     */

    private void saveSequencedDoublePlate(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.PLATE,
                MaterialPart.DOUBLE_PLATE
        )) {
            return;
        }

        String plate = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.PLATE
        );

        String doublePlate = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.DOUBLE_PLATE
        );

        JsonArray sequence = new JsonArray();

        sequence.add(
                deploying(
                        doublePlate,
                        plate
                )
        );

        sequence.add(
                pressingStep(doublePlate)
        );

        saveSequenced(
                futures,
                output,
                material,
                "double_plate",
                plate,
                doublePlate,
                1,
                sequence
        );
    }


    private void saveSequencedBearing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.LARGE_RING,
                MaterialPart.BEARING_BALL,
                MaterialPart.BEARING
        )) {
            return;
        }

        String largeRing = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.LARGE_RING
        );

        String bearingBall = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.BEARING_BALL
        );

        String bearing = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.BEARING
        );

        JsonArray sequence = new JsonArray();

        sequence.add(
                deploying(
                        bearing,
                        bearingBall
                )
        );

        sequence.add(
                fillingStep(
                        bearing,
                        "create_expansion:aromatic_extract",
                        25
                )
        );

        saveSequenced(
                futures,
                output,
                material,
                "bearing",
                largeRing,
                bearing,
                8,
                sequence
        );
    }


    private void saveSequencedFrame(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material
    ) {
        if (!MaterialRecipeHelper.hasItems(
                material,
                MaterialPart.ROD,
                MaterialPart.FRAME
        )) {
            return;
        }

        String rod = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.ROD
        );

        String frame = MaterialRecipeHelper.itemId(
                material,
                MaterialPart.FRAME
        );

        JsonArray sequence = new JsonArray();

        sequence.add(
                deploying(
                        frame,
                        rod
                )
        );

        saveSequenced(
                futures,
                output,
                material,
                "frame",
                rod,
                frame,
                11,
                sequence
        );
    }


    private void saveSequenced(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            IndustrialMaterial material,
            String name,
            String ingredient,
            String result,
            int loops,
            JsonArray sequence
    ) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:sequenced_assembly"
        );

        json.add(
                "ingredient",
                item(ingredient)
        );

        json.addProperty(
                "loops",
                loops
        );

        json.add(
                "results",
                results(result, 1)
        );

        json.add(
                "sequence",
                sequence
        );

        JsonObject transitional = new JsonObject();

        transitional.addProperty(
                "id",
                result
        );

        json.add(
                "transitional_item",
                transitional
        );

        save(
                futures,
                output,
                "sequenced_assembly/materials/"
                        + material.id()
                        + "_"
                        + name,
                json
        );
    }


    private JsonObject deploying(
            String transitionalItem,
            String heldItem
    ) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:deploying"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(transitionalItem),
                        item(heldItem)
                )
        );

        json.add(
                "results",
                results(
                        transitionalItem,
                        1
                )
        );

        return json;
    }


    private JsonObject fillingStep(
            String transitionalItem,
            String fluidId,
            int fluidAmount
    ) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:filling"
        );

        JsonObject fluidIngredient = new JsonObject();

        fluidIngredient.addProperty(
                "type",
                "neoforge:single"
        );

        fluidIngredient.addProperty(
                "amount",
                fluidAmount
        );

        fluidIngredient.addProperty(
                "fluid",
                fluidId
        );

        json.add(
                "ingredients",
                ingredients(
                        item(transitionalItem),
                        fluidIngredient
                )
        );

        json.add(
                "results",
                results(
                        transitionalItem,
                        1
                )
        );

        return json;
    }


    private JsonObject pressingStep(
            String transitionalItem
    ) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "create:pressing"
        );

        json.add(
                "ingredients",
                ingredients(
                        item(transitionalItem)
                )
        );

        json.add(
                "results",
                results(
                        transitionalItem,
                        1
                )
        );

        return json;
    }


    /*
     * ============================================================
     * JSON HELPERS
     * ============================================================
     */

    private JsonObject item(String id) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "item",
                id
        );

        return json;
    }


    private JsonArray ingredients(
            JsonObject... ingredients
    ) {
        JsonArray array = new JsonArray();

        for (JsonObject ingredient : ingredients) {
            array.add(ingredient);
        }

        return array;
    }


    private JsonArray repeatedIngredients(
            String id,
            int count
    ) {
        JsonArray array = new JsonArray();

        for (int i = 0; i < count; i++) {
            array.add(item(id));
        }

        return array;
    }


    private JsonArray results(
            String id,
            int count
    ) {
        JsonArray array = new JsonArray();

        JsonObject result = new JsonObject();

        result.addProperty(
                "id",
                id
        );

        if (count > 1) {
            result.addProperty(
                    "count",
                    count
            );
        }

        array.add(result);

        return array;
    }


    /*
     * ============================================================
     * CRAFTING HELPERS
     * ============================================================
     */

    private void saveShaped(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            String recipeId,
            List<String> pattern,
            char key,
            String input,
            String result,
            int resultCount
    ) {
        JsonObject json = new JsonObject();

        json.addProperty(
                "type",
                "minecraft:crafting_shaped"
        );

        JsonArray patternJson = new JsonArray();

        for (String row : pattern) {
            patternJson.add(row);
        }

        json.add(
                "pattern",
                patternJson
        );

        JsonObject keyJson = new JsonObject();

        keyJson.add(
                String.valueOf(key),
                item(input)
        );

        json.add(
                "key",
                keyJson
        );

        JsonObject resultJson = new JsonObject();

        resultJson.addProperty(
                "id",
                result
        );

        if (resultCount > 1) {
            resultJson.addProperty(
                    "count",
                    resultCount
            );
        }

        json.add(
                "result",
                resultJson
        );

        save(
                futures,
                output,
                recipeId,
                json
        );
    }


    /*
     * ============================================================
     * SAVE
     * ============================================================
     */

    private void save(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            String recipeId,
            JsonObject json
    ) {
        ResourceLocation id =
                ResourceLocation.fromNamespaceAndPath(
                        CreateExpansion.MOD_ID,
                        recipeId
                );

        futures.add(
                DataProvider.saveStable(
                        output,
                        json,
                        path(id)
                )
        );
    }


    private Path path(ResourceLocation id) {
        return recipes.json(id);
    }


    @Override
    public String getName() {
        return "Create Material Recipes";
    }
}