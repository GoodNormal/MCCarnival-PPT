package org.MCCarnival.mCCarnivalPPT;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCCarnivalPPT extends JavaPlugin {

    // 插件实例
    private static MCCarnivalPPT instance;
    
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
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PPTItemListener(), this);
        
        getLogger().info("MCCarnival-PPT插件已启用！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
