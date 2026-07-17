package net.mads.createexpansion.machine.machines.kinetic.rollingmill;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.machine.machines.kinetic.simple.SimpleKineticMachineItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class KineticRollingMillRegistration {
    public static final String ID = "kinetic_rolling_mill";
    public static final String DISPLAY_NAME = "Mechanical Rolling Mill";
    public static final String RECIPE_DISPLAY_NAME = "Rolling";
    public static final double STRESS_IMPACT = 24.0D;

    private KineticRollingMillRegistration() {
    }

    public static DeferredHolder<Block, KineticRollingMillBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ID, KineticRollingMillBlock::new);
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items, Supplier<KineticRollingMillBlock> block) {
        return items.register(ID, () -> new SimpleKineticMachineItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticRollingMillBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticRollingMillBlock> block
    ) {
        return blockEntities.register(ID, () -> BlockEntityType.Builder.of(KineticRollingMillBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ID, DISPLAY_NAME);
    }
}
