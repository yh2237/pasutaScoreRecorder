package net._2237yh.pasuta_ScoreRecorder.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public MainConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public boolean isShowScoreMessages() {
        return config.getBoolean("show-score-messages", true);
    }

    public void setShowScoreMessages(boolean value) {
        config.set("show-score-messages", value);
        plugin.saveConfig();
    }

    public boolean isMigrationCompleted() {
        return config.getBoolean("migration-completed", false);
    }

    public void setMigrationCompleted(boolean value) {
        config.set("migration-completed", value);
        plugin.saveConfig();
    }
}
