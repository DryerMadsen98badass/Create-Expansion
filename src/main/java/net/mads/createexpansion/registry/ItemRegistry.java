package net.mads.createexpansion.registry;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialItem;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.StaticMachinePortType;
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
    public static final Map<String, DeferredHolder<Item, BlockItem>> MACHINE_CASINGS = new LinkedHashMap<>();
    public static final Map<String, Map<MachinePortType, DeferredHolder<Item, BlockItem>>> MACHINE_PORTS = new LinkedHashMap<>();
    public static final Map<StaticMachinePortType, DeferredHolder<Item, BlockItem>> STATIC_MACHINE_PORTS = new LinkedHashMap<>();
    public static final Map<String, Map<MaterialPart, DeferredHolder<Item, ? extends Item>>> MATERIAL_ITEMS = new LinkedHashMap<>();

    static {
        for (MachineTier tier : MachineTier.ALL) {
            MACHINE_CASINGS.put(tier.id(), ITEMS.register(tier.casingRegistryName(), () ->
                    new BlockItem(BlockRegistry.MACHINE_CASINGS.get(tier.id()).get(), new Item.Properties())));

            Map<MachinePortType, DeferredHolder<Item, BlockItem>> ports = new LinkedHashMap<>();
            for (MachinePortType portType : MachinePortType.ALL) {
                ports.put(portType, ITEMS.register(portType.registryName(tier), () ->
                        new BlockItem(BlockRegistry.MACHINE_PORTS.get(tier.id()).get(portType).get(), new Item.Properties())));
            }
            MACHINE_PORTS.put(tier.id(), ports);
        }

        for (StaticMachinePortType portType : StaticMachinePortType.ALL) {
            STATIC_MACHINE_PORTS.put(portType, ITEMS.register(portType.id(), () ->
                    new BlockItem(BlockRegistry.STATIC_MACHINE_PORTS.get(portType).get(), new Item.Properties())));
        }

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
        Map<MaterialPart, DeferredHolder<Item, ? extends Item>> items = MATERIAL_ITEMS.get(material.id());
        if (items == null) {
            throw new IllegalStateException("No items registered for material '" + material.id() + "'");
        }

        DeferredHolder<Item, ? extends Item> holder = items.get(part);
        if (holder == null) {
            throw new IllegalStateException("No item registered for material '" + material.id() + "' part '" + part + "'");
        }

        return holder;
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getAllMaterialItems() {
        return MATERIAL_ITEMS.values().stream()
                .flatMap(items -> items.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllMachineCasingItems() {
        return MACHINE_CASINGS.values();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllMachinePortItems() {
        return MACHINE_PORTS.values().stream()
                .flatMap(ports -> ports.values().stream())
                .toList();
    }

    public static Collection<DeferredHolder<Item, BlockItem>> getAllStaticMachinePortItems() {
        return STATIC_MACHINE_PORTS.values();
    }
}
