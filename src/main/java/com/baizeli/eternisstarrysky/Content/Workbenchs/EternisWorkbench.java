package com.baizeli.eternisstarrysky.Content.Workbenchs;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class EternisWorkbench
{
	public static final String ASSETS_WORKBENCH = EternisStarrySky.resource("textures/gui/eternisstarrysky_workbench.png");
	public static final String ASSETS_INVENTORY = EternisStarrySky.resource("textures/gui/eternisstarrysky_inventory.png");
	public static final ResourceLocation RESOURCE_WORKBENCH = ResourceLocation.parse(ASSETS_WORKBENCH);
	public static final ResourceLocation RESOURCE_INVENTORY = ResourceLocation.parse(ASSETS_INVENTORY);
	public static final int ASSETS_WORKBENCH_WIDTH = 256;
	public static final int ASSETS_INVENTORY_WIDTH = 215;
	public static final int ASSETS_WORKBENCH_HEIGHT = 212;
	public static final int ASSETS_INVENTORY_HEIGHT = 124;
	public static final int GRAPHICS_TYPE_WORKBENCH = 0;
	public static final int GRAPHICS_TYPE_INVENTORY = 1;
	public static final int WORKBENCH_SLOT_X = 12;
	public static final int WORKBENCH_SLOT_Y = 27;
	public static final int WORKBENCH_RESULT_X = 210;
	public static final int WORKBENCH_RESULT_Y = 99;
	public static final int INVENTORY_SLOT_X = 27;
	public static final int INVENTORY_SLOT_Y = 25;

	public static void workbench(GuiGraphics graphics, int x, int y, int type)
	{
		switch (type)
		{
			case GRAPHICS_TYPE_WORKBENCH:
				graphics.blit(RESOURCE_WORKBENCH, x, y, 0, 0, ASSETS_WORKBENCH_WIDTH, ASSETS_WORKBENCH_HEIGHT, ASSETS_WORKBENCH_WIDTH, ASSETS_WORKBENCH_HEIGHT);
				break;
			case GRAPHICS_TYPE_INVENTORY:
				graphics.blit(RESOURCE_INVENTORY, x, y, 0, 0, ASSETS_INVENTORY_WIDTH, ASSETS_INVENTORY_HEIGHT, ASSETS_INVENTORY_WIDTH, ASSETS_INVENTORY_HEIGHT);
				break;
		}
	}
}
