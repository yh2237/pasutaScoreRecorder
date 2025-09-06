package net._2237yh.pasuta_ScoreRecorder.manager;

import net._2237yh.pasuta_ScoreRecorder.config.MessageConfig;
import net._2237yh.pasuta_ScoreRecorder.config.MessageData;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreManager {
    private final Map<UUID, Integer> scores = new HashMap<>();
    private final JavaPlugin plugin;
    private File scoreFile;
    private YamlConfiguration config;
    private MessageConfig messageConfig;
    private ScoreboardManager scoreboardManager;
    private WalletManager walletManager;

    public ScoreManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        scoreFile = new File(plugin.getDataFolder(), "scores.yml");
        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("scores.yml の作成に失敗しました！");
            }
        }
        config = YamlConfiguration.loadConfiguration(scoreFile);
        loadScores();
    }

    private void loadScores() {
        if (!config.contains("scores")) return;

        for (String key : config.getConfigurationSection("scores").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int score = config.getInt("scores." + key);
                scores.put(uuid, score);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("不正なUUID: " + key);
            }
        }
    }

    public void saveScores() {
        config.set("scores", null);
        for (Map.Entry<UUID, Integer> entry : scores.entrySet()) {
            config.set("scores." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(scoreFile);
        } catch (IOException e) {
            plugin.getLogger().warning("scores.yml の保存に失敗しました！");
        }
    }

    public void addScore(Player player, int amount) {
        UUID id = player.getUniqueId();
        int oldScore = getScore(player);
        int newScore = oldScore + amount;
        scores.put(id, newScore);

        // ウォレット連携
        if (walletManager != null) {
            int walletGained = (newScore / 100) - (oldScore / 100);
            if (walletGained > 0) {
                walletManager.addWallet(player, walletGained);
            }
        }

        saveScores();

        if (scoreboardManager != null && player.isOnline()) {
            scoreboardManager.updateScoreboard(player);
        }

        if (messageConfig != null) {
            for (Map.Entry<Integer, MessageData> entry : messageConfig.getMessages().entrySet()) {
                int threshold = entry.getKey();
                MessageData messageData = entry.getValue();
                if (oldScore < threshold && newScore >= threshold) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageData.getMessage()));
                    if (messageData.getSound() != null && !messageData.getSound().isEmpty()) {
                        try {
                            player.playSound(player.getLocation(), Sound.valueOf(messageData.getSound().toUpperCase().replace("MINECRAFT:", "")), 1, 1);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("無効なサウンド: " + messageData.getSound());
                        }
                    }
                }
            }
        }
    }

    public int getScore(OfflinePlayer player) {
        return scores.getOrDefault(player.getUniqueId(), 0);
    }

    public void resetScore(Player player) {
        scores.put(player.getUniqueId(), 0);
        saveScores();
    }

    public boolean hasScore(Player player) {
        return scores.containsKey(player.getUniqueId());
    }

    public Map<UUID, Integer> getAllScores() {
        return new HashMap<>(scores);
    }

    public List<Map.Entry<UUID, Integer>> getTopScores(int limit) {
        return scores.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .toList();
    }

    public void setScore(OfflinePlayer player, int score) {
        scores.put(player.getUniqueId(), score);
        saveScores();
        if (scoreboardManager != null && player.isOnline()) {
            scoreboardManager.updateScoreboard(player.getPlayer());
        }
    }

    public void setMessageConfig(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    public void setScoreboardManager(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    public void setWalletManager(WalletManager walletManager) {
        this.walletManager = walletManager;
    }
}
