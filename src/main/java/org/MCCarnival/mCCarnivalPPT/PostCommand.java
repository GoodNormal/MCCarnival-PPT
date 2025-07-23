package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PostCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有管理可以使用此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§c用法: /post set <数字>");
            return true;
        }

        String action = args[0].toLowerCase();
        
        // 处理set子命令
        if (action.equals("set")) {
            if (args.length < 2) {
                player.sendMessage("§c用法: /post set <数字>");
                return true;
            }
            
            try {
                int targetPage = Integer.parseInt(args[1]);
                if (targetPage < 1) {
                    player.sendMessage("§c页数必须大于等于1！");
                    return true;
                }
                
                setPageNumberForce(player, targetPage);
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage("§c请输入有效的数字！");
                return true;
            }
        } else {
            player.sendMessage("§c用法: /post set <数字>");
            return true;
        }
    }
    
    /**
     * 强制设置附近ItemDisplay的页数（无最大值限制）
     */
    private void setPageNumberForce(Player player, int pageNumber) {
        // 获取玩家周围的ItemDisplay实体
        Location playerLoc = player.getLocation();
        int searchRange = MCCarnivalPPT.getSearchRange();
        Collection<Entity> nearbyEntities = playerLoc.getWorld().getNearbyEntities(playerLoc, searchRange, searchRange, searchRange);
        
        boolean found = false;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ItemDisplay) {
                ItemDisplay itemDisplay = (ItemDisplay) entity;
                ItemStack item = itemDisplay.getItemStack();
                
                // 检查是否是支持的物品类型
                if (item != null && MCCarnivalPPT.getSupportedItems().contains(item.getType())) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setCustomModelData(pageNumber);
                        item.setItemMeta(meta);
                        itemDisplay.setItemStack(item);
                        
                        player.sendMessage("§a成功强制设置海报的页数为: " + pageNumber);
                        found = true;
                    }
                }
            }
        }
        
        if (!found) {
            player.sendMessage("§c未找到附近的可以换页的ppt");
        }
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("set");
            String input = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        }
        
        return completions;
    }
}