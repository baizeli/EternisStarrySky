package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class EffectEntry
{
	public Button remove;
	public EditBox ID;
	public EditBox duration;
	public EditBox amplifier;
	private String previousD = "0";
	private String previousA = "0";

	public GuiEventListener click(double x, double y)
	{
		if (this.remove.mouseClicked(x, y, 0)) return this.remove;
		if (this.ID.mouseClicked(x, y, 0)) return this.ID;
		if (this.duration.mouseClicked(x, y, 0)) return this.duration;
		if (this.amplifier.mouseClicked(x, y, 0)) return this.amplifier;
		return null;
	}

	public void duration(String duration)
	{
		if (duration.isEmpty())
		{
			this.previousD = duration;
			return;
		}

		try
		{
			Number val = Integer.parseInt(duration);
			if (val.intValue() < 0)
			{
				this.duration.setValue(this.previousD);
				return;
			}
			this.previousD = duration;
		}
		catch (NumberFormatException e)
		{
			this.duration.setValue(this.previousD);
		}
	}

	public void amplifier(String amplifier)
	{
		if (amplifier.isEmpty())
		{
			this.previousA = amplifier;
			return;
		}

		try
		{
			Number val = Integer.parseInt(amplifier);
			if (val.intValue() < 0 || val.intValue() > 255)
			{
				this.amplifier.setValue(this.previousA);
				return;
			}
			this.previousA = amplifier;
		}
		catch (NumberFormatException e)
		{
			this.amplifier.setValue(this.previousA);
		}
	}
}
