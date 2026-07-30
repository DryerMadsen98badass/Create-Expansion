package net.mads.createexpansion.machine.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

/** A machine or recipe action performed against a controller-relative block position. */
public record BlockInteraction(
        Type type,
        RelativePos pos,
        InteractionPhase when,
        BlockRequirement requirement,
        Optional<ResourceLocation> targetBlockId
) {
    public static final Codec<BlockInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Type::valueOf, Type::name).fieldOf("type").forGetter(BlockInteraction::type),
            RelativePos.CODEC.fieldOf("pos").forGetter(BlockInteraction::pos),
            Codec.STRING.xmap(InteractionPhase::valueOf, InteractionPhase::name).optionalFieldOf("when", InteractionPhase.ON_COMPLETE).forGetter(BlockInteraction::when),
            BlockRequirement.CODEC.fieldOf("requirement").forGetter(BlockInteraction::requirement),
            ResourceLocation.CODEC.optionalFieldOf("target").forGetter(BlockInteraction::targetBlockId)
    ).apply(instance, BlockInteraction::new));

    /** Checks that a requirement matches but does not change the world. */
    public static Builder require() {
        return new Builder(Type.REQUIRE);
    }

    /** Removes the matched block when the action chance succeeds. */
    public static Builder consume() {
        return new Builder(Type.CONSUME);
    }

    /** Replaces the matched block with another block when the action chance succeeds. */
    public static Builder convert() {
        return new Builder(Type.CONVERT);
    }

    /** Places a block when the requirement matches and the action chance succeeds. */
    public static Builder place() {
        return new Builder(Type.PLACE);
    }

    /** Adds wear to a matched block and breaks it when its wear limit is reached. */
    public static Builder damage() {
        return new Builder(Type.DAMAGE);
    }

    public Block targetBlock() {
        return targetBlockId.map(BuiltInRegistries.BLOCK::get).orElse(Blocks.AIR);
    }

    public enum Type {
        REQUIRE,
        CONSUME,
        CONVERT,
        PLACE,
        DAMAGE
    }

    /** Fluent builder shown in machine and recipe examples. */
    public static final class Builder {
        private final Type type;
        private RelativePos pos = new RelativePos(0, 0, 0);
        private InteractionPhase when = InteractionPhase.ON_COMPLETE;
        private BlockRequirement requirement = BlockRequirement.anyBlock();
        private Optional<ResourceLocation> targetBlockId = Optional.empty();

        private Builder(Type type) {
            this.type = type;
        }

        /** Sets the controller-relative position. */
        public Builder at(int x, int y, int z) {
            this.pos = new RelativePos(x, y, z);
            return this;
        }

        /** Sets when this interaction is evaluated. */
        public Builder when(InteractionPhase when) {
            this.when = when;
            return this;
        }

        /** Sets the block or fluid requirement for the target position. */
        public Builder requires(BlockRequirement requirement) {
            this.requirement = requirement;
            return this;
        }

        /** Sets the block used by convert or place. */
        public Builder to(String blockId) {
            this.targetBlockId = Optional.of(id(blockId));
            return this;
        }

        /** Sets the block used by place. */
        public Builder block(String blockId) {
            return to(blockId);
        }

        /** Sets a top-level action chance for simple one-requirement interactions. */
        public Builder actionChance(float chance) {
            this.requirement = this.requirement.actionChance(chance);
            return this;
        }

        public BlockInteraction build() {
            if ((type == Type.CONVERT || type == Type.PLACE) && targetBlockId.isEmpty()) {
                throw new IllegalStateException(type + " block interaction needs .to(...) or .block(...)");
            }
            return new BlockInteraction(type, pos, when, requirement, targetBlockId);
        }
    }

    private static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }
}
