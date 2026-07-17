package net.mads.createexpansion.machine.machines.kinetic.centrifuge;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class KineticCentrifugeRegistration {
    public static final String ID = "kinetic_centrifuge";
    public static final String PART_ID = "kinetic_centrifuge_part";
    public static final String DISPLAY_NAME = "Mechanical Centrifuge";
    public static final String RECIPE_DISPLAY_NAME = "Centrifuging";
    public static final double STRESS_IMPACT = 16.0D;

    private KineticCentrifugeRegistration() {
    }

    public static DeferredHolder<Block, KineticCentrifugeBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ID, () -> new KineticCentrifugeBlock());
    }

    public static DeferredHolder<Block, KineticCentrifugePartBlock> registerPartBlock(DeferredRegister<Block> blocks) {
        return blocks.register(PART_ID, () -> new KineticCentrifugePartBlock());
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items, Supplier<KineticCentrifugeBlock> block) {
        return items.register(ID, () -> new KineticCentrifugeItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCentrifugeBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticCentrifugeBlock> block
    ) {
        return blockEntities.register(ID, () -> BlockEntityType.Builder.of(KineticCentrifugeBlockEntity::new, block.get()).build(null));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCentrifugePartBlockEntity>> registerPartBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticCentrifugePartBlock> block
    ) {
        return blockEntities.register(PART_ID, () -> BlockEntityType.Builder.of(KineticCentrifugePartBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ID, DISPLAY_NAME);
    }
}
