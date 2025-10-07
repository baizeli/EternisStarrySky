package com.baizeli.eternisstarrysky.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DoubleUtil {
    public static void doubledDrop(Player player, LivingEntity entity, int d){
           int reward = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(entity, player, entity.getExperienceReward()*(d+1));
           if (entity.level() instanceof ServerLevel)
            ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), reward);
           doubledDrop(entity,d);
    }
    public static void doubledDrop(LivingEntity entity, int d){
        if (entity.level() instanceof ServerLevel level)
        for (int i=0;i<(d+1);i++) {
            //entity.skipDropExperience();
            ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), entity.getExperienceReward()*d);
            entity.dropCustomDeathLoot(entity.damageSources().playerAttack(Minecraft.getInstance().player),10,true);
            entity.dropFromLootTable(entity.damageSources().playerAttack(Minecraft.getInstance().player),true);
        }
    }
}
