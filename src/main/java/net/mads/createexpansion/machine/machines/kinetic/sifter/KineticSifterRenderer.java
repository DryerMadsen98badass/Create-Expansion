package net.mads.createexpansion.machine.machines.kinetic.sifter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class KineticSifterRenderer extends KineticBlockEntityRenderer<KineticSifterBlockEntity> {
    public KineticSifterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(KineticSifterBlockEntity sifter) {
        return true;
    }

    @Override
    protected void renderSafe(KineticSifterBlockEntity sifter, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState state = sifter.getBlockState();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer cogwheel = CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, state);
        standardKineticRotationTransform(cogwheel, sifter, light).renderInto(poseStack, vertexConsumer);
    }
}
