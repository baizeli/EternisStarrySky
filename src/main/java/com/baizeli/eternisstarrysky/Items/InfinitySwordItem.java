package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.RainbowEffectHelper;
import com.baizeli.eternisstarrysky.Util.KillUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public final class InfinitySwordItem extends SwordItem {
    private final int attackDamage;
    private final double attackSpeed;
    private final boolean TRUE;
    public boolean getTRUE() {return TRUE;}

    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BLOCK; }
    @Override public int getUseDuration(ItemStack stack) { return Integer.MAX_VALUE; }
    @Override public boolean isFoil(ItemStack stack) { return true; }

    public InfinitySwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties, boolean TRUE) {
        super(tier, attackDamage, attackSpeed, properties.rarity(Rarity.EPIC).fireResistant());
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.TRUE = TRUE;
    }

    @Override
    public boolean isDamageable(ItemStack stack) { return false; }
    @Override
    public int getMaxDamage(ItemStack stack) { return 0; }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        stack.getOrCreateTag().putInt("HideFlags", 2);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!stack.getOrCreateTag().contains("HideFlags")) {
            stack.getOrCreateTag().putInt("HideFlags", 2);
        }
    }
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    // 格挡松手 - 结束格挡
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity2, int timeLeft)
    {
        if (entity2 instanceof Player player)
        {
            if (TRUE)
            {
                if (!level.isClientSide) {
                    Vec3 playerPos = player.position();
                    double radius = 17.0;

                    AABB searchArea = new AABB(
                            playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                            playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
                    );

                    List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
                            entity -> entity != null
                                    && entity != player
                                    && entity.distanceTo(player) <= radius
                                    && !(entity instanceof Player)); // 排除所有玩家

                    nearbyEntities.sort((e1, e2) -> Float.compare(e1.distanceTo(player), e2.distanceTo(player)));

                    List<LivingEntity> targetsToKill = nearbyEntities.subList(0, Math.min(9, nearbyEntities.size()));

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 2.0F);

                    for (int i = 0; i < 50; i++) {
                        double offsetX = (level.random.nextDouble() - 0.5) * radius * 2;
                        double offsetY = (level.random.nextDouble() - 0.5) * radius * 2;
                        double offsetZ = (level.random.nextDouble() - 0.5) * radius * 2;

                        level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                                playerPos.x + offsetX, playerPos.y + offsetY, playerPos.z + offsetZ,
                                0, 0, 0);
                        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                                playerPos.x + offsetX, playerPos.y + offsetY, playerPos.z + offsetZ,
                                0, 0.1, 0);
                    }

                    if (targetsToKill instanceof ArmorStand) {
                        return;
                    }

                    // 排除展示框
                    if (targetsToKill instanceof ItemFrame) {
                        return;
                    }

                    // 排除画
                    if (targetsToKill instanceof Painting) {
                        return;
                    }

                    for (LivingEntity entity : targetsToKill) {

                        if (!(entity instanceof Monster) && !(entity instanceof Player)) {
                            return;
                        }

                        if (entity != null && entity.isAlive() && entity != player) {
                            entity.removeAllEffects();
                            entity.invulnerableTime = 0;
                            entity.hurtTime = 0;

                            entity.hurt(player.damageSources().playerAttack(player), Float.MAX_VALUE);
                            entity.setHealth(0);
                            entity.kill();
                            entity.remove(Entity.RemovalReason.KILLED);

                            ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), entity.getExperienceReward());
                            entity.dropCustomDeathLoot(entity.damageSources().playerAttack(player),10,true);
                            entity.dropFromLootTable(entity.damageSources().playerAttack(player),true);

                            for (int i = 0; i < 10; i++) {
                                level.addParticle(ParticleTypes.LARGE_SMOKE,
                                        entity.getX() + (level.random.nextDouble() - 0.5) * 2,
                                        entity.getY() + level.random.nextDouble() * 2,
                                        entity.getZ() + (level.random.nextDouble() - 0.5) * 2,
                                        0, 0.1, 0);
                            }
                        }
                    }

                    if (!targetsToKill.isEmpty()) {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.0F, 0.1F);
                    }
                }

                Vec3 playerPos = player.position();
                double radius = 20.0;
                AABB searchArea = new AABB(
                        playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                        playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
                );

                List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, searchArea);
                int itemCount = 0;

                for (ItemEntity itemEntity : itemEntities) {
                    double distance = itemEntity.position().distanceTo(playerPos);
                    if (distance <= radius) {
                        itemEntity.setPos(playerPos.x, playerPos.y, playerPos.z);
                        itemCount++;
                    }
                }

                if (!level.isClientSide && itemCount > 0) {
                    player.displayClientMessage(
                            Component.literal("§6吸取了 §e" + itemCount + " §6个掉落物！"),
                            true
                    );
                }
            }
            else {
                LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                lightning.setVisualOnly(true);
                lightning.moveTo(player.position().x, player.position().y, player.position().z);

                Random random = new Random();
                int randomId = random.nextInt(10001) + 20000;
                lightning.setId(200000000 + randomId);

                level.addFreshEntity(lightning);

                player.setHealth(0);
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        Component originalName = super.getName(stack);
        if (stack.hasCustomHoverName()) {
            return originalName;
        }
        stack.getOrCreateTag().putInt("HideFlags", 2);
        return originalName;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {



        if (entity instanceof LivingEntity livingEntity)
        {
            if (entity.level().isClientSide()) return true;
            ServerLevel serverLevel = (ServerLevel) livingEntity.level();

            ExperienceOrb.award(serverLevel, livingEntity.position(), livingEntity.getExperienceReward());
            livingEntity.dropCustomDeathLoot(livingEntity.damageSources().playerAttack(player), 10, true);
            livingEntity.dropFromLootTable(livingEntity.damageSources().playerAttack(player), true);
            livingEntity.remove(Entity.RemovalReason.KILLED);
        }

        if (TRUE)
        {
            if (entity instanceof LivingEntity target) {
                target.removeAllEffects();
                target.invulnerableTime = 0;
                target.hurtTime = 0;
            }

            if (entity != null) {
                if (entity instanceof Player targetPlayer && targetPlayer.isCreative()) {
                    return false;
                }

                if (entity instanceof LivingEntity livingEntity)
                {
                    Level level = livingEntity.level();

                    livingEntity.removeAllEffects();
                    livingEntity.invulnerableTime = 0;
                    livingEntity.hurtTime = 0;

                    livingEntity.hurt(new DamageSource(player.damageSources().fellOutOfWorld().typeHolder(), player), Float.MAX_VALUE);
                    // livingEntity.setHealth(0);

                    LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                    lightning.setVisualOnly(true);
                    lightning.moveTo(entity.position().x, entity.position().y, entity.position().z);

                    Random random = new Random();
                    int randomId = random.nextInt(10001) + 20000;
                    lightning.setId(200000000 + randomId);

                    level.addFreshEntity(lightning);
                }

                return true;
            }
        }
        else
        {
            player.setHealth(0);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        if (TRUE) tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.infinity_sword2").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2F));
        else tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.infinity_sword7").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2F));

        tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("AIR").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2f));
        tooltip.add(Component.translatable("item.isMain"));
        if (TRUE) tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.infinity_sword_damage_true").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2F).append(Component.translatable("item.attDamage").getString()));
        else tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.infinity_sword_damage").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 4F, 1, 0.05F, 1F).append(Component.translatable("item.attDamage").getString()));
        tooltip.add(Component.translatable(" §22.0").append(Component.translatable("item.attSpeed").getString()));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("详细:").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("右键点击则击杀以你为中心").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("半径17格范围内的所有生物").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("按住 Shift 查看更多信息").withStyle(ChatFormatting.GRAY));
        }
    }
}