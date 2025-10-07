package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Items.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BowClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        AnimationConfig.loadConfig();

        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.WHISPER_OF_THE_PAST.get(),
                    ResourceLocation.parse("pull"), (stack, level, entity, seed) -> {
                        if (entity == null) {
                            return 0.0F;
                        } else {
                            return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                        }
                    });

            ItemProperties.register(ModItems.WHISPER_OF_THE_PAST.get(),
                    ResourceLocation.parse("pulling"), (stack, level, entity, seed) -> {
                        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                    });
        });
    }
}
