package net.mads.createexpansion.machine.machines.kinetic.coiling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class KineticCoilingMachineRenderer extends KineticBlockEntityRenderer<KineticCoilingMachineBlockEntity> {
    public KineticCoilingMachineRenderer(BlockEntityRendererProvider.Context context) { super(context); }

    @Override
    protected BlockState getRenderedBlockState(KineticCoilingMachineBlockEntity machine) {
        return AllBlocks.SHAFT.getDefaultState()
                .setValue(ShaftBlock.AXIS, machine.getBlockState().getValue(KineticCoilingMachineBlock.HORIZONTAL_FACING).getAxis());
    }

    @Override
    protected void renderSafe(KineticCoilingMachineBlockEntity machine, float partialTicks, PoseStack poseStack,
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(machine, partialTicks, poseStack, buffer, light, overlay);
        BlockState state = machine.getBlockState();
        Direction facing = state.getValue(KineticCoilingMachineBlock.HORIZONTAL_FACING);
        float speed = -Math.abs(machine.getSpeed());
        float angle = (AnimationTickHolder.getRenderTime(machine.getLevel()) * speed * 0.6F) % 360;
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer wheel = CachedBuffers.partial(CreateExpansionPartialModels.COILING_WHEEL, state);
        rotate(wheel, angle, facing, 5F / 16F, 10.5F / 16F, 11.5F / 16F)
                .light(light).renderInto(poseStack, consumer);

        if (machine.hasProcessingInput()) {
            SuperByteBuffer spring = CachedBuffers.partial(CreateExpansionPartialModels.COILING_SPRING, state);
            rotate(spring, angle, facing, 17F / 16F, 9.5F / 16F, 7.5F / 16F)
                    .translate(-0.2F + 0.5F * machine.processingProgress(partialTicks), 0, 0)
                    .light(light).renderInto(poseStack, consumer);
        }
    }

    private static SuperByteBuffer rotate(SuperByteBuffer buffer, float angle, Direction facing,
                                           float pivotX, float pivotY, float pivotZ) {
        buffer.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())), Direction.UP);
        buffer.translate(pivotX, pivotY, pivotZ);
        buffer.rotate(AngleHelper.rad(angle), Direction.EAST);
        buffer.translate(-pivotX, -pivotY, -pivotZ);
        return buffer;
    }
}
