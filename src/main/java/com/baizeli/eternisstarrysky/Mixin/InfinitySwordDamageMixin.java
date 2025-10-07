package com.baizeli.eternisstarrysky.Mixin;

import com.baizeli.eternisstarrysky.Items.AvaritiaSword;
import com.baizeli.eternisstarrysky.Items.InfinitySwordTrue;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class InfinitySwordDamageMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDamageAmount(float amount, DamageSource damageSource) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof Player player)
        {
            if (isBlockingWithInfinitySword(player))
            {
                return 0.0f;
            }
        }

        return amount;
    }

    private boolean isBlockingWithInfinitySword(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }

        ItemStack usingItem = player.getUseItem();
        return (usingItem.getItem() instanceof AvaritiaSword &&
                usingItem.getUseAnimation() == UseAnim.BLOCK) | (usingItem.getItem() instanceof InfinitySwordTrue &&
                usingItem.getUseAnimation() == UseAnim.BLOCK);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE",
            target = "net/minecraft/world/entity/LivingEntity.actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"),
            cancellable = true)
    private void onActuallyHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof Player player)
        {
            if (isBlockingWithInfinitySword(player))
            {
                addBlockEffect(player, damageSource);
            }
        }
    }

    private void addBlockEffect(Player player, DamageSource damageSource) {
        Level level = player.level();
        if (!level.isClientSide)
        {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.6F, 2.0F);
        }
    }
}
