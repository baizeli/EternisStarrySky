package com.baizeli.eternisstarrysky.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomArrowEntity extends AbstractArrow {

    private boolean spawn = false;
    private boolean track = false;
    private long hitTime = -1;

    // 追踪相关字段
    private LivingEntity currentTarget = null;
    private long trackStartTime = -1; // 追踪开始时间
    private static final int TRACK_DURATION = 600; // 30秒 * 20 tick/秒
    private static final double TRACK_RANGE = 30.0; // 追踪半径
    private static final int TARGET_COOLDOWN = 20; // 1.5秒 * 20 tick/秒

    private Map<LivingEntity, Long> attackedTargets = new HashMap<>();
    public CustomArrowEntity(EntityType<? extends CustomArrowEntity> entityType, Level level) {super(entityType, level);}
    public CustomArrowEntity(Level level, LivingEntity shooter) {super(ModEntities.CUSTOM_ARROW.get(), shooter, level);}
    public CustomArrowEntity(Level level, double x, double y, double z) {super(ModEntities.CUSTOM_ARROW.get(), x, y, z, level);}
    public void setSpawn(boolean spawn) {this.spawn = spawn;}

    @Override
    protected ItemStack getPickupItem() {
        if (!spawn) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Items.ARROW);
    }

    public void setTrack(boolean track) {
        this.track = track;
        if (track && trackStartTime == -1) {
            trackStartTime = level().getGameTime();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {

        Entity hitEntity = result.getEntity();
        if (hitEntity == this.getOwner()) {return;}

        if (track && hitEntity instanceof LivingEntity livingEntity) {
            float damage = (float) this.getBaseDamage();
            DamageSource damageSource = this.damageSources().arrow(this, this.getOwner());

            if (livingEntity.hurt(damageSource, damage)) {
                livingEntity.invulnerableTime = 0;
                livingEntity.hurtTime = 0;

                this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                attackedTargets.put(livingEntity, level().getGameTime());
                currentTarget = findNextTarget();
                if (currentTarget == null) {this.setNoGravity(false);}
            }

            this.inGround = false;

            return;
        }

        super.onHitEntity(result);

        if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
            livingEntity.hurtTime = 0;
        }

        if (spawn) {
            createArrowRain();
        } else {
            hitTime = level().getGameTime();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (track) {
            this.inGround = false;
            return;
        }

        super.onHitBlock(result);
        if (spawn) createArrowRain();
        else hitTime = level().getGameTime();
    }

    @Override
    public void tick() {

        if (track) trackTarget();
        super.tick();

        if (!spawn && level().isClientSide) level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        else
        {
            level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        if (!spawn && !track && hitTime != -1) {
            long currentTime = level().getGameTime();
            if (currentTime - hitTime >= 30) {
                this.kill();
            }
        }
    }

    private boolean checkObstacleAbove(Vec3 center) {
        // 检查头顶15格范围内是否有方块
        for (int y = 1; y <= 15; y++) {
            BlockPos checkPos = new BlockPos((int) center.x, (int) center.y + y, (int) center.z);
            if (!level().getBlockState(checkPos).isAir()) {
                return true;
            }
        }
        return false;
    }

    private void createVerticalArrowRain(Vec3 center, Random random) {
        for (int i = 0; i < 36; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = random.nextDouble() * 9.0;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double spawnX = center.x + offsetX;
            double spawnY = center.y + 10 + random.nextDouble() * 10;
            double spawnZ = center.z + offsetZ;

            CustomArrowEntity rainArrow = new CustomArrowEntity(level(), spawnX, spawnY, spawnZ);
            rainArrow.setSpawn(false);
            rainArrow.setBaseDamage(75);

            double targetX = center.x + (random.nextDouble() - 0.5) * 8;
            double targetZ = center.z + (random.nextDouble() - 0.5) * 8;
            Vec3 targetPos = new Vec3(targetX, center.y, targetZ);
            Vec3 direction = targetPos.subtract(rainArrow.position()).normalize();

            rainArrow.setDeltaMovement(direction.scale(1.5 + random.nextDouble() * 1.0));
            rainArrow.setOwner(this.getOwner()); // 确保箭雨箭矢也有正确的射击者

            level().addFreshEntity(rainArrow);
        }
    }

    private void createHorizontalArrowRain(Vec3 center, Random random) {
        // 水平环绕攻击逻辑
        for (int i = 0; i < 36; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 10 + random.nextDouble() * 5;

            double spawnX = center.x + Math.cos(angle) * distance;
            double spawnY = center.y + random.nextDouble() * 6 - 3;
            double spawnZ = center.z + Math.sin(angle) * distance;

            BlockPos spawnPos = new BlockPos((int) spawnX, (int) spawnY, (int) spawnZ);
            while (!level().getBlockState(spawnPos).isAir() && spawnY < center.y + 10) {
                spawnY += 1;
                spawnPos = new BlockPos((int) spawnX, (int) spawnY, (int) spawnZ);
            }

            CustomArrowEntity rainArrow = new CustomArrowEntity(level(), spawnX, spawnY, spawnZ);
            rainArrow.setSpawn(false);
            rainArrow.setBaseDamage(75);

            double targetX = center.x + (random.nextDouble() - 0.5) * 4;
            double targetY = center.y + (random.nextDouble() - 0.5) * 2;
            double targetZ = center.z + (random.nextDouble() - 0.5) * 4;

            Vec3 targetPos = new Vec3(targetX, targetY, targetZ);
            Vec3 direction = targetPos.subtract(rainArrow.position()).normalize();

            rainArrow.setDeltaMovement(direction.scale(2.0 + random.nextDouble() * 1.0));
            rainArrow.setOwner(this.getOwner());

            level().addFreshEntity(rainArrow);
        }
    }

    private void createArrowRain() {
        if (!level().isClientSide) {
            Vec3 center = this.position();
            Random random = new Random();

            boolean hasObstacleAbove = checkObstacleAbove(center);

            if (hasObstacleAbove) createHorizontalArrowRain(center, random);
            else createVerticalArrowRain(center, random);
        }
    }

    private LivingEntity findNextTarget() {
        if (level().isClientSide) return null;

        Vec3 arrowPos = this.position();
        LivingEntity nearestTarget = null;
        double nearestDistance = TRACK_RANGE;
        long currentTime = level().getGameTime();

        attackedTargets.entrySet().removeIf(entry -> currentTime - entry.getValue() > TARGET_COOLDOWN || !entry.getKey().isAlive());

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(TRACK_RANGE))) {

            if (entity == this.getOwner() || !entity.isAlive() ||
                    attackedTargets.containsKey(entity)) {
                continue;
            }

            double distance = entity.position().distanceTo(arrowPos);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestTarget = entity;
            }
        }

        return nearestTarget;
    }

    // 追踪逻辑
    private void trackTarget() {

        if (!track || level().isClientSide) return;
        if (level().getGameTime() - trackStartTime > TRACK_DURATION) {
            this.kill();
            return;
        }

        this.inGround = false;

        if (currentTarget == null || !currentTarget.isAlive() ||
                attackedTargets.containsKey(currentTarget)) {
            currentTarget = findNextTarget();
        }

        if (currentTarget != null) {
            Vec3 targetPos = currentTarget.position().add(0, currentTarget.getBbHeight() / 2, 0);
            Vec3 arrowPos = this.position();

            Vec3 direction = targetPos.subtract(arrowPos).normalize();

            double speed = 2.0;
            this.setDeltaMovement(direction.scale(speed));
            this.setNoGravity(true);

        } else {
            this.setNoGravity(false);
        }
    }
}

