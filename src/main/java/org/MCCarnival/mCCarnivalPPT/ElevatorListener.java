package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElevatorListener implements Listener {
    
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> messageCooldowns = new HashMap<>();
    
    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // 只处理非创造模式和非观察者模式的玩家
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        
        // 检查玩家是否跳跃（Y坐标增加）
        if (event.getTo() == null || event.getFrom().getY() >= event.getTo().getY()) {
            return;
        }
        
        // 检查Y坐标变化是否足够大（跳跃的特征）
        double yDiff = event.getTo().getY() - event.getFrom().getY();
        if (yDiff < 0.1) {
            return;
        }
        
        // 检查冷却时间
        if (isOnCooldown(player)) {
            return;
        }
        
        Block blockBelow = event.getFrom().subtract(0, 1, 0).getBlock();
        Material blockType = blockBelow.getType();
        
        // 检查是否踩在电梯方块上
        if (blockType == ElevatorConfig.getUpBlock()) {
            // 上行方块：只能向上传送
            teleportUp(player, ElevatorConfig.getUpBlockDistance());
        } else if (blockType == ElevatorConfig.getBidirectionalBlock()) {
            // 双向方块：跳跃向上传送
            teleportUp(player, ElevatorConfig.getBidirectionalBlockUpDistance());
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        // 只处理非创造模式和非观察者模式的玩家
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        
        // 检查玩家是否开始潜行（按下shift）
        if (!event.isSneaking()) {
            return;
        }
        
        // 检查冷却时间
        if (isOnCooldown(player)) {
            return;
        }
        
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
        Material blockType = blockBelow.getType();
        
        // 检查是否踩在电梯方块上
        if (blockType == ElevatorConfig.getDownBlock()) {
            // 下行方块：只能向下传送
            teleportDown(player, ElevatorConfig.getDownBlockDistance());
        } else if (blockType == ElevatorConfig.getBidirectionalBlock()) {
            // 双向方块：shift向下传送
            teleportDown(player, ElevatorConfig.getBidirectionalBlockDownDistance());
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // 为非创造模式玩家启用飞行能力（用于检测双击跳跃）
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            if (!player.getAllowFlight()) {
                player.setAllowFlight(true);
            }
        }
    }
    
    private void teleportUp(Player player, int moveDistance) {
        Location currentLoc = player.getLocation();
        
        // 计算目标位置
        Location targetLoc = currentLoc.clone().add(0, moveDistance, 0);
        
        // 检查目标位置是否安全
        if (isSafeLocation(targetLoc)) {
            performTeleport(player, targetLoc);
            player.sendMessage("§a向上传送 " + moveDistance + " 格！");
        } else {
            sendSafetyMessage(player, "§c目标位置不安全，无法传送！");
        }
    }
    
    private void teleportDown(Player player, int moveDistance) {
        Location currentLoc = player.getLocation();
        
        // 计算目标位置
        Location targetLoc = currentLoc.clone().subtract(0, moveDistance, 0);
        
        // 检查目标位置是否安全
        if (isSafeLocation(targetLoc)) {
            performTeleport(player, targetLoc);
            player.sendMessage("§a向下传送 " + moveDistance + " 格！");
        } else {
            sendSafetyMessage(player, "§c目标位置不安全，无法传送！");
        }
    }
    

    
    private boolean isSafeLocation(Location loc) {
        // 检查世界边界
        if (loc.getY() < 0 || loc.getY() > 320) {
            return false;
        }
        
        // 检查目标位置和上方一格是否为空气或可通过的方块
        Block targetBlock = loc.getBlock();
        Block aboveBlock = loc.clone().add(0, 1, 0).getBlock();
        
        return (targetBlock.getType().isAir() || !targetBlock.getType().isSolid()) &&
               (aboveBlock.getType().isAir() || !aboveBlock.getType().isSolid());
    }
    

    
    private void performTeleport(Player player, Location targetLoc) {
        // 设置冷却时间
        setCooldown(player);
        
        // 播放音效
        if (ElevatorConfig.isSoundEnabled()) {
            player.playSound(player.getLocation(), ElevatorConfig.getSoundType(), 1.0f, 1.0f);
        }
        
        // 显示粒子效果
        if (ElevatorConfig.isParticleEnabled()) {
            player.getWorld().spawnParticle(ElevatorConfig.getParticleType(), 
                player.getLocation().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.1);
        }
        
        // 传送玩家
        player.teleport(targetLoc);
        
        // 传送后的粒子效果
        if (ElevatorConfig.isParticleEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getWorld().spawnParticle(ElevatorConfig.getParticleType(), 
                        targetLoc.add(0, 1, 0), 20, 0.5, 1, 0.5, 0.1);
                }
            }.runTaskLater(MCCarnivalPPT.getInstance(), 5L);
        }
        
        // 播放传送后音效
        if (ElevatorConfig.isSoundEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(targetLoc, ElevatorConfig.getSoundType(), 1.0f, 1.2f);
                }
            }.runTaskLater(MCCarnivalPPT.getInstance(), 5L);
        }
        
        // 传送成功消息已在各自的方法中发送
    }
    
    private boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }
        
        long lastUse = cooldowns.get(playerId);
        long cooldownTime = ElevatorConfig.getCooldownSeconds() * 1000L;
        
        return System.currentTimeMillis() - lastUse < cooldownTime;
    }
    
    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    private void sendSafetyMessage(Player player, String message) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // 检查消息冷却时间（1秒）
        if (messageCooldowns.containsKey(playerId)) {
            long lastMessageTime = messageCooldowns.get(playerId);
            if (currentTime - lastMessageTime < 1000) {
                return; // 在冷却时间内，不发送消息
            }
        }
        
        // 发送消息并更新冷却时间
        player.sendMessage(message);
        messageCooldowns.put(playerId, currentTime);
    }
}