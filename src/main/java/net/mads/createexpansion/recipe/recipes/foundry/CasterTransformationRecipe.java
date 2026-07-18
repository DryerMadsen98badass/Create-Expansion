package net.mads.createexpansion.recipe.recipes.foundry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.registry.BlockRegistry;
import net.mads.createexpansion.registry.RecipeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CasterTransformationRecipe implements Recipe<CasterTransformationRecipeInput> {
    public static final MapCodec<CasterTransformationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("template").forGetter(CasterTransformationRecipe::template),
            FluidStack.CODEC.fieldOf("fluid").forGetter(CasterTransformationRecipe::fluid),
            ItemStack.CODEC.fieldOf("result").forGetter(CasterTransformationRecipe::result)
    ).apply(instance, CasterTransformationRecipe::new));

    private final Ingredient template;
    private final FluidStack fluid;
    private final ItemStack result;

    public CasterTransformationRecipe(Ingredient template, FluidStack fluid, ItemStack result) {
        this.template = template;
        this.fluid = fluid.copy();
        this.result = result.copy();
    }

    @Override
    public boolean matches(CasterTransformationRecipeInput input, Level level) {
        return template.test(input.item())
                && !input.fluid().isEmpty()
                && input.fluid().getAmount() >= fluid.getAmount()
                && FluidStack.isSameFluidSameComponents(input.fluid(), fluid);
    }

    public Ingredient template() {
        return template;
    }

    public FluidStack fluid() {
        return fluid.copy();
    }

    public ItemStack result() {
        return result.copy();
    }

    @Override
    public ItemStack assemble(CasterTransformationRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(template);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BlockRegistry.FOUNDRY_MOLD_CASTER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CASTER_TRANSFORMATION_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CASTER_TRANSFORMATION_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CasterTransformationRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, CasterTransformationRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<CasterTransformationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CasterTransformationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
