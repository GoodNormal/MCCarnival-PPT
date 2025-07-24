package org.MCCarnival.mCCarnivalPPT.ppt;

import org.MCCarnival.mCCarnivalPPT.core.MCCarnivalPPT;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PPTCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§c用法: /ppt <next|up|pagepen [玩家名]|page <数字>|forbid>");
            return true;
        }

        String action = args[0].toLowerCase();
        
        // 处理pagepen子命令
        if (action.equals("pagepen")) {
            if (args.length == 1) {
                // 没有指定目标，给当前玩家
                givePPTPen(player);
            } else {
                // 指定了目标玩家
                String targetName = args[1];
                Player targetPlayer = player.getServer().getPlayer(targetName);
                
                if (targetPlayer == null) {
                    player.sendMessage("§c找不到玩家: " + targetName);
                    return true;
                }
                
                givePPTPen(targetPlayer);
                if (!targetPlayer.equals(player)) {
                    player.sendMessage("§a已给 " + targetPlayer.getName() + " 发放PPT翻页笔！");
                }
            }
            return true;
        }
        
        // 处理forbid子命令
        if (action.equals("forbid")) {
            boolean currentState = MCCarnivalPPT.isPagingForbidden();
            MCCarnivalPPT.setPagingForbidden(!currentState);
            String status = !currentState ? "禁用" : "启用";
            player.sendMessage("§a翻页笔功能已" + status + "！");
            return true;
        }
        
        // 处理page子命令
        if (action.equals("page")) {
            if (args.length < 2) {
                player.sendMessage("§c用法: /ppt page <数字>");
                return true;
            }
            
            try {
                int targetPage = Integer.parseInt(args[1]);
                if (targetPage < 1) {
                    player.sendMessage("§c页数必须大于等于1！");
                    return true;
                }
                
                setPageNumber(player, targetPage);
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage("§c请输入有效的数字！");
                return true;
            }
        }
        
        // 处理next和up命令
        if (!action.equals("next") && !action.equals("up")) {
            player.sendMessage("§c用法: /ppt <next|up|pagepen [玩家名]|page <数字>|forbid>");
            return true;
        }

        // 获取玩家周围的ItemDisplay实体
        Location playerLoc = player.getLocation();
        Collection<Entity> nearbyEntities = playerLoc.getWorld().getNearbyEntities(playerLoc, 10, 10, 10);
        
        boolean found = false;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ItemDisplay) {
                ItemDisplay itemDisplay = (ItemDisplay) entity;
                ItemStack item = itemDisplay.getItemStack();
                
                // 检查是否是幻翼膜
                if (item != null && item.getType() == Material.PHANTOM_MEMBRANE) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.hasCustomModelData()) {
                        int currentCustomModelData = meta.getCustomModelData();
                        int newCustomModelData;
                        
                        // 检查当前页数是否超过最大值限制
                        int maxPage = MCCarnivalPPT.getMaxPage();
                        if (currentCustomModelData > maxPage) {
                            player.sendMessage("§c无法翻页：当前页数 " + currentCustomModelData + " 超过最大值 " + maxPage + "。");
                            found = true;
                            continue;
                        }
                        
                        if (action.equals("next")) {
                            newCustomModelData = currentCustomModelData + 1;
                            // 检查最大值限制
                            if (newCustomModelData > maxPage) {
                                player.sendMessage("§c无法翻页：已达到最大页数 " + maxPage + "！");
                                found = true;
                                continue;
                            }
                        } else { // up
                            newCustomModelData = currentCustomModelData - 1;
                            // 检查最小值限制
                            if (newCustomModelData < 1) {
                                player.sendMessage("§c无法翻页：ppt已经到底第一页了");
                                found = true;
                                continue;
                            }
                        }
                        
                        meta.setCustomModelData(newCustomModelData);
                        item.setItemMeta(meta);
                        itemDisplay.setItemStack(item);
                        
                        player.sendMessage("§a成功修改ItemDisplay的CustomModelData为: " + newCustomModelData);
                        found = true;
                    }
                }
            }
        }
        
        if (!found) {
            player.sendMessage("§c未找到附近的幻翼膜ItemDisplay实体！");
        }
        
        return true;
    }
    
    /**
     * 设置附近ItemDisplay的页数
     */
    private void setPageNumber(Player player, int pageNumber) {
        // 获取玩家周围的ItemDisplay实体
        Location playerLoc = player.getLocation();
        Collection<Entity> nearbyEntities = playerLoc.getWorld().getNearbyEntities(playerLoc, 10, 10, 10);
        
        boolean found = false;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ItemDisplay) {
                ItemDisplay itemDisplay = (ItemDisplay) entity;
                ItemStack item = itemDisplay.getItemStack();
                
                // 检查是否是幻翼膜
                if (item != null && item.getType() == Material.PHANTOM_MEMBRANE) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setCustomModelData(pageNumber);
                        item.setItemMeta(meta);
                        itemDisplay.setItemStack(item);
                        
                        player.sendMessage("§a成功设置ItemDisplay的页数为: " + pageNumber);
                        found = true;
                    }
                }
            }
        }
        
        if (!found) {
            player.sendMessage("§c未找到附近的幻翼膜ItemDisplay实体！");
        }
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
        player.sendMessage("§a已获得PPT翻页笔！左键下一页，右键上一页。");
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("next", "up", "pagepen", "page", "forbid");
            String input = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("pagepen")) {
            // 为 pagepen 子命令提供在线玩家名补全
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