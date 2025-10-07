package com.baizeli.eternisstarrysky.Render;

import com.baizeli.eternisstarrysky.Entity.CustomArrowEntity;
import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomArrowRenderer extends ArrowRenderer<CustomArrowEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EternisStarrySky.MOD_ID, "textures/arrow.png");
    public CustomArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public ResourceLocation getTextureLocation(CustomArrowEntity arrow) {
        return TEXTURE;
    }
}
