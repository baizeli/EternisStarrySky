package com.baizeli.eternisstarrysky.Bypass;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BypassHelp {

    public static boolean out = false; // true 就禁用

    public static void resetEntityAttribute(Entity entity, String attributeName) {
        if (!(entity instanceof LivingEntity living)) return;

        switch (attributeName) {
            case "damage":
            case "attack":
                living.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(living.getAttribute(Attributes.ATTACK_DAMAGE).getAttribute().getDefaultValue());
                break;
            case "scale":
            case "size":
                //living.getAttribute(Attributes.SCALE).setBaseValue(1.0);
                break;
            case "life":
            case "health":
                living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(living.getAttribute(Attributes.MAX_HEALTH).getAttribute().getDefaultValue());
                break;
            case "speed":
            case "movement":
                living.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(living.getAttribute(Attributes.MOVEMENT_SPEED).getAttribute().getDefaultValue());
                break;
            case "jump":
            case "jump_strength":
                living.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(living.getAttribute(Attributes.JUMP_STRENGTH).getAttribute().getDefaultValue());
                break;
            case "armor":
                living.getAttribute(Attributes.ARMOR).setBaseValue(0.0);
                break;
            case "luck":
                living.getAttribute(Attributes.LUCK).setBaseValue(0.0);
                break;
        }
    }

    public static long parseTimeString(String[] parts, int startIndex) {
        long totalTime = 0;

        for (int i = startIndex; i < parts.length; i++) {
            String part = parts[i];
            try {
                if (part.endsWith("天")) {
                    int days = Integer.parseInt(part.substring(0, part.length() - 1));
                    totalTime += TimeUnit.DAYS.toMillis(days);
                } else if (part.endsWith("时")) {
                    int hours = Integer.parseInt(part.substring(0, part.length() - 1));
                    totalTime += TimeUnit.HOURS.toMillis(hours);
                } else if (part.endsWith("分")) {
                    int minutes = Integer.parseInt(part.substring(0, part.length() - 1));
                    totalTime += TimeUnit.MINUTES.toMillis(minutes);
                }
            } catch (NumberFormatException e) {
            }
        }

        return totalTime;
    }

    public static boolean setEntityAttribute(Entity entity, String attributeName, String value) {
        try {
            switch (attributeName) {
                case "damage":
                case "attack":
                    if (entity instanceof LivingEntity living) {
                        double damageValue = Double.parseDouble(value);
                        living.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damageValue);
                        return true;
                    }
                    break;

                case "scale":
                case "size":
                    if (entity instanceof LivingEntity living) {
                        float scaleValue = Float.parseFloat(value);
                        //living.getAttribute(Attributes.SCALE).setBaseValue(scaleValue);
                        return true;
                    }
                    break;

                case "life":
                case "health":
                    if (entity instanceof LivingEntity living) {
                        float healthValue = Float.parseFloat(value);
                        living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(healthValue);
                        living.setHealth(healthValue);
                        return true;
                    }
                    break;

                case "speed":
                case "movement":
                    if (entity instanceof LivingEntity living) {
                        double speedValue = Double.parseDouble(value);
                        living.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speedValue);
                        return true;
                    }
                    break;

                case "jump":
                case "jump_strength":
                    if (entity instanceof LivingEntity living) {
                        double jumpValue = Double.parseDouble(value);
                        living.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpValue);
                        return true;
                    }
                    break;

                case "armor":
                    if (entity instanceof LivingEntity living) {
                        double armorValue = Double.parseDouble(value);
                        living.getAttribute(Attributes.ARMOR).setBaseValue(armorValue);
                        return true;
                    }
                    break;

                case "luck":
                    if (entity instanceof LivingEntity living) {
                        double luckValue = Double.parseDouble(value);
                        living.getAttribute(Attributes.LUCK).setBaseValue(luckValue);
                        return true;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    public static Item getItemByName(String name) {
        if (name.matches("[a-z0-9_:./-]+") && name.contains(":")) {
            try {
                ResourceLocation resourceLocation = new ResourceLocation(name);
                if (BuiltInRegistries.ITEM.containsKey(resourceLocation)) {
                    return BuiltInRegistries.ITEM.get(resourceLocation);
                }
            } catch (Exception e) {
            }
        }

        if (name.matches("[a-z0-9_-]+")) {
            try {
                ResourceLocation minecraftLocation = new ResourceLocation("minecraft", name);
                if (BuiltInRegistries.ITEM.containsKey(minecraftLocation)) {
                    return BuiltInRegistries.ITEM.get(minecraftLocation);
                }
            } catch (Exception e) {
            }

            Set<String> namespaces = BuiltInRegistries.ITEM.keySet().stream()
                    .map(ResourceLocation::getNamespace)
                    .collect(Collectors.toSet());

            for (String namespace : namespaces) {
                try {
                    ResourceLocation location = new ResourceLocation(namespace, name);
                    if (BuiltInRegistries.ITEM.containsKey(location)) {
                        return BuiltInRegistries.ITEM.get(location);
                    }
                } catch (Exception e) {
                }
            }
        }

        List<Item> allItems = BuiltInRegistries.ITEM.stream().collect(Collectors.toList());

        Item exactMatch = allItems.stream()
                .filter(item -> getItemDisplayName(item).equals(name))
                .findFirst()
                .orElse(null);

        if (exactMatch != null) {
            return exactMatch;
        }

        // 包含匹配
        return allItems.stream()
                .filter(item -> {
                    String displayName = getItemDisplayName(item);
                    return displayName.contains(name) ||
                            displayName.toLowerCase().contains(name.toLowerCase());
                })
                .findFirst()
                .orElse(null);
    }

    public static String getItemDisplayName(Item item) {
        return new ItemStack(item).getHoverName().getString();
    }

    public static List<Entity> findEntitiesByName(ServerPlayer player, String name, double range) {
        Vec3 playerPos = player.position();
        AABB searchBox = new AABB(
                playerPos.x - range, playerPos.y - range, playerPos.z - range,
                playerPos.x + range, playerPos.y + range, playerPos.z + range
        );

        List<Entity> entities = player.level().getEntitiesOfClass(Entity.class, searchBox)
                .stream()
                .filter(entity -> entity != player)
                .collect(Collectors.toList());

        // 优先匹配完全相同的名称
        List<Entity> exactMatches = entities.stream()
                .filter(entity -> {
                    String displayName = getEntityDisplayName(entity);
                    String entityTypeName = entity.getType().getDescription().getString();
                    return displayName.equals(name) || entityTypeName.equals(name);
                })
                .collect(Collectors.toList());

        if (!exactMatches.isEmpty()) {
            return exactMatches;
        }

        // 然后匹配包含关系
        return entities.stream()
                .filter(entity -> {
                    String displayName = getEntityDisplayName(entity);
                    String entityTypeName = entity.getType().getDescription().getString();
                    return displayName.contains(name) || entityTypeName.contains(name) ||
                            displayName.toLowerCase().contains(name.toLowerCase()) ||
                            entityTypeName.toLowerCase().contains(name.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    public static String getEntityDisplayName(Entity entity) {
        return entity.getDisplayName().getString();
    }

    public static void handleRemoveCommand(ServerPlayer player, String[] parts) {
        int removeLevel = 1;
        double range = 5.0;
        String targetName = null;

        if (parts.length > 1) {
            targetName = parts[1];
        }

        if (parts.length > 2) {
            try {
                removeLevel = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                player.sendSystemMessage(Component.literal("§c移除等级必须是数字！"));
                return;
            }
        }

        if (parts.length > 3) {
            try {
                range = Double.parseDouble(parts[3]);
            } catch (NumberFormatException e) {
                player.sendSystemMessage(Component.literal("§c范围必须是数字！"));
                return;
            }
        }

        if (targetName != null) {
            List<Entity> targetEntities = BypassHelp.findEntitiesByName(player, targetName, range);
            if (!targetEntities.isEmpty()) {
                int removedCount = 0;
                String entityName = "";
                for (Entity entity : targetEntities) {
                    entityName = BypassHelp.getEntityDisplayName(entity);

                    switch (removeLevel) {
                        case 1:
                            entity.remove(Entity.RemovalReason.KILLED);
                            break;
                        case 2:
                            entity.remove(Entity.RemovalReason.DISCARDED);
                            break;
                        case 3:
                            entity.discard();
                            break;
                        default:
                            entity.kill();
                            break;
                    }
                    removedCount++;
                }
                player.sendSystemMessage(Component.literal("§a成功移除 " + entityName + " " + removedCount + "x"));
            } else {
                player.sendSystemMessage(Component.literal("§c未找到目标实体！"));
            }
        } else {
            Entity targetEntity = getTargetEntity(player, range);
            if (targetEntity != null) {
                String entityName = BypassHelp.getEntityDisplayName(targetEntity);

                switch (removeLevel) {
                    case 1:
                        targetEntity.remove(Entity.RemovalReason.KILLED);
                        break;
                    case 2:
                        targetEntity.remove(Entity.RemovalReason.DISCARDED);
                        break;
                    case 3:
                        targetEntity.discard();
                        break;
                    default:
                        targetEntity.kill();
                        break;
                }

                player.sendSystemMessage(Component.literal("§a成功移除 " + entityName + " 1x"));
            } else {
                player.sendSystemMessage(Component.literal("§c未找到目标实体！"));
            }
        }
    }

    public static void handleGainCommand(ServerPlayer player, String[] parts) {
        if (parts.length < 2) {
            player.sendSystemMessage(Component.literal("§c用法：\\bypassop gain <物品名称> [数量] [玩家名]"));
            return;
        }

        String itemName = parts[1];
        int amount = 1;
        ServerPlayer targetPlayer = player;

        if (parts.length > 2) {
            try {
                amount = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                player.sendSystemMessage(Component.literal("§c数量必须是数字！"));
                return;
            }
        }

        if (parts.length > 3) {
            targetPlayer = player.getServer().getPlayerList().getPlayerByName(parts[3]);
            if (targetPlayer == null) {
                player.sendSystemMessage(Component.literal("§c找不到玩家：" + parts[3]));
                return;
            }
        }

        Item item = BypassHelp.getItemByName(itemName);
        if (item != null) {
            ItemStack itemStack = new ItemStack(item, amount);
            targetPlayer.getInventory().add(itemStack);

            String displayName = BypassHelp.getItemDisplayName(item);
            player.sendSystemMessage(Component.literal("§a成功添加 [" + displayName + "] " + amount + "x"));
        } else {
            player.sendSystemMessage(Component.literal("§c不存在 " + itemName + " 该物品"));
        }
    }

    public static void handleOpLevelCommand(ServerPlayer player, String[] parts) {
        if (parts.length < 2) {
            player.sendSystemMessage(Component.literal("§c用法：\\bypassop oplevel <等级> [玩家名]"));
            return;
        }

        int opLevel;
        try {
            opLevel = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            player.sendSystemMessage(Component.literal("§cOP等级必须是数字！"));
            return;
        }

        ServerPlayer targetPlayer = player;
        if (parts.length > 2) {
            targetPlayer = player.getServer().getPlayerList().getPlayerByName(parts[2]);
            if (targetPlayer == null) {
                player.sendSystemMessage(Component.literal("§c找不到玩家：" + parts[2]));
                return;
            }
        }

        player.getServer().getPlayerList().op(targetPlayer.getGameProfile());
        player.sendSystemMessage(Component.literal("§a成功设置 " + targetPlayer.getName().getString() + " 的OP等级为 " + opLevel));
    }

    public static Entity getTargetEntity(ServerPlayer player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0F).scale(range));

        AABB searchBox = new AABB(
                Math.min(start.x, end.x) - 1, Math.min(start.y, end.y) - 1, Math.min(start.z, end.z) - 1,
                Math.max(start.x, end.x) + 1, Math.max(start.y, end.y) + 1, Math.max(start.z, end.z) + 1
        );

        return player.level().getEntitiesOfClass(Entity.class, searchBox)
                .stream()
                .filter(entity -> entity != player)
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                .orElse(null);
    }

    public static void handleItemSetCommand(ServerPlayer player, String[] parts) {
        if (parts.length < 3) {
            player.sendSystemMessage(Component.literal("§c用法：\\bypassop itemset <属性> <值> [额外参数]"));
            return;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c请先手持一个物品！"));
            return;
        }

        String property = parts[1].toLowerCase();
        String value = parts[2];

        switch (property) {
            case "stack":
            case "count":
                try {
                    int stackSize = Integer.parseInt(value);
                    heldItem.setCount(stackSize);
                    player.sendSystemMessage(Component.literal("§a成功设置堆叠数量为：" + stackSize));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c堆叠数量必须是数字！"));
                }
                break;

            case "id":
            case "item":
                Item newItem = BypassHelp.getItemByName(value);
                if (newItem != null) {
                    int count = heldItem.getCount();
                    CompoundTag tag = heldItem.getTag();
                    ItemStack newStack = new ItemStack(newItem, count);
                    if (tag != null) {
                        newStack.setTag(tag.copy());
                    }
                    player.setItemInHand(InteractionHand.MAIN_HAND, newStack);
                    player.sendSystemMessage(Component.literal("§a成功转换物品为：" + BypassHelp.getItemDisplayName(newItem)));
                } else {
                    player.sendSystemMessage(Component.literal("§c未找到物品：" + value));
                }
                break;

            case "damage":
            case "attack":
                try {
                    double damageValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.attack_damage", "Attack Damage", damageValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置攻击力为：" + damageValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c攻击力数值无效！"));
                }
                break;

            case "name":
                String displayName = combineArgs(parts, 2);
                displayName = parseColorCodes(displayName);
                heldItem.setHoverName(Component.literal(displayName));
                player.sendSystemMessage(Component.literal("§a成功设置物品名称"));
                break;

            case "lore":
                if (parts.length < 4) {
                    player.sendSystemMessage(Component.literal("§c用法：\\bypassop itemset lore <行数> <内容>"));
                    return;
                }

                try {
                    int lineNumber = Integer.parseInt(value);
                    String loreText = combineArgs(parts, 3);
                    loreText = parseColorCodes(loreText);

                    CompoundTag tag = heldItem.getOrCreateTag();
                    CompoundTag display = tag.getCompound("display");
                    ListTag lore = display.getList("Lore", 8); // 8 = StringTag type

                    while (lore.size() < lineNumber) {
                        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(""))));
                    }

                    lore.set(lineNumber - 1, StringTag.valueOf(Component.Serializer.toJson(Component.literal(loreText))));

                    display.put("Lore", lore);
                    tag.put("display", display);
                    heldItem.setTag(tag);
                    player.sendSystemMessage(Component.literal("§a成功设置第" + lineNumber + "行描述"));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c行数必须是数字！"));
                }
                break;

            case "enc":
            case "enchant":
                if (parts.length < 4) {
                    player.sendSystemMessage(Component.literal("§c用法：\\bypassop itemset enc <附魔名> <等级>"));
                    return;
                }

                String enchantName = parts[2];
                try {
                    int level = Integer.parseInt(parts[3]);
                    Enchantment enchantment = getEnchantmentByName(enchantName);
                    if (enchantment != null) {
                        // 直接修改NBT以支持高等级附魔
                        CompoundTag tag = heldItem.getOrCreateTag();
                        ListTag enchantments = tag.getList("Enchantments", 10);

                        // 移除已存在的相同附魔
                        enchantments.removeIf(enchTag -> {
                            CompoundTag compound = (CompoundTag) enchTag;
                            return compound.getString("id").equals(BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString());
                        });

                        // 添加新附魔
                        CompoundTag enchantTag = new CompoundTag();
                        enchantTag.putString("id", BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString());
                        enchantTag.putInt("lvl", level);
                        enchantments.add(enchantTag);

                        tag.put("Enchantments", enchantments);
                        heldItem.setTag(tag);

                        player.sendSystemMessage(Component.literal("§a成功添加附魔：" + getEnchantmentDisplayName(enchantment) + " " + level));
                    } else {
                        player.sendSystemMessage(Component.literal("§c未找到附魔：" + enchantName));
                    }
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c附魔等级必须是数字！"));
                }
                break;

            case "unbreakable":
                boolean unbreakable = value.equalsIgnoreCase("true") || value.equals("1");
                CompoundTag unbreakableTag = heldItem.getOrCreateTag();
                unbreakableTag.putBoolean("Unbreakable", unbreakable);
                heldItem.setTag(unbreakableTag);
                player.sendSystemMessage(Component.literal("§a成功设置无损坏：" + unbreakable));
                break;

            case "durability":
            case "damage_value":
                try {
                    int durability = Integer.parseInt(value);
                    heldItem.setDamageValue(durability);
                    player.sendSystemMessage(Component.literal("§a成功设置耐久损伤为：" + durability));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c耐久值必须是数字！"));
                }
                break;

            case "speed":
            case "attack_speed":
                try {
                    double speedValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.attack_speed", "Attack Speed", speedValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置攻击速度为：" + speedValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c攻击速度数值无效！"));
                }
                break;

            case "nbt":
                try {
                    String nbtString = combineArgs(parts, 2);
                    CompoundTag newTag = TagParser.parseTag(nbtString);
                    CompoundTag currentTag = heldItem.getOrCreateTag();
                    currentTag.merge(newTag);
                    heldItem.setTag(currentTag);
                    player.sendSystemMessage(Component.literal("§a成功添加NBT数据"));
                } catch (Exception e) {
                    player.sendSystemMessage(Component.literal("§cNBT格式错误：" + e.getMessage()));
                }
                break;

            case "armor":
                try {
                    double armorValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.armor", "Armor", armorValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置护甲值为：" + armorValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c护甲值必须是数字！"));
                }
                break;

            case "armor_toughness":
            case "toughness":
                try {
                    double toughnessValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.armor_toughness", "Armor Toughness", toughnessValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置护甲韧性为：" + toughnessValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c护甲韧性必须是数字！"));
                }
                break;

            case "knockback":
            case "knockback_resistance":
                try {
                    double knockbackValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.knockback_resistance", "Knockback Resistance", knockbackValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置击退抗性为：" + knockbackValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c击退抗性必须是数字！"));
                }
                break;

            case "movement_speed":
            case "move_speed":
                try {
                    double speedValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.movement_speed", "Movement Speed", speedValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置移动速度为：" + speedValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c移动速度必须是数字！"));
                }
                break;

            case "luck":
                try {
                    double luckValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.luck", "Luck", luckValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置幸运值为：" + luckValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c幸运值必须是数字！"));
                }
                break;

            case "max_health":
            case "health":
                try {
                    double healthValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.max_health", "Max Health", healthValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置最大生命值为：" + healthValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c生命值必须是数字！"));
                }
                break;

            case "reach":
            case "reach_distance":
                try {
                    double reachValue = Double.parseDouble(value);
                    addOrUpdateAttribute(heldItem, "generic.attack_reach", "Attack Reach", reachValue, "mainhand");
                    player.sendSystemMessage(Component.literal("§a成功设置攻击距离为：" + reachValue));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c攻击距离必须是数字！"));
                }
                break;

            case "repair_cost":
                try {
                    int repairCost = Integer.parseInt(value);
                    CompoundTag tag = heldItem.getOrCreateTag();
                    tag.putInt("RepairCost", repairCost);
                    heldItem.setTag(tag);
                    player.sendSystemMessage(Component.literal("§a成功设置修复费用为：" + repairCost));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c修复费用必须是数字！"));
                }
                break;

            case "hide_flags":
            case "hideflags":
                try {
                    int hideFlags = Integer.parseInt(value);
                    CompoundTag tag = heldItem.getOrCreateTag();
                    tag.putInt("HideFlags", hideFlags);
                    heldItem.setTag(tag);
                    player.sendSystemMessage(Component.literal("§a成功设置隐藏标志为：" + hideFlags));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c隐藏标志必须是数字！"));
                }
                break;

            case "custom_model_data":
            case "cmd":
                try {
                    int customModelData = Integer.parseInt(value);
                    CompoundTag tag = heldItem.getOrCreateTag();
                    tag.putInt("CustomModelData", customModelData);
                    heldItem.setTag(tag);
                    player.sendSystemMessage(Component.literal("§a成功设置自定义模型数据为：" + customModelData));
                } catch (NumberFormatException e) {
                    player.sendSystemMessage(Component.literal("§c自定义模型数据必须是数字！"));
                }
                break;

            case "clear_enchants":
                CompoundTag tag = heldItem.getOrCreateTag();
                tag.remove("Enchantments");
                heldItem.setTag(tag);
                player.sendSystemMessage(Component.literal("§a成功清除所有附魔"));
                break;

            case "clear_attributes":
                CompoundTag attrTag = heldItem.getOrCreateTag();
                attrTag.remove("AttributeModifiers");
                heldItem.setTag(attrTag);
                player.sendSystemMessage(Component.literal("§a成功清除所有属性修饰符"));
                break;

            case "clear_lore":
                CompoundTag loreTag = heldItem.getOrCreateTag();
                CompoundTag display = loreTag.getCompound("display");
                display.remove("Lore");
                loreTag.put("display", display);
                heldItem.setTag(loreTag);
                player.sendSystemMessage(Component.literal("§a成功清除所有描述"));
                break;

            default:
                player.sendSystemMessage(Component.literal("§c未知属性：" + property));
                player.sendSystemMessage(Component.literal("§e可用属性："));
                player.sendSystemMessage(Component.literal("§e基础: stack, id, damage, name, lore, durability, unbreakable"));
                player.sendSystemMessage(Component.literal("§e附魔: enc, clear_enchants"));
                player.sendSystemMessage(Component.literal("§e属性: speed, armor, toughness, knockback, movement_speed, luck, health, reach"));
                player.sendSystemMessage(Component.literal("§e高级: nbt, repair_cost, hide_flags, custom_model_data"));
                player.sendSystemMessage(Component.literal("§e清理: clear_attributes, clear_lore"));
                break;
        }
    }

    public static void addOrUpdateAttribute(ItemStack item, String attributeName, String displayName, double value, String slot) {
        CompoundTag tag = item.getOrCreateTag();
        ListTag modifiers = tag.getList("AttributeModifiers", 10);

        modifiers.removeIf(modifier -> {
            CompoundTag compound = (CompoundTag) modifier;
            return compound.getString("AttributeName").equals(attributeName);
        });

        CompoundTag attributeModifier = new CompoundTag();
        attributeModifier.putString("AttributeName", attributeName);
        attributeModifier.putString("Name", displayName);
        attributeModifier.putDouble("Amount", value);
        attributeModifier.putInt("Operation", 0);
        attributeModifier.putString("Slot", slot);
        attributeModifier.putIntArray("UUID", new int[]{
                (int) (Math.random() * Integer.MAX_VALUE),
                (int) (Math.random() * Integer.MAX_VALUE),
                (int) (Math.random() * Integer.MAX_VALUE),
                (int) (Math.random() * Integer.MAX_VALUE)
        });
        modifiers.add(attributeModifier);

        tag.put("AttributeModifiers", modifiers);
        item.setTag(tag);
    }

    public static String combineArgs(String[] parts, int startIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            if (i > startIndex) builder.append(" ");
            builder.append(parts[i]);
        }
        return builder.toString();
    }

    public static String parseColorCodes(String text) {
        // 转换 & 为 §
        text = text.replace('&', '§');
        return text;
    }

    public static Enchantment getEnchantmentByName(String name) {
        Map<String, String> chineseEnchantments = Map.of(
                "锋利", "sharpness",
                "保护", "protection",
                "效率", "efficiency",
                "耐久", "unbreaking",
                "经验修补", "mending",
                "火焰附加", "fire_aspect",
                "抢夺", "looting",
                "击退", "knockback",
                "力量", "power",
                "冲击", "punch"
        );

        String enchantmentId = chineseEnchantments.getOrDefault(name.toLowerCase(), name.toLowerCase());

        if (enchantmentId.contains(":")) {
            try {
                ResourceLocation location = new ResourceLocation(enchantmentId);
                return BuiltInRegistries.ENCHANTMENT.get(location);
            } catch (Exception e) {
            }
        }

        try {
            ResourceLocation minecraftLocation = new ResourceLocation("minecraft", enchantmentId);
            if (BuiltInRegistries.ENCHANTMENT.containsKey(minecraftLocation)) {
                return BuiltInRegistries.ENCHANTMENT.get(minecraftLocation);
            }
        } catch (Exception e) {
        }

        // 通过显示名称查找
        return BuiltInRegistries.ENCHANTMENT.stream()
                .filter(enchantment -> {
                    String displayName = Component.translatable(enchantment.getDescriptionId()).getString();
                    return displayName.contains(name) ||
                            displayName.toLowerCase().contains(name.toLowerCase());
                })
                .findFirst()
                .orElse(null);
    }

    public static String getEnchantmentDisplayName(Enchantment enchantment) {
        return Component.translatable(enchantment.getDescriptionId()).getString();
    }

    public static void handleSetCommand(ServerPlayer player, String[] parts) {
        if (parts.length < 4) {
            player.sendSystemMessage(Component.literal("§c用法：\\bypassop set <目标> <属性> <值>"));
            return;
        }

        String targetName = parts[1];
        String attributeName = parts[2].toLowerCase();
        String value = parts[3];

        Entity targetEntity = null;

        if (targetName.equals("target")) {
            targetEntity = BypassHelp.getTargetEntity(player, 10.0);
        } else {
            ServerPlayer targetPlayer = player.getServer().getPlayerList().getPlayerByName(targetName);
            if (targetPlayer != null) {
                targetEntity = targetPlayer;
            }
        }

        if (targetEntity == null) {
            player.sendSystemMessage(Component.literal("§c未找到目标实体！"));
            return;
        }

        if (value.equals("un")) {
            BypassHelp.resetEntityAttribute(targetEntity, attributeName);
            player.sendSystemMessage(Component.literal("§a成功重置 " + BypassHelp.getEntityDisplayName(targetEntity) + " 的 " + attributeName + " 属性"));
        } else {
            boolean success = BypassHelp.setEntityAttribute(targetEntity, attributeName, value);
            if (success) {
                player.sendSystemMessage(Component.literal("§a成功设置 " + BypassHelp.getEntityDisplayName(targetEntity) + " 的 " + attributeName + " 为 " + value));
            } else {
                player.sendSystemMessage(Component.literal("§c设置属性失败！"));
            }
        }
    }
}
