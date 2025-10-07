package com.baizeli.eternisstarrysky.config.menu;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.widget.ScrollPanel;

import java.util.LinkedList;
import java.util.List;

public class ConfigArray extends ScrollPanel
{
	private final List<ConfigValue> value = new LinkedList<>();
	private int previousY = Integer.MIN_VALUE;
	private final int x;

	public ConfigArray(Minecraft client, int width, int height, int top, int left)
	{
		super(client, width, height, top, left);
		this.x = left;
	}

	@Override
	protected int getContentHeight()
	{
		int content = 0;
		for (ConfigValue value : this.value)
			if (value.visible)
				content += value.height + ConfigMenu.PADDING;
		return content - ConfigMenu.PADDING;
	}

	@Override
	protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY)
	{
		relativeY -= 4;
		if (this.height >= this.getContentHeight())
			relativeY = ConfigMenu.PADDING;
		this.previousY = relativeY;
		for (ConfigValue configValue : this.value)
		{
			if (!configValue.visible)
				continue;
			configValue.draw(guiGraphics, x, relativeY, mouseX, mouseY);
			relativeY += configValue.height + ConfigMenu.PADDING;
		}
	}

	@Override
	protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick)
	{
		super.drawBackground(guiGraphics, tess, partialTick);
	}

	@Override
	public NarrationPriority narrationPriority()
	{
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_)
	{
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (this.click(mouseX, mouseY))
			return true;
		this.setFocused(null);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public void push(ConfigValue value)
	{
		value.width = this.width;
		value.parent = this;
		this.value.add(value);
	}

	public boolean click(double x, double y)
	{
		if (y < 6)
			return false;
		if (y > this.height + 6)
			return false;
		if (x < 6)
			return false;
		if (x > this.width + 6)
			return false;
		double y1 = y - this.previousY;
		int cy = 0;
		for (ConfigValue configValue : this.value)
		{
			if (!configValue.visible)
				continue;
			if (cy > y1)
				return false;
			if (cy + configValue.height >= y1)
			{
				return configValue.click(x, y);
			}
			cy += configValue.height + ConfigMenu.PADDING;
		}
		return false;
	}
}
