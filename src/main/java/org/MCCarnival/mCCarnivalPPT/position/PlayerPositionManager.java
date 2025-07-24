package org.MCCarnival.mCCarnivalPPT.position;

import org.MCCarnival.mCCarnivalPPT.core.MCCarnivalPPT;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerPositionManager implements Listener {
    
    private static boolean enabled = false;
    private static Location baseLocation = null;
    private static final Map<UUID, Integer> playerPositions = new HashMap<>();
    private static final Set<UUID> exemptPlayers = new HashSet<>();
    
    // 排列配置（从配置文件读取）
    private static int PLAYERS_PER_ROW = 20;  // 每行20个玩家
    private static int MAX_PLAYERS_PER_LINE = 30;  // 每排最多30个玩家
    private static double Z_SPACING = 1.0;  // Z轴间距
    private static double X_SPACING = 1.0;  // X轴间距
    private static double Y_SPACING = 1.0;  // Y轴间距
    
    // 朝向配置（从配置文件读取）
    private static float playerYaw = -90.0f;  // 玩家yaw朝向
    private static float playerPitch = 0.0f;  // 玩家pitch朝向
    
    // 排列方向配置（从配置文件读取）
    private static boolean useXAxis = false;  // true: 以X轴为主轴, false: 以Z轴为主轴
    
    // 定时任务配置（从配置文件读取）
    private static long teleportInterval = 2L;  // 传送间隔（tick）
    
    // 定时任务
    private static BukkitRunnable positionTask = null;
    
    // 加载配置
    public static void loadConfig() {
        MCCarnivalPPT plugin = MCCarnivalPPT.getInstance();
        
        // 加载position.yml配置文件
        plugin.saveResource("position.yml", false);
        org.bukkit.configuration.file.FileConfiguration positionConfig = 
            org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                new java.io.File(plugin.getDataFolder(), "position.yml"));
        
        // 排列配置
        PLAYERS_PER_ROW = positionConfig.getInt("layout.players-per-row", 20);
        MAX_PLAYERS_PER_LINE = positionConfig.getInt("layout.max-players-per-line", 30);
        X_SPACING = positionConfig.getDouble("layout.spacing.x", 1.0);
        Y_SPACING = positionConfig.getDouble("layout.spacing.y", 1.0);
        Z_SPACING = positionConfig.getDouble("layout.spacing.z", 1.0);
        
        // 朝向配置
        playerYaw = (float) positionConfig.getDouble("orientation.yaw", -90.0);
        playerPitch = (float) positionConfig.getDouble("orientation.pitch", 0.0);
        
        // 排列方向配置
        useXAxis = positionConfig.getBoolean("axis.use-x-axis", false);
        
        // 定时任务配置
        teleportInterval = positionConfig.getLong("task.teleport-interval", 2L);
    }
    
    public static void setEnabled(boolean enable) {
        enabled = enable;
        if (enable) {
            startPositionTask();
        } else {
            stopPositionTask();
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
    
    private static void startPositionTask() {
        if (positionTask != null) {
            positionTask.cancel();
        }
        
        positionTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!enabled || baseLocation == null) return;
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp() || isExempt(player.getUniqueId())) continue;
                    
                    UUID playerId = player.getUniqueId();
                    if (playerPositions.containsKey(playerId)) {
                        Location targetLocation = calculatePlayerLocation(playerPositions.get(playerId));
                        if (targetLocation != null) {
                            player.teleport(targetLocation);
                        }
                    }
                }
            }
        };
        
        // 根据配置文件设置的间隔执行
        positionTask.runTaskTimer(MCCarnivalPPT.getInstance(), 0L, teleportInterval);
    }
    
    private static void stopPositionTask() {
        if (positionTask != null) {
            positionTask.cancel();
            positionTask = null;
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
        double x, y, z;
        if (useXAxis) {
            // 以X轴为主轴排列
            x = baseLocation.getX() + (col * X_SPACING);
            y = baseLocation.getY() + (rowInLine * Y_SPACING);
            z = baseLocation.getZ() + (line * Z_SPACING);
        } else {
            // 以Z轴为主轴排列（默认）
            x = baseLocation.getX() + (line * X_SPACING);
            y = baseLocation.getY() + (rowInLine * Y_SPACING);
            z = baseLocation.getZ() + (col * Z_SPACING);
        }
        
        Location targetLocation = new Location(baseLocation.getWorld(), x, y, z);
        targetLocation.setYaw(playerYaw);
        targetLocation.setPitch(playerPitch);
        
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
    
    // 朝向配置方法
    public static void setPlayerYaw(float yaw) {
        playerYaw = yaw;
    }
    
    public static float getPlayerYaw() {
        return playerYaw;
    }
    
    public static void setPlayerPitch(float pitch) {
        playerPitch = pitch;
    }
    
    public static float getPlayerPitch() {
        return playerPitch;
    }
    
    // 排列方向配置方法
    public static void setUseXAxis(boolean useX) {
        useXAxis = useX;
    }
    
    public static boolean isUseXAxis() {
        return useXAxis;
    }
    
    public static String getMainAxis() {
        return useXAxis ? "X轴" : "Z轴";
    }
}