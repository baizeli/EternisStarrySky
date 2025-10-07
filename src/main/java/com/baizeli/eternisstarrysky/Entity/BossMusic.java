package com.baizeli.eternisstarrysky.Entity;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class BossMusic extends AbstractTickableSoundInstance {
    BossEntity boss;
    private final float baseVolume;
    private static Float originalMusicVolume = null;
    private boolean shouldStop = false;

    protected BossMusic(BossEntity boss) {
        super(boss.getBossMusic(), SoundSource.MUSIC, RandomSource.create());
        this.boss = boss;
        this.pitch = 1.0f;
        this.looping = true;

        this.baseVolume = 0.5f;

        this.volume = this.baseVolume; // 初始化音量

        if (originalMusicVolume == null) {
            originalMusicVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);
            try {
                java.lang.reflect.Method method = Minecraft.getInstance().options.getClass().getDeclaredMethod("setSoundCategoryVolume", SoundSource.class, float.class);
                method.setAccessible(true);
                method.invoke(Minecraft.getInstance().options, SoundSource.MUSIC, 0.0f);
                Minecraft.getInstance().options.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public static BossMusic create(BossEntity boss) {
        if (boss == null || boss.getBossMusic() == null) {
            return null;
        }
        return new BossMusic(boss);
    }

    @Override
    public void tick() {
        if (this.boss == null || !this.boss.isAlive() || Minecraft.getInstance().player == null || shouldStop) {
            this.stopMusic();
            return;
        }

        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) <= 0.0F) {
            this.volume = 0.0F;
            return;
        }

        // 根据距离动态调整音量
        float maxDistance = 60.0f;
        float distance = this.boss.distanceTo(Minecraft.getInstance().player);
        float playerMusicVolumeSetting = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC);

        if (distance > maxDistance) {
            this.volume = 0.0f;
        } else if (distance < 5.0f) {
            this.volume = this.baseVolume * playerMusicVolumeSetting;
        }
        else {
            float distanceFactor = 1.0f - (Math.max(0, distance - 5.0f) / (maxDistance - 5.0f));
            this.volume = this.baseVolume * distanceFactor * playerMusicVolumeSetting;
        }
        this.volume = Mth.clamp(this.volume, 0.0f, 1.0f);


        this.x = this.boss.getX();
        this.y = this.boss.getY();
        this.z = this.boss.getZ();
    }

    public boolean canPlayMusic() {
        boolean b = true;
        try {
            SoundEngine soundEngine = Minecraft.getInstance().getSoundManager().soundEngine;
            for (SoundInstance soundInstance : soundEngine.tickingSounds) {
                if (!soundInstance.getLocation().equals(this.getLocation()) || !(soundInstance.getVolume() > 0.0f)) continue;
                b = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !Minecraft.getInstance().getSoundManager().isActive(this) && Minecraft.getInstance().level.isClientSide() && b;
    }

    public void stopMusic() {
        super.stop();
        // 恢复原始音乐音量
        restoreMusicVolume();
    }

    public boolean isStopped() {
        if (super.isStopped() || shouldStop) {
            // 恢复原始音乐音量
            restoreMusicVolume();
            return true;
        }
        return false;
    }

    // 设置停止标记
    public void setShouldStop() {
        this.shouldStop = true;
    }

    // 恢复原始音乐音量
    private static void restoreMusicVolume() {
        if (originalMusicVolume != null) {
            try {
                java.lang.reflect.Method method = Minecraft.getInstance().options.getClass().getDeclaredMethod("setSoundCategoryVolume", SoundSource.class, float.class);
                method.setAccessible(true);
                method.invoke(Minecraft.getInstance().options, SoundSource.MUSIC, originalMusicVolume);
                Minecraft.getInstance().options.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
            originalMusicVolume = null;
        }
    }

    public static void playMusic(BossMusic music, BossEntity bossEntity) {
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) <= 0.0f) {
            music = null;
        }
        if (music != null) {
            music = new BossMusic(bossEntity);
        }
        if (music != null && music.canPlayMusic()) {
            Minecraft.getInstance().getSoundManager().play(music);
        }
    }

    public float getVolume() {
        return this.volume;
    }
}