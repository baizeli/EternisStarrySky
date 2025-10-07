package com.baizeli.eternisstarrysky.Content.Workbenchs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VanillaWorkbenchScreen extends AbstractContainerScreen<VanillaWorkbenchMenu>
{
    public VanillaWorkbenchScreen(VanillaWorkbenchMenu menu, Inventory inventory, Component component)
    {
        super(menu, inventory, component);
        this.imageWidth = EternisWorkbench.ASSETS_WORKBENCH_WIDTH;
        this.imageHeight = EternisWorkbench.ASSETS_WORKBENCH_HEIGHT + EternisWorkbench.ASSETS_INVENTORY_HEIGHT;
    }

    @Override
    protected void init()
    {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 背景
        // guiGraphics.fill(x, y + 256, x + imageWidth, y + imageHeight, 0xFFcccccc);
        EternisWorkbench.workbench(guiGraphics, x, y, EternisWorkbench.GRAPHICS_TYPE_WORKBENCH);
        int invX = (this.width - EternisWorkbench.ASSETS_INVENTORY_WIDTH) >> 1;
        int invY = y + EternisWorkbench.ASSETS_WORKBENCH_HEIGHT;
        EternisWorkbench.workbench(guiGraphics, invX, invY, EternisWorkbench.GRAPHICS_TYPE_INVENTORY);
    }

    @Override protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, -this.font.lineHeight, 0x404040, false);// 绘制标题
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta)
    {
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}