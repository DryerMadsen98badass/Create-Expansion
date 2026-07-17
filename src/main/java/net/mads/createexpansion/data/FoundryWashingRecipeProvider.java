package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.machines.foundry.FoundryCastingRecipes;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FoundryWashingRecipeProvider implements DataProvider {
    private final PackOutput.PathProvider recipes;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FoundryWashingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (IndustrialMaterial material : IndustrialMaterials.ALL) {
                for (FoundryCastingRecipes.CastShape shape : FoundryCastingRecipes.shapes().values()) {
                    saveCastCooling(output, futures, material, shape);
                    saveMoldCooling(output, futures, material, shape);
                }
            }
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    private void saveCastCooling(CachedOutput output, List<CompletableFuture<?>> futures, IndustrialMaterial material, FoundryCastingRecipes.CastShape shape) {
        if (!material.has(shape.castPart()) || !material.has(shape.cooledPart())) {
            return;
        }

        Item input = item(material, shape.castPart());
        Item result = item(material, shape.cooledPart());
        if (input == Items.AIR || result == Items.AIR) {
            return;
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "splashing/foundry_casts/" + material.id() + "_" + shape.cooledPart().id());
        futures.add(DataProvider.saveStable(output, splashing(input, result, shape.durationTicks()), path(id)));
    }

    private void saveMoldCooling(CachedOutput output, List<CompletableFuture<?>> futures, IndustrialMaterial material, FoundryCastingRecipes.CastShape shape) {
        if (!material.has(shape.hotMoldPart()) || !material.has(shape.moldPart())) {
            return;
        }

        Item input = item(material, shape.hotMoldPart());
        Item result = item(material, shape.moldPart());
        if (input == Items.AIR || result == Items.AIR) {
            return;
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "splashing/foundry_molds/" + material.id() + "_" + shape.moldPart().id());
        futures.add(DataProvider.saveStable(output, splashing(input, result, shape.durationTicks()), path(id)));
    }

    private Item item(IndustrialMaterial material, MaterialPart part) {
        if (material.hasExistingPart(part)) {
            return BuiltInRegistries.ITEM.get(material.existingPart(part));
        }
        return ItemRegistry.getMaterialItem(material, part).get();
    }

    private JsonObject splashing(Item input, Item result, int processingTime) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "create:splashing");

        JsonArray ingredients = new JsonArray();
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", BuiltInRegistries.ITEM.getKey(input).toString());
        ingredients.add(ingredient);
        json.add("ingredients", ingredients);

        JsonArray results = new JsonArray();
        JsonObject output = new JsonObject();
        output.addProperty("id", BuiltInRegistries.ITEM.getKey(result).toString());
        results.add(output);
        json.add("results", results);

        json.addProperty("processingTime", processingTime);
        return json;
    }

    private Path path(ResourceLocation id) {
        return recipes.json(id);
    }

    @Override
    public String getName() {
        return "Foundry Washing Recipes";
    }
}
