package net.mads.createexpansion.transport;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public record FluidTransportTier(
        String id,
        String name,
        int color,
        double pumpRate,
        double pumpStressImpact,
        int tankCapacity
) {
    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9]+(?:_[a-z0-9]+)*");
    private static final Map<String, FluidTransportTier> ALL = new LinkedHashMap<>();

    public FluidTransportTier {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
    }

    public int maximumPipeRate() {
        return Math.max(1, (int) Math.floor(pumpRate * 256.0D));
    }

    public String pipeId() {
        return id + "_fluid_pipe";
    }

    public String glassPipeId() {
        return id + "_glass_fluid_pipe";
    }

    public String pumpId() {
        return id + "_mechanical_pump";
    }

    public String tankId() {
        return id + "_fluid_tank";
    }

    public String pipeDisplayName() {
        return name + " Fluid Pipe";
    }

    public String glassPipeDisplayName() {
        return name + " Glass Fluid Pipe";
    }

    public String pumpDisplayName() {
        return name + " Mechanical Pump";
    }

    public String tankDisplayName() {
        return name + " Fluid Tank";
    }

    public static Builder transportTier(String id, String name) {
        return new Builder(id, name);
    }

    public static List<FluidTransportTier> all() {
        FluidTransportTiers.bootstrap();
        return List.copyOf(ALL.values());
    }

    public static FluidTransportTier byId(String id) {
        FluidTransportTiers.bootstrap();
        FluidTransportTier tier = ALL.get(id);
        if (tier == null) {
            throw new IllegalArgumentException("Unknown fluid transport tier: " + id);
        }
        return tier;
    }

    private static FluidTransportTier register(FluidTransportTier tier) {
        FluidTransportTier previous = ALL.putIfAbsent(tier.id(), tier);
        if (previous != null) {
            throw new IllegalStateException("Duplicate fluid transport tier id: " + tier.id());
        }
        return tier;
    }

    public static final class Builder {
        private final String id;
        private final String name;
        private int color = 0xFFFFFF;
        private double pumpRate;
        private double pumpStressImpact;
        private int tankCapacity;

        private Builder(String id, String name) {
            this.id = Objects.requireNonNull(id, "id").trim();
            this.name = Objects.requireNonNull(name, "name").trim();
        }

        public Builder color(int color) {
            this.color = color & 0xFFFFFF;
            return this;
        }

        public Builder pumpRate(double pumpRate) {
            this.pumpRate = pumpRate;
            return this;
        }

        public Builder pumpStressImpact(double pumpStressImpact) {
            this.pumpStressImpact = pumpStressImpact;
            return this;
        }

        public Builder tankCapacity(int tankCapacity) {
            this.tankCapacity = tankCapacity;
            return this;
        }

        public FluidTransportTier build() {
            if (!VALID_ID.matcher(id).matches()) {
                throw new IllegalStateException("Invalid fluid transport tier id: " + id);
            }
            if (name.isBlank()) {
                throw new IllegalStateException("name cannot be blank for " + id);
            }
            if (!Double.isFinite(pumpRate) || pumpRate <= 0.0D) {
                throw new IllegalStateException("pumpRate must be finite and greater than zero for " + id);
            }
            if (pumpRate * 256.0D > Integer.MAX_VALUE) {
                throw new IllegalStateException("pumpRate is too large for the pipe limit of " + id);
            }
            if (!Double.isFinite(pumpStressImpact) || pumpStressImpact < 0.0D) {
                throw new IllegalStateException("pumpStressImpact must be finite and non-negative for " + id);
            }
            if (tankCapacity <= 0) {
                throw new IllegalStateException("tankCapacity must be greater than zero for " + id);
            }
            return register(new FluidTransportTier(id, name, color, pumpRate, pumpStressImpact, tankCapacity));
        }
    }
}
