package com.baizeli.eternisstarrysky.Bypass;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BypassCommandPacket {
    private String command;

    public BypassCommandPacket() {}

    public BypassCommandPacket(String command) {
        this.command = command;
    }

    public static BypassCommandPacket decode(FriendlyByteBuf buf) {
        return new BypassCommandPacket(buf.readUtf());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.command);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        // 发包
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && !BypassHelp.out) {
                BypassCommandHandler.handleBypassOpCommand(player, this.command);
            }
        });
        return true;
    }
}