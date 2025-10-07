package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.RainbowEffectHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreateStar extends Item {

    public CreateStar(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component originalName = super.getName(stack);
        if (stack.hasCustomHoverName()) {
            return originalName;
        }
        stack.getOrCreateTag().putInt("HideFlags", 2);
        return RainbowEffectHelper.createCustomGradientText(originalName.getString(), RainbowEffectHelper.BLUE, 3, 1, 0.03F, 1F);
    }
}
