package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.network.chat.Component;

public abstract class NumberValue extends ConfigValue
{
	public Number value;
	public Number min;
	public Number max;
	private String previous = "";

	public NumberValue(AbstractContainerEventHandler parent, String key, Font font)
	{
		super(parent, key, font);
		this.valueWidget = new EditBox(this.font, 0, 0, this.valueWidth, this.lineHeight, Component.empty());
		((EditBox) this.valueWidget).setResponder(this::onChange);
	}

	@Override
	public void update()
	{
		EditBox box = (EditBox) this.valueWidget;
		box.setTooltip(Tooltip.create(Component.literal(this.min + " ~ " + this.max)));
		if (box.isFocused())
			return;
		if (box.getValue().isEmpty())
		{
			Number def = this.min;
			if (def.doubleValue() < 0)
				def = 0;
			this.previous = String.valueOf(def);
			box.setValue(this.previous);
		}
	}

	public void onChange(String value)
	{
		if (value.isEmpty())
		{
			this.previous = value;
			return;
		}
		try
		{
			Number val = this.parseValue(value);
			if (!this.range(val))
			{
				((EditBox) this.valueWidget).setValue(this.previous);
				return;
			}
			this.value = val;
			this.previous = value;
		}
		catch (NumberFormatException e)
		{
			((EditBox) this.valueWidget).setValue(this.previous);
		}
	}

	public abstract Number parseValue(String value);

	public abstract boolean range(Number val);

	public void value(Number v)
	{
		this.value = v;
		this.previous = String.valueOf(this.value);
		((EditBox) this.valueWidget).setValue(this.previous);
	}
}
