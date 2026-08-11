package net.mads.createexpansion.transport;

import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class FluidTransportRates {
    /** Create's unmodified formula is RPM / 2, or 0.5 mB per RPM per tick. */
    private static final double VANILLA_CREATE_PUMP_RATE = 0.5D;

    private FluidTransportRates() {
    }

    public static double pumpRate(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof TieredFluidPump pump) {
            return pump.transportTier().pumpRate();
        }
        if (block instanceof PumpBlock) {
            return FluidTransportTiers.CREATE_PUMP_RATE;
        }
        return 0.0D;
    }

    public static float scalePumpPressure(PumpBlockEntity pump, float vanillaPressure) {
        double configuredRate = pumpRate(pump.getBlockState());
        if (configuredRate <= 0.0D) {
            return vanillaPressure;
        }

        double scaled = vanillaPressure * configuredRate / VANILLA_CREATE_PUMP_RATE;
        if (!Double.isFinite(scaled)) {
            return Float.MAX_VALUE;
        }
        return (float) Math.min(Float.MAX_VALUE, scaled);
    }

    public static int pipeRate(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof TieredFluidPipe pipe) {
            return pipe.transportTier().maximumPipeRate();
        }
        if (block instanceof FluidPipeBlock || block instanceof GlassFluidPipeBlock) {
            return FluidTransportTiers.CREATE_PIPE_RATE;
        }
        return Integer.MAX_VALUE;
    }

    public static boolean isOpenAt(BlockState state, Direction direction) {
        Block block = state.getBlock();
        if (block instanceof FluidPipeBlock) {
            return FluidPipeBlock.isOpenAt(state, direction);
        }
        if (block instanceof GlassFluidPipeBlock) {
            return AxisPipeBlock.isOpenAt(state, direction);
        }
        if (block instanceof PumpBlock) {
            return PumpBlock.isOpenAt(state, direction);
        }
        return false;
    }

    public static boolean isPipe(BlockState state) {
        Block block = state.getBlock();
        return block instanceof FluidPipeBlock || block instanceof GlassFluidPipeBlock;
    }

    public static boolean isPump(BlockState state) {
        return state.getBlock() instanceof PumpBlock;
    }

    public static boolean isPipeOrPump(BlockState state) {
        return isPipe(state) || isPump(state);
    }
}
