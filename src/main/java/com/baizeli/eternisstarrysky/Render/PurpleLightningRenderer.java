package com.baizeli.eternisstarrysky.Render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class PurpleLightningRenderer extends EntityRenderer<LightningBolt> {
    public boolean useRainbow = true;
    private final LightningBoltRenderer vanillaRenderer;

    public PurpleLightningRenderer(EntityRendererProvider.Context p_174286_) {
        super(p_174286_);
        this.vanillaRenderer = new LightningBoltRenderer(p_174286_);
    }

    @Override
    public void render(LightningBolt lightning, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // 检查ID是否在指定范围内
        if (lightning.getId() > 200000000 - 1) LightningRainbow(lightning, entityYaw, partialTicks, poseStack, buffer, packedLight);
        else vanillaRenderer.render(lightning, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void LightningRainbow(LightningBolt lightning, float partialTicks, float something, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
    {
        float[] afloat = new float[8];
        float[] afloat1 = new float[8];
        float f = 0.0F;
        float f1 = 0.0F;
        RandomSource randomsource = RandomSource.create(lightning.seed);

        for(int i = 7; i >= 0; --i) {
            afloat[i] = f;
            afloat1[i] = f1;
            f += (float)(randomsource.nextInt(11) - 5);
            f1 += (float)(randomsource.nextInt(11) - 5);
        }

        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = poseStack.last().pose();

        RandomSource random = RandomSource.create(lightning.seed);

        // 获取世界时间用于动态变化
        long worldTime = lightning.level().getGameTime();
        float timeOffset = (worldTime + partialTicks) * 0.1F;

        for(int j = 0; j < 4; ++j) {
            RandomSource randomsource1 = RandomSource.create(lightning.seed);

            for(int k = 0; k < 3; ++k) {
                int l = 7;
                int i1 = 0;
                if (k > 0) {
                    l = 7 - k;
                }

                if (k > 0) {
                    i1 = l - 2;
                }

                float f2 = afloat[l] - f;
                float f3 = afloat1[l] - f1;

                for(int j1 = l; j1 >= i1; --j1) {
                    float f4 = f2;
                    float f5 = f3;
                    if (k == 0) {
                        f2 += (float)(randomsource1.nextInt(11) - 5);
                        f3 += (float)(randomsource1.nextInt(11) - 5);
                    } else {
                        f2 += (float)(randomsource1.nextInt(31) - 15);
                        f3 += (float)(randomsource1.nextInt(31) - 15);
                    }

                    float f10 = 0.1F + (float)j * 0.2F;
                    if (k == 0) {
                        f10 *= (float)j1 * 0.1F + 1.0F;
                    }

                    float f11 = 0.1F + (float)j * 0.2F;
                    if (k == 0) {
                        f11 *= ((float)j1 - 1.0F) * 0.1F + 1.0F;
                    }

                    // 计算当前段的高度比例和颜色
                    float heightRatio = (float)j1 / 7.0F;
                    float[] auroraColor = getAuroraColor(heightRatio, timeOffset, lightning.seed + j1);

                    // 使用修正后的quad函数，保持原参数顺序
                    quadGradient(matrix4f, vertexconsumer, f2, f3, j1, f4, f5,
                            auroraColor[0], auroraColor[1], auroraColor[2],
                            f11, f10, heightRatio, timeOffset, lightning.seed + j1,
                            false, false, true, false);
                    quadGradient(matrix4f, vertexconsumer, f2, f3, j1, f4, f5,
                            auroraColor[0], auroraColor[1], auroraColor[2],
                            f11, f10, heightRatio, timeOffset, lightning.seed + j1,
                            true, false, true, true);
                    quadGradient(matrix4f, vertexconsumer, f2, f3, j1, f4, f5,
                            auroraColor[0], auroraColor[1], auroraColor[2],
                            f11, f10, heightRatio, timeOffset, lightning.seed + j1,
                            true, true, false, true);
                    quadGradient(matrix4f, vertexconsumer, f2, f3, j1, f4, f5,
                            auroraColor[0], auroraColor[1], auroraColor[2],
                            f11, f10, heightRatio, timeOffset, lightning.seed + j1,
                            false, true, false, false);
                }
            }
        }
    }

    private static void quadGradient(Matrix4f matrix, VertexConsumer buffer,
                                     float x1, float z1, int y1, float x2, float z2,
                                     float r, float g, float b,
                                     float width2, float width1,
                                     float heightRatio, float timeOffset, long seed,
                                     boolean p1, boolean p2, boolean p3, boolean p4) {
        float[] bottomColor = getAuroraColor(heightRatio, timeOffset, seed);
        float[] topColor = getAuroraColor(heightRatio + 0.1F, timeOffset, seed + 1);

        buffer.vertex(matrix, x1 + (p1 ? width1 : -width1), (float)(y1 * 16), z1 + (p2 ? width1 : -width1)).color(bottomColor[0], bottomColor[1], bottomColor[2], 0.3F).endVertex();
        buffer.vertex(matrix, x2 + (p1 ? width2 : -width2), (float)((y1 + 1) * 16), z2 + (p2 ? width2 : -width2)).color(topColor[0], topColor[1], topColor[2], 0.3F).endVertex();
        buffer.vertex(matrix, x2 + (p3 ? width2 : -width2), (float)((y1 + 1) * 16), z2 + (p4 ? width2 : -width2)).color(topColor[0], topColor[1], topColor[2], 0.3F).endVertex();
        buffer.vertex(matrix, x1 + (p3 ? width1 : -width1), (float)(y1 * 16), z1 + (p4 ? width1 : -width1)).color(bottomColor[0], bottomColor[1], bottomColor[2], 0.3F).endVertex();
    }

    private static float[] getAuroraColor(float heightRatio, float timeOffset, long seed) {
        RandomSource random = RandomSource.create(seed);

        float baseHue = (heightRatio * 0.8F + timeOffset * 0.5F + random.nextFloat() * 0.3F) % 1.0F;
        float hue;

        if (baseHue < 0.4F) hue = 0.33F + baseHue * 0.17F;
        else if (baseHue < 0.7F) hue = 0.55F + (baseHue - 0.4F) * 0.17F;
        else hue = 0.72F + (baseHue - 0.7F) * 0.17F;

        float saturation = 0.6F + heightRatio * 0.4F;
        float brightness = 0.5F + heightRatio * 0.5F;

        return hsvToRgb(hue, saturation, brightness);
    }

    private static float[] hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = v - c;

        float r, g, b;

        if (h < 1/6f) {
            r = c; g = x; b = 0;
        } else if (h < 2/6f) {
            r = x; g = c; b = 0;
        } else if (h < 3/6f) {
            r = 0; g = c; b = x;
        } else if (h < 4/6f) {
            r = 0; g = x; b = c;
        } else if (h < 5/6f) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }

        return new float[]{r + m, g + m, b + m};
    }

    @Override
    public ResourceLocation getTextureLocation(LightningBolt p_115264_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
