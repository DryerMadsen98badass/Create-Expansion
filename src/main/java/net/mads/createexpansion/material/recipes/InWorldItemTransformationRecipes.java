package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID)
public final class InWorldItemTransformationRecipes {


    private static final Map<Item, Item> WATER_SOURCE_RECIPES =
            new LinkedHashMap<>();

    private static boolean recipesBuilt;

    private InWorldItemTransformationRecipes() {
    }

    @SubscribeEvent
    public static void onEntityTick(
            EntityTickEvent.Post event
    ) {
        if (!(event.getEntity()
                instanceof ItemEntity itemEntity)) {
            return;
        }

        Level level = itemEntity.level();

        if (level.isClientSide) {
            return;
        }

        if (!itemEntity.isAlive()) {
            return;
        }

        ItemStack inputStack =
                itemEntity.getItem();

        if (inputStack.isEmpty()) {
            return;
        }

        buildRecipes();

        Item outputItem =
                WATER_SOURCE_RECIPES.get(
                        inputStack.getItem()
                );

        if (outputItem == null) {
            return;
        }

        if (!isInsideWaterSource(itemEntity)) {
            return;
        }

        transformItem(
                itemEntity,
                outputItem
        );
    }

    private static void buildRecipes() {
        if (recipesBuilt) {
            return;
        }

        recipesBuilt = true;

        for (IndustrialMaterial material
                : IndustrialMaterials.ALL) {

            addMaterialWaterCleaningRecipe(
                    material
            );
        }

        CreateExpansion.LOGGER.info(
                "Created {} in-world water source item transformation recipes",
                WATER_SOURCE_RECIPES.size()
        );
    }

    private static void addMaterialWaterCleaningRecipe(
            IndustrialMaterial material
    ) {
        if (!material.has(
                MaterialPart.IMPURE_DUST
        )) {
            return;
        }

        if (!material.has(
                MaterialPart.TINY_DUST
        )) {
            return;
        }

        Item input =
                resolveMaterialItem(
                        material,
                        MaterialPart.IMPURE_DUST
                );

        Item output =
                resolveMaterialItem(
                        material,
                        MaterialPart.TINY_DUST
                );

        if (input == null || output == null) {
            return;
        }

        if (input == output) {
            return;
        }

        WATER_SOURCE_RECIPES.put(
                input,
                output
        );
    }

    private static Item resolveMaterialItem(
            IndustrialMaterial material,
            MaterialPart part
    ) {
        if (material.hasExistingPart(part)) {
            return BuiltInRegistries.ITEM.get(
                    material.existingPart(part)
            );
        }

        Map<
                MaterialPart,
                ? extends net.neoforged.neoforge.registries.DeferredHolder<
                        Item,
                        ? extends Item
                        >
                > materialItems =
                ItemRegistry.MATERIAL_ITEMS.get(
                        material.id()
                );

        if (materialItems == null) {
            return null;
        }

        var itemHolder =
                materialItems.get(part);

        if (itemHolder == null) {
            return null;
        }

        return itemHolder.get();
    }

    private static boolean isInsideWaterSource(
            ItemEntity itemEntity
    ) {
        Level level =
                itemEntity.level();

        BlockPos entityPosition =
                itemEntity.blockPosition();

        if (isWaterSource(
                level,
                entityPosition
        )) {
            return true;
        }

        /*
         * Item-entityen kan flyte helt på toppen av vannet.
         * Da kan blockPosition være blokken rett over vannet.
         */
        return isWaterSource(
                level,
                entityPosition.below()
        );
    }

    private static boolean isWaterSource(
            Level level,
            BlockPos position
    ) {
        FluidState fluidState =
                level.getFluidState(position);

        return fluidState.is(
                FluidTags.WATER
        ) && fluidState.isSource();
    }

    private static void transformItem(
            ItemEntity itemEntity,
            Item outputItem
    ) {
        ItemStack inputStack =
                itemEntity.getItem();

        int count =
                inputStack.getCount();

        ItemStack outputStack =
                new ItemStack(
                        outputItem,
                        count
                );

        /*
         * Beholder pickup-delay, bevegelse og selve
         * ItemEntity-instansen. Bare stacken erstattes.
         */
        itemEntity.setItem(outputStack);
    }
}