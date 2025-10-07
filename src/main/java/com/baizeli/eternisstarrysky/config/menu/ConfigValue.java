package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ConfigValue
{
	public static final int COLOR_DEFAULT  = 0;
	public static final int COLOR_HOVER    = 1;
	public static final int COLOR_INACTIVE = 2;
	public AbstractContainerEventHandler parent;
	public final String key;
	public final Font font;
	public AbstractWidget valueWidget;
	public boolean visible = true;
	public boolean active = true;
	public Component tooltip;
	public int width = 200;
	public int valueWidth = 100;
	public int height = 20;
	public int lineHeight = 20;
	public int indent = 50;
	public final StringWidget text;
	public boolean resetEnable = true;
	public boolean resetActive = false;
	private final Button reset;
	public int previousX;
	public int previousY;
	public double mouseX;
	public double mouseY;
	public final int[] color = {0xFFFFFF, 0xFFFF55, 0x555555};
	public Consumer<ConfigValue> resetValue = null;

	public ConfigValue(AbstractContainerEventHandler parent, String key, Font font)
	{
		this.parent = parent;
		this.key = key;
		this.font = font;
		int textWidth = this.width - (2 * this.indent) - this.valueWidth - ConfigMenu.PADDING;
		int x = this.previousX;
		int y = this.previousY;
		this.text = new StringWidget(x + this.indent, y, textWidth, this.lineHeight, Component.translatable(this.key), this.font);
		this.text.alignLeft();
		this.reset = new Button.Builder(Component.literal("§c×"), this::onReset)
			.bounds(x + this.width - this.indent + ConfigMenu.PADDING, y, this.lineHeight, this.lineHeight)
			.tooltip(Tooltip.create(Component.translatable("eternisstarrysky.config.reset")))
			.build();
	}

	public AbstractWidget updateWidget(AbstractWidget widget)
	{
		int widgetX = this.previousX + (this.width - this.valueWidth - this.indent);
		if (this.valueWidget != null)
			widget.setPosition(widgetX, this.previousY);
		return widget;
	}

	public void draw(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
	{
		this.previousX = x;
		this.previousY = y;

		this.text.setPosition(x + this.indent, y);
		this.text.setWidth(this.width - (2 * this.indent) - this.valueWidth - ConfigMenu.PADDING);
		this.reset.setPosition(x + this.width - this.indent + ConfigMenu.PADDING, y);

		this.valueWidget = this.updateWidget(this.valueWidget);

		this.mouseX = mouseX;
		this.mouseY = mouseY;
		int textColor = this.color[this.hoverText() ? COLOR_HOVER : COLOR_DEFAULT];
		if (!this.active)
			textColor = this.color[COLOR_INACTIVE];
		this.text.setColor(textColor);
		this.text.active = this.active;
		if (this.tooltip != null)
			this.text.setTooltip(Tooltip.create(this.tooltip));
		if (this.valueWidget != null)
			this.valueWidget.active = this.active;
		this.resetActive = this.active && (this.resetValue != null);
		if (this.resetEnable)
			this.reset.active = this.resetActive;

		this.update();

		this.text.render(graphics, mouseX, mouseY, 0);
		if (this.valueWidget != null)
			this.valueWidget.render(graphics, mouseX, mouseY, 0);
		if (this.resetEnable)
			this.reset.render(graphics, mouseX, mouseY, 0);
	}

	public boolean click(double x, double y)
	{
		if (!this.active)
			return false;
		if (this.text.mouseClicked(x, y, 0))
		{
			this.parent.setFocused(null);
			this.clickText();
			return true;
		}
		if (this.valueWidget != null && this.valueWidget.mouseClicked(x, y, 0))
		{
			this.parent.setFocused(this.valueWidget);
			this.clickWidget();
			return true;
		}
		if (this.resetActive && this.reset.mouseClicked(x, y, 0))
		{
			this.parent.setFocused(this.reset);
			return true;
		}
		return false;
	}

	public boolean hoverText()
	{
		int textWidth = this.text.getWidth();
		if (this.mouseX < this.previousX + this.indent)
			return false;
		if (this.mouseX > this.previousX + this.indent + textWidth)
			return false;
		if (this.mouseY < this.previousY)
			return false;
		return !(this.mouseY > this.previousY + this.lineHeight);
	}

	public void visible(boolean visible)
	{
		this.visible = visible;
	}

	public void update()
	{
	}

	public void clickText()
	{
	}

	public void clickWidget()
	{
	}

	public void onReset(Button button)
	{
		if (this.resetValue != null)
			this.resetValue.accept(this);
	}
}
