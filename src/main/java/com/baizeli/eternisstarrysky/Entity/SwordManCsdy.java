package com.baizeli.eternisstarrysky.Entity;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import com.baizeli.eternisstarrysky.SoundsRegister;
import com.google.common.base.Suppliers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SwordManCsdy extends BossEntity implements GeoEntity {

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.model.stand");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.model.walk");

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        if (name != null && name.getString().contains("沉睡的艺") && !isReal()) {
            this.setHealth(this.getMaxHealth());
            setReal(true);
        }
    }

    private CsdyMeleeGoal meleeGoal; // 持有对近战Goal的引用
    private int attackBehaviorCooldown = 0; // 控制攻击行为（动画+伤害）的整体冷却
    private AnimationController<SwordManCsdy> mainAnimationController; // 主动画控制器

    private transient Object clientBossMusicInstance; // transient 防止序列化，客户端专用
    private boolean musicStarted = false;

    public boolean isDead;
    private float oldHealth;
    private float lastHealth;
    private int updateTimer;
    private RemovalReason oldRemovalReason;
    private boolean damageTooHigh;

    private static final EntityDataAccessor<Float> DATA_HEALTH_ID = getHealthDataAccessor();

    private static EntityDataAccessor<Float> getHealthDataAccessor() {
        for (String fieldName : new String[]{"DATA_HEALTH_ID", "f_20961_", "health"}) {
            try {
                Field field = LivingEntity.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(null);
                if (value instanceof EntityDataAccessor) {
                    return (EntityDataAccessor<Float>) value;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private final ServerBossEvent bossEvent;
    public SwordManCsdy(EntityType<? extends BossEntity> type, Level level) {
        super(type, level);
        this.entityData.set(DATA_HEALTH_ID,this.getMaxHealth());
        this.setMaxUpStep(0.6F);
        this.xpReward = 0;
        this.setPersistenceRequired();
        this.oldHealth = this.getHealth();
        this.bossEvent = (ServerBossEvent)(new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS // 血条样式
        )).setDarkenScreen(true); // 是否使屏幕变暗
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_ATTACKING, false);
        this.entityData.define(DATA_IS_REAL, false);
    }

    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING =
            SynchedEntityData.defineId(SwordManCsdy.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_REAL =
            SynchedEntityData.defineId(SwordManCsdy.class, EntityDataSerializers.BOOLEAN);

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACKING, attacking);
    }

    public boolean isReal() {
        return this.entityData.get(DATA_IS_REAL);
    }

    public void setReal(boolean real) {
        this.entityData.set(DATA_IS_REAL, real);
    }

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(EternisStarrySky.MODID, "entities/sword_man_csdy");

    @Override
    protected @NotNull ResourceLocation getDefaultLootTable() {
        return LOOT_TABLE;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

         if (!this.level().isClientSide && this.bossEvent != null) {
             this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
         }

        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            serverLevel.setWeatherParameters(0, 400, true, true);
        }
        this.invulnerableTime = 0;

    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        // 确保当Boss死亡或被移除时，血条也从所有玩家屏幕上消失
        this.bossEvent.removeAllPlayers();
    }

    @Override
    public SoundEvent getBossMusic() {
        return SoundsRegister.GIRL_A.get();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        if (isReal()) return false;

        // 计算开方后的伤害
        float sqrtDamage = (float) Math.sqrt(damage);
        // 减少80%（即只保留20%）
        float realDamage = sqrtDamage * 0.2f;

        if (realDamage < 100f) {
            return false;
        }
        teleportToAttacker(source);
        return super.hurt(source, realDamage); // 传递处理后的伤害值
    }

    private void teleportToAttacker(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null || attacker == this) return;

        double x = attacker.getX() + (random.nextDouble() - 0.5) * 1.2;
        double y = attacker.getY() + random.nextInt(2);
        double z = attacker.getZ() + (random.nextDouble() - 0.5) * 1.2;

        this.teleportTo(x,y,z);
    }

    @Override
    public void setHealth(float value) {
        if (isReal()) return;

        float currentHealth = this.getHealth();
        float healthLoss = currentHealth - value;

        // 对血量损失进行同样的处理：开方后减80%
        float processedHealthLoss = (float) (Math.sqrt(healthLoss) * 0.2f);

        float threshold = 325.0f;
        if (processedHealthLoss > threshold) {
            value = currentHealth - processedHealthLoss * 0.2f;
        }

        super.setHealth(value);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 行为选择器 (goalSelector)
        this.goalSelector.addGoal(0, new CsdyMeleeGoal(this, 1.0D, false)); // 2: 近战攻击
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));

        // 目标选择器 (targetSelector)
        this.targetSelector.addGoal(1, new PersistentHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::mainAnimController));
    }

    private PlayState mainAnimController(AnimationState<SwordManCsdy> state) {
        if (state.isMoving() || this.getTarget() != null || isAttacking()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 4.4);
        builder = builder.add(Attributes.MAX_HEALTH, 10000.0);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 1600.0);
        builder = builder.add(Attributes.ATTACK_SPEED, 20.0);
        builder = builder.add(Attributes.FOLLOW_RANGE, 128);
        return builder;
    }

}