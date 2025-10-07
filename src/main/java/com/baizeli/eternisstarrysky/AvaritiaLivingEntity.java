package com.baizeli.eternisstarrysky;

import com.baizeli.ModuleAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class AvaritiaLivingEntity
{
	private static final MethodHandle DIE;
	private static final MethodHandle TICK_DEATH;

	public static void die(LivingEntity entity, DamageSource source)
	{
		try
		{
			DIE.invoke(entity, source);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	public static void tickDeath(LivingEntity e)
	{
		try
		{
			TICK_DEATH.invoke(e);
		}
		catch (Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}

	static
	{
		try
		{
			DIE = ModuleAccess.LOOKUP.findSpecial(LivingEntity.class, ObfuscationMap.mapping("die"), MethodType.methodType(void.class, DamageSource.class), LivingEntity.class);
			TICK_DEATH = ModuleAccess.LOOKUP.findSpecial(LivingEntity.class, ObfuscationMap.mapping("tickDeath"), MethodType.methodType(void.class), LivingEntity.class);
		}
		catch (Throwable t)
		{
			ModuleAccess.exception(t);
			throw new RuntimeException(t);
		}
	}
}
