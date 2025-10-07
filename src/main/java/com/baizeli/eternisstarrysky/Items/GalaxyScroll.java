package com.baizeli.eternisstarrysky.Items;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

public class GalaxyScroll extends Item
{
	public GalaxyScroll(Properties p_41383_)
	{
		super(p_41383_);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_)
	{
		if (p_41433_ instanceof ServerPlayer)
			return super.use(p_41432_, p_41433_, p_41434_);
		PatchouliAPI.get().openBookGUI(ResourceLocation.parse(EternisStarrySky.resource("galaxy_scroll")));
		return InteractionResultHolder.success(p_41433_.getItemInHand(p_41434_));
	}
}
