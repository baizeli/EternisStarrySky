package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Items.InfinityEternalArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfinityEternalArmorEventHandler {

    @SubscribeEvent
    public static void stopFly(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player player) {
            if (player.getTags().contains("eternisstarrysky.hasSuit") && !InfinityEternalArmorItem.hasFullSet(player) && !player.isCreative()) {
                player.removeTag("eternisstarrysky.hasSuit");
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }
//    @SubscribeEvent
//    public static void onLivingHurt(LivingHurtEvent event) {
//        if (!(event.getEntity() instanceof Player player)) {
//            return;
//        }
//        if (!InfinityEternalArmorItem.hasFullSet(player)) {
//            return;
//        }
//
//        float currentHealth = player.getHealth();
//        float maxHealth = player.getMaxHealth();
//        float healthPercentage = currentHealth / maxHealth;
//
//        if (healthPercentage < 0.7f) {
//            float originalDamage = event.getAmount();
//            float reducedDamage = originalDamage * 0.4f;
//            event.setAmount(reducedDamage);
//
//            showDamageReductionMessage(player, originalDamage, reducedDamage);
//        }
//    }
//
//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        if (event.phase != TickEvent.Phase.END) return;
//
//        Player player = event.player;
//
//        if (InfinityEternalArmorItem.hasFullSet(player)) {
//            if (player.tickCount % 100 == 0) {
//                if (player.getHealth() < player.getMaxHealth()) {
//                    player.heal(1.0f);
//                }
//            }
//        }
//    }
//
//    private static void showDamageReductionMessage(Player player, float originalDamage, float reducedDamage) {
//        if (!player.level().isClientSide()) {
//            Component message = Component.literal("§6§l伤害减免 §r§7(")
//                    .append(Component.literal(String.format("%.1f", originalDamage)).withStyle(ChatFormatting.RED))
//                    .append(Component.literal(" → "))
//                    .append(Component.literal(String.format("%.1f", reducedDamage)).withStyle(ChatFormatting.GREEN))
//                    .append(Component.literal(")"));
//
//            player.displayClientMessage(message, true);
//        }
//    }
}