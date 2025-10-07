package com.baizeli.eternisstarrysky;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class ObfuscationMap
{
	private static final boolean SRG;
	private static final Map<String, String> MCP2SRG = new HashMap<String, String>();

	public static String mapping(String mcp)
	{
		if (ObfuscationMap.SRG && MCP2SRG.containsKey(mcp))
			mcp = MCP2SRG.get(mcp);
		return mcp;
	}

	static
	{
		boolean srg = true;
		try
		{
			LivingEntity.class.getDeclaredMethod("tickDeath");
			srg = false;
		}
		catch (NoSuchMethodException ignored)
		{
		}
		SRG = srg;
		MCP2SRG.put("die", "m_6667_");
		MCP2SRG.put("tickDeath", "m_6153_");
	}
}
