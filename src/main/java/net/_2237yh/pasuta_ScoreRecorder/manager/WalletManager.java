package net._2237yh.pasuta_ScoreRecorder.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WalletManager {
    private final Map<UUID, Integer> wallets = new HashMap<>();
    private final JavaPlugin plugin;
    private File walletFile;
    private YamlConfiguration config;
    private ScoreboardManager scoreboardManager;

    public WalletManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        walletFile = new File(plugin.getDataFolder(), "wallets.yml");
        if (!walletFile.exists()) {
            try {
                walletFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("wallets.yml の作成に失敗しました！");
            }
        }
        config = YamlConfiguration.loadConfiguration(walletFile);
        loadWallets();
    }

    private void loadWallets() {
        if (!config.contains("wallets")) return;

        for (String key : config.getConfigurationSection("wallets").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int wallet = config.getInt("wallets." + key);
                wallets.put(uuid, wallet);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("不正なUUID: " + key);
            }
        }
    }

    public void saveWallets() {
        config.set("wallets", null);
        for (Map.Entry<UUID, Integer> entry : wallets.entrySet()) {
            config.set("wallets." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(walletFile);
        } catch (IOException e) {
            plugin.getLogger().warning("wallets.yml の保存に失敗しました！");
        }
    }

    public int getWallet(OfflinePlayer player) {
        return wallets.getOrDefault(player.getUniqueId(), 0);
    }

    public void setWallet(OfflinePlayer player, int amount) {
        wallets.put(player.getUniqueId(), amount);
        saveWallets();
        if (scoreboardManager != null && player.isOnline()) {
            scoreboardManager.updateScoreboard(player.getPlayer());
        }
    }

    public void addWallet(OfflinePlayer player, int amount) {
        int current = getWallet(player);
        setWallet(player, current + amount);
    }

    public void removeWallet(OfflinePlayer player, int amount) {
        int current = getWallet(player);
        setWallet(player, Math.max(0, current - amount));
    }

    public void setScoreboardManager(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }
}
