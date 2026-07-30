package net.mads.createexpansion.machine.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Optional;

/** World condition used by a machine or one CE recipe. */
public record MachineCondition(
        Kind kind,
        Optional<ResourceLocation> id,
        Optional<RelativePos> pos,
        int min,
        int max,
        InteractionPhase check,
        ConditionFailure onFailure,
        List<MachineCondition> children
) {
    public static final Codec<MachineCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Kind::valueOf, Kind::name).fieldOf("kind").forGetter(MachineCondition::kind),
            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(MachineCondition::id),
            RelativePos.CODEC.optionalFieldOf("pos").forGetter(MachineCondition::pos),
            Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(MachineCondition::min),
            Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(MachineCondition::max),
            Codec.STRING.xmap(InteractionPhase::valueOf, InteractionPhase::name).optionalFieldOf("check", InteractionPhase.ON_START).forGetter(MachineCondition::check),
            Codec.STRING.xmap(ConditionFailure::valueOf, ConditionFailure::name).optionalFieldOf("on_failure", ConditionFailure.CANCEL).forGetter(MachineCondition::onFailure),
            Codec.lazyInitialized(() -> MachineCondition.CODEC).listOf().optionalFieldOf("children", List.of()).forGetter(MachineCondition::children)
    ).apply(instance, MachineCondition::new));

    public MachineCondition {
        children = List.copyOf(children);
    }

    public static MachineCondition weather(Weather weather) {
        return new MachineCondition(Kind.WEATHER, Optional.of(ResourceLocation.withDefaultNamespace(weather.name().toLowerCase())), Optional.empty(), 0, 0, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of());
    }

    public static RangeBuilder time() {
        return new RangeBuilder(Kind.TIME);
    }

    public static RangeBuilder height() {
        return new RangeBuilder(Kind.HEIGHT);
    }

    public static RangeBuilder redstone() {
        return new RangeBuilder(Kind.REDSTONE);
    }

    public static RangeBuilder light() {
        return new RangeBuilder(Kind.LIGHT);
    }

    public static MachineCondition biome(String biomeId) {
        return idCondition(Kind.BIOME, biomeId);
    }

    public static MachineCondition biomeTag(String tagId) {
        return idCondition(Kind.BIOME_TAG, tagId);
    }

    public static MachineCondition dimension(String dimensionId) {
        return idCondition(Kind.DIMENSION, dimensionId);
    }

    public static MachineCondition canSeeSky() {
        return new MachineCondition(Kind.CAN_SEE_SKY, Optional.empty(), Optional.empty(), 0, 0, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of());
    }

    public static MachineCondition all(MachineCondition... conditions) {
        return new MachineCondition(Kind.ALL, Optional.empty(), Optional.empty(), 0, 0, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of(conditions));
    }

    public static MachineCondition any(MachineCondition... conditions) {
        return new MachineCondition(Kind.ANY, Optional.empty(), Optional.empty(), 0, 0, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of(conditions));
    }

    public static MachineCondition not(MachineCondition condition) {
        return new MachineCondition(Kind.NOT, Optional.empty(), Optional.empty(), 0, 0, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of(condition));
    }

    /** Evaluates this condition using controller-relative coordinates. */
    public boolean matches(Level level, BlockPos origin, net.minecraft.core.Direction facing) {
        BlockPos target = pos.map(relative -> relative.rotate(origin, facing)).orElse(origin);
        return switch (kind) {
            case WEATHER -> matchesWeather(level);
            case TIME -> inRange((int) (level.getDayTime() % 24000L));
            case HEIGHT -> inRange(target.getY());
            case REDSTONE -> inRange(level.getBestNeighborSignal(target));
            case LIGHT -> inRange(level.getBrightness(LightLayer.BLOCK, target));
            case BIOME -> id.map(value -> level.getBiome(target).is(ResourceKey.create(Registries.BIOME, value))).orElse(false);
            case BIOME_TAG -> id.map(value -> level.getBiome(target).is(TagKey.create(Registries.BIOME, value))).orElse(false);
            case DIMENSION -> id.map(value -> level.dimension().location().equals(value)).orElse(false);
            case CAN_SEE_SKY -> level.canSeeSky(target);
            case ALL -> children.stream().allMatch(child -> child.matches(level, origin, facing));
            case ANY -> children.stream().anyMatch(child -> child.matches(level, origin, facing));
            case NOT -> children.isEmpty() || !children.getFirst().matches(level, origin, facing);
        };
    }

    /** Uses another relative position as the sample point. */
    public MachineCondition at(int x, int y, int z) {
        return new MachineCondition(kind, id, Optional.of(new RelativePos(x, y, z)), min, max, check, onFailure, children);
    }

    /** Sets when this condition is checked. */
    public MachineCondition check(InteractionPhase check) {
        return new MachineCondition(kind, id, pos, min, max, check, onFailure, children);
    }

    /** Sets what happens when this condition fails. */
    public MachineCondition onFailure(ConditionFailure onFailure) {
        return new MachineCondition(kind, id, pos, min, max, check, onFailure, children);
    }

    private boolean matchesWeather(Level level) {
        String value = id.map(ResourceLocation::getPath).orElse("");
        return switch (value) {
            case "clear" -> !level.isRaining() && !level.isThundering();
            case "rain" -> level.isRaining() && !level.isThundering();
            case "thunder" -> level.isThundering();
            default -> false;
        };
    }

    private boolean inRange(int value) {
        return value >= min && value <= max;
    }

    private static MachineCondition idCondition(Kind kind, String rawId) {
        return new MachineCondition(kind, Optional.of(id(rawId)), Optional.empty(), 0, 0, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of());
    }

    private static ResourceLocation id(String id) {
        return id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, id);
    }

    public enum Kind {
        WEATHER,
        TIME,
        HEIGHT,
        REDSTONE,
        LIGHT,
        BIOME,
        BIOME_TAG,
        DIMENSION,
        CAN_SEE_SKY,
        ALL,
        ANY,
        NOT
    }

    /** Builder for conditions using numeric ranges. */
    public static final class RangeBuilder {
        private final Kind kind;

        private RangeBuilder(Kind kind) {
            this.kind = kind;
        }

        public MachineCondition between(int min, int max) {
            return new MachineCondition(kind, Optional.empty(), Optional.empty(), min, max, InteractionPhase.ON_START, ConditionFailure.CANCEL, List.of());
        }

        public MachineCondition minimum(int min) {
            return between(min, Integer.MAX_VALUE);
        }

        public MachineCondition maximum(int max) {
            return between(Integer.MIN_VALUE, max);
        }
    }
}
