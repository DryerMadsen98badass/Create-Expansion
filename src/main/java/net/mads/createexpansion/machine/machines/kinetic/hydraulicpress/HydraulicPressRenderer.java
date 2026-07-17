package net.mads.createexpansion.machine.machines.kinetic.hydraulicpress;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class HydraulicPressRenderer implements BlockEntityRenderer<HydraulicPressBlockEntity> {
    public HydraulicPressRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(HydraulicPressBlockEntity press, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        SuperByteBuffer head = CachedBuffers.partial(CreateExpansionPartialModels.HYDRAULIC_PRESS_HEAD,
                press.getBlockState());
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutoutMipped());
        head.translate(0, -press.getRenderedHeadOffset(partialTick), 0)
                .light(packedLight)
                .renderInto(poseStack, consumer);
    }

    @Override
    public boolean shouldRenderOffScreen(HydraulicPressBlockEntity press) {
        return true;
    }
}
