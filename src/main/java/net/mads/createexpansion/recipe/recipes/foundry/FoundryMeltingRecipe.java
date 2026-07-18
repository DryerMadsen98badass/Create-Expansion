package net.mads.createexpansion.recipe.recipes.foundry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.material.MaterialPart;
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
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.EnumMap;
import java.util.Map;

public class FoundryMeltingRecipe implements Recipe<FoundryMeltingRecipeInput> {
    public static final int TICKS_PER_NUGGET = 80;
    public static final int MB_PER_NUGGET = 16;
    public static final int MOLD_NUGGETS = 36;
    private static final Map<MaterialPart, Integer> MELTING_AMOUNTS = buildMeltingAmounts();

    public static final MapCodec<FoundryMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FoundryMeltingRecipe::ingredient),
            FluidStack.CODEC.fieldOf("result").forGetter(FoundryMeltingRecipe::result),
            ExtraCodecs.POSITIVE_INT.fieldOf("temperature").forGetter(FoundryMeltingRecipe::temperature),
            ExtraCodecs.POSITIVE_INT.fieldOf("nuggets").forGetter(FoundryMeltingRecipe::nuggets)
    ).apply(instance, FoundryMeltingRecipe::new));

    private final Ingredient ingredient;
    private final FluidStack result;
    private final int temperature;
    private final int nuggets;

    public FoundryMeltingRecipe(Ingredient ingredient, FluidStack result, int temperature, int nuggets) {
        this.ingredient = ingredient;
        this.result = result.copy();
        this.temperature = temperature;
        this.nuggets = nuggets;
    }

    @Override
    public boolean matches(FoundryMeltingRecipeInput input, Level level) {
        return !input.isEmpty() && input.temperature() >= temperature && ingredient.test(input.item());
    }

    public boolean matchesItem(ItemStack stack) {
        return !stack.isEmpty() && ingredient.test(stack);
    }

    public int durationAt(int foundryTemperature) {
        if (foundryTemperature < temperature) {
            return Integer.MAX_VALUE;
        }
        return Math.max(1, Math.round((float) baseDuration() * temperature / foundryTemperature));
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public FluidStack result() {
        return result.copy();
    }

    public int temperature() {
        return temperature;
    }

    public int nuggets() {
        return nuggets;
    }

    public int baseDuration() {
        return Math.max(1, Math.round(result.getAmount() * (TICKS_PER_NUGGET / (float) MB_PER_NUGGET)));
    }

    public static Map<MaterialPart, Integer> meltingAmounts() {
        return MELTING_AMOUNTS;
    }

    private static Map<MaterialPart, Integer> buildMeltingAmounts() {
        EnumMap<MaterialPart, Integer> amounts = new EnumMap<>(MaterialPart.class);
        amount(amounts, 16, MaterialPart.NUGGET, MaterialPart.TINY_DUST, MaterialPart.CAST_NUGGET);
        amount(amounts, 36, MaterialPart.SMALL_DUST, MaterialPart.BOLT, MaterialPart.SCREW,
                MaterialPart.SMALL_RING, MaterialPart.BEARING_BALL, MaterialPart.CAST_BOLT,
                MaterialPart.CAST_SCREW, MaterialPart.CAST_SMALL_RING, MaterialPart.CAST_BEARING_BALL);
        amount(amounts, 72, MaterialPart.ROD, MaterialPart.RING, MaterialPart.CAST_ROD, MaterialPart.CAST_RING);
        amount(amounts, 144, MaterialPart.INGOT, MaterialPart.DUST, MaterialPart.PLATE, MaterialPart.FOIL,
                MaterialPart.LONG_ROD, MaterialPart.WIRE, MaterialPart.FINE_WIRE, MaterialPart.LARGE_RING,
                MaterialPart.SMALL_GEAR, MaterialPart.SPRING, MaterialPart.CAST_INGOT,
                MaterialPart.CAST_PLATE, MaterialPart.CAST_LONG_ROD, MaterialPart.CAST_LARGE_RING,
                MaterialPart.CAST_SMALL_GEAR, MaterialPart.HEAT_EXCHANGER_PLATE);
        amount(amounts, 288, MaterialPart.DOUBLE_PLATE, MaterialPart.REINFORCED_PLATE,
                MaterialPart.BEARING, MaterialPart.CAST_BEARING);
        amount(amounts, 576, MaterialPart.GEAR, MaterialPart.ROTOR, MaterialPart.COIL,
                MaterialPart.TOOL_HEAD_BUZZ_SAW, MaterialPart.CAST_GEAR, MaterialPart.CAST_ROTOR);
        amount(amounts, 864, MaterialPart.FRAME);
        amount(amounts, 1152, MaterialPart.LARGE_GEAR, MaterialPart.CASING, MaterialPart.MACHINE_HULL);
        amount(amounts, 1296, MaterialPart.BLOCK, MaterialPart.CAST_BLOCK, MaterialPart.DENSE_PLATE);

        for (MaterialPart part : MaterialPart.values()) {
            if (part.name().endsWith("_MOLD")) {
                amounts.put(part, MOLD_NUGGETS * MB_PER_NUGGET);
            }
        }
        return Map.copyOf(amounts);
    }

    private static void amount(Map<MaterialPart, Integer> amounts, int amountMb, MaterialPart... parts) {
        for (MaterialPart part : parts) {
            amounts.put(part, amountMb);
        }
    }

    @Override
    public ItemStack assemble(FoundryMeltingRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ingredient);
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BlockRegistry.FOUNDRY_CONTROLLER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FOUNDRY_MELTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FOUNDRY_MELTING_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FoundryMeltingRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, FoundryMeltingRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<FoundryMeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FoundryMeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
