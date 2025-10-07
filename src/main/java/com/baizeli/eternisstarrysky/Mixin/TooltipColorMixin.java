package com.baizeli.eternisstarrysky.Mixin;
import com.baizeli.eternisstarrysky.Items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTooltipEvent.Color.class)
public abstract class TooltipColorMixin {

    // 颜色定义
    @Unique
    private static final int BLUE_COLOR = 0xFF4FC3F7;  // 浅蓝 (RGB: 79,195,247)
    @Unique
    private static final int WHITE_COLOR = 0xFFFFFFFF; // 纯白 (RGB: 255,255,255)

    // 渐变周期（毫秒）
    @Unique
    private static final long COLOR_CYCLE_TIME = 3000; // 3秒完成一次蓝→白→蓝循环

    /**
     * 获取当前动态渐变颜色（线性插值）
     * @return 当前时刻的渐变色（不透明）
     */
    @Unique
    private int getCurrentGradientColor() {
        // 获取当前时间在周期中的进度（0.0 ~ 1.0）
        float progress = (System.currentTimeMillis() % COLOR_CYCLE_TIME) / (float) COLOR_CYCLE_TIME;

        // 使用正弦函数使过渡更平滑（可选）
        float lerpFactor = (float) Math.sin(progress * Math.PI * 2) * 0.5f + 0.5f;

        // 线性插值（Lerp）计算当前颜色
        return lerpColor(BLUE_COLOR, WHITE_COLOR, lerpFactor);
    }

    /**
     * 颜色插值计算（RGB通道分别插值）
     */
    @Unique
    private int lerpColor(int startColor, int endColor, float factor) {
        int r = (int) ((startColor >> 16 & 0xFF) * (1 - factor) + (endColor >> 16 & 0xFF) * factor);
        int g = (int) ((startColor >> 8 & 0xFF) * (1 - factor) + (endColor >> 8 & 0xFF) * factor);
        int b = (int) ((startColor & 0xFF) * (1 - factor) + (endColor & 0xFF) * factor);
        return 0xFF000000 | (r << 16) | (g << 8) | b; // 固定不透明度
    }

    // ---- 注入点 ----
    @Inject(method = "getBorderStart", at = @At("HEAD"), cancellable = true, remap = false)
    private void overrideBorderStart(CallbackInfoReturnable<Integer> cir) {
        if (isTargetItem()) {
            cir.setReturnValue(getCurrentGradientColor());
        }
    }

    @Inject(method = "getBorderEnd", at = @At("HEAD"), cancellable = true, remap = false)
    private void overrideBorderEnd(CallbackInfoReturnable<Integer> cir) {
        if (isTargetItem()) {
            // 边框结束色比开始色延迟1/4周期，形成动态渐变
            long offsetTime = (System.currentTimeMillis() + COLOR_CYCLE_TIME / 4) % COLOR_CYCLE_TIME;
            float progress = offsetTime / (float) COLOR_CYCLE_TIME;
            float lerpFactor = (float) Math.sin(progress * Math.PI * 2) * 0.5f + 0.5f;
            cir.setReturnValue(lerpColor(BLUE_COLOR, WHITE_COLOR, lerpFactor));
        }
    }

    @Inject(method = "getBackgroundStart", at = @At("HEAD"), cancellable = true, remap = false)
    private void overrideBackgroundStart(CallbackInfoReturnable<Integer> cir) {
        if (isTargetItem()) {
            cir.setReturnValue(getCurrentGradientColor() & 0x77FFFFFF); // 47%透明度
        }
    }

    @Inject(method = "getBackgroundEnd", at = @At("HEAD"), cancellable = true, remap = false)
    private void overrideBackgroundEnd(CallbackInfoReturnable<Integer> cir) {
        if (isTargetItem()) {
            cir.setReturnValue(getCurrentGradientColor() & 0x55FFFFFF); // 33%透明度
        }
    }

    // 检查目标物品
    @Unique
    private boolean isTargetItem() {
        RenderTooltipEvent.Color event = (RenderTooltipEvent.Color)(Object)this;
        ItemStack stack = event.getItemStack();
        return stack != null && (
                stack.getItem() == ModItems.INFINITY_SWORD_TRUE.get() ||
                        stack.getItem() == ModItems.AVARITIA_SWORD.get()||
                          stack.getItem() == ModItems.INFINITY_ETERNAL_HELMET.get()||
                          stack.getItem() == ModItems.INFINITY_ETERNAL_CHESTPLATE.get()||
                          stack.getItem() == ModItems.INFINITY_ETERNAL_LEGGINGS.get()||
                          stack.getItem() == ModItems.INFINITY_ETERNAL_BOOTS.get()||
                          stack.getItem() == ModItems.INFINITY_SWORD.get()||
                          stack.getItem() == ModItems.PURPLEITE_GALAXY_INGOT.get()||
                          stack.getItem() == ModItems.ETERNIS_APPLE.get()||
                          stack.getItem() == ModItems.WHISPER_OF_THE_PAST.get()||
                          stack.getItem() == ModItems.CREATE_STAR.get()||
                          stack.getItem() == ModItems.PURE_FRUIT.get()||
                          stack.getItem() == ModItems.GALAXY_SCROLL.get()||
                          stack.getItem() == ModItems.IMPURE_FRUIT.get()
        );
    }
}
