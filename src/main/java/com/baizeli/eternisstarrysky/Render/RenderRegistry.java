package com.baizeli.eternisstarrysky.Render;

import com.baizeli.eternisstarrysky.Entity.ModEntities;
import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderRegistry {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.LIGHTNING_BOLT, PurpleLightningRenderer::new);
        event.registerEntityRenderer(ModEntities.CUSTOM_ARROW.get(), CustomArrowRenderer::new);
    }
}