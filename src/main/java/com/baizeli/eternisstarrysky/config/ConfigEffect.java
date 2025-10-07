package com.baizeli.eternisstarrysky.config;

public class ConfigEffect
{
	public String key;
	public int duration;
	public int amplifier;

	public ConfigEffect()
	{
	}

	public ConfigEffect(String key, int duration, int amplifier)
	{
		this.key = key;
		this.duration = duration;
		this.amplifier = amplifier;
	}
}
