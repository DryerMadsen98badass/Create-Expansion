package net.mads.createexpansion.recipe.recipes.assembly;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeDefinition;
import net.mads.createexpansion.recipe.recipetypes.DirtyAssemblerRecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public final class AssemblyRecipeBuilder {
    private final String id;
    private Ingredient base;
    private String baseItemId;
    private final List<SizedIngredient> inputs = new ArrayList<>();
    private final List<DirtyAssemblerInput> dirtyAssemblerInputs = new ArrayList<>();
    private ItemStack result = ItemStack.EMPTY;
    private String resultItemId;
    private int loops = 1;

    public AssemblyRecipeBuilder(String id) {
        this.id = id;
    }

    public AssemblyRecipeBuilder baseBlock(String itemId) {
        Item item = item(itemId);
        if (!(item instanceof BlockItem)) {
            throw new IllegalArgumentException("Assembly base must be a block item: " + itemId);
        }
        base = Ingredient.of(item);
        baseItemId = itemId;
        return this;
    }

    public AssemblyRecipeBuilder inputItem(String itemId) {
        return inputItem(itemId, 1);
    }

    public AssemblyRecipeBuilder inputItem(String itemId, int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Assembly input count must be at least 1 for " + itemId);
        }
        inputs.add(SizedIngredient.of(item(itemId), count));
        dirtyAssemblerInputs.add(new DirtyAssemblerInput(itemId, count));
        return this;
    }

    public AssemblyRecipeBuilder outputBlock(String itemId) {
        return outputBlock(itemId, 1);
    }

    public AssemblyRecipeBuilder outputBlock(String itemId, int count) {
        result = new ItemStack(item(itemId), count);
        resultItemId = itemId;
        return this;
    }

    public AssemblyRecipeBuilder loops(int loops) {
        if (loops < 1) {
            throw new IllegalArgumentException("Assembly loops must be at least 1");
        }
        this.loops = loops;
        return this;
    }

    public void save(RecipeOutput output) {
        if (id.isBlank() || base == null || inputs.isEmpty() || result.isEmpty()) {
            throw new IllegalStateException("Invalid assembly recipe: " + id);
        }
        output.accept(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "assembly/" + id),
                new AssemblyRecipe(base, inputs, new ProcessingOutput(result, 1.0F), loops), null);
        saveDirtyAssemblerRecipe(output);
    }

    private void saveDirtyAssemblerRecipe(RecipeOutput output) {
        if (dirtyAssemblerInputs.size() > DirtyAssemblerRecipeType.MAX_SEQUENTIAL_INPUTS) {
            return;
        }

        RecipeDefinition definition = RecipeDefinition.recipe()
                .recipeDefinition(RecipeDefinition.Option.id(dirtyAssemblerId()))
                .recipeDefinition(RecipeDefinition.Option.recipeType(CERecipeTypes.DIRTY_ASSEMBLER))
                .recipeDefinition(RecipeDefinition.Option.inputItem(baseItemId, 1))
                .recipeDefinition(RecipeDefinition.Option.chancedOutputItem(
                        resultItemId,
                        result.getCount(),
                        DirtyAssemblerRecipeType.SUCCESS_CHANCE
                ))
                .recipeDefinition(RecipeDefinition.Option.duration(dirtyAssemblerDuration()))
                .recipeDefinition(RecipeDefinition.Option.tier(DirtyAssemblerRecipeType.BASE_TIER));

        for (DirtyAssemblerInput input : dirtyAssemblerInputs) {
            definition.recipeDefinition(RecipeDefinition.Option.inputItem(
                    input.itemId(),
                    multipliedInputCount(input.count())
            ));
        }

        definition.save(output);
    }

    private String dirtyAssemblerId() {
        String prefix = "assembly/";
        return id.startsWith(prefix) ? id.substring(prefix.length()) : id;
    }

    private int dirtyAssemblerDuration() {
        long items = 0L;
        for (DirtyAssemblerInput input : dirtyAssemblerInputs) {
            items += (long) input.count() * loops;
        }
        long duration = items * DirtyAssemblerRecipeType.TICKS_PER_ITEM;
        if (duration < 1L || duration > Integer.MAX_VALUE) {
            throw new IllegalStateException("Dirty Assembler duration is out of range for assembly recipe: " + id);
        }
        return (int) duration;
    }

    private int multipliedInputCount(int count) {
        long multiplied = (long) count * loops;
        if (multiplied < 1L || multiplied > Integer.MAX_VALUE) {
            throw new IllegalStateException("Dirty Assembler input count is out of range for assembly recipe: " + id);
        }
        return (int) multiplied;
    }

    private static Item item(String itemId) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
    }

    private record DirtyAssemblerInput(String itemId, int count) {
    }
}
