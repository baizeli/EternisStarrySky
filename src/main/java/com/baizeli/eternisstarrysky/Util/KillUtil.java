package com.baizeli.eternisstarrysky.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class KillUtil{
    public static void kill(Entity e, Player scoure){
        if (e instanceof LivingEntity living) {
            if (e.level() instanceof ServerLevel serverLevel) {
                e.remove(Entity.RemovalReason.KILLED);
                serverLevel.entityTickList.remove(e);
                serverLevel.entityManager.knownUuids.remove(e.getUUID());
                serverLevel.entityManager.visibleEntityStorage.remove(e);
                //DoubleUtil.doubledDrop(scoure, living, 1);
            }
        }
    }
}