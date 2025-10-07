package com.baizeli.eternisstarrysky.Entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SwordManCsdyRenderer extends GeoEntityRenderer<SwordManCsdy> {

    public SwordManCsdyRenderer(EntityRendererProvider.Context context) {
        super(context, new SwordManCsdyModel());
    }
}