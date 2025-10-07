package com.baizeli.eternisstarrysky.config.menu;

import com.baizeli.eternisstarrysky.Configuration;
import com.baizeli.eternisstarrysky.config.ConfigEffect;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EffectArrayValue extends ConfigValue
{
	public ImmutableList<ConfigEffect> defaultValue;
	private boolean expand = false;
	public final ArrayList<EffectEntry> value = new ArrayList<>();
	private final Button addon;
	private final byte[] buffer = new byte[4096];

	public EffectArrayValue(Screen screen, String key, Font font)
	{
		super(screen, key, font);
		this.resetEnable = true;
		this.addon = new Button.Builder(Component.literal("+"), this::onAddon)
			.size(this.boxWidth(), this.lineHeight)
			.build();
		this.resetValue = this::reset;
	}

	@Override
	public void draw(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
	{
		super.draw(graphics, x, y, mouseX, mouseY);

		x += this.indent + (2 * this.lineHeight) + ConfigMenu.PADDING;
		y += this.lineHeight + ConfigMenu.PADDING;
		for (EffectEntry entry : this.value)
		{
			entry.remove.setPosition(x - this.lineHeight - ConfigMenu.PADDING, y);
			entry.remove.setWidth(this.lineHeight);
			entry.remove.setHeight(this.lineHeight);
			int boxWidth = this.boxWidth();
			int idWidth = (boxWidth - ConfigMenu.PADDING) / 2;
			int numWidth = (boxWidth - idWidth - 2 * ConfigMenu.PADDING) / 2;
			entry.ID.setPosition(x, y);
			entry.ID.setWidth(idWidth);
			entry.ID.setHeight(this.lineHeight);
			entry.duration.setPosition(x + idWidth + ConfigMenu.PADDING, y);
			entry.duration.setWidth(numWidth);
			entry.duration.setHeight(this.lineHeight);
			entry.amplifier.setPosition(x + idWidth + numWidth + 2 * ConfigMenu.PADDING, y);
			entry.amplifier.setWidth(numWidth);
			entry.amplifier.setHeight(this.lineHeight);
			int valLen = entry.ID.getValue().length();
			// Suggestion

			int hintLength = Configuration.searchEffectID(entry.ID.getValue(), this.buffer);
			if (hintLength > 0)
				entry.ID.setSuggestion(new String(this.buffer, valLen, hintLength - valLen));
			else
				entry.ID.setSuggestion("");

			y += this.lineHeight + ConfigMenu.PADDING;
		}
		this.addon.setPosition(x, y);
		this.addon.setWidth(this.boxWidth());
		this.addon.setHeight(this.lineHeight);
		this.updateHeight();

		if (!this.expand)
			return;
		for (EffectEntry value : this.value)
		{
			value.remove.render(graphics, mouseX, mouseY, 0);
			int color = 0xFF5555;
			// Check valid
			if (Configuration.validateMobEffect(value.ID.getValue()))
				color = 0x55FF55;

			value.ID.setTextColor(color);
			value.ID.render(graphics, mouseX, mouseY, 0);
			value.duration.render(graphics, mouseX, mouseY, 0);
			value.amplifier.render(graphics, mouseX, mouseY, 0);
		}
		this.addon.render(graphics, mouseX, mouseY, 0);
	}

	@Override
	public boolean click(double x, double y)
	{
		if (super.click(x, y))
			return true;

		if ((x < this.indent) || (x > this.previousX + this.width - this.indent))
			return false;

		if (!this.expand)
			return false;

		boolean flag = false;
		for (int i = 0; i < this.value.size(); i++)
		{
			EffectEntry value = this.value.get(i);
			GuiEventListener elem = value.click(x, y);
			if (elem == value.remove)
			{
				this.value.remove(i);
				return true;
			}
			if (elem != null)
			{
				flag = true;
				this.parent.setFocused(elem);
				break;
			}
		}
		if (flag)
			return true;
		flag = this.addon.mouseClicked(x, y, 0);
		if (flag)
			this.parent.setFocused(this.addon);
		return flag;
	}

	@Override
	public void update()
	{
		this.resetActive = this.defaultValue != null;
	}

	@Override
	public void clickText()
	{
		this.expand = !this.expand;
		this.updateHeight();
	}

	public void reset(ConfigValue ignored)
	{
		this.value(this.defaultValue);
	}

	public void updateHeight()
	{
		int newHeight = this.lineHeight;
		if (this.expand)
			newHeight += ((this.value.size() + 1) * (this.lineHeight + ConfigMenu.PADDING)) + ConfigMenu.PADDING;
		this.height = newHeight;
	}

	public void onAddon(Button b)
	{
		this.value.add(this.entry(new ConfigEffect("", 0, 0)));
	}

	public void value(List<? extends ConfigEffect> value)
	{
		this.value.clear();
		for (ConfigEffect s : value)
		{
			this.value.add(this.entry(s));
		}
	}

	public List<ConfigEffect> value()
	{
		LinkedList<ConfigEffect> list = new LinkedList<>();
		for (EffectEntry value : this.value)
			if (!value.ID.getValue().isEmpty())
				list.add(new ConfigEffect(value.ID.getValue(), Integer.parseInt(value.duration.getValue()), Integer.parseInt(value.amplifier.getValue())));
		return list;
	}

	public EffectEntry entry(ConfigEffect eff)
	{
		EffectEntry entry = new EffectEntry();
		entry.remove = new Button.Builder(Component.literal("§c-"), (b) -> {})
			.size(this.lineHeight, this.lineHeight)
			.build();
		int x = this.previousX + this.indent + (2 * this.lineHeight) + ConfigMenu.PADDING;
		int y = this.previousY + this.lineHeight + ConfigMenu.PADDING;
		int boxWidth = this.boxWidth();
		int editWidth = (boxWidth - (2 * ConfigMenu.PADDING)) / 3;
		entry.ID = new SuggestionEdit(this.font, x, y, editWidth, this.lineHeight, Component.empty());
		entry.ID.setValue(eff.key);
		entry.ID.setHighlightPos(0);
		entry.ID.setCursorPosition(0);
		x += editWidth + ConfigMenu.PADDING;
		entry.duration = new EditBox(this.font, x, y, editWidth, this.lineHeight, Component.empty());
		entry.duration.setValue(String.valueOf(eff.duration));
		entry.duration.setHighlightPos(0);
		entry.duration.setCursorPosition(0);
		entry.duration.setTooltip(Tooltip.create(Component.literal("0 ~ 2147483647")));
		entry.duration.setResponder(entry::duration);
		x += editWidth + ConfigMenu.PADDING;
		entry.amplifier = new EditBox(this.font, x, y, editWidth, this.lineHeight, Component.empty());
		entry.amplifier.setValue(String.valueOf(eff.amplifier));
		entry.amplifier.setHighlightPos(0);
		entry.amplifier.setCursorPosition(0);
		entry.amplifier.setTooltip(Tooltip.create(Component.literal("0 ~ 255")));
		entry.amplifier.setResponder(entry::amplifier);
		return entry;
	}

	public int boxWidth()
	{
		return this.width - (2 * this.indent) - (2 * this.lineHeight) - ConfigMenu.PADDING;
	}
}
