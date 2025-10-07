package com.baizeli.eternisstarrysky.Items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class BJZGItem extends Item {
    public BJZGItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            player.hurt(level.damageSources().magic(), 4500.0f);

            if (player.isAlive()) {
                ItemStack dragonBook = new ItemStack(ModItems.PURE_FRUIT.get());
                if (!player.getInventory().add(dragonBook)) {
                    player.drop(dragonBook, false);
                }
            }
        }

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }
}