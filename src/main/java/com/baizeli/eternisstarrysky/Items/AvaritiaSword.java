package com.baizeli.eternisstarrysky.Items;

import com.baizeli.Sounds;
import com.baizeli.eternisstarrysky.AvaritiaKill;
import com.baizeli.eternisstarrysky.AvaritiaLivingEntity;
import com.baizeli.eternisstarrysky.AvaritiaVulnerable;
import com.baizeli.eternisstarrysky.Util.TextUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AvaritiaSword extends SwordItem
{
    public AvaritiaSword(int p_43270_, float p_43271_, Properties p_43272_) {
        super(new Tier() {
            @Override
            public int getUses() {
                return 0;
            }

            @Override
            public float getSpeed() {
                return 1.6F;
            }

            @Override
            public float getAttackDamageBonus() {
                return Float.POSITIVE_INFINITY;
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public int getEnchantmentValue() {
                return 100;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return null;
            }
        }, p_43270_, p_43271_, p_43272_);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return ImmutableMultimap.of();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level p_41428_, LivingEntity p_41429_, ItemStack p_41430_, int p_41431_) {
        if (p_41429_ instanceof AvaritiaVulnerable avaritia)
            avaritia.vulnerable(false);
    }

    @Override
    public void releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
        if (p_41414_ instanceof AvaritiaVulnerable avaritia)
            avaritia.vulnerable(true);
        super.releaseUsing(p_41412_, p_41413_, p_41414_, p_41415_);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BLOCK;
    }
    @Override
    public int getUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
        if (!(player instanceof ServerPlayer))
        {
            Sounds.play(SoundEvents.AMETHYST_BLOCK_STEP, player, 10.0F, 1.0F);
            return false;
        }
        if (!(entity instanceof LivingEntity living))
            return false;

        DamageSource source = new DamageSource(player.damageSources().genericKill().typeHolder(), player);
        // living.die(new DamageSource(entity.damageSources().genericKill().typeHolder(), player));
        living.getCombatTracker().recordDamage(source, Float.POSITIVE_INFINITY);
        living.setHealth(0);
        living.gameEvent(GameEvent.ENTITY_DAMAGE);
        living.setLastHurtByMob(player);
        living.lastHurtByPlayerTime = 100;
        living.lastHurtByPlayer = player;
        if (!living.checkTotemDeathProtection(source))
        {
            SoundEvent sound = living.getDeathSound();
            if (sound != null)
                living.playSound(sound, living.getSoundVolume(), living.getVoicePitch());
            AvaritiaLivingEntity.die(living, source);
            ((AvaritiaKill) living).dead(true);
            living.brain.clearMemories();
        }
        InfinitySword.sweep(player, entity, stack, Float.POSITIVE_INFINITY);
        return true;
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return Component.literal("§4寰宇支配之剑");
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.clear();
        p_41423_.add(getName(p_41421_));
        p_41423_.add(Component.literal("这把剑似乎是从1.7.10穿越过来的"));
        p_41423_.add(Component.literal("§7").append(Component.translatable("item.modifiers.mainhand")));
        p_41423_.add(Component.translatable(TextUtils.makeFabulous("infinity ")).append(TextUtils.makeGreen(I18n.get("attribute.name.generic.attack_damage"))));
        p_41423_.add(Component.translatable(TextUtils.makeGreen("1.6 ")).append(TextUtils.makeGreen(I18n.get("attribute.name.generic.attack_speed"))));
    }
}
