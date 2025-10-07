package com.baizeli.eternisstarrysky.Entity;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EternisStarrySky.MOD_ID);

    public static final RegistryObject<EntityType<CustomArrowEntity>> CUSTOM_ARROW = ENTITY_TYPES.register("custom_arrow",
            () -> EntityType.Builder.<CustomArrowEntity>of(CustomArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("custom_arrow"));

    public static final RegistryObject<EntityType<SwordManCsdy>> SWORD_MAN_CSDY = ENTITY_TYPES.register("sword_man_csdy",
        () -> EntityType.Builder.of(new SwordManFactory(), MobCategory.MONSTER)
            .sized(0.6F, 1.8F)
            .clientTrackingRange(10)
            .updateInterval(1)
            .build("sword_man_csdy"));
}