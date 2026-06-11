package net.mads.createexpansion.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public record CEChancedItemOutput(ItemStack stack, int chance) {
    public static final int MAX_CHANCE = 10_000;

    public static final Codec<CEChancedItemOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(CEChancedItemOutput::stack),
            ExtraCodecs.intRange(0, MAX_CHANCE).optionalFieldOf("chance", MAX_CHANCE).forGetter(CEChancedItemOutput::chance)
    ).apply(instance, CEChancedItemOutput::new));

    public boolean guaranteed() {
        return chance >= MAX_CHANCE;
    }
}
