package com.baizeli.eternisstarrysky.Mixin;

import com.baizeli.eternisstarrysky.EntityMarker;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SynchedEntityData.class)
public class synchedDataMixin
{
	// 使用 @Shadow 获取 SynchedEntityData 的 entity 字段
	@Shadow @Final
	private Entity entity;

	@Inject(method = "get", at = @At("RETURN"), cancellable = true)
	public void onGetData(EntityDataAccessor<?> key, CallbackInfoReturnable<Object> cir)
	{
		// 直接使用 shadow 字段获取实体
		if (this.entity == null)
			return;
		if (!(cir.getReturnValue() instanceof Float))
			return;
		if (!EntityMarker.has(this.entity, EntityMarker.ENTITY_DATA_HEALTH))
			return;
		cir.setReturnValue(0.0F);
	}
}