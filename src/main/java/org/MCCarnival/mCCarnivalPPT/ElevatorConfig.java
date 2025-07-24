package org.MCCarnival.mCCarnivalPPT;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class ElevatorConfig {
    
    private static JsonObject config;
    private static final Logger logger = MCCarnivalPPT.getInstance().getLogger();
    
    public static void loadConfig() {
        try {
            File configFile = new File(MCCarnivalPPT.getInstance().getDataFolder(), "elevator.json");
            
            if (!configFile.exists()) {
                // 如果配置文件不存在，从resources复制
                MCCarnivalPPT.getInstance().saveResource("elevator.json", false);
            }
            
            Gson gson = new Gson();
            FileReader reader = new FileReader(configFile);
            config = gson.fromJson(reader, JsonObject.class);
            reader.close();
            
            logger.info("电梯配置文件加载成功！");
            
        } catch (IOException e) {
            logger.severe("无法加载电梯配置文件: " + e.getMessage());
            // 使用默认配置
            createDefaultConfig();
        }
    }
    
    private static final String DEFAULT_CONFIG = "{\"elevator\":{\"enabled\":true,\"move-distance\":10,\"up-block\":\"EMERALD_BLOCK\",\"down-block\":\"REDSTONE_BLOCK\",\"bidirectional-block\":\"DIAMOND_BLOCK\",\"up-block-distance\":10,\"down-block-distance\":10,\"bidirectional-block-up-distance\":10,\"bidirectional-block-down-distance\":10,\"sound-enabled\":true,\"sound-type\":\"ENTITY_ENDERMAN_TELEPORT\",\"particle-enabled\":true,\"particle-type\":\"PORTAL\",\"cooldown-seconds\":3}}";
    
    private static void createDefaultConfig() {
        Gson gson = new Gson();
        config = gson.fromJson(DEFAULT_CONFIG, JsonObject.class);
    }
    
    public static boolean isEnabled() {
        if (config == null) return false;
        return config.getAsJsonObject("elevator").get("enabled").getAsBoolean();
    }
    
    public static int getMoveDistance() {
        if (config == null) return 5;
        return config.getAsJsonObject("elevator").get("move-distance").getAsInt();
    }
    
    public static int getUpBlockDistance() {
        if (config == null) return 10;
        return config.getAsJsonObject("elevator").get("up-block-distance").getAsInt();
    }
    
    public static int getDownBlockDistance() {
        if (config == null) return 10;
        return config.getAsJsonObject("elevator").get("down-block-distance").getAsInt();
    }
    
    public static int getBidirectionalBlockUpDistance() {
        if (config == null) return 10;
        return config.getAsJsonObject("elevator").get("bidirectional-block-up-distance").getAsInt();
    }
    
    public static int getBidirectionalBlockDownDistance() {
        if (config == null) return 10;
        return config.getAsJsonObject("elevator").get("bidirectional-block-down-distance").getAsInt();
    }
    
    public static Material getUpBlock() {
        if (config == null) return Material.EMERALD_BLOCK;
        try {
            String blockName = config.getAsJsonObject("elevator").get("up-block").getAsString();
            return Material.valueOf(blockName.toUpperCase());
        } catch (Exception e) {
            logger.warning("无效的上行方块类型，使用默认值: EMERALD_BLOCK");
            return Material.EMERALD_BLOCK;
        }
    }
    
    public static Material getDownBlock() {
        if (config == null) return Material.REDSTONE_BLOCK;
        try {
            String blockName = config.getAsJsonObject("elevator").get("down-block").getAsString();
            return Material.valueOf(blockName.toUpperCase());
        } catch (Exception e) {
            logger.warning("无效的下行方块类型，使用默认值: REDSTONE_BLOCK");
            return Material.REDSTONE_BLOCK;
        }
    }
    
    public static Material getBidirectionalBlock() {
        if (config == null) return Material.DIAMOND_BLOCK;
        try {
            String blockName = config.getAsJsonObject("elevator").get("bidirectional-block").getAsString();
            return Material.valueOf(blockName.toUpperCase());
        } catch (Exception e) {
            logger.warning("无效的双向方块类型，使用默认值: DIAMOND_BLOCK");
            return Material.DIAMOND_BLOCK;
        }
    }
    

    
    public static boolean isSoundEnabled() {
        if (config == null) return true;
        return config.getAsJsonObject("elevator").get("sound-enabled").getAsBoolean();
    }
    
    public static Sound getSoundType() {
        if (config == null) return Sound.ENTITY_ENDERMAN_TELEPORT;
        try {
            String soundName = config.getAsJsonObject("elevator").get("sound-type").getAsString();
            return Sound.valueOf(soundName.toUpperCase());
        } catch (Exception e) {
            logger.warning("无效的音效类型，使用默认值: ENTITY_ENDERMAN_TELEPORT");
            return Sound.ENTITY_ENDERMAN_TELEPORT;
        }
    }
    
    public static boolean isParticleEnabled() {
        if (config == null) return true;
        return config.getAsJsonObject("elevator").get("particle-enabled").getAsBoolean();
    }
    
    public static Particle getParticleType() {
        if (config == null) return Particle.PORTAL;
        try {
            String particleName = config.getAsJsonObject("elevator").get("particle-type").getAsString();
            return Particle.valueOf(particleName.toUpperCase());
        } catch (Exception e) {
            logger.warning("无效的粒子效果类型，使用默认值: PORTAL");
            return Particle.PORTAL;
        }
    }
    
    public static int getCooldownSeconds() {
        if (config == null) return 2;
        return config.getAsJsonObject("elevator").get("cooldown-seconds").getAsInt();
    }
}