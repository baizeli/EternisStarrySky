package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Items.InfinityEternalArmorItem;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID)
public class ArmorEventHandler {

//    @SubscribeEvent
//    public static void onLivingKnockBack(LivingKnockBackEvent event) {
//        if (event.getEntity() instanceof Player player) {
//
//            if (InfinityEternalArmorItem.hasFullSet(player)) {
//
//                event.setCanceled(true);
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public static void onLivingHurt(LivingHurtEvent event) {
//        if (event.getEntity() instanceof Player player) {
//            if (InfinityEternalArmorItem.hasFullSet(player)) {
//
//                if (player.getHealth() / player.getMaxHealth() < 0.7f) {
//                    event.setAmount(event.getAmount() * 0.4f);
//                }
//            }
//        }
//    }
}