package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Items.WeaponRenderConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Helper {
    public static boolean isBlocking(Player player)
    {
        if (player.isUsingItem())
        {
            ItemStack usedItem = player.getUseItem();
            return WeaponRenderConfig.isSpecialWeapon(usedItem);
        }
        return false;
    }
}
