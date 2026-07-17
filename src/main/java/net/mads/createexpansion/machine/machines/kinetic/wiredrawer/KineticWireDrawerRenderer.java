package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;

public class KineticWireDrawerRenderer extends KineticBlockEntityRenderer<KineticWireDrawerBlockEntity> {
    public KineticWireDrawerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(KineticWireDrawerBlockEntity wireDrawer, float partialTicks, PoseStack poseStack,
                              MultiBufferSource buffer, int light, int overlay) {
        BlockState state = wireDrawer.getBlockState();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer shaft = CachedBuffers.partial(CreateExpansionPartialModels.WIRE_DRAWER_SHAFT, state);
        float angle = getAngleForBe(wireDrawer, wireDrawer.getBlockPos(), Axis.Z);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        kineticRotationTransform(shaft, wireDrawer, Axis.X, angle, light)
                .renderInto(poseStack, vertexConsumer);
        poseStack.popPose();
    }
}
