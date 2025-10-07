package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

public class DoubleValue extends NumberValue
{
	public DoubleValue(AbstractContainerEventHandler parent, String key, Font font)
	{
		super(parent, key, font);
		this.min = 0.0;
		this.max = 1.0;
	}

	@Override
	public Number parseValue(String value)
	{
		return Double.parseDouble(value);
	}

	@Override
	public boolean range(Number val)
	{
		return this.min.doubleValue() <= val.doubleValue() && val.doubleValue() <= this.max.doubleValue();
	}
}
