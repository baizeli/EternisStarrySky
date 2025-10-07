package com.baizeli.eternisstarrysky.Entity;

import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class BossEntity extends Monster {
    public final BossEvent bossEvent;

    private transient Supplier<BossMusic> clientBossMusicInstance; // transient 防止序列化，客户端专用
    private boolean musicStarted = false;

    public BossEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bossEvent = (new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.PURPLE, // 血条颜色
                BossEvent.BossBarOverlay.PROGRESS // 血条样式
        )).setDarkenScreen(true); // 是否使屏幕变暗
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pServerPlayer) {
        super.startSeenByPlayer(pServerPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pServerPlayer) {
        super.stopSeenByPlayer(pServerPlayer);
    }

    public ResourceLocation getBossBarOverlay() {
        return null;
    }

    public Font getBossBarFont() {
        return Minecraft.getInstance().font;
    }

    @Nullable
    public SoundEvent getBossMusic() {
        return null;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true; // 强制生物不会被自然卸载
    }

    public float getMaxDamageHurt() {
        return Float.MAX_VALUE;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return super.doHurtTarget(pEntity);
    }

    public BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.WHITE;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance) {
        return false;
    }

    @Override
    public void forceAddEffect(MobEffectInstance pInstance, @Nullable Entity pEntity) {

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (isUnsafeDamage(pSource)) return false;
        this.setDeltaMovement(Vec3.ZERO);
        Entity entity = pSource.getEntity();
        if (entity instanceof LivingEntity living && !(living instanceof BossEntity) && !(living instanceof Player)) this.setTarget(living);
        if (entity instanceof ServerPlayer player && !player.isCreative()) this.setTarget(player);
        return super.hurt(pSource, pAmount);
    }

    public static boolean isUnsafeDamage(DamageSource d) {
        return d.is(DamageTypes.GENERIC)
                || d.is(DamageTypes.GENERIC_KILL)
                || d.is(DamageTypes.FELL_OUT_OF_WORLD)
                || d.is(DamageTypes.ARROW)
                || d.is(DamageTypes.WITHER)
                || d.is(DamageTypes.WITHER_SKULL)
                || d.is(DamageTypes.EXPLOSION)
                || d.is(DamageTypes.MAGIC);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

    }

    @Override
    public void setHealth(float pHealth) {
        if (pHealth < getHealth() - getMaxDamageHurt()) pHealth = getHealth() - getMaxDamageHurt();
        super.setHealth(pHealth);
    }

    @Override
    public void kill() {
    }

    @Override
    public void tick() {
        super.tick();

        // Boss 血条和名称更新
        this.bossEvent.setName(this.getDisplayName());
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.resetFallDistance();

        if (this.getHealth() > 0){
            this.deathTime = 0;
        }

        // 只在客户端处理音乐
        if (level().isClientSide()) {
            handleBossMusic();
        }
    }


    private void handleBossMusic() {
        Minecraft mc = Minecraft.getInstance();
        float musicVolume = mc.options.getSoundSourceVolume(SoundSource.MUSIC);

        if (this.isAlive() && musicVolume > 0.0F) {
            if (!musicStarted || clientBossMusicInstance == null || clientBossMusicInstance.get().isStopped()) {
                clientBossMusicInstance = Suppliers.ofInstance(BossMusic.create(this));
            }

            if (clientBossMusicInstance.get() != null && !mc.getSoundManager().isActive(clientBossMusicInstance.get())) {
                mc.getSoundManager().play(clientBossMusicInstance.get());
                musicStarted = true;
            }
        }
        else if (musicStarted) {
            stopBossMusic();
        }
    }

    // 安全的音乐停止方法
    private void stopBossMusic() {
        if (clientBossMusicInstance != null) {
            // 使用新的停止方法
            clientBossMusicInstance.get().setShouldStop();
            Minecraft.getInstance().getSoundManager().stop(clientBossMusicInstance.get());
        }
        musicStarted = false;
        clientBossMusicInstance = null;
    }

    @Override
    public void die(@NotNull DamageSource pSource) {
        if (isDeadOrDying()) {
            super.die(pSource);
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (isDeadOrDying()) {
            super.remove(reason);
        }
    }

    @Override
    public boolean isDeadOrDying() {
        return getHealth() <= 0;
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public boolean isNoAi() {
        return false;
    }

    @Override
    public void setNoAi(boolean noAi) {
    }

    @Override
    public void updateFluidHeightAndDoFluidPushing(Predicate<FluidState> shouldUpdate) {

    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }
}