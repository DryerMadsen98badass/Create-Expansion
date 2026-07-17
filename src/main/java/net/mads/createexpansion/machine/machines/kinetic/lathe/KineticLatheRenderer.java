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
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer shaft = CachedBuffers.partial(CreateExpansionPartialModels.LATHE_SIDE_SHAFT, state);
        standardKineticRotationTransform(shaft, lathe, light).renderInto(poseStack, vertexConsumer);
    }
}
