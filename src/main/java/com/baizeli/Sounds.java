package com.baizeli;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class Sounds
{
	private static final RandomSource RANDOM = RandomSource.create();

	public static void play(SoundEvent sound, Player player, float volume, float pitch)
	{
		if (player == null)
			return;
		if (sound == null)
			return;
		if (!(player instanceof ServerPlayer sp))
		{
			player.playSound(sound, volume, pitch);
			return;
		}

		Optional<Holder<SoundEvent>> optional = ForgeRegistries.SOUND_EVENTS.getHolder(sound);
		if (optional.isEmpty())
			return;
		long seed = RANDOM.nextLong();
		ClientboundSoundPacket packet = new ClientboundSoundPacket(
			optional.get(),
			SoundSource.BLOCKS,
			player.getX(),
			player.getY() + 0.5F,
			player.getZ(),
			volume,
			pitch,
			seed
		);
		sp.connection.send(packet);
	}
}
