package net.mads.createexpansion.recipe.recipes.hydraulicpress;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class HydraulicPressingRecipe implements Recipe<HydraulicPressingRecipeInput> {
    public static final MapCodec<HydraulicPressingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(HydraulicPressingRecipe::ingredients),
            ProcessingOutput.CODEC.listOf().fieldOf("results").forGetter(HydraulicPressingRecipe::results),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("blows", 1).forGetter(HydraulicPressingRecipe::blows)
    ).apply(instance, HydraulicPressingRecipe::new));

    private final List<Ingredient> ingredients;
    private final List<ProcessingOutput> results;
    private final int blows;

    public HydraulicPressingRecipe(List<Ingredient> ingredients, List<ProcessingOutput> results, int blows) {
        this.ingredients = List.copyOf(ingredients);
        this.results = List.copyOf(results);
        this.blows = blows;
    }

    @Override public boolean matches(HydraulicPressingRecipeInput input, Level level) { return matchesItem(input.item()); }
    public boolean matchesItem(ItemStack stack) { return !stack.isEmpty() && !ingredients.isEmpty() && ingredients.getFirst().test(stack); }
    public ItemStack result() { return results.isEmpty() ? ItemStack.EMPTY : results.getFirst().getStack().copy(); }
    public List<Ingredient> ingredients() { return ingredients; }
    public List<ProcessingOutput> results() { return results; }
    public int blows() { return blows; }
    @Override public ItemStack assemble(HydraulicPressingRecipeInput input, HolderLookup.Provider registries) { return result(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return result(); }
    @Override public NonNullList<Ingredient> getIngredients() { NonNullList<Ingredient> list = NonNullList.create(); list.addAll(ingredients); return list; }
    @Override public ItemStack getToastSymbol() { return new ItemStack(BlockRegistry.HYDRAULIC_PRESS.get()); }
    @Override public RecipeSerializer<?> getSerializer() { return RecipeRegistry.HYDRAULIC_PRESSING_RECIPE_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return RecipeRegistry.HYDRAULIC_PRESSING_RECIPE_TYPE.get(); }

    public static class Serializer implements RecipeSerializer<HydraulicPressingRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, HydraulicPressingRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
        @Override public MapCodec<HydraulicPressingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, HydraulicPressingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
