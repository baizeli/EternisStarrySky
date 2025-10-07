package com.baizeli.eternisstarrysky.Entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SwordManFactory implements EntityType.EntityFactory<SwordManCsdy>
{
	@Override
	public SwordManCsdy create(EntityType<SwordManCsdy> entityType, Level level)
	{
		return new SwordManCsdy(entityType, level);
	}
}
