package com.baizeli.eternisstarrysky.Content.Workbenchs;

import mezz.jei.common.gui.elements.DrawableNineSliceTexture;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;

public class VoidTexture extends DrawableNineSliceTexture
{
	public VoidTexture()
	{
		super(null, null, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset, int width, int height)
	{
	}

	@Override
	public void draw(GuiGraphics guiGraphics, ImmutableRect2i area)
	{
	}
}
