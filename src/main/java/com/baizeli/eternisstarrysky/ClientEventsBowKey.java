package com.baizeli.eternisstarrysky;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsBowKey {

    public static boolean BowType = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event)
    {
        if (ModKeyBindings.BOW_TYPE_KEY.consumeClick())
        {
            BowType = !BowType;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null)
            {
                String message = BowType ? "已切换：追踪箭矢" : "已切换：箭雨箭矢";
                mc.player.displayClientMessage(Component.literal(message), true);
            }
        }
    }
}