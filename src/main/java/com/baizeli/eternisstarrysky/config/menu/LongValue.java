package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

public class LongValue extends NumberValue
{
	public LongValue(AbstractContainerEventHandler parent, String key, Font font)
	{
		super(parent, key, font);
		this.min = Long.MIN_VALUE;
		this.max = Long.MAX_VALUE;
	}

	@Override
	public Number parseValue(String value)
	{
		return Long.parseLong(value);
	}

	@Override
	public boolean range(Number val)
	{
		return this.min.longValue() <= val.longValue() && val.longValue() <= this.max.longValue();
	}
}
