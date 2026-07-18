package net.mads.createexpansion.machine.machines.foundry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mads.createexpansion.material.MaterialLookup;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class FoundryMoldCasterRenderer implements BlockEntityRenderer<FoundryMoldCasterBlockEntity> {
    public FoundryMoldCasterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FoundryMoldCasterBlockEntity caster, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack mold = caster.moldForRender();
        renderStack(mold, caster, poseStack, buffer, packedOverlay, 15.01 / 16.0, moldYRotation(mold));
        renderCastingFluid(caster.castingFluidForRender(), caster.hasCastingVisual(), caster.castingFillForRender(partialTick), caster, poseStack, buffer);
        renderStack(caster.outputForRender(), caster, poseStack, buffer, packedOverlay, 15.06 / 16.0, 180);
    }

    private static float moldYRotation(ItemStack stack) {
        MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
        if (target == null) {
            return 0;
        }
        return switch (target.part()) {
            case CAST_SCREW_MOLD, HOT_CAST_SCREW_MOLD -> 180;
            case CAST_ROTOR_MOLD, HOT_CAST_ROTOR_MOLD -> 90;
            default -> 0;
        };
    }

    private void renderStack(ItemStack stack, FoundryMoldCasterBlockEntity caster, PoseStack poseStack, MultiBufferSource buffer, int packedOverlay, double y, float yRotation) {
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, y, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(14.0F / 16.0F, 14.0F / 16.0F, 14.0F / 16.0F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                LightTexture.FULL_BRIGHT,
                packedOverlay,
                poseStack,
                buffer,
                caster.getLevel(),
                0
        );
        poseStack.popPose();
    }

    private void renderCastingFluid(FluidStack fluid, boolean active, float fillProgress, FoundryMoldCasterBlockEntity caster, PoseStack poseStack, MultiBufferSource buffer) {
        if (!active || fluid.isEmpty() || fillProgress <= 0) {
            return;
        }

        boolean pushed = false;
        try {
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(extensions.getStillTexture());
            int color = extensions.getTintColor();
            int red = color >> 16 & 0xFF;
            int green = color >> 8 & 0xFF;
            int blue = color & 0xFF;
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));

            poseStack.pushPose();
            pushed = true;
            Matrix4f matrix = poseStack.last().pose();
            float halfSize = (12.0F / 16.0F) * Math.min(1.0F, fillProgress) * 0.5F;
            float min = 0.5F - halfSize;
            float max = 0.5F + halfSize;
            float y = 15.04F / 16.0F;
            quad(consumer, matrix, min, y, min, max, y, max, sprite, red, green, blue);
            if (hasPouringDrainAbove(caster)) {
                poseStack.translate(0, 1, 0);
                matrix = poseStack.last().pose();
                stream(consumer, matrix, sprite, red, green, blue, streamDirection(caster));
            }
            poseStack.popPose();
            pushed = false;
        } catch (RuntimeException ignored) {
            if (pushed) {
                poseStack.popPose();
            }
        }
    }

    private static boolean hasPouringDrainAbove(FoundryMoldCasterBlockEntity caster) {
        if (caster.getLevel() == null) {
            return false;
        }

        BlockState stateAbove = caster.getLevel().getBlockState(caster.getBlockPos().above());
        return stateAbove.getBlock() instanceof FoundryDrainBlock
                && stateAbove.hasProperty(FoundryDrainBlock.POURING)
                && stateAbove.getValue(FoundryDrainBlock.POURING);
    }

    private static Direction streamDirection(FoundryMoldCasterBlockEntity caster) {
        if (caster.getLevel() == null) {
            return Direction.SOUTH;
        }

        BlockState stateAbove = caster.getLevel().getBlockState(caster.getBlockPos().above());
        if (stateAbove.getBlock() instanceof FoundryDrainBlock && stateAbove.hasProperty(FoundryDrainBlock.FACING)) {
            return stateAbove.getValue(FoundryDrainBlock.FACING).getOpposite();
        }
        return Direction.SOUTH;
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, float minX, float y, float minZ, float maxX, float maxY, float maxZ,
                             TextureAtlasSprite sprite, int red, int green, int blue) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        vertex(consumer, matrix, minX, y, minZ, u0, v0, red, green, blue);
        vertex(consumer, matrix, minX, y, maxZ, u0, v1, red, green, blue);
        vertex(consumer, matrix, maxX, maxY, maxZ, u1, v1, red, green, blue);
        vertex(consumer, matrix, maxX, maxY, minZ, u1, v0, red, green, blue);
    }

    private static void stream(VertexConsumer consumer, Matrix4f matrix, TextureAtlasSprite sprite, int red, int green, int blue, Direction direction) {
        modelBox(consumer, matrix, 12, 5, 7, 16, 6, 9, direction, sprite, red, green, blue,
                new float[]{0, 1, 4, 2},
                new float[]{0, 1, 2, 2},
                new float[]{0, 1, 4, 2},
                new float[]{0, 1, 2, 2},
                new float[]{0, 0, 4, 2},
                new float[]{0, 0, 4, 2});
        modelBox(consumer, matrix, 12, -1, 7, 13, 5, 9, direction, sprite, red, green, blue,
                new float[]{0, 1, 1, 7},
                new float[]{0, 1, 2, 7},
                new float[]{3, 1, 4, 7},
                new float[]{0, 1, 2, 7},
                new float[]{3, 0, 4, 2},
                new float[]{3, 0, 4, 2});
    }

    private static void modelBox(VertexConsumer consumer, Matrix4f matrix, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                 Direction direction, TextureAtlasSprite sprite, int red, int green, int blue,
                                 float[] northUv, float[] eastUv, float[] southUv, float[] westUv, float[] upUv, float[] downUv) {
        modelFace(consumer, matrix, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, direction, sprite, northUv, red, green, blue);
        modelFace(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ, minX, maxY, maxZ, maxX, maxY, maxZ, direction, sprite, southUv, red, green, blue);
        modelFace(consumer, matrix, minX, minY, maxZ, minX, minY, minZ, minX, maxY, minZ, minX, maxY, maxZ, direction, sprite, westUv, red, green, blue);
        modelFace(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, direction, sprite, eastUv, red, green, blue);
        modelFace(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ, direction, sprite, upUv, red, green, blue);
        modelFace(consumer, matrix, minX, minY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, minX, minY, minZ, direction, sprite, downUv, red, green, blue);
    }

    private static void modelFace(VertexConsumer consumer, Matrix4f matrix,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float x3, float y3, float z3,
                                  float x4, float y4, float z4,
                                  Direction direction, TextureAtlasSprite sprite, float[] uv, int red, int green, int blue) {
        float u0 = u(sprite, uv[0]);
        float v0 = v(sprite, uv[1]);
        float u1 = u(sprite, uv[2]);
        float v1 = v(sprite, uv[3]);
        modelVertex(consumer, matrix, x1, y1, z1, direction, u0, v1, red, green, blue);
        modelVertex(consumer, matrix, x2, y2, z2, direction, u1, v1, red, green, blue);
        modelVertex(consumer, matrix, x3, y3, z3, direction, u1, v0, red, green, blue);
        modelVertex(consumer, matrix, x4, y4, z4, direction, u0, v0, red, green, blue);
    }

    private static float u(TextureAtlasSprite sprite, float pixel) {
        return sprite.getU0() + (sprite.getU1() - sprite.getU0()) * pixel / 16.0F;
    }

    private static float v(TextureAtlasSprite sprite, float pixel) {
        return sprite.getV0() + (sprite.getV1() - sprite.getV0()) * pixel / 16.0F;
    }

    private static void modelVertex(VertexConsumer consumer, Matrix4f matrix, float pixelX, float pixelY, float pixelZ, Direction direction,
                                    float u, float v, int red, int green, int blue) {
        float x = pixelX / 16.0F;
        float y = pixelY / 16.0F;
        float z = pixelZ / 16.0F;
        float centeredX = x - 0.5F;
        float centeredZ = z - 0.5F;
        float rotatedX = switch (direction) {
            case SOUTH -> -centeredZ;
            case WEST -> -centeredX;
            case NORTH -> centeredZ;
            default -> centeredX;
        };
        float rotatedZ = switch (direction) {
            case SOUTH -> centeredX;
            case WEST -> -centeredZ;
            case NORTH -> -centeredX;
            default -> centeredZ;
        };
        vertex(consumer, matrix, rotatedX + 0.5F, y, rotatedZ + 0.5F, u, v, red, green, blue);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float u, float v, int red, int green, int blue) {
        consumer.addVertex(matrix, x, y, z)
                .setColor(red, green, blue, 220)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0, 1, 0);
    }
}
