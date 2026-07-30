package net.mads.createexpansion.recipe.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CreateRecipeBuilder {

    private final List<CompletableFuture<?>> futures;
    private final CachedOutput output;
    private final PackOutput.PathProvider recipes;
    private final String recipeId;
    private final String type;

    private final JsonArray ingredients = new JsonArray();
    private final JsonArray results = new JsonArray();

    private JsonObject lastResult;
    private Integer duration;
    private Integer loops;
    private String heatRequirement;
    private String transitionalItem;
    private JsonArray sequence;

    private CreateRecipeBuilder(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId,
            String type
    ) {
        this.futures = futures;
        this.output = output;
        this.recipes = recipes;
        this.recipeId = recipeId;
        this.type = type;
    }

    public static CreateRecipeBuilder cutting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "cutting");
    }

    public static CreateRecipeBuilder pressing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "pressing");
    }

    public static CreateRecipeBuilder crushing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "crushing");
    }

    public static CreateRecipeBuilder milling(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "milling");
    }

    public static CreateRecipeBuilder mixing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "mixing");
    }

    public static CreateRecipeBuilder deploying(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "deploying");
    }

    public static CreateRecipeBuilder filling(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "filling");
    }

    public static CreateRecipeBuilder emptying(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "emptying");
    }

    public static CreateRecipeBuilder haunting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "haunting");
    }

    public static CreateRecipeBuilder itemApplication(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "item_application");
    }

    public static CreateRecipeBuilder sandpaperPolishing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "sandpaper_polishing");
    }

    public static CreateRecipeBuilder splashing(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "splashing");
    }

    public static CreateRecipeBuilder compacting(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "compacting");
    }

    public static CreateRecipeBuilder sequencedAssembly(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId
    ) {
        return create(futures, output, recipes, recipeId, "sequenced_assembly");
    }

    private static CreateRecipeBuilder create(
            List<CompletableFuture<?>> futures,
            CachedOutput output,
            PackOutput.PathProvider recipes,
            String recipeId,
            String type
    ) {
        return new CreateRecipeBuilder(
                futures,
                output,
                recipes,
                recipeId,
                "create:" + type
        );
    }

    public CreateRecipeBuilder inputItem(String id) {
        ingredients.add(CreateRecipeJson.item(id));
        return this;
    }

    public CreateRecipeBuilder inputItem(String id, int count) {
        JsonObject ingredient = CreateRecipeJson.item(id);

        if (count > 1) {
            ingredient.addProperty("count", count);
        }

        ingredients.add(ingredient);
        return this;
    }

    public CreateRecipeBuilder inputFluid(String id, int amount) {
        ingredients.add(CreateRecipeJson.fluid(id, amount));
        return this;
    }

    public CreateRecipeBuilder outputItem(String id) {
        return outputItem(id, 1);
    }

    public CreateRecipeBuilder outputItem(String id, int count) {
        lastResult = CreateRecipeJson.result(id, count);
        results.add(lastResult);
        return this;
    }

    public CreateRecipeBuilder outputFluid(String id, int amount) {
        lastResult = CreateRecipeJson.fluidResult(id, amount);
        results.add(lastResult);
        return this;
    }

    public CreateRecipeBuilder chance(float chance) {
        if (lastResult == null) {
            throw new IllegalStateException(
                    "chance() must be called after outputItem() or outputFluid()"
            );
        }

        lastResult.addProperty("chance", chance);
        return this;
    }

    public CreateRecipeBuilder duration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException(
                    "Recipe duration must be greater than zero: " + recipeId
            );
        }

        this.duration = duration;
        return this;
    }

    public CreateRecipeBuilder heatRequirement(String heatRequirement) {
        if (heatRequirement == null || heatRequirement.isBlank()) {
            throw new IllegalArgumentException(
                    "Heat requirement cannot be blank: " + recipeId
            );
        }

        this.heatRequirement = heatRequirement;
        return this;
    }

    public CreateRecipeBuilder heated() {
        this.heatRequirement = "heated";
        return this;
    }

    public CreateRecipeBuilder superheated() {
        this.heatRequirement = "superheated";
        return this;
    }

    public CreateRecipeBuilder loops(int loops) {
        if (loops <= 0) {
            throw new IllegalArgumentException(
                    "Sequence loops must be greater than zero: " + recipeId
            );
        }

        this.loops = loops;
        return this;
    }

    public CreateRecipeBuilder transitionalItem(String id) {
        this.transitionalItem = id;
        return this;
    }

    public CreateRecipeBuilder sequence(JsonArray sequence) {
        this.sequence = sequence;
        return this;
    }

    public void save() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);

        if ("create:sequenced_assembly".equals(type)) {
            if (ingredients.size() > 0) {
                json.add("ingredient", ingredients.get(0));
            }

            if (loops != null) {
                json.addProperty("loops", loops);
            }

            if (transitionalItem != null) {
                JsonObject transitional = new JsonObject();
                transitional.addProperty("id", transitionalItem);
                json.add("transitional_item", transitional);
            }

            if (sequence != null) {
                json.add("sequence", sequence);
            }
        } else {
            json.add("ingredients", ingredients);
        }

        json.add("results", results);

        if (duration != null) {
            json.addProperty("processing_time", duration);
        }

        if (heatRequirement != null) {
            json.addProperty("heat_requirement", heatRequirement);
        }

        CreateRecipeJson.save(
                futures,
                output,
                recipes,
                recipeId,
                json
        );
    }
}