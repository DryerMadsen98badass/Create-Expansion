package net.mads.createexpansion.recipe.recipes.assembly;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class AssemblyRecipe implements Recipe<AssemblyRecipeInput> {
    public static final MapCodec<AssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(AssemblyRecipe::base),
            SizedIngredient.FLAT_CODEC.listOf().fieldOf("inputs").forGetter(AssemblyRecipe::inputs),
            ProcessingOutput.CODEC.fieldOf("result").forGetter(AssemblyRecipe::resultOutput),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("loops", 1).forGetter(AssemblyRecipe::loops)
    ).apply(instance, AssemblyRecipe::new));

    private final Ingredient base;
    private final List<SizedIngredient> inputs;
    private final ProcessingOutput result;
    private final int loops;

    public AssemblyRecipe(Ingredient base, List<SizedIngredient> inputs, ProcessingOutput result, int loops) {
        this.base = base;
        this.inputs = List.copyOf(inputs);
        this.result = result;
        this.loops = loops;
    }

    @Override
    public boolean matches(AssemblyRecipeInput input, Level level) {
        return matchesBase(input.base());
    }

    public boolean matchesBase(ItemStack stack) {
        return !stack.isEmpty() && base.test(stack);
    }

    public boolean matchesAction(int action, ItemStack stack) {
        SizedIngredient input = inputForAction(action);
        if (input == null || stack.isEmpty()) {
            return false;
        }
        return input.ingredient().test(stack);
    }

    public SizedIngredient inputForAction(int action) {
        if (action < 0 || action >= totalActions()) {
            return null;
        }

        int actionInLoop = action % actionsPerLoop();
        int passed = 0;
        for (SizedIngredient input : inputs) {
            passed += input.count();
            if (actionInLoop < passed) {
                return input;
            }
        }
        return null;
    }

    public boolean completeAfterAction(int action) {
        return action + 1 >= totalActions();
    }

    public int actionsPerLoop() {
        int total = 0;
        for (SizedIngredient input : inputs) {
            total += input.count();
        }
        return total;
    }

    public int totalActions() {
        return actionsPerLoop() * loops;
    }

    public Ingredient base() {
        return base;
    }

    public List<SizedIngredient> inputs() {
        return inputs;
    }

    public ProcessingOutput resultOutput() {
        return result;
    }

    public int loops() {
        return loops;
    }

    public ItemStack result() {
        return result.getStack().copy();
    }

    @Override
    public ItemStack assemble(AssemblyRecipeInput input, HolderLookup.Provider registries) {
        return result();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(base);
        inputs.forEach(input -> list.add(input.ingredient()));
        return list;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.CRAFTING_TABLE);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ASSEMBLY_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ASSEMBLY_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AssemblyRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<AssemblyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
