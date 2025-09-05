package net._2237yh.pasuta_ScoreRecorder.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockScoreConfig {
    private final Map<Material, Integer> blockScores = new HashMap<>();
    private final Set<Material> excludedBlocks = new HashSet<>();
    private int defaultScore = 0;

    public BlockScoreConfig(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "blocks.yml");
        if (!configFile.exists()) {
            plugin.saveResource("blocks.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        defaultScore = config.getInt("default", 0);

        if (config.contains("excluded")) {
            for (String key : config.getStringList("excluded")) {
                Material mat = Material.matchMaterial(key.replace("minecraft:", ""));
                if (mat != null) {
                    excludedBlocks.add(mat);
                }
            }
        }

        for (String key : config.getKeys(false)) {
            if (key.equals("default") || key.equals("excluded")) continue;
            Material material = Material.matchMaterial(key);
            if (material != null) {
                blockScores.put(material, config.getInt(key));
            }
        }
    }

    public int getScore(Material material) {
        if (excludedBlocks.contains(material)) return 0;
        return blockScores.getOrDefault(material, defaultScore);
    }
}
