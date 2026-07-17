package net.mads.createexpansion.machine.machines.kinetic.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class KineticCentrifugeRenderer extends KineticBlockEntityRenderer<KineticCentrifugeBlockEntity> {
    private static final float BASIN_OFFSET = 28 / 16F;

    public KineticCentrifugeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(KineticCentrifugeBlockEntity centrifuge) {
        return true;
    }

    @Override
    protected void renderSafe(KineticCentrifugeBlockEntity centrifuge, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState state = centrifuge.getBlockState();
        VertexConsumer cutout = buffer.getBuffer(RenderType.cutoutMipped());
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        poseStack.translate(0, -0.5F, 0);
        blockRenderer.renderSingleBlock(shaft(Direction.Axis.Y), poseStack, buffer, light, overlay);
        poseStack.popPose();

        SuperByteBuffer rotor = CachedBuffers.partial(CreateExpansionPartialModels.CENTRIFUGE_ROTOR, state);
        standardKineticRotationTransform(rotor, centrifuge, light).renderInto(poseStack, cutout);

        int mountedBasins = centrifuge.mountedBasins();
        if (mountedBasins > 0) {
            renderBasin(centrifuge, state, poseStack, cutout, light, BASIN_OFFSET, 0, mountedBasins, 1);
            renderBasin(centrifuge, state, poseStack, cutout, light, -BASIN_OFFSET, 0, mountedBasins, 2);
            renderBasin(centrifuge, state, poseStack, cutout, light, 0, BASIN_OFFSET, mountedBasins, 3);
            renderBasin(centrifuge, state, poseStack, cutout, light, 0, -BASIN_OFFSET, mountedBasins, 4);
        }
    }

    private void renderBasin(KineticCentrifugeBlockEntity centrifuge, BlockState state, PoseStack poseStack,
                             VertexConsumer vertexConsumer, int light, float xOffset, float zOffset,
                             int mountedBasins, int requiredBasins) {
        if (mountedBasins < requiredBasins) {
            return;
        }

        SuperByteBuffer basin = CachedBuffers.partial(CreateExpansionPartialModels.CENTRIFUGE_BASIN, state);
        standardKineticRotationTransform(basin, centrifuge, light)
                .translate(xOffset, 0, zOffset)
                .renderInto(poseStack, vertexConsumer);
    }
}
