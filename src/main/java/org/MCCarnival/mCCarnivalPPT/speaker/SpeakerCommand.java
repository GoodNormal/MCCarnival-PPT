package org.MCCarnival.mCCarnivalPPT.speaker;

import org.MCCarnival.mCCarnivalPPT.core.MCCarnivalPPT;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpeakerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c用法: /speaker <set|stop> <玩家名>");
            return true;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage("§c找不到玩家: " + playerName);
            return true;
        }

        switch (action) {
            case "set":
                setSpeakerMode(sender, targetPlayer);
                break;
            case "stop":
                stopSpeakerMode(sender, targetPlayer);
                break;
            default:
                sender.sendMessage("§c用法: /speaker <set|stop> <玩家名>");
                break;
        }

        return true;
    }

    /**
     * 设置玩家为演讲者模式
     */
    private void setSpeakerMode(CommandSender sender, Player targetPlayer) {
        // 设置玩家体型为1.5倍
        AttributeInstance scaleAttribute = targetPlayer.getAttribute(Attribute.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(1.5);
        }

        // 添加紫色发光效果
        PotionEffect glowingEffect = new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false);
        targetPlayer.addPotionEffect(glowingEffect);

        // 设置发光颜色为紫色（通过团队颜色实现）
        targetPlayer.setGlowing(true);
        
        // 发放PPT翻页笔
        givePPTPen(targetPlayer);

        // 发送消息
        targetPlayer.sendMessage("§d你已被设置为演讲者模式！体型变大1.5倍，获得紫色发光效果，并获得了PPT翻页笔。");
        sender.sendMessage("§a已将 " + targetPlayer.getName() + " 设置为演讲者模式！");
    }

    /**
     * 停止玩家的演讲者模式
     */
    private void stopSpeakerMode(CommandSender sender, Player targetPlayer) {
        // 恢复玩家体型为正常大小
        AttributeInstance scaleAttribute = targetPlayer.getAttribute(Attribute.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(1.0);
        }

        // 移除发光效果
        targetPlayer.removePotionEffect(PotionEffectType.GLOWING);
        targetPlayer.setGlowing(false);

        // 回收PPT翻页笔
        removePPTPen(targetPlayer);

        // 发送消息
        targetPlayer.sendMessage("§a演讲者模式已关闭！体型恢复正常，发光效果已移除，PPT翻页笔已回收。");
        sender.sendMessage("§a已关闭 " + targetPlayer.getName() + " 的演讲者模式！");
    }

    /**
     * 给玩家发放PPT翻页笔
     */
    private void givePPTPen(Player player) {
        // 从配置文件读取翻页笔配置
        MCCarnivalPPT plugin = MCCarnivalPPT.getInstance();
        String materialName = plugin.getConfig().getString("pagepen.material", "STICK");
        String displayName = plugin.getConfig().getString("pagepen.display-name", "§6PPT翻页笔");
        int customModelData = plugin.getConfig().getInt("pagepen.custom-model-data", 1);
        List<String> lore = plugin.getConfig().getStringList("pagepen.lore");

        // 创建物品
        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            material = Material.STICK; // 默认使用木棍
            plugin.getLogger().warning("无效的翻页笔物品类型: " + materialName + "，使用默认值 STICK");
        }

        ItemStack pptPen = new ItemStack(material);
        ItemMeta meta = pptPen.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setCustomModelData(customModelData);

            // 设置说明文字
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }

            pptPen.setItemMeta(meta);
        }

        // 给玩家物品
        player.getInventory().addItem(pptPen);
    }

    /**
     * 回收玩家的PPT翻页笔
     */
    private void removePPTPen(Player player) {
        // 从配置文件读取翻页笔配置
        MCCarnivalPPT plugin = MCCarnivalPPT.getInstance();
        String materialName = plugin.getConfig().getString("pagepen.material", "STICK");
        String displayName = plugin.getConfig().getString("pagepen.display-name", "§6PPT翻页笔");
        int customModelData = plugin.getConfig().getInt("pagepen.custom-model-data", 1);

        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            material = Material.STICK;
        }

        // 遍历玩家背包，移除匹配的翻页笔
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.hasCustomModelData()) {
                    if (meta.getDisplayName().equals(displayName) && meta.getCustomModelData() == customModelData) {
                        player.getInventory().setItem(i, null);
                    }
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("set", "stop");
            String input = args[0].toLowerCase();

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            // 为第二个参数提供在线玩家名补全
            String input = args[1].toLowerCase();
            for (Player onlinePlayer : sender.getServer().getOnlinePlayers()) {
                String playerName = onlinePlayer.getName();
                if (playerName.toLowerCase().startsWith(input)) {
                    completions.add(playerName);
                }
            }
        }

        return completions;
    }
}