package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.ClientEventsBowKey;
import com.baizeli.eternisstarrysky.Configuration;
import com.baizeli.eternisstarrysky.Entity.CustomArrowEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class NewBowItem extends BowItem {

    @Override public Predicate<ItemStack> getAllSupportedProjectiles() {return (stack) -> stack.getItem() instanceof ArrowItem;}
    public NewBowItem(Properties properties) {super(properties);}

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int timeCharged) {
        if (shooter instanceof Player player) {
            boolean hasInfiniteArrows = player.getAbilities().instabuild ||
                    EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;

            ItemStack arrowStack = player.getProjectile(stack);
            if (arrowStack.isEmpty() && !hasInfiniteArrows) {
                return;
            }

            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(Items.ARROW); // 默认使用自定义箭
            }

            if (!level.isClientSide) {
                ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);

                CustomArrowEntity arrow = new CustomArrowEntity(level, player);
                arrow.setBaseDamage(Configuration.WHISPER_OF_THE_PAST_DAMAGE.get());
                if (ClientEventsBowKey.BowType)
                {
                    arrow.setTrack(true);
                }
                else
                {
                    arrow.setSpawn(true);
                }

                int drawTime = this.getUseDuration(stack) - timeCharged;
                float power = getPowerForTime(drawTime);

                if (power >= 0.1D) {

                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
                    arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

                    if (power == 1.0F) arrow.setCritArrow(true);

                    int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                    if (powerLevel > 0) arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);

                    int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                    if (punchLevel > 0) arrow.setKnockback(punchLevel);
                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) arrow.setSecondsOnFire(100);

                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

                    if (hasInfiniteArrows && (arrowStack.getItem() == Items.ARROW || arrowStack.getItem() == Items.ARROW)) {
                        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    level.addFreshEntity(arrow);
                }

                if (!hasInfiniteArrows && !player.getAbilities().instabuild) {
                    arrowStack.shrink(1);
                    if (arrowStack.isEmpty()) {
                        player.getInventory().removeItem(arrowStack);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }
}
