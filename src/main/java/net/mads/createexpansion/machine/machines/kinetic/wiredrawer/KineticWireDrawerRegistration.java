package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

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

public final class KineticWireDrawerRegistration {
    public static final String ID = "kinetic_wire_drawer";
    public static final String PART_ID = "kinetic_wire_drawer_part";
    public static final String DISPLAY_NAME = "Mechanical Wire Drawer";
    public static final String RECIPE_DISPLAY_NAME = "Wire Drawing";
    public static final double STRESS_IMPACT = 16.0D;

    private KineticWireDrawerRegistration() {
    }

    public static DeferredHolder<Block, KineticWireDrawerBlock> registerBlock(DeferredRegister<Block> blocks) {
        return blocks.register(ID, KineticWireDrawerBlock::new);
    }

    public static DeferredHolder<Block, KineticWireDrawerPartBlock> registerPartBlock(DeferredRegister<Block> blocks) {
        return blocks.register(PART_ID, KineticWireDrawerPartBlock::new);
    }

    public static DeferredHolder<Item, BlockItem> registerItem(DeferredRegister<Item> items, Supplier<KineticWireDrawerBlock> block) {
        return items.register(ID, () -> new SimpleKineticMachineItem(block.get(), new Item.Properties()));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticWireDrawerBlockEntity>> registerBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticWireDrawerBlock> block
    ) {
        return blockEntities.register(ID, () -> BlockEntityType.Builder.of(KineticWireDrawerBlockEntity::new, block.get()).build(null));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticWireDrawerPartBlockEntity>> registerPartBlockEntity(
            DeferredRegister<BlockEntityType<?>> blockEntities,
            Supplier<KineticWireDrawerPartBlock> block
    ) {
        return blockEntities.register(PART_ID, () -> BlockEntityType.Builder.of(KineticWireDrawerPartBlockEntity::new, block.get()).build(null));
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        add.accept("block." + CreateExpansion.MOD_ID + "." + ID, DISPLAY_NAME);
        add.accept("block." + CreateExpansion.MOD_ID + "." + PART_ID, DISPLAY_NAME);
    }
}
