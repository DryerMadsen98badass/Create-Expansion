package net.mads.createexpansion.recipe;

import com.mojang.serialization.Codec;

final class CodecHelpers {
    static final Codec<CERecipe.EnergyDirection> ENERGY_DIRECTION = Codec.STRING.xmap(
            value -> "generate".equals(value) ? CERecipe.EnergyDirection.GENERATE : CERecipe.EnergyDirection.CONSUME,
            value -> value == CERecipe.EnergyDirection.GENERATE ? "generate" : "consume"
    );

    private CodecHelpers() {
    }
}
