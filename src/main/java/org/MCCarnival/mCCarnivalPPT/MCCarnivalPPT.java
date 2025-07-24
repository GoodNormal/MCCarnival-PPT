package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MCCarnivalPPT extends JavaPlugin {

    // 插件实例
    private static MCCarnivalPPT instance;
    
    public static MCCarnivalPPT getInstance() {
        return instance;
    }
    
    // 控制翻页笔是否被禁用
    private static boolean pagingForbidden = false;
    
    public static boolean isPagingForbidden() {
        return pagingForbidden;
    }
    
    public static void setPagingForbidden(boolean forbidden) {
        pagingForbidden = forbidden;
    }
    
    /**
     * 获取PPT最大页数
     */
    public static int getMaxPage() {
        return instance.getConfig().getInt("ppt.max-page", 100);
    }
    
    /**
     * 获取搜索范围
     */
    public static int getSearchRange() {
        return instance.getConfig().getInt("settings.search-range", 10);
    }
    
    /**
     * 获取支持的物品类型列表
     */
    public static List<Material> getSupportedItems() {
        List<String> itemNames = instance.getConfig().getStringList("supported-items");
        List<Material> materials = new ArrayList<>();
        
        // 如果配置为空，使用默认的幻翼膜
        if (itemNames.isEmpty()) {
            materials.add(Material.PHANTOM_MEMBRANE);
            return materials;
        }
        
        for (String itemName : itemNames) {
            try {
                Material material = Material.valueOf(itemName.toUpperCase());
                materials.add(material);
            } catch (IllegalArgumentException e) {
                instance.getLogger().warning("配置文件中的物品类型无效: " + itemName);
            }
        }
        
        // 如果没有有效的物品类型，使用默认的幻翼膜
        if (materials.isEmpty()) {
            materials.add(Material.PHANTOM_MEMBRANE);
        }
        
        return materials;
    }

    @Override
    public void onEnable() {
        // 设置插件实例
        instance = this;
        
        // 保存默认配置文件
        saveDefaultConfig();
        
        // Plugin startup logic
        
        // 创建命令处理器实例
        PPTCommand pptCommand = new PPTCommand();
        
        // 注册命令处理器和Tab补全器
        this.getCommand("ppt").setExecutor(pptCommand);
        this.getCommand("ppt").setTabCompleter(pptCommand);
        
        PostCommand postCommand = new PostCommand();
        this.getCommand("post").setExecutor(postCommand);
        this.getCommand("post").setTabCompleter(postCommand);
        
        ElevatorCommand elevatorCommand = new ElevatorCommand();
        this.getCommand("elevator").setExecutor(elevatorCommand);
        this.getCommand("elevator").setTabCompleter(elevatorCommand);
        
        PositionCommand positionCommand = new PositionCommand();
        this.getCommand("position").setExecutor(positionCommand);
        this.getCommand("position").setTabCompleter(positionCommand);
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PPTItemListener(), this);
        
        // 加载电梯配置并注册电梯监听器
        ElevatorConfig.loadConfig();
        if (ElevatorConfig.isEnabled()) {
            getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
            getLogger().info("电梯功能已启用！");
        }
        
        // 注册玩家位置管理器监听器
        getServer().getPluginManager().registerEvents(new PlayerPositionManager(), this);
        getLogger().info("玩家位置管理功能已加载！");
        
        getLogger().info("MCCarnival-PPT插件已启用！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
