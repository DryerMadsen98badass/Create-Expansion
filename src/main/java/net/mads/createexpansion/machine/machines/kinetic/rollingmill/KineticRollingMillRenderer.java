package net.mads.createexpansion.machine.machines.kinetic.rollingmill;

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

public class KineticRollingMillRenderer extends KineticBlockEntityRenderer<KineticRollingMillBlockEntity> {
    public KineticRollingMillRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(KineticRollingMillBlockEntity rollingMill, float partialTicks, PoseStack poseStack,
                              MultiBufferSource buffer, int light, int overlay) {
        BlockState state = rollingMill.getBlockState();
        Direction facing = state.getValue(KineticRollingMillBlock.FACING);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutoutMipped());
        float angle = getAngleForBe(rollingMill, rollingMill.getBlockPos(), facing.getAxis());
        float yRotation = modelYRotation(facing);

        SuperByteBuffer rotor1 = CachedBuffers.partial(CreateExpansionPartialModels.ROLLING_MILL_ROTOR_1, state);
        poseStack.pushPose();
        rotateModel(poseStack, yRotation);
        poseStack.translate(0, 8.414213F / 16F, 8 / 16F);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotation(angle));
        poseStack.translate(0, -8.414213F / 16F, -8 / 16F);
        rotor1.light(light).renderInto(poseStack, vertexConsumer);
        poseStack.popPose();

        SuperByteBuffer rotor2 = CachedBuffers.partial(CreateExpansionPartialModels.ROLLING_MILL_ROTOR_2, state);
        poseStack.pushPose();
        rotateModel(poseStack, yRotation);
        poseStack.translate(0, 11 / 16F, 8.114213F / 16F);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotation(-angle));
        poseStack.translate(0, -11 / 16F, -8.114213F / 16F);
        rotor2.light(light).renderInto(poseStack, vertexConsumer);
        poseStack.popPose();
    }

    private static void rotateModel(PoseStack poseStack, float degrees) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(degrees));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }

    private static float modelYRotation(Direction facing) {
        return switch (facing) {
            case SOUTH -> 90;
            case WEST -> 180;
            case NORTH -> 270;
            default -> 0;
        };
    }
}
