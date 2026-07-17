package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.recipe.CERecipe;
import net.mads.createexpansion.recipe.CERecipeSerializer;
import net.mads.createexpansion.recipe.recipes.foundry.FoundryMeltingRecipe;
import net.mads.createexpansion.recipe.recipes.centrifuge.CentrifugingRecipe;
import net.mads.createexpansion.recipe.recipes.lathe.TurningRecipe;
import net.mads.createexpansion.recipe.recipes.sifter.SiftingRecipe;
import net.mads.createexpansion.recipe.recipes.rolling.RollingRecipe;
import net.mads.createexpansion.recipe.recipes.wiredrawer.WireDrawingRecipe;
import net.mads.createexpansion.recipe.recipes.hydraulicpress.HydraulicPressingRecipe;
import net.mads.createexpansion.recipe.recipes.coiling.CoilingRecipe;
import net.mads.createexpansion.recipe.recipetypes.CentrifugingRecipeType;
import net.mads.createexpansion.recipe.recipetypes.FoundryMeltingRecipeType;
import net.mads.createexpansion.recipe.recipetypes.SiftingRecipeType;
import net.mads.createexpansion.recipe.recipetypes.TurningRecipeType;
import net.mads.createexpansion.recipe.recipetypes.RollingRecipeType;
import net.mads.createexpansion.recipe.recipetypes.WireDrawingRecipeType;
import net.mads.createexpansion.recipe.recipetypes.HydraulicPressingRecipeType;
import net.mads.createexpansion.recipe.recipetypes.CoilingRecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateExpansion.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CreateExpansion.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, CERecipeSerializer> MACHINE_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("machine", CERecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CERecipe>> MACHINE_RECIPE_TYPE =
            RECIPE_TYPES.register("machine", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CreateExpansion.MOD_ID + ":machine";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, SiftingRecipe.Serializer<SiftingRecipe>> SIFTING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(SiftingRecipeType.NAME, () -> new SiftingRecipe.Serializer<>(SiftingRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<SiftingRecipe>> SIFTING_RECIPE_TYPE =
            RECIPE_TYPES.register(SiftingRecipeType.NAME, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return SiftingRecipeType.ID.toString();
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, CentrifugingRecipe.Serializer> CENTRIFUGING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(CentrifugingRecipeType.NAME, CentrifugingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CentrifugingRecipe>> CENTRIFUGING_RECIPE_TYPE =
            RECIPE_TYPES.register(CentrifugingRecipeType.NAME, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CentrifugingRecipeType.ID.toString();
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, TurningRecipe.Serializer> TURNING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(TurningRecipeType.NAME, TurningRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TurningRecipe>> TURNING_RECIPE_TYPE =
            RECIPE_TYPES.register(TurningRecipeType.NAME, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return TurningRecipeType.ID.toString();
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RollingRecipe.Serializer> ROLLING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(RollingRecipeType.NAME, RollingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<RollingRecipe>> ROLLING_RECIPE_TYPE =
            RECIPE_TYPES.register(RollingRecipeType.NAME, () -> namedType(RollingRecipeType.NAME));

    public static final DeferredHolder<RecipeSerializer<?>, WireDrawingRecipe.Serializer> WIRE_DRAWING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(WireDrawingRecipeType.NAME, WireDrawingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WireDrawingRecipe>> WIRE_DRAWING_RECIPE_TYPE =
            RECIPE_TYPES.register(WireDrawingRecipeType.NAME, () -> namedType(WireDrawingRecipeType.NAME));

    public static final DeferredHolder<RecipeSerializer<?>, HydraulicPressingRecipe.Serializer> HYDRAULIC_PRESSING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(HydraulicPressingRecipeType.NAME, HydraulicPressingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<HydraulicPressingRecipe>> HYDRAULIC_PRESSING_RECIPE_TYPE =
            RECIPE_TYPES.register(HydraulicPressingRecipeType.NAME, () -> namedType(HydraulicPressingRecipeType.NAME));

    public static final DeferredHolder<RecipeSerializer<?>, CoilingRecipe.Serializer> COILING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(CoilingRecipeType.NAME, CoilingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CoilingRecipe>> COILING_RECIPE_TYPE =
            RECIPE_TYPES.register(CoilingRecipeType.NAME, () -> namedType(CoilingRecipeType.NAME));

    public static final DeferredHolder<RecipeSerializer<?>, FoundryMeltingRecipe.Serializer> FOUNDRY_MELTING_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register(FoundryMeltingRecipeType.NAME, FoundryMeltingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FoundryMeltingRecipe>> FOUNDRY_MELTING_RECIPE_TYPE =
            RECIPE_TYPES.register(FoundryMeltingRecipeType.NAME, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return FoundryMeltingRecipeType.ID.toString();
                }
            });

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
    }

    private static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeType<T> namedType(String name) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return CreateExpansion.MOD_ID + ":" + name;
            }
        };
    }
}
