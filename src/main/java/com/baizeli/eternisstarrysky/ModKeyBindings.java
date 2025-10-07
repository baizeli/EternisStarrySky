package com.baizeli.eternisstarrysky;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final String KEY_CATEGORY_TUTORIAL = "key.categories.eternisstarrysky";
    public static final String KEY_BOW_TYPE = "key.eternisstarrysky.bow_type";

    public static KeyMapping BOW_TYPE_KEY;

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        BOW_TYPE_KEY = new KeyMapping(KEY_BOW_TYPE, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_MOUSE_BUTTON_3, KEY_CATEGORY_TUTORIAL);
        event.register(BOW_TYPE_KEY);
    }
}