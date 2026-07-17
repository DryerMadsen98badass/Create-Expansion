package net.mads.createexpansion.machine.machines.kinetic.rollingmill;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.mads.createexpansion.machine.machines.kinetic.simple.AbstractSimpleKineticMachineBlock;
import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class KineticRollingMillBlock extends AbstractSimpleKineticMachineBlock<KineticRollingMillBlockEntity> {
    public static final MapCodec<KineticRollingMillBlock> CODEC = simpleCodec(properties -> new KineticRollingMillBlock());

    @Override
    protected MapCodec<? extends KineticBlock> codec() {
        return CODEC;
    }

    @Override
    public Class<KineticRollingMillBlockEntity> getBlockEntityClass() {
        return KineticRollingMillBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticRollingMillBlockEntity> getBlockEntityType() {
        return BlockEntityRegistry.KINETIC_ROLLING_MILL.get();
    }
}
