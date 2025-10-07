package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Bypass.BypassCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EternisStarrySky.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static int id() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.messageBuilder(BypassCommandPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(BypassCommandPacket::decode)
                .encoder(BypassCommandPacket::encode)
                .consumerMainThread(BypassCommandPacket::handle)
                .add();
    }

    public static void sendToServer(BypassCommandPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}