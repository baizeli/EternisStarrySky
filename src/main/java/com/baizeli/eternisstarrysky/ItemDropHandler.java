package com.baizeli.eternisstarrysky;

import com.baizeli.eternisstarrysky.Items.InfinitySwordTrue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = EternisStarrySky.MOD_ID)
public class ItemDropHandler {

    private static final Map<UUID, Integer> lastHeldSlots = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        lastHeldSlots.put(player.getUUID(), player.getInventory().selected);
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemStack itemStack = event.getEntity().getItem();
        if (itemStack.getItem() instanceof InfinitySwordTrue infinitySword) {
            event.setCanceled(true);

            if (event.getPlayer() != null) {
                Player player = event.getPlayer();
                Integer lastSlot = lastHeldSlots.get(player.getUUID());

                if (lastSlot != null) {
                    player.getInventory().setItem(lastSlot, itemStack.copy());
                } else {
                    player.getInventory().setItem(player.getInventory().selected, itemStack.copy());
                }
            }
        }
    }
}
