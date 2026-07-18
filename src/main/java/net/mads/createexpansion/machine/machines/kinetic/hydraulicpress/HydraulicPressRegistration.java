package net.mads.createexpansion.machine.machines.kinetic.hydraulicpress;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class HydraulicPressRegistration {
    public static final String ID = "hydraulic_press";
    public static final String DISPLAY_NAME = "Hydraulic Press";
    public static final String RECIPE_DISPLAY_NAME = "Hydraulic Pressing";

    private HydraulicPressRegistration() {
    }

    public static DeferredHolder<Block, HydraulicPressBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ID, () -> new HydraulicPressBlock());
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items, Supplier<HydraulicPressBlock> block) {
        return items.register(ID, () -> new HydraulicPressItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<HydraulicPressBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<HydraulicPressBlock> block
    ) {
        return blockEntities.register(ID,
                () -> BlockEntityType.Builder.of(HydraulicPressBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ID, DISPLAY_NAME);
    }
}
