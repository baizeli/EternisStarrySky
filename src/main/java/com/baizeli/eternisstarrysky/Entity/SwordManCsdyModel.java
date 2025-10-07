package com.baizeli.eternisstarrysky.Entity;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwordManCsdyModel extends GeoModel<SwordManCsdy> {
    private final ResourceLocation model = new ResourceLocation(EternisStarrySky.MODID, "geo/sword_man_csdy.geo.json");
    private final ResourceLocation texture = new ResourceLocation(EternisStarrySky.MODID, "textures/entity/sword_man_csdy.png");
    private final ResourceLocation animations = new ResourceLocation(EternisStarrySky.MODID, "animations/sword_man_csdy.animation.json");

    @Override
    public ResourceLocation getModelResource(SwordManCsdy swordManCsdy) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(SwordManCsdy swordManCsdy) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SwordManCsdy swordManCsdy) {
        return animations;
    }
}