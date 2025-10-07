package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.config.menu.ConfigMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.BiFunction;

public class ConfigurationFactory implements BiFunction<Minecraft, Screen, Screen>
{
	@Override
	public Screen apply(Minecraft minecart, Screen screen)
	{
		ConfigMenu configScreen = new ConfigMenu(screen);
		minecart.pushGuiLayer(configScreen);
		return configScreen;
	}
}
