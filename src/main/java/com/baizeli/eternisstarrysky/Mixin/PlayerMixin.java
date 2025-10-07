package com.baizeli.eternisstarrysky.Mixin;

import com.baizeli.eternisstarrysky.AvaritiaVulnerable;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements AvaritiaVulnerable
{
	@Unique
	public boolean vulnerable = true;

	@Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
	public void hurt(DamageSource p_36154_, float p_36155_, CallbackInfoReturnable<Boolean> cir)
	{
		if (p_36154_.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
			return;
		if (!this.vulnerable)
			cir.setReturnValue(true);
	}

	@Unique
	@Override
	public boolean vulnerable()
	{
		return this.vulnerable;
	}

	@Unique
	@Override
	public void vulnerable(boolean vulnerable)
	{
		this.vulnerable = vulnerable;
	}
}
