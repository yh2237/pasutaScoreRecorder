package net._2237yh.pasuta_ScoreRecorder.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.TreeMap;

public class MessageConfig {
    private final TreeMap<Integer, MessageData> messages = new TreeMap<>();

    public MessageConfig(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "message.yml");
        if (!configFile.exists()) {
            plugin.saveResource("message.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (config.contains("messages")) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                try {
                    int score = Integer.parseInt(key);
                    String message = config.getString("messages." + key + ".message");
                    String sound = config.getString("messages." + key + ".sound");
                    messages.put(score, new MessageData(message, sound));
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("不正なスコア: " + key);
                }
            }
        }
    }

    public MessageData getMessage(int score) {
        return messages.get(score);
    }

    public TreeMap<Integer, MessageData> getMessages() {
        return messages;
    }
}
