package com.baizeli.eternisstarrysky.config.menu;

import com.baizeli.eternisstarrysky.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ConfigMenu extends Screen
{
	public static final int PADDING = 6;
	private final Screen parent;
	public final EffectArrayValue ETERNIS_APPLE_EFFECTS;
	public final GroupValue GROUP_WHISPER_OF_THE_PAST;
	public final DoubleValue WHISPER_OF_THE_PAST_DAMAGE;

	public ConfigMenu(Screen parent)
	{
		super(Component.translatable("eternisstarrysky.config.title"));
		this.parent = parent;
		Minecraft mc = parent.getMinecraft();
		Configuration.setup();

		this.ETERNIS_APPLE_EFFECTS = new EffectArrayValue(this, "eternisstarrysky.config.eternis_apple.effects", mc.font);
		this.ETERNIS_APPLE_EFFECTS.value(Configuration.ETERNIS_APPLE_EFFECTS.get());
		this.ETERNIS_APPLE_EFFECTS.defaultValue = Configuration.DEFAULT_ETERNIS_APPLE_EFFECTS;
		this.ETERNIS_APPLE_EFFECTS.tooltip = Component.translatable("eternisstarrysky.config.eternis_apple.effects.tooltip");

		this.GROUP_WHISPER_OF_THE_PAST = new GroupValue(this, "eternisstarrysky.config.whisper_of_the_past", mc.font);

		this.WHISPER_OF_THE_PAST_DAMAGE = new DoubleValue(this, "eternisstarrysky.config.whisper_of_the_past.damage", mc.font);
		this.WHISPER_OF_THE_PAST_DAMAGE.min = 0.0;
		this.WHISPER_OF_THE_PAST_DAMAGE.max = Double.MAX_VALUE;
		this.WHISPER_OF_THE_PAST_DAMAGE.value(Configuration.WHISPER_OF_THE_PAST_DAMAGE.get());
		this.WHISPER_OF_THE_PAST_DAMAGE.resetValue = (val) -> ((DoubleValue) val).value(Configuration.WHISPER_OF_THE_PAST_DAMAGE.getDefault());
		this.GROUP_WHISPER_OF_THE_PAST.group.add(this.WHISPER_OF_THE_PAST_DAMAGE);
	}

	@Override
	public void onClose()
	{
		if (this.minecraft == null)
			super.onClose();
		else
			this.minecraft.setScreen(this.parent);
	}

	@Override
	public void init()
	{
		this.clearWidgets();

		int containerWidth = this.width - (2 * PADDING);
		int containerHeight = this.height - (3 * PADDING) - 20;

		int y = this.height - 20 - PADDING;
		int doneButtonWidth = Math.min(200, (this.width - (PADDING * 3)) / 2);
		Button saveButton = new Button.Builder(Component.translatable("eternisstarrysky.config.save"), b -> this.save())
			.bounds((width - (doneButtonWidth * 2) - PADDING) / 2, y, doneButtonWidth, 20)
			.build();
		Button doneButton = new Button.Builder(Component.translatable("eternisstarrysky.config.done"), (button1) -> this.close())
			.bounds((width - PADDING) / 2 + PADDING, y, doneButtonWidth, 20)
			.build();
		this.addRenderableWidget(saveButton);
		this.addRenderableWidget(doneButton);

		ConfigArray array = new ConfigArray(this.getMinecraft(), containerWidth, containerHeight, PADDING, PADDING);
		array.push(this.ETERNIS_APPLE_EFFECTS);
		array.push(this.GROUP_WHISPER_OF_THE_PAST);
		array.push(this.WHISPER_OF_THE_PAST_DAMAGE);
		this.addRenderableWidget(array);
	}

	@Override
	public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_)
	{
		this.renderBackground(p_281549_);
		super.render(p_281549_, p_281550_, p_282878_, p_282465_);
	}

	@Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_)
	{
		return super.keyPressed(p_96552_, p_96553_, p_96554_);
	}

	public void save()
	{
		Configuration.ETERNIS_APPLE_EFFECTS.set(List.copyOf(this.ETERNIS_APPLE_EFFECTS.value()));
		Configuration.saveEternisApple();
		Configuration.WHISPER_OF_THE_PAST_DAMAGE.set(this.WHISPER_OF_THE_PAST_DAMAGE.value.doubleValue());
		Configuration.WHISPER_OF_THE_PAST_DAMAGE.save();
	}

	public void close()
	{
		this.save();
		this.onClose();
	}
}
