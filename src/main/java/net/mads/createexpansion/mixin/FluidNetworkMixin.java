package net.mads.createexpansion.mixin;

import com.simibubi.create.content.fluids.FluidNetwork;
import net.createmod.catnip.math.BlockFace;
import net.mads.createexpansion.transport.FluidTransportRates;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(FluidNetwork.class)
public abstract class FluidNetworkMixin {
    @Shadow Level world;
    @Shadow BlockFace start;
    @Shadow int transferSpeed;
    @Shadow Set<BlockPos> visited;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/content/fluids/FluidNetwork;transferSpeed:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int createExpansion$limitTransferSpeedToWeakestPipe(FluidNetwork network) {
        int pipeLimit = createExpansion$pipeRateAt(start == null ? null : start.getPos());

        if (start != null) {
            pipeLimit = Math.min(pipeLimit, createExpansion$pipeRateAt(start.getConnectedPos()));
        }

        if (visited != null) {
            for (BlockPos pos : visited) {
                pipeLimit = Math.min(pipeLimit, createExpansion$pipeRateAt(pos));
            }
        }

        return pipeLimit == Integer.MAX_VALUE
                ? transferSpeed
                : Math.min(transferSpeed, pipeLimit);
    }

    @Unique
    private int createExpansion$pipeRateAt(BlockPos pos) {
        if (world == null || pos == null || !world.isLoaded(pos)) {
            return Integer.MAX_VALUE;
        }

        BlockState state = world.getBlockState(pos);
        return FluidTransportRates.isPipe(state)
                ? FluidTransportRates.pipeRate(state)
                : Integer.MAX_VALUE;
    }
}
