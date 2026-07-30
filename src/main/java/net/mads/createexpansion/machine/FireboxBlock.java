package net.mads.createexpansion.machine;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FireboxBlock extends Block {

    public static final MapCodec<FireboxBlock> CODEC =
            simpleCodec(FireboxBlock::new);

    public static final BooleanProperty ACTIVE =
            BooleanProperty.create("active");

    public FireboxBlock() {
        this(
                BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops()
                        .strength(
                                5.0F,
                                6.0F
                        )
                        .lightLevel(
                                state ->
                                        state.getValue(ACTIVE)
                                                ? 12
                                                : 0
                        )
                        .sound(SoundType.STONE)
        );
    }

    public FireboxBlock(
            BlockBehaviour.Properties properties
    ) {
        super(properties);

        registerDefaultState(
                stateDefinition
                        .any()
                        .setValue(
                                ACTIVE,
                                false
                        )
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<
                    Block,
                    BlockState
                    > builder
    ) {
        builder.add(ACTIVE);
    }
}