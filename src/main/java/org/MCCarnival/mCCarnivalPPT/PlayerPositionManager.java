package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerPositionManager implements Listener {
    
    private static boolean enabled = false;
    private static Location baseLocation = null;
    private static final Map<UUID, Integer> playerPositions = new HashMap<>();
    private static final Set<UUID> exemptPlayers = new HashSet<>();
    
    // 排列配置
    private static final int PLAYERS_PER_ROW = 20;  // 每行20个玩家
    private static final int MAX_PLAYERS_PER_LINE = 30;  // 每排最多30个玩家
    private static final double Z_SPACING = 1.0;  // Z轴间距
    private static final double X_SPACING = 1.0;  // X轴间距
    private static final double Y_SPACING = 1.0;  // Y轴间距
    
    public static void setEnabled(boolean enable) {
        enabled = enable;
        if (!enable) {
            playerPositions.clear();
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setBaseLocation(Location location) {
        baseLocation = location.clone();
    }
    
    public static Location getBaseLocation() {
        return baseLocation;
    }
    
    public static void addExemptPlayer(UUID playerId) {
        exemptPlayers.add(playerId);
    }
    
    public static void removeExemptPlayer(UUID playerId) {
        exemptPlayers.remove(playerId);
    }
    
    public static boolean isExempt(UUID playerId) {
        return exemptPlayers.contains(playerId);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled || baseLocation == null) return;
        
        Player player = event.getPlayer();
        if (player.isOp() || isExempt(player.getUniqueId())) return;
        
        // 延迟1秒后分配位置，确保玩家完全加载
        new BukkitRunnable() {
            @Override
            public void run() {
                assignPlayerPosition(player);
            }
        }.runTaskLater(MCCarnivalPPT.getInstance(), 20L);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!enabled) return;
        
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // 从位置管理器中移除离开的玩家
        if (playerPositions.containsKey(playerId)) {
            removePlayer(playerId);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!enabled || baseLocation == null) return;
        
        Player player = event.getPlayer();
        if (player.isOp() || isExempt(player.getUniqueId())) return;
        
        // 检查玩家是否偏离了指定位置
        UUID playerId = player.getUniqueId();
        if (playerPositions.containsKey(playerId)) {
            Location targetLocation = calculatePlayerLocation(playerPositions.get(playerId));
            Location currentLocation = player.getLocation();
            
            // 如果玩家偏离了指定位置超过0.5格，则传送回去
            if (targetLocation.distance(currentLocation) > 0.5) {
                player.teleport(targetLocation);
            }
        }
    }
    
    private void assignPlayerPosition(Player player) {
        UUID playerId = player.getUniqueId();
        
        // 获取下一个可用位置
        int position = getNextAvailablePosition();
        playerPositions.put(playerId, position);
        
        // 计算并传送到指定位置
        Location targetLocation = calculatePlayerLocation(position);
        if (targetLocation != null) {
            player.teleport(targetLocation);
            player.sendMessage("§a您已被分配到位置 #" + (position + 1));
        }
    }
    
    private int getNextAvailablePosition() {
        // 找到下一个可用的位置编号
        int position = 0;
        while (playerPositions.containsValue(position)) {
            position++;
        }
        return position;
    }
    
    private static Location calculatePlayerLocation(int position) {
        if (baseLocation == null) return null;
        
        // 计算行号和列号
        int row = position / PLAYERS_PER_ROW;  // 第几行（从0开始）
        int col = position % PLAYERS_PER_ROW;  // 行内第几个（从0开始）
        
        // 计算排号（每排最多30个玩家）
        int line = row / MAX_PLAYERS_PER_LINE;  // 第几排（从0开始）
        int rowInLine = row % MAX_PLAYERS_PER_LINE;  // 排内第几行（从0开始）
        
        // 计算实际坐标
        double x = baseLocation.getX() + (line * X_SPACING);
        double y = baseLocation.getY() + (rowInLine * Y_SPACING);
        double z = baseLocation.getZ() + (col * Z_SPACING);
        
        Location targetLocation = new Location(baseLocation.getWorld(), x, y, z);
        targetLocation.setYaw(-90.0f);
        targetLocation.setPitch(0.0f);
        
        return targetLocation;
    }
    
    public static void repositionAllPlayers() {
        if (!enabled || baseLocation == null) return;
        
        // 清空现有位置分配
        playerPositions.clear();
        
        // 获取所有需要固定位置的在线玩家
        List<Player> playersToPosition = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp() && !isExempt(player.getUniqueId())) {
                playersToPosition.add(player);
            }
        }
        
        // 重新分配所有玩家的位置
        for (int i = 0; i < playersToPosition.size(); i++) {
            Player player = playersToPosition.get(i);
            UUID playerId = player.getUniqueId();
            
            playerPositions.put(playerId, i);
            Location targetLocation = calculatePlayerLocation(i);
            if (targetLocation != null) {
                player.teleport(targetLocation);
                player.sendMessage("§a您已被分配到位置 #" + (i + 1));
            }
        }
    }
    
    public static void removePlayer(UUID playerId) {
        if (playerPositions.remove(playerId) != null) {
            // 重新排列剩余玩家
            repositionAllPlayers();
        }
    }
    
    public static int getPlayerCount() {
        return playerPositions.size();
    }
    
    public static Map<UUID, Integer> getPlayerPositions() {
        return new HashMap<>(playerPositions);
    }
}