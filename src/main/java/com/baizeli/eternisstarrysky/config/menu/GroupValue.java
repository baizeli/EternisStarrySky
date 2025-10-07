package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

import java.util.LinkedList;
import java.util.List;

public class GroupValue extends ConfigValue
{
	public List<ConfigValue> group = new LinkedList<>();

	public GroupValue(AbstractContainerEventHandler parent, String key, Font font)
	{
		super(parent, key, font);
		this.resetEnable = false;
	}

	@Override
	public void update()
	{
		this.text.setWidth(this.width - (2 * this.indent));
		this.text.alignCenter();
	}

	@Override
	public void clickText()
	{
		for (ConfigValue value : this.group)
			value.visible(!value.visible);
	}

	@Override
	public void visible(boolean visible)
	{
		super.visible(visible);
		for (ConfigValue value : this.group)
			value.visible(visible);
	}
}
