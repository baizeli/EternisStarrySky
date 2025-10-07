package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.RainbowEffectHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public final class BagItem extends Item {

    private final int type;

    public BagItem(Properties p_41383_, int type) {
        super(p_41383_);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if (type == 0) tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.bag1").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2f));
        if (type == 1) tooltip.add(Component.translatable("item.eternisstarrysky.primogem1"));
    }
}