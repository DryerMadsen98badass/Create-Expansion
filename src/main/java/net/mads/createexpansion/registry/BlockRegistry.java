package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialBlock;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, CreateExpansion.MOD_ID);
    public static final Map<String, Map<MaterialPart, DeferredHolder<Block, ? extends Block>>> MATERIAL_BLOCKS = new LinkedHashMap<>();

    static {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            Map<MaterialPart, DeferredHolder<Block, ? extends Block>> blocks = new LinkedHashMap<>();

            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (!part.isBlock()) {
                    continue;
                }

                DeferredHolder<Block, MaterialBlock> block = BLOCKS.register(part.registryName(material), () -> new MaterialBlock(material, part));
                blocks.put(part, block);
            }

            MATERIAL_BLOCKS.put(material.id(), blocks);
        }
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    public static DeferredHolder<Block, ? extends Block> getMaterialBlock(IndustrialMaterial material, MaterialPart part) {
        return MATERIAL_BLOCKS.get(material.id()).get(part);
    }

    public static Collection<DeferredHolder<Block, ? extends Block>> getAllMaterialBlocks() {
        return MATERIAL_BLOCKS.values().stream()
                .flatMap(blocks -> blocks.values().stream())
                .toList();
    }
}
