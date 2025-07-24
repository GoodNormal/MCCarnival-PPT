package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class PPTItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // 从配置文件读取翻页笔配置
        MCCarnivalPPT plugin = MCCarnivalPPT.getInstance();
        String materialName = plugin.getConfig().getString("pagepen.material", "STICK");
        String displayName = plugin.getConfig().getString("pagepen.display-name", "§6PPT翻页笔");
        int customModelData = plugin.getConfig().getInt("pagepen.custom-model-data", 1);
        
        Material expectedMaterial;
        try {
            expectedMaterial = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            expectedMaterial = Material.STICK; // 默认使用木棍
        }
        
        // 检查是否是PPT翻页笔
        if (item == null || item.getType() != expectedMaterial) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != customModelData) {
            return;
        }
        
        // 检查物品名称是否为PPT翻页笔
        if (!meta.hasDisplayName() || !meta.getDisplayName().equals(displayName)) {
            return;
        }
        
        // 检查玩家是否有使用翻页笔的权限
        if (!player.hasPermission("mccarnival.ppt.penuse")) {
            player.sendMessage("§c你没有使用PPT翻页笔的权限！");
            event.setCancelled(true);
            return;
        }
        
        // 检查翻页功能是否被禁用
        if (MCCarnivalPPT.isPagingForbidden()) {
            player.sendMessage("§c翻页笔功能已被禁用！");
            event.setCancelled(true);
            return;
        }
        
        Action action = event.getAction();
        boolean isNext = false;
        
        // 左键向上翻页(next)，右键向下翻页(up)
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            isNext = true;
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            isNext = false;
        } else {
            return;
        }
        
        // 取消事件防止其他交互
        event.setCancelled(true);
        
        // 获取玩家周围的ItemDisplay实体
        Location playerLoc = player.getLocation();
        int searchRange = MCCarnivalPPT.getSearchRange();
        Collection<Entity> nearbyEntities = playerLoc.getWorld().getNearbyEntities(playerLoc, searchRange, searchRange, searchRange);
        
        boolean found = false;
        for (Entity entity : nearbyEntities) {
            if (entity instanceof ItemDisplay) {
                ItemDisplay itemDisplay = (ItemDisplay) entity;
                ItemStack displayItem = itemDisplay.getItemStack();
                
                // 检查是否是支持的物品类型
                if (displayItem != null && MCCarnivalPPT.getSupportedItems().contains(displayItem.getType())) {
                    ItemMeta displayMeta = displayItem.getItemMeta();
                    if (displayMeta != null && displayMeta.hasCustomModelData()) {
                        int currentCustomModelData = displayMeta.getCustomModelData();
                        int newCustomModelData;
                        
                        // 检查当前页数是否超过最大值限制
                        int maxPage = MCCarnivalPPT.getMaxPage();
                        if (currentCustomModelData > maxPage) {
                            player.sendMessage("§c无法翻页：当前页数 " + currentCustomModelData + " 超过最大值 " + maxPage + "，请使用 /post set 命令！");
                            found = true;
                            continue;
                        }
                        
                        if (isNext) {
                            newCustomModelData = currentCustomModelData + 1;
                            // 检查最大值限制
                            if (newCustomModelData > maxPage) {
                                player.sendMessage("§c无法翻页：已达到最大页数 " + maxPage + "！");
                                found = true;
                                continue;
                            }
                        } else {
                            newCustomModelData = currentCustomModelData - 1;
                            // 检查最小值限制
                            if (newCustomModelData < 1) {
                                player.sendMessage("§c无法翻页：已经是第一页了！");
                                found = true;
                                continue;
                            }
                        }
                        
                        displayMeta.setCustomModelData(newCustomModelData);
                        displayItem.setItemMeta(displayMeta);
                        itemDisplay.setItemStack(displayItem);
                        
                        String direction = isNext ? "下一页" : "上一页";
                        player.sendMessage("§a使用PPT翻页笔" + direction + "，第 " + newCustomModelData + " 页");
                        found = true;
                    }
                }
            }
        }
        
        if (!found) {
            player.sendMessage("§c未找到附近的可以换页的ppt！");
        }
    }
}