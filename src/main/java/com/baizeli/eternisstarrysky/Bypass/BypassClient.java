package com.baizeli.eternisstarrysky.Bypass;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import com.baizeli.eternisstarrysky.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID, value = Dist.CLIENT)
public class BypassClient {

    @SubscribeEvent
    public static void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();
        if (BypassHelp.out) return;
        if (message.startsWith("\\bypassop")) {
            event.setCanceled(true);
            NetworkHandler.sendToServer(new BypassCommandPacket(message));

            if (net.minecraft.client.Minecraft.getInstance().player != null) {
                net.minecraft.client.Minecraft.getInstance().player.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("§7正在执行......")
                );
            }
        }
    }
}