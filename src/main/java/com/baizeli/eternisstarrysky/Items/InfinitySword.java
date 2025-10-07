package com.baizeli.eternisstarrysky.Items;

import com.baizeli.Sounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class InfinitySword extends SwordItem
{
	private static final double KILL_RADIUS = 5;

	public InfinitySword(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_)
	{
		super(p_43269_, p_43270_, p_43271_, p_43272_);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
	{
		if (!(player instanceof ServerPlayer))
		{
			Sounds.play(SoundEvents.AMETHYST_BLOCK_STEP, player, 10.0F, 1.0F);
			return false;
		}

		if (!(entity instanceof LivingEntity))
			return false;

		if (((LivingEntity) entity).isDeadOrDying())
			return false;

		float strength = player.getAttackStrengthScale(0.5F);
		List<LivingEntity> nearbyEntities = List.of((LivingEntity) entity);
		if (strength >= 1.0)
		{
			Predicate<LivingEntity> predicate = (e) -> e.getId() != player.getId();
			nearbyEntities = InfinitySword.getNearbyLivingEntities(entity, KILL_RADIUS, predicate);
		}

		DamageSource source = new DamageSource(player.damageSources().fellOutOfWorld().typeHolder(), player);
		nearbyEntities.forEach(living -> {
			player.crit(living);
			living.hurt(source, this.getDamage() * strength);
		});

		InfinitySword.sweep(player, entity, stack, this.getDamage());

		player.resetAttackStrengthTicker();
		return true;
	}

	public static List<LivingEntity> getNearbyLivingEntities(Entity entity, double radius, Predicate<LivingEntity> predicate)
	{
		Level level;
		if (entity == null || (level = entity.level()).isClientSide)
			return List.of();

		EntityTypeTest<Entity, LivingEntity> typeTest = EntityTypeTest.forClass(LivingEntity.class);
		Vec3 pos = entity.position();
		AABB aabb = new AABB(
			pos.x - radius, pos.y - radius, pos.z - radius,
			pos.x + radius, pos.y + radius, pos.z + radius
		);
		List<LivingEntity> nearbyEntities = new LinkedList<>();
		if (predicate == null)
			predicate = (e) -> true;
		level.getEntities(typeTest, aabb, predicate, nearbyEntities, Integer.MAX_VALUE);
		return nearbyEntities;
	}

	public static void sweep(Player player, Entity target, ItemStack item, double damage)
	{
		if (!(player instanceof ServerPlayer))
			return;


		float strength = player.getAttackStrengthScale(0.5F);
		boolean cooldown = strength >= 1.0;
		boolean sprinting = (cooldown && player.isSprinting());
		boolean crit = cooldown &&
			player.fallDistance > 0.0F &&
			!player.onGround() &&
			!player.onClimbable() &&
			!player.isInWater() &&
			!player.hasEffect(MobEffects.BLINDNESS) &&
			!player.isPassenger() &&
			!sprinting;
		double d0 = player.walkDist - player.walkDistO;
		boolean sweep = cooldown &&
			!sprinting &&
			!crit &&
			player.onGround() &&
			(d0 < player.getSpeed());

		if (!sweep)
			return;

		int sweepLevel = item.getEnchantmentLevel(Enchantments.SWEEPING_EDGE) + 2;
		double sweepDamage = (1.0 - (1.0 / sweepLevel)) * damage;

		AABB sweepHitBox = player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target);
		for (LivingEntity living : player.level().getEntitiesOfClass(LivingEntity.class, sweepHitBox))
		{
			double entityReachSq = Mth.square(player.getEntityReach());
			boolean flag = living != player;
			flag &= living != target;
			flag &= !player.isAlliedTo(living);
			flag &= (!(living instanceof ArmorStand) || !((ArmorStand) living).isMarker());
			flag &= player.distanceToSqr(living) < entityReachSq;
			if (flag)
			{
				double ratioX = Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
				double ratioZ = -Mth.cos(player.getYRot() * ((float) Math.PI / 180));
				living.knockback(0.4, ratioX, ratioZ);
				living.hurt(player.damageSources().fellOutOfWorld(), (float) sweepDamage);
			}
		}

		SoundEvent sound = SoundEvents.PLAYER_ATTACK_SWEEP;
		SoundSource source = player.getSoundSource();
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, source, 1.0F, 1.0F);
		player.sweepAttack();
	}
}
