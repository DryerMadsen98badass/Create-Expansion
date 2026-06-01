package net.mads.createexpansion.registry;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.mads.createexpansion.CreateExpansion;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, CreateExpansion.MOD_ID);
    public static final Map<String, Map<MaterialPart, DeferredHolder<Item, ? extends Item>>> MATERIAL_ITEMS = new LinkedHashMap<>();

    static {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            Map<MaterialPart, DeferredHolder<Item, ? extends Item>> items = new LinkedHashMap<>();

            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (part.isFluid()) {
                    continue;
                }

                DeferredHolder<Item, ? extends Item> item;

                if (part.isBlock()) {
                    item = ITEMS.register(part.registryName(material), () ->
                            new BlockItem(BlockRegistry.getMaterialBlock(material, part).get(), new Item.Properties()));
                } else {
                    item = ITEMS.register(part.registryName(material), () -> new MaterialItem(material, part));
                }

                items.put(part, item);
            }

            MATERIAL_ITEMS.put(material.id(), items);
        }
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static DeferredHolder<Item, ? extends Item> getMaterialItem(IndustrialMaterial material, MaterialPart part) {
        return MATERIAL_ITEMS.get(material.id()).get(part);
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getAllMaterialItems() {
        return MATERIAL_ITEMS.values().stream()
                .flatMap(items -> items.values().stream())
                .toList();
    }
}
