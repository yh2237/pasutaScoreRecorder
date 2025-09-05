package net._2237yh.pasuta_ScoreRecorder.manager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PanelManager {
    private final JavaPlugin plugin;
    private final File panelFile;
    private FileConfiguration panelConfig;
    private final Map<UUID, Boolean> panelStatus = new HashMap<>();

    public PanelManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.panelFile = new File(plugin.getDataFolder(), "panels.yml");
        loadPanelSettings();
    }

    public void loadPanelSettings() {
        if (!panelFile.exists()) {
            try {
                panelFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("panels.yml の作成に失敗しました！");
            }
        }
        panelConfig = YamlConfiguration.loadConfiguration(panelFile);
        if (panelConfig.contains("players")) {
            panelConfig.getConfigurationSection("players").getKeys(false).forEach(uuidString -> {
                UUID uuid = UUID.fromString(uuidString);
                panelStatus.put(uuid, panelConfig.getBoolean("players." + uuidString));
            });
        }
    }

    public void savePanelSettings() {
        try {
            panelConfig.save(panelFile);
        } catch (IOException e) {
            plugin.getLogger().warning("panels.yml の保存に失敗しました。");
        }
    }

    public boolean isPanelEnabled(Player player) {
        return panelStatus.getOrDefault(player.getUniqueId(), true);
    }

    public void setPanelEnabled(Player player, boolean enabled) {
        panelStatus.put(player.getUniqueId(), enabled);
        panelConfig.set("players." + player.getUniqueId().toString(), enabled);
        savePanelSettings();
    }
}
