package com.baizeli.eternisstarrysky.Items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class ModArmorMaterials {
    public static final ArmorMaterial INFINITY_ETERNAL = new ArmorMaterial() {

        @Override public Ingredient getRepairIngredient() {return Ingredient.of(ModItems.ETERNIS_APPLE.get());}
        @Override public int getDurabilityForType(ArmorItem.Type type) {return Integer.MAX_VALUE;}
        @Override public SoundEvent getEquipSound() {return SoundEvents.ARMOR_EQUIP_NETHERITE;}
        @Override public String getName() {return "infinity_eternal";}
        @Override public float getKnockbackResistance() {return 0.2F;}
        @Override public int getEnchantmentValue() {return 30;}
        @Override public float getToughness() {return 8.0F;}

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 7;
                case CHESTPLATE -> 12;
                case LEGGINGS -> 9;
                case BOOTS -> 6;
            };
        }
    };
}