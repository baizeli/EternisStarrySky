package com.baizeli.eternisstarrysky.Mixin;

import com.baizeli.eternisstarrysky.AvaritiaKill;
import com.baizeli.eternisstarrysky.AvaritiaLivingEntity;
import com.baizeli.eternisstarrysky.Items.InfinitySwordTrue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements AvaritiaKill
{
	@Shadow public int deathTime;

	@Shadow public abstract void remove(Entity.RemovalReason p_276115_);

	@Shadow public abstract float tickHeadTurn(float p_21260_, float p_21261_);

	@Shadow public abstract boolean isAlive();

	@Unique
	private boolean ava = false;
	@Unique
	private int death = 0;

	@Inject(
		method = "baseTick()V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z")
	)
	public void baseTick(CallbackInfo ci)
	{
		if (this.ava)
			this.death++;
		else
			this.death = 0;
		if (this.death >= 20)
		{
			this.deathTime = 20;
			AvaritiaLivingEntity.tickDeath((LivingEntity) (Object) this);
		}
	}

	/*
	@Overwrite
	public float getHealth(){
		 if (InfinitySwordTrue.entities.contains(this)){
			return 0;
		 }
		 return InfinitySwordTrue.getHealth(this);
	}
	 */

	@Override
	public boolean dead()
	{
		return this.ava;
	}

	@Override
	public void dead(boolean dead)
	{
		this.ava = dead;
	}
}
