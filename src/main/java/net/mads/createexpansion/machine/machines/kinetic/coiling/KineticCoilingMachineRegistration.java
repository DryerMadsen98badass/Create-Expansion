package net.mads.createexpansion.machine.machines.kinetic.coiling;

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

public final class KineticCoilingMachineRegistration {
    public static final String ID = "spring_coiling_machine";
    public static final String DISPLAY_NAME = "Spring Coiling Machine";
    public static final String RECIPE_DISPLAY_NAME = "Coiling";
    public static final double STRESS_IMPACT = 16.0D;

    private KineticCoilingMachineRegistration() {}

    public static DeferredHolder<Block, KineticCoilingMachineBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ID, () -> new KineticCoilingMachineBlock());
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items,
                                                                Supplier<KineticCoilingMachineBlock> block) {
        return items.register(ID, () -> new SimpleKineticMachineItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCoilingMachineBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities, Supplier<KineticCoilingMachineBlock> block) {
        return blockEntities.register(ID,
                () -> BlockEntityType.Builder.of(KineticCoilingMachineBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ID, DISPLAY_NAME);
    }
}
