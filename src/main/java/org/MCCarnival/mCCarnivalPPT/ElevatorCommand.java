package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElevatorCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.isOp()) {
            sender.sendMessage("§c你没有权限使用此命令！");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "status":
                handleStatus(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            default:
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        try {
            ElevatorConfig.loadConfig();
            sender.sendMessage("§a电梯配置文件重载成功！");
        } catch (Exception e) {
            sender.sendMessage("§c电梯配置文件重载失败: " + e.getMessage());
        }
    }
    
    private void handleStatus(CommandSender sender) {
        boolean enabled = ElevatorConfig.isEnabled();
        sender.sendMessage("§6=== 电梯系统状态 ===");
        sender.sendMessage("§f状态: " + (enabled ? "§a启用" : "§c禁用"));
        
        if (enabled) {
            sender.sendMessage("§f上行方块: §e" + ElevatorConfig.getUpBlock().name() + " §f(距离: §e" + ElevatorConfig.getUpBlockDistance() + "§f格)");
            sender.sendMessage("§f下行方块: §e" + ElevatorConfig.getDownBlock().name() + " §f(距离: §e" + ElevatorConfig.getDownBlockDistance() + "§f格)");
            sender.sendMessage("§f双向方块: §e" + ElevatorConfig.getBidirectionalBlock().name() + " §f(上行距离: §e" + ElevatorConfig.getBidirectionalBlockUpDistance() + "§f格, 下行距离: §e" + ElevatorConfig.getBidirectionalBlockDownDistance() + "§f格)");
            sender.sendMessage("§f冷却时间: §e" + ElevatorConfig.getCooldownSeconds() + " 秒");
            sender.sendMessage("§f音效: " + (ElevatorConfig.isSoundEnabled() ? "§a启用" : "§c禁用"));
            sender.sendMessage("§f粒子效果: " + (ElevatorConfig.isParticleEnabled() ? "§a启用" : "§c禁用"));
        }
    }
    
    private void handleInfo(CommandSender sender) {
        sender.sendMessage("§6=== 电梯系统使用说明 ===");
        sender.sendMessage("§f1. 站在 §e" + ElevatorConfig.getUpBlock().name() + " §f上单击跳跃可向上传送 §e" + ElevatorConfig.getUpBlockDistance() + " §f格");
        sender.sendMessage("§f2. 站在 §e" + ElevatorConfig.getDownBlock().name() + " §f上按Shift键可向下传送 §e" + ElevatorConfig.getDownBlockDistance() + " §f格");
        sender.sendMessage("§f3. 站在 §e" + ElevatorConfig.getBidirectionalBlock().name() + " §f上单击跳跃向上传送 §e" + ElevatorConfig.getBidirectionalBlockUpDistance() + " §f格，按Shift键向下传送 §e" + ElevatorConfig.getBidirectionalBlockDownDistance() + " §f格");
        sender.sendMessage("§f4. 每种方块都有独立的传送距离配置");
        sender.sendMessage("§f5. 传送有 §e" + ElevatorConfig.getCooldownSeconds() + " §f秒冷却时间");
        sender.sendMessage("§f6. 配置文件位置: §eelevator.json");
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6=== 电梯系统命令帮助 ===");
        sender.sendMessage("§f/elevator reload §7- 重载配置文件");
        sender.sendMessage("§f/elevator status §7- 查看系统状态");
        sender.sendMessage("§f/elevator info §7- 查看使用说明");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            List<String> completions = Arrays.asList("reload", "status", "info");
            List<String> result = new ArrayList<>();
            
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(completion);
                }
            }
            
            return result;
        }
        
        return new ArrayList<>();
    }
}