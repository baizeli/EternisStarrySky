package com.baizeli.eternisstarrysky.Mixin;

import com.baizeli.eternisstarrysky.EntityMarker;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements EntityMarker
{
	@Unique
	private long mark = 0;

	@Override
	public void mark(long mark)
	{
		this.mark = mark;
	}

	@Override
	public long mark()
	{
		return this.mark;
	}
}
