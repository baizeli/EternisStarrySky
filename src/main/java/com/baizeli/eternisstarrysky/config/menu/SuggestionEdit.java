package com.baizeli.eternisstarrysky.config.menu;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SuggestionEdit extends EditBox
{
	private String suggestion = null;

	public SuggestionEdit(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_)
	{
		super(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, p_94119_);
	}

	@Override
	public void setSuggestion(String suggestion)
	{
		this.suggestion = suggestion;
		super.setSuggestion(suggestion);
	}

	@Override
	public boolean keyPressed(int p_94132_, int p_94133_, int p_94134_)
	{
		if (p_94132_ == 258) // Tab
		{
			if (this.suggestion != null && !this.suggestion.isEmpty())
				this.setValue(this.getValue() + this.suggestion);
			return true;
		}
		return super.keyPressed(p_94132_, p_94133_, p_94134_);
	}
}
