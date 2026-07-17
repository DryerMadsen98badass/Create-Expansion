package net.mads.createexpansion.fluid;

import java.util.List;

public class IndustrialFluids {
    public static final IndustrialFluid CRUDE_OIL = fluid("crude_oil", "Crude Oil", 0x19130D)
            .viscosity(2000)
            .build();

    public static final IndustrialFluid STEAM = gas("steam", "Steam", 0xE6E6E6)
        .temperature(400)
        .density(-200)
        .viscosity(50)
        .build();  

    public static final List<IndustrialFluid> ALL = List.of(
            CRUDE_OIL,
            STEAM
    );



    public static FluidBuilder fluid(String id, String displayName, int color) {
        return new FluidBuilder(id, displayName, color, IndustrialFluid.Kind.LIQUID)
                .density(1000)
                .temperature(300)
                .viscosity(1000);
    }

    public static FluidBuilder gas(String id, String displayName, int color) {
        return new FluidBuilder(id, displayName, color, IndustrialFluid.Kind.GAS)
                .density(-100)
                .temperature(300)
                .viscosity(100);
    }

    public static FluidBuilder molten(String id, String displayName, int color, int temperature) {
        return new FluidBuilder(id, displayName, color, IndustrialFluid.Kind.MOLTEN)
                .density(2000)
                .temperature(temperature)
                .viscosity(6000)
                .lightLevel(10);
    }

    public static final class FluidBuilder {
        private final String id;
        private final String displayName;
        private final int color;
        private final IndustrialFluid.Kind kind;
        private int temperature;
        private int density;
        private int viscosity;
        private int lightLevel;

        private FluidBuilder(String id, String displayName, int color, IndustrialFluid.Kind kind) {
            this.id = id;
            this.displayName = displayName;
            this.color = color;
            this.kind = kind;
        }

        public FluidBuilder temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public FluidBuilder density(int density) {
            this.density = density;
            return this;
        }

        public FluidBuilder viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public FluidBuilder lightLevel(int lightLevel) {
            this.lightLevel = lightLevel;
            return this;
        }

        public IndustrialFluid build() {
            return new IndustrialFluid(id, displayName, color, kind, temperature, density, viscosity, lightLevel);
        }
    }
}
