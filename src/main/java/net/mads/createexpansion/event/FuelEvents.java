package net.mads.createexpansion.event;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.item.SimpleItemDefinition;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID)
public class FuelEvents {

    private static final Map<Item, Integer> FUELS = new HashMap<>();

    private static void buildFuelMap() {

        // 🔥 ITEMS
        for (SimpleItemDefinition def : SimpleItems.ALL) {
            if (!def.isFurnaceFuel()) continue;

            var item = ItemRegistry.SIMPLE_ITEMS.get(def.id());
            if (item != null) {
                FUELS.put(item.get(), def.furnaceBurnTimeTicks());
            }
        }

        // 🔥 BLOCKS (via BlockItem)
        for (SimpleBlockDefinition def : SimpleBlocks.ALL) {
            if (!def.isFurnaceFuel()) continue;

            var item = ItemRegistry.SIMPLE_BLOCK_ITEMS.get(def.id());
            if (item != null) {
                FUELS.put(item.get(), def.furnaceBurnTimeTicks());
            }
        }

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (!material.isFurnaceFuel(part)) continue;

                Item item = materialItem(material, part);
                if (item != null) {
                    FUELS.put(item, material.furnaceBurnTimeTicks(part));
                }
            }
        }
    }

    private static Item materialItem(IndustrialMaterial material, MaterialPart part) {
        if (material.hasExistingPart(part)) {
            return BuiltInRegistries.ITEM.get(material.existingPart(part));
        }

        var materialParts = ItemRegistry.MATERIAL_ITEMS.get(material.id());
        if (materialParts == null) {
            return null;
        }

        var item = materialParts.get(part);
        return item == null ? null : item.get();
    }

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {

        // bygg én gang
        if (FUELS.isEmpty()) {
            buildFuelMap();
        }

        Item item = event.getItemStack().getItem();

        Integer burnTime = FUELS.get(item);
        if (burnTime != null) {
            event.setBurnTime(burnTime);
        }
    }
}
