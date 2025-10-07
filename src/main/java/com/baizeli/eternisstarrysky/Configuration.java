package com.baizeli.eternisstarrysky;

import com.baizeli.Trie;
import com.baizeli.eternisstarrysky.config.ConfigEffect;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Configuration
{
	private static final String KEY_ETERNIS_APPLE_EFFECTS = "effects";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File ETERNIS_APPLE_CONFIG = new File("./config/eternisstarrysky-EternisApple.json");
	private static boolean MOB_EFFECTS_STEUP = false;
	public static final short[] NAMESPACE_DICTIONARY;
	public static final ImmutableList<ConfigEffect> DEFAULT_ETERNIS_APPLE_EFFECTS;
	public static final AtomicReference<List<ConfigEffect>> ETERNIS_APPLE_EFFECTS;
	public static final ForgeConfigSpec.DoubleValue WHISPER_OF_THE_PAST_DAMAGE;
	public static final ForgeConfigSpec SPECIFICATION;
	public static final Logger LOGGER = LoggerFactory.getLogger("Configuration");
	public static final Trie MOB_EFFECTS;

	public static void saveEternisApple()
	{
		JsonObject eternisApple = new JsonObject();
		JsonArray effects = new JsonArray();
		for (ConfigEffect eff : ETERNIS_APPLE_EFFECTS.get())
		{
			JsonArray effect = new JsonArray();
			effect.add(eff.key);
			effect.add(eff.duration);
			effect.add(eff.amplifier);
			effects.add(effect);
		}
		eternisApple.add(KEY_ETERNIS_APPLE_EFFECTS, effects);
		String config = GSON.toJson(eternisApple);
		try (FileWriter writer = new FileWriter(ETERNIS_APPLE_CONFIG))
		{
			writer.write(config);
			writer.flush();
		}
		catch (IOException e)
		{
			LOGGER.error("Cannot save Eternis Apple config", e);
		}
	}

	public static void setup()
	{
		if (MOB_EFFECTS_STEUP)
			return;
		Configuration.MOB_EFFECTS.clear();
		for (ResourceLocation eid : ForgeRegistries.MOB_EFFECTS.getKeys())
		{
			try
			{
				Configuration.MOB_EFFECTS.add(eid.toString().getBytes(StandardCharsets.UTF_8));
			}
			catch (Throwable e)
			{
				LOGGER.warn("Cannot add effect ID: {}", eid, e);
			}
		}
		MOB_EFFECTS_STEUP = true;
	}

	public static int searchEffectID(String prefix, byte[] str)
	{
		if (prefix.isEmpty())
		{
			String val = "minecraft:";
			int len = Math.min(str.length, val.length());
			System.arraycopy(val.getBytes(), 0, str, 0, len);
			return len;
		}

		byte[] pfx = prefix.getBytes(StandardCharsets.UTF_8);
		byte[] buf = new byte[str.length];
		int slen = Configuration.MOB_EFFECTS.search(pfx, 0, pfx.length, buf, 0, buf.length);
		if (slen == -1)
			return -1;

		int cidx = -1;
		for (int i = 0; (i < pfx.length) && (cidx == -1); i++)
			if (pfx[i] == ':')
				cidx = i;

		int retVal = 0;
		for (; retVal < Math.min(slen, str.length); retVal++)
		{
			str[retVal] = buf[retVal];
			if (str[retVal] == ':')
			{
				retVal++;
				break;
			}
		}
		if (cidx != -1)
		{
			for (; retVal < Math.min(slen, str.length); retVal++)
				str[retVal] = buf[retVal];
		}
		return retVal;
	}

	public static boolean validateMobEffect(Object id)
	{
		if (!(id instanceof String))
			return false;
		return ForgeRegistries.MOB_EFFECTS.containsKey(ResourceLocation.tryParse((String) id));
	}

	static
	{
		NAMESPACE_DICTIONARY = Trie.DEFAULT_DICTIONARY.clone();
		NAMESPACE_DICTIONARY[':'] = 26;
		NAMESPACE_DICTIONARY['_'] = 27;
		for (int i = 0; i < 10; i++)
			NAMESPACE_DICTIONARY['0' + i] = (short) (28 + i);

		MOB_EFFECTS = new Trie(NAMESPACE_DICTIONARY);

		// Eternis Apple
		List<ConfigEffect> defaultEffects = List.of(
			new ConfigEffect("minecraft:regeneration", 600, 9),
			new ConfigEffect("minecraft:resistance", 600, 3),
			new ConfigEffect("minecraft:absorption", 600, 9)
		);
		DEFAULT_ETERNIS_APPLE_EFFECTS = ImmutableList.copyOf(defaultEffects);
		ETERNIS_APPLE_EFFECTS = new AtomicReference<>(DEFAULT_ETERNIS_APPLE_EFFECTS);

		boolean newFile = false;
		try
		{
			newFile = ETERNIS_APPLE_CONFIG.createNewFile();
			if (newFile)
				saveEternisApple();
		}
		catch (IOException e)
		{
			LOGGER.error("Cannot create Eternis Apple config", e);
		}

		if (!newFile)
		{
			try (FileReader reader = new FileReader(ETERNIS_APPLE_CONFIG))
			{
				JsonObject eternisApple = JsonParser.parseReader(reader).getAsJsonObject();
				if (eternisApple.get(KEY_ETERNIS_APPLE_EFFECTS).isJsonArray())
				{
					JsonArray effects = eternisApple.get(KEY_ETERNIS_APPLE_EFFECTS).getAsJsonArray();
					List<ConfigEffect> configEffects = new LinkedList<>();
					for (JsonElement element : effects)
					{
						if (!element.isJsonArray())
							continue;

						JsonArray effect = element.getAsJsonArray();
						if (effect.size() != 3)
							continue;

						JsonElement e0 = effect.get(0);
						JsonElement e1 = effect.get(1);
						JsonElement e2 = effect.get(2);
						if (!(e0.isJsonPrimitive()) || !(e1.isJsonPrimitive()) || !(e2.isJsonPrimitive()))
							continue;

						JsonPrimitive p0 = e0.getAsJsonPrimitive();
						JsonPrimitive p1 = e1.getAsJsonPrimitive();
						JsonPrimitive p2 = e2.getAsJsonPrimitive();
						if (!p0.isString())
							continue;
						if (!p1.isNumber())
							continue;
						if (!p2.isNumber())
							continue;

						ConfigEffect eff = new ConfigEffect();
						eff.key = p0.getAsString();
						eff.duration = p1.getAsInt();
						eff.amplifier = p2.getAsInt();
						configEffects.add(eff);
					}
					ETERNIS_APPLE_EFFECTS.set(List.copyOf(configEffects));
				}
			}
			catch (IOException e)
			{
				LOGGER.error("Cannot read Eternis Apple config", e);
			}
		}

		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		WHISPER_OF_THE_PAST_DAMAGE = builder
			.comment("Damage of Whisper of the Past")
			.defineInRange("WHISPER_OF_THE_PAST_DAMAGE", 60, 0.0, Double.MAX_VALUE);
		SPECIFICATION = builder.build();
	}
}
