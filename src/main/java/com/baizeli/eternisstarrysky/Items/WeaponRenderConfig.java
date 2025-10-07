package com.baizeli.eternisstarrysky.Items;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class WeaponRenderConfig {
    private static final Set<Class<?>> SPECIAL_WEAPONS = new HashSet<>();

    static {
        registerWeapon(InfinitySwordTrue.class);
        registerWeapon(InfinitySword.class);
        registerWeapon(AvaritiaSword.class);
    }

    public static void registerWeapon(Class<?> weaponClass) {
        SPECIAL_WEAPONS.add(weaponClass);
    }

    public static boolean isSpecialWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return SPECIAL_WEAPONS.stream()
                .anyMatch(weaponClass -> weaponClass.isInstance(stack.getItem()));
    }
}