package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.RainbowEffectHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public final class EternisMaterial extends Item
{

    private final int type;

    public EternisMaterial(Properties p_41383_, int type)
    {
        super(p_41383_);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        if (type == 0) tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.purpleite_galaxy_ingot1").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2f));
        if (type == 1) tooltip.add(Component.translatable("item.eternisstarrysky.primogem1"));
    }
}