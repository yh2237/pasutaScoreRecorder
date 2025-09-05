package net._2237yh.pasuta_ScoreRecorder.config;

import org.bukkit.entity.EntityType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MobScoreConfig {
    private final Map<EntityType, Integer> mobScores = new HashMap<>();
    private final Set<EntityType> excludedMobs = new HashSet<>();
    private int defaultScore = 0;

    public MobScoreConfig(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "mobs.yml");
        if (!configFile.exists()) {
            plugin.saveResource("mobs.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        defaultScore = config.getInt("default", 0);

        if (config.contains("excluded")) {
            for (String key : config.getStringList("excluded")) {
                EntityType type = EntityType.fromName(key.replace("minecraft:", ""));
                if (type != null) {
                    excludedMobs.add(type);
                }
            }
        }

        for (String key : config.getKeys(false)) {
            if (key.equals("default") || key.equals("excluded")) continue;
            EntityType type = EntityType.fromName(key.replace("minecraft:", ""));
            if (type != null) {
                mobScores.put(type, config.getInt(key));
            }
        }
    }

    public int getScore(EntityType type) {
        if (excludedMobs.contains(type)) return 0;
        return mobScores.getOrDefault(type, defaultScore);
    }
}
