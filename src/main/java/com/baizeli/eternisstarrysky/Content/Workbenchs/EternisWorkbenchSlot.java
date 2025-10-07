package com.baizeli.eternisstarrysky.Content.Workbenchs;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class EternisWorkbenchSlot extends SlotItemHandler
{
	public EternisWorkbenchSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPickup(Player playerIn)
	{
		return true;
	}
}
