package com.baizeli.eternisstarrysky;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundsRegister {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EternisStarrySky.MOD_ID);

    public static final RegistryObject<SoundEvent> GIRL_A = SOUND_EVENTS.register("girl_a",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(EternisStarrySky.MOD_ID, "girl_a")));
}