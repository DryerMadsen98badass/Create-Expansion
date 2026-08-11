package net.mads.createexpansion.machine.machines.kinetic.singleblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.client.MachineControlScheduleOverlayRenderer;
import net.mads.createexpansion.machine.SingleBlockMachineBlock;
import net.mads.createexpansion.machine.SingleBlockMachineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class KineticSingleBlockMachineRenderer
        extends KineticBlockEntityRenderer<SingleBlockMachineBlockEntity> {

    public KineticSingleBlockMachineRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        super(context);
    }

    @Override
    protected void renderSafe(
            SingleBlockMachineBlockEntity machine,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        MachineControlScheduleOverlayRenderer.render(
                machine.machineControlScheduleSides(),
                poseStack,
                buffer,
                light
        );

        BlockState state = machine.getBlockState();
        if (!(state.getBlock() instanceof SingleBlockMachineBlock block)
                || block.instance() == null
                || !block.instance().definition().usesKinetic()) {
            return;
        }

        Direction input = block.kineticDirection(state);
        if (input == null) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer shaft = CachedBuffers.partial(
                CreateExpansionPartialModels.LATHE_SIDE_SHAFT,
                state
        );

        float angle = getAngleForBe(
                machine,
                machine.getBlockPos(),
                input.getAxis()
        );

        if (input.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            angle = -angle;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        rotateWestFacingModel(poseStack, input);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        kineticRotationTransform(
                shaft,
                machine,
                Direction.Axis.X,
                angle,
                light
        ).renderInto(poseStack, consumer);
        poseStack.popPose();
    }

    /**
     * lathe_side_shaft.json occupies x=0..2 and therefore points out through
     * the west face before any transformation.
     */
    private static void rotateWestFacingModel(
            PoseStack poseStack,
            Direction direction
    ) {
        switch (direction) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case UP -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            case DOWN -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            case WEST -> {
            }
        }
    }
}
