package org.MCCarnival.mCCarnivalPPT.position;

import org.MCCarnival.mCCarnivalPPT.core.MCCarnivalPPT;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class PositionCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mccarnival.position.admin")) {
            sender.sendMessage("§c您没有权限使用此命令！");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "enable":
                handleEnable(sender);
                break;
            case "disable":
                handleDisable(sender);
                break;
            case "setbase":
                handleSetBase(sender);
                break;
            case "status":
                handleStatus(sender);
                break;
            case "reposition":
                handleReposition(sender);
                break;
            case "exempt":
                if (args.length >= 3) {
                    handleExempt(sender, args[1], args[2]);
                } else {
                    sender.sendMessage("§c用法: /position exempt <add|remove> <玩家名>");
                }
                break;
            case "setyaw":
                if (args.length >= 2) {
                    handleSetYaw(sender, args[1]);
                } else {
                    sender.sendMessage("§c用法: /position setyaw <角度>");
                }
                break;
            case "setpitch":
                if (args.length >= 2) {
                    handleSetPitch(sender, args[1]);
                } else {
                    sender.sendMessage("§c用法: /position setpitch <角度>");
                }
                break;
            case "setaxis":
                if (args.length >= 2) {
                    handleSetAxis(sender, args[1]);
                } else {
                    sender.sendMessage("§c用法: /position setaxis <x|z>");
                }
                break;
            case "reload":
                handleReload(sender);
                break;
            case "help":
                showHelp(sender);
                break;
            default:
                sender.sendMessage("§c未知的子命令！使用 /position help 查看帮助");
                break;
        }
        
        return true;
    }
    
    private void handleEnable(CommandSender sender) {
        if (PlayerPositionManager.getBaseLocation() == null) {
            sender.sendMessage("§c请先使用 /position setbase 设置基准点！");
            return;
        }
        
        PlayerPositionManager.setEnabled(true);
        sender.sendMessage("§a玩家位置固定功能已启用！");
        
        // 立即重新排列所有在线的非OP玩家
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp() && !PlayerPositionManager.isExempt(player.getUniqueId())) {
                count++;
            }
        }
        
        if (count > 0) {
            PlayerPositionManager.repositionAllPlayers();
            sender.sendMessage("§a已重新排列 " + count + " 个玩家的位置");
        }
    }
    
    private void handleDisable(CommandSender sender) {
        PlayerPositionManager.setEnabled(false);
        sender.sendMessage("§c玩家位置固定功能已禁用！");
    }
    
    private void handleSetBase(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c此命令只能由玩家执行！");
            return;
        }
        
        Player player = (Player) sender;
        Location location = player.getLocation();
        PlayerPositionManager.setBaseLocation(location);
        
        sender.sendMessage("§a基准点已设置为当前位置：");
        sender.sendMessage("§7世界: §e" + location.getWorld().getName());
        sender.sendMessage("§7坐标: §e" + String.format("%.2f, %.2f, %.2f", location.getX(), location.getY(), location.getZ()));
        sender.sendMessage("§7朝向: §e" + String.format("%.1f, %.1f", location.getYaw(), location.getPitch()));
        
        // 如果功能已启用，重新排列玩家
        if (PlayerPositionManager.isEnabled()) {
            PlayerPositionManager.repositionAllPlayers();
            sender.sendMessage("§a已根据新基准点重新排列所有玩家");
        }
    }
    
    private void handleStatus(CommandSender sender) {
        sender.sendMessage("§6=== 玩家位置固定系统状态 ===");
        sender.sendMessage("§7启用状态: " + (PlayerPositionManager.isEnabled() ? "§a开启" : "§c关闭"));
        
        Location base = PlayerPositionManager.getBaseLocation();
        if (base != null) {
            sender.sendMessage("§7基准点: §e" + String.format("%.2f, %.2f, %.2f", base.getX(), base.getY(), base.getZ()));
            sender.sendMessage("§7世界: §e" + base.getWorld().getName());
        } else {
            sender.sendMessage("§7基准点: §c未设置");
        }
        
        sender.sendMessage("§7固定玩家数量: §e" + PlayerPositionManager.getPlayerCount());
        
        // 显示朝向配置
        sender.sendMessage("§7玩家朝向: §eYaw=" + PlayerPositionManager.getPlayerYaw() + ", Pitch=" + PlayerPositionManager.getPlayerPitch());
        
        // 显示排列配置
        sender.sendMessage("§7排列主轴: §e" + PlayerPositionManager.getMainAxis());
        
        // 显示排列规则
        sender.sendMessage("§7排列规则:");
        if (PlayerPositionManager.isUseXAxis()) {
            sender.sendMessage("§7- 每行20个玩家，X轴间距1格");
            sender.sendMessage("§7- 每20个玩家换行（Y轴+1）");
            sender.sendMessage("§7- 每排最多30行，然后Z轴延伸");
        } else {
            sender.sendMessage("§7- 每行20个玩家，Z轴间距1格");
            sender.sendMessage("§7- 每20个玩家换行（Y轴+1）");
            sender.sendMessage("§7- 每排最多30行，然后X轴延伸");
        }
    }
    
    private void handleReposition(CommandSender sender) {
        if (!PlayerPositionManager.isEnabled()) {
            sender.sendMessage("§c玩家位置固定功能未启用！");
            return;
        }
        
        PlayerPositionManager.repositionAllPlayers();
        sender.sendMessage("§a已重新排列所有玩家的位置！");
    }
    
    private void handleExempt(CommandSender sender, String action, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§c玩家 " + playerName + " 不在线或不存在！");
            return;
        }
        
        UUID playerId = target.getUniqueId();
        
        switch (action.toLowerCase()) {
            case "add":
                PlayerPositionManager.addExemptPlayer(playerId);
                PlayerPositionManager.removePlayer(playerId);
                sender.sendMessage("§a已将玩家 " + target.getName() + " 添加到豁免列表");
                break;
            case "remove":
                PlayerPositionManager.removeExemptPlayer(playerId);
                sender.sendMessage("§a已将玩家 " + target.getName() + " 从豁免列表移除");
                
                // 如果功能启用，立即分配位置
                if (PlayerPositionManager.isEnabled() && !target.isOp()) {
                    PlayerPositionManager.repositionAllPlayers();
                    sender.sendMessage("§a已为该玩家重新分配位置");
                }
                break;
            default:
                sender.sendMessage("§c无效的操作！使用 add 或 remove");
                break;
        }
    }
    
    private void handleSetYaw(CommandSender sender, String yawStr) {
        try {
            float yaw = Float.parseFloat(yawStr);
            PlayerPositionManager.setPlayerYaw(yaw);
            sender.sendMessage("§a玩家Yaw朝向已设置为: §e" + yaw);
            
            // 如果功能启用，重新排列玩家
            if (PlayerPositionManager.isEnabled()) {
                PlayerPositionManager.repositionAllPlayers();
                sender.sendMessage("§a已根据新朝向重新排列所有玩家");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§c无效的角度值！请输入有效的数字");
        }
    }
    
    private void handleSetPitch(CommandSender sender, String pitchStr) {
        try {
            float pitch = Float.parseFloat(pitchStr);
            PlayerPositionManager.setPlayerPitch(pitch);
            sender.sendMessage("§a玩家Pitch朝向已设置为: §e" + pitch);
            
            // 如果功能启用，重新排列玩家
            if (PlayerPositionManager.isEnabled()) {
                PlayerPositionManager.repositionAllPlayers();
                sender.sendMessage("§a已根据新朝向重新排列所有玩家");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§c无效的角度值！请输入有效的数字");
        }
    }
    
    private void handleSetAxis(CommandSender sender, String axis) {
        switch (axis.toLowerCase()) {
            case "x":
                PlayerPositionManager.setUseXAxis(true);
                sender.sendMessage("§a排列主轴已设置为: §eX轴");
                break;
            case "z":
                PlayerPositionManager.setUseXAxis(false);
                sender.sendMessage("§a排列主轴已设置为: §eZ轴");
                break;
            default:
                sender.sendMessage("§c无效的轴向！请使用 x 或 z");
                return;
        }
        
        // 如果功能启用，重新排列玩家
        if (PlayerPositionManager.isEnabled()) {
            PlayerPositionManager.repositionAllPlayers();
            sender.sendMessage("§a已根据新排列方式重新排列所有玩家");
        }
    }
    
    private void handleReload(CommandSender sender) {
        // 重新加载主配置文件
        MCCarnivalPPT.getInstance().reloadConfig();
        // 重新加载position.yml配置文件
        PlayerPositionManager.loadConfig();
        
        sender.sendMessage("§a配置文件已重新加载！");
        sender.sendMessage("§7主配置文件(config.yml)和位置配置文件(position.yml)已重新加载");
        sender.sendMessage("§7新的配置参数已生效，如果功能已启用，请使用 /position reposition 重新排列玩家");
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6=== 玩家位置固定系统命令帮助 ===");
        sender.sendMessage("§e/position enable §7- 启用位置固定功能");
        sender.sendMessage("§e/position disable §7- 禁用位置固定功能");
        sender.sendMessage("§e/position setbase §7- 设置当前位置为基准点");
        sender.sendMessage("§e/position status §7- 查看系统状态");
        sender.sendMessage("§e/position reposition §7- 重新排列所有玩家位置");
        sender.sendMessage("§e/position setyaw <角度> §7- 设置玩家Yaw朝向");
        sender.sendMessage("§e/position setpitch <角度> §7- 设置玩家Pitch朝向");
        sender.sendMessage("§e/position setaxis <x|z> §7- 设置排列主轴");
        sender.sendMessage("§e/position exempt add <玩家> §7- 添加玩家到豁免列表");
        sender.sendMessage("§e/position exempt remove <玩家> §7- 从豁免列表移除玩家");
        sender.sendMessage("§e/position reload §7- 重新加载配置文件");
        sender.sendMessage("§e/position help §7- 显示此帮助信息");
        sender.sendMessage("§7注意：OP玩家自动豁免位置固定");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String[] subcommands = {"enable", "disable", "setbase", "status", "reposition", "exempt", "setyaw", "setpitch", "setaxis", "reload", "help"};
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("exempt")) {
            String[] actions = {"add", "remove"};
            for (String action : actions) {
                if (action.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(action);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setaxis")) {
            String[] axes = {"x", "z"};
            for (String axis : axes) {
                if (axis.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(axis);
                }
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("setyaw") || args[0].equalsIgnoreCase("setpitch"))) {
            // 提供一些常用角度值作为建议
            String[] angles = {"0", "90", "-90", "180", "-180"};
            for (String angle : angles) {
                if (angle.startsWith(args[1])) {
                    completions.add(angle);
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("exempt")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}