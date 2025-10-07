package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.Configuration;
import com.baizeli.eternisstarrysky.config.ConfigEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class EternisAppleItem extends Item
{
    private static final int COOLDOWN_TICKS = 45 * 20;
    public EternisAppleItem(Properties properties) { super(properties); }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.EAT; }
    @Override public int getUseDuration(ItemStack stack) { return 32; }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player)
        {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            FoodData foodData = player.getFoodData();
            foodData.setFoodLevel(20);
            foodData.setSaturation(2000.0f);

            // MobEffect value = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse("minecraft:speed"));
            /*
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 9));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 3));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 9));
            */
            for (ConfigEffect effect : Configuration.ETERNIS_APPLE_EFFECTS.get())
            {
                MobEffect eff = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse(effect.key));
                if (eff != null)
                    player.addEffect(new MobEffectInstance(eff, effect.duration, effect.amplifier));
            }
        }
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemstack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }
}