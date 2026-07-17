package net.mads.createexpansion.machine.machines.kinetic.sifter;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class KineticSifterRegistration {
    public static final String ID = "kinetic_sifter";
    public static final String DISPLAY_NAME = "Mechanical Sifter";
    public static final String RECIPE_DISPLAY_NAME = "Sifting";
    public static final double STRESS_IMPACT = 16.0D;

    private KineticSifterRegistration() {
    }

    public static DeferredHolder<Block, KineticSifterBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ID, () -> new KineticSifterBlock());
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items, Supplier<KineticSifterBlock> block) {
        return items.register(ID, () -> new KineticSifterItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticSifterBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticSifterBlock> block
    ) {
        return blockEntities.register(ID, () -> BlockEntityType.Builder.of(KineticSifterBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ID, DISPLAY_NAME);
    }
}
