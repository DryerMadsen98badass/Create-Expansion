package net.mads.createexpansion.machine.machines.kinetic.lathe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;

public class KineticLatheRenderer extends KineticBlockEntityRenderer<KineticLatheBlockEntity> {
    public KineticLatheRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(KineticLatheBlockEntity lathe) {
        return true;
    }

    @Override
    protected void renderSafe(KineticLatheBlockEntity lathe, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState state = lathe.getBlockState();
        Direction facing = state.getValue(KineticLatheBlock.FACING);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer shaft = CachedBuffers.partial(CreateExpansionPartialModels.LATHE_SIDE_SHAFT, state);
        float angle = getAngleForBe(lathe, lathe.getBlockPos(), facing.getAxis());
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(shaftModelYRotation(facing)));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        kineticRotationTransform(shaft, lathe, Axis.X, angle, light).renderInto(poseStack, vertexConsumer);
        poseStack.popPose();
    }

    private static float shaftModelYRotation(Direction facing) {
        return switch (facing) {
            case WEST -> 180;
            case NORTH -> 90;
            case SOUTH -> -90;
            default -> 0;
        };
    }
}
