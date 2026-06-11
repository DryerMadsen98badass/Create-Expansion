package net.mads.createexpansion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mads.createexpansion.machine.MachinePortBlock;
import net.mads.createexpansion.machine.MachinePortBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MachinePortOverlayRenderer implements BlockEntityRenderer<MachinePortBlockEntity> {
    private static final float OFFSET = 0.002F;

    public MachinePortOverlayRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MachinePortBlockEntity port, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation overlay = port.assembledOverlayTexture();
        if (overlay == null || port.controllerPos() == null) {
            return;
        }

        BlockState state = port.getBlockState();
        if (!(state.getBlock() instanceof MachinePortBlock)) {
            return;
        }

        Direction front = state.getValue(MachinePortBlock.FACING);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocation(overlay)));
        PoseStack.Pose pose = poseStack.last();
        for (Direction direction : Direction.values()) {
            renderFace(pose, consumer, direction, front, packedLight);
        }
    }

    private static ResourceLocation textureLocation(ResourceLocation texture) {
        String path = texture.getPath();
        if (path.startsWith("textures/") && path.endsWith(".png")) {
            return texture;
        }
        return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "textures/" + path + ".png");
    }

    private static void renderFace(PoseStack.Pose pose, VertexConsumer consumer, Direction direction, Direction front, int packedLight) {
        boolean isFront = direction == front;

        if (isFront) {
            // Front: Only render the 2 outermost pixel layers (edge border)
            float edge = 2.0F / 16.0F; // 2 pixels = 2/16
            float inner = 1.0F - edge;  // 14/16
            renderEdgeBorder(pose, consumer, direction, edge, inner, packedLight);
        } else {
            // Other sides: Render full overlay
            renderFullFace(pose, consumer, direction, packedLight);
        }
    }

    private static void renderFullFace(PoseStack.Pose pose, VertexConsumer consumer, Direction direction, int packedLight) {
        switch (direction) {
            case DOWN -> {
                float y = -OFFSET;
                vertex(pose, consumer, 0, y, 1, 0, 1, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, 1, 1, 1, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, 0, 1, 0, 0, -1, 0, packedLight);
                vertex(pose, consumer, 0, y, 0, 0, 0, 0, -1, 0, packedLight);
            }
            case UP -> {
                float y = 1 + OFFSET;
                vertex(pose, consumer, 0, y, 0, 0, 1, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, 0, 1, 1, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, 1, 1, 0, 0, 1, 0, packedLight);
                vertex(pose, consumer, 0, y, 1, 0, 0, 0, 1, 0, packedLight);
            }
            case NORTH -> {
                float z = -OFFSET;
                vertex(pose, consumer, 1, 0, z, 0, 1, 0, 0, -1, packedLight);
                vertex(pose, consumer, 0, 0, z, 1, 1, 0, 0, -1, packedLight);
                vertex(pose, consumer, 0, 1, z, 1, 0, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, 1, z, 0, 0, 0, 0, -1, packedLight);
            }
            case SOUTH -> {
                float z = 1 + OFFSET;
                vertex(pose, consumer, 0, 0, z, 0, 1, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, 0, z, 1, 1, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, 1, z, 1, 0, 0, 0, 1, packedLight);
                vertex(pose, consumer, 0, 1, z, 0, 0, 0, 0, 1, packedLight);
            }
            case WEST -> {
                float x = -OFFSET;
                vertex(pose, consumer, x, 0, 0, 0, 1, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, 0, 1, 1, 1, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 1, 1, 0, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 0, 0, 0, -1, 0, 0, packedLight);
            }
            case EAST -> {
                float x = 1 + OFFSET;
                vertex(pose, consumer, x, 0, 1, 0, 1, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, 0, 0, 1, 1, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 0, 1, 0, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 1, 0, 0, 1, 0, 0, packedLight);
            }
        }
    }

    private static void renderEdgeBorder(PoseStack.Pose pose, VertexConsumer consumer, Direction direction, float edge, float inner, int packedLight) {
        switch (direction) {
            case NORTH -> {
                float z = -OFFSET;
                // Top edge (y: inner to 1, x: 0 to 1)
                vertex(pose, consumer, 0, inner, z, 0, edge, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, inner, z, 1, edge, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, 1, z, 1, 0, 0, 0, -1, packedLight);
                vertex(pose, consumer, 0, 1, z, 0, 0, 0, 0, -1, packedLight);
                // Bottom edge (y: 0 to edge, x: 0 to 1)
                vertex(pose, consumer, 0, 0, z, 0, 1, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, 0, z, 1, 1, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, edge, z, 1, inner, 0, 0, -1, packedLight);
                vertex(pose, consumer, 0, edge, z, 0, inner, 0, 0, -1, packedLight);
                // Left edge (x: 0 to edge, y: edge to inner)
                vertex(pose, consumer, 0, edge, z, 0, inner, 0, 0, -1, packedLight);
                vertex(pose, consumer, edge, edge, z, edge, inner, 0, 0, -1, packedLight);
                vertex(pose, consumer, edge, inner, z, edge, edge, 0, 0, -1, packedLight);
                vertex(pose, consumer, 0, inner, z, 0, edge, 0, 0, -1, packedLight);
                // Right edge (x: inner to 1, y: edge to inner)
                vertex(pose, consumer, inner, edge, z, inner, inner, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, edge, z, 1, inner, 0, 0, -1, packedLight);
                vertex(pose, consumer, 1, inner, z, 1, edge, 0, 0, -1, packedLight);
                vertex(pose, consumer, inner, inner, z, inner, edge, 0, 0, -1, packedLight);
            }
            case SOUTH -> {
                float z = 1 + OFFSET;
                // Top edge (y: inner to 1, x: 0 to 1)
                vertex(pose, consumer, 0, 1, z, 0, 0, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, 1, z, 1, 0, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, inner, z, 1, edge, 0, 0, 1, packedLight);
                vertex(pose, consumer, 0, inner, z, 0, edge, 0, 0, 1, packedLight);
                // Bottom edge (y: 0 to edge, x: 0 to 1)
                vertex(pose, consumer, 0, edge, z, 0, inner, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, edge, z, 1, inner, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, 0, z, 1, 1, 0, 0, 1, packedLight);
                vertex(pose, consumer, 0, 0, z, 0, 1, 0, 0, 1, packedLight);
                // Left edge (x: 0 to edge, y: edge to inner)
                vertex(pose, consumer, 0, edge, z, 0, inner, 0, 0, 1, packedLight);
                vertex(pose, consumer, edge, edge, z, edge, inner, 0, 0, 1, packedLight);
                vertex(pose, consumer, edge, inner, z, edge, edge, 0, 0, 1, packedLight);
                vertex(pose, consumer, 0, inner, z, 0, edge, 0, 0, 1, packedLight);
                // Right edge (x: inner to 1, y: edge to inner)
                vertex(pose, consumer, inner, edge, z, inner, inner, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, edge, z, 1, inner, 0, 0, 1, packedLight);
                vertex(pose, consumer, 1, inner, z, 1, edge, 0, 0, 1, packedLight);
                vertex(pose, consumer, inner, inner, z, inner, edge, 0, 0, 1, packedLight);
            }
            case WEST -> {
                float x = -OFFSET;
                // Top edge (y: inner to 1, z: 0 to 1)
                vertex(pose, consumer, x, inner, 0, 0, edge, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 1, 1, edge, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 1, 1, 0, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 0, 0, 0, -1, 0, 0, packedLight);
                // Bottom edge (y: 0 to edge, z: 0 to 1)
                vertex(pose, consumer, x, 0, 0, 0, 1, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, 0, 1, 1, 1, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, 1, 1, inner, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, 0, 0, inner, -1, 0, 0, packedLight);
                // Left edge (z: 0 to edge, y: edge to inner)
                vertex(pose, consumer, x, edge, 0, 0, inner, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, edge, edge, inner, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, edge, edge, edge, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 0, 0, edge, -1, 0, 0, packedLight);
                // Right edge (z: inner to 1, y: edge to inner)
                vertex(pose, consumer, x, edge, inner, inner, inner, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, 1, 1, inner, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 1, 1, edge, -1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, inner, inner, edge, -1, 0, 0, packedLight);
            }
            case EAST -> {
                float x = 1 + OFFSET;
                // Top edge (y: inner to 1, z: 0 to 1)
                vertex(pose, consumer, x, 1, 0, 0, 0, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, 1, 1, 1, 0, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 1, 1, edge, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 0, 0, edge, 1, 0, 0, packedLight);
                // Bottom edge (y: 0 to edge, z: 0 to 1)
                vertex(pose, consumer, x, edge, 0, 0, inner, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, 1, 1, inner, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, 0, 1, 1, 1, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, 0, 0, 0, 1, 1, 0, 0, packedLight);
                // Left edge (z: 0 to edge, y: edge to inner)
                vertex(pose, consumer, x, edge, 0, 0, inner, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, edge, edge, inner, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, edge, edge, edge, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 0, 0, edge, 1, 0, 0, packedLight);
                // Right edge (z: inner to 1, y: edge to inner)
                vertex(pose, consumer, x, edge, inner, inner, inner, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, edge, 1, 1, inner, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, 1, 1, edge, 1, 0, 0, packedLight);
                vertex(pose, consumer, x, inner, inner, inner, edge, 1, 0, 0, packedLight);
            }
            case UP -> {
                float y = 1 + OFFSET;
                // Top edge (z: 0 to edge, x: 0 to 1)
                vertex(pose, consumer, 0, y, 0, 0, 1, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, 0, 1, 1, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, edge, 1, edge, 0, 1, 0, packedLight);
                vertex(pose, consumer, 0, y, edge, 0, edge, 0, 1, 0, packedLight);
                // Bottom edge (z: inner to 1, x: 0 to 1)
                vertex(pose, consumer, 0, y, inner, 0, inner, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, inner, 1, inner, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, 1, 1, 1, 0, 1, 0, packedLight);
                vertex(pose, consumer, 0, y, 1, 0, 1, 0, 1, 0, packedLight);
                // Left edge (x: 0 to edge, z: edge to inner)
                vertex(pose, consumer, 0, y, edge, 0, edge, 0, 1, 0, packedLight);
                vertex(pose, consumer, edge, y, edge, edge, edge, 0, 1, 0, packedLight);
                vertex(pose, consumer, edge, y, inner, edge, inner, 0, 1, 0, packedLight);
                vertex(pose, consumer, 0, y, inner, 0, inner, 0, 1, 0, packedLight);
                // Right edge (x: inner to 1, z: edge to inner)
                vertex(pose, consumer, inner, y, edge, inner, edge, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, edge, 1, edge, 0, 1, 0, packedLight);
                vertex(pose, consumer, 1, y, inner, 1, inner, 0, 1, 0, packedLight);
                vertex(pose, consumer, inner, y, inner, inner, inner, 0, 1, 0, packedLight);
            }
            case DOWN -> {
                float y = -OFFSET;
                // Top edge (z: 0 to edge, x: 0 to 1)
                vertex(pose, consumer, 0, y, edge, 0, edge, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, edge, 1, edge, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, 0, 1, 0, 0, -1, 0, packedLight);
                vertex(pose, consumer, 0, y, 0, 0, 0, 0, -1, 0, packedLight);
                // Bottom edge (z: inner to 1, x: 0 to 1)
                vertex(pose, consumer, 0, y, 1, 0, 1, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, 1, 1, 1, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, inner, 1, inner, 0, -1, 0, packedLight);
                vertex(pose, consumer, 0, y, inner, 0, inner, 0, -1, 0, packedLight);
                // Left edge (x: 0 to edge, z: edge to inner)
                vertex(pose, consumer, 0, y, inner, 0, inner, 0, -1, 0, packedLight);
                vertex(pose, consumer, edge, y, inner, edge, inner, 0, -1, 0, packedLight);
                vertex(pose, consumer, edge, y, edge, edge, edge, 0, -1, 0, packedLight);
                vertex(pose, consumer, 0, y, edge, 0, edge, 0, -1, 0, packedLight);
                // Right edge (x: inner to 1, z: edge to inner)
                vertex(pose, consumer, inner, y, edge, inner, edge, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, edge, 1, edge, 0, -1, 0, packedLight);
                vertex(pose, consumer, 1, y, inner, 1, inner, 0, -1, 0, packedLight);
                vertex(pose, consumer, inner, y, inner, inner, inner, 0, -1, 0, packedLight);
            }
        }
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ, int packedLight) {
        consumer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
