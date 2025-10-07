package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.RainbowEffectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class InfinityEternalArmorItem extends ArmorItem {

    public InfinityEternalArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return "eternisstarrysky:textures/models/armor/null.png";
        } else {
            return "eternisstarrysky:textures/models/armor/null.png";
        }
    }

    @Override
    public boolean isDamageable(ItemStack stack) { return false; }
    @Override
    public int getMaxDamage(ItemStack stack) { return 0; }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
//        tooltipComponents.add(Component.literal(""));
//        tooltipComponents.add(Component.literal("套装效果:").withStyle(ChatFormatting.GOLD));
//
//        if (level != null && level.isClientSide())
//        {
//            Player player = Minecraft.getInstance().player;
//            if (player != null && hasFullSet(player))
//            {
//                tooltipComponents.add(Component.literal("  当血量低于70%时获得60%伤害减免").withStyle(ChatFormatting.GRAY));
//                tooltipComponents.add(Component.literal("  生存模式飞行").withStyle(ChatFormatting.GRAY));
//                tooltipComponents.add(Component.literal("  移除所有负面效果").withStyle(ChatFormatting.GRAY));
//                tooltipComponents.add(Component.literal("  免疫击退").withStyle(ChatFormatting.GRAY));
//            } else
//            {
//                tooltipComponents.add(Component.literal("  需要穿戴完整套装才能激活").withStyle(ChatFormatting.GRAY));
//            }
//        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 检查玩家是否穿着完整套装
            if (InfinityEternalArmorItem.hasFullSet(player)) {
                //如果没有标签就添加
                if (!player.getTags().contains("eternisstarrysky.hasSuit"))
                    player.addTag("eternisstarrysky.hasSuit");
                // 允许飞行
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }

                // 移除所有负面效果
                removeNegativeEffects(player);

                // 免疫摔落伤害
                player.fallDistance = 0;
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
//        if (entity instanceof Player player && !level.isClientSide) {
//            if (!InfinityEternalArmorItem.hasFullSet(player)) {
//                if (!player.isCreative() && player.getAbilities().mayfly) {
//                    player.getAbilities().mayfly = false;
//                    player.getAbilities().flying = false;
//                    player.onUpdateAbilities();
//                }
//            }
//        }
    }

    private void removeNegativeEffects(Player player) {
        List<MobEffect> effectsToRemove = new ArrayList<>();

        for (MobEffectInstance effectInstance : player.getActiveEffects()) {
            MobEffect effect = effectInstance.getEffect();
            if (!effect.isBeneficial()) {
                effectsToRemove.add(effect);
            }
        }

        for (MobEffect effect : effectsToRemove) {
            player.removeEffect(effect);
        }
    }

    public static boolean hasFullSet(Player player) {
        ItemStack helmet = player.getInventory().getArmor(3);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack boots = player.getInventory().getArmor(0);

        return helmet.getItem().getClass().getName().equals(ModItems.INFINITY_ETERNAL_HELMET.get().getClass().getName()) &&
                chestplate.getItem().getClass().getName().equals(ModItems.INFINITY_ETERNAL_CHESTPLATE.get().getClass().getName()) &&
                leggings.getItem().getClass().getName().equals(ModItems.INFINITY_ETERNAL_LEGGINGS.get().getClass().getName()) &&
                boots.getItem().getClass().getName().equals(ModItems.INFINITY_ETERNAL_BOOTS.get().getClass().getName());
    }
}