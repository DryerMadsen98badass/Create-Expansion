package net.mads.createexpansion.machine.machines.kinetic.lathe;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class KineticLatheRegistration {
    public static final String ID = "lathe";
    public static final String ITEM_ID = "kinetic_lathe";
    public static final String DISPLAY_NAME = "Mechanical Lathe";
    public static final String RECIPE_DISPLAY_NAME = "Turning";
    public static final double STRESS_IMPACT = 16.0D;

    private KineticLatheRegistration() {
    }

    public static DeferredHolder<Block, KineticLatheBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ITEM_ID, () -> new KineticLatheBlock());
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items, Supplier<KineticLatheBlock> block) {
        return items.register(ITEM_ID, () -> new KineticLatheItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticLatheBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticLatheBlock> block
    ) {
        return blockEntities.register(ITEM_ID, () -> BlockEntityType.Builder.of(KineticLatheBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ITEM_ID, DISPLAY_NAME);
    }
}
