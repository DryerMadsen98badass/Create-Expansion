package net.mads.createexpansion.worldgen;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.BlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;

final class OreDepositLayer {
    private final String id;
    private final IndustrialMaterial material;
    private final int weight;

    private OreDepositLayer(String id, IndustrialMaterial material, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Ore deposit layer weight must be positive");
        }

        this.id = id;
        this.material = material;
        this.weight = weight;
    }

    static OreDepositLayer material(IndustrialMaterial material, int weight) {
        return new OreDepositLayer(material.id(), material, weight);
    }

    static OreDepositLayer coal(int weight) {
        return new OreDepositLayer("coal", null, weight);
    }

    int weight() {
        return weight;
    }

    String id() {
        return id;
    }

    Optional<BlockState> stateFor(MaterialPart part) {
        if (material == null) {
            return coalStateFor(part);
        }

        if (material.hasExistingPart(part)) {
            Block block = BuiltInRegistries.BLOCK.get(material.existingPart(part));
            if (block == Blocks.AIR) {
                return Optional.empty();
            }

            return Optional.of(block.defaultBlockState());
        }

        if (!material.has(part)) {
            return Optional.empty();
        }

        Map<MaterialPart, ? extends net.neoforged.neoforge.registries.DeferredHolder<Block, ? extends Block>> blocks =
                BlockRegistry.MATERIAL_BLOCKS.get(material.id());
        if (blocks == null || !blocks.containsKey(part)) {
            return Optional.empty();
        }

        return Optional.of(blocks.get(part).get().defaultBlockState());
    }

    private Optional<BlockState> coalStateFor(MaterialPart part) {
        return switch (part) {
            case ORE -> Optional.of(Blocks.COAL_ORE.defaultBlockState());
            case DEEPSLATE_ORE -> Optional.of(Blocks.DEEPSLATE_COAL_ORE.defaultBlockState());
            default -> Optional.empty();
        };
    }

    @Override
    public String toString() {
        return id;
    }
}
