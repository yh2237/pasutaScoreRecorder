package net._2237yh.pasuta_ScoreRecorder;

import net._2237yh.pasuta_ScoreRecorder.command.MigrateCommand;
import net._2237yh.pasuta_ScoreRecorder.command.PanelCommand;
import net._2237yh.pasuta_ScoreRecorder.command.ScoreGiveCommand;
import net._2237yh.pasuta_ScoreRecorder.command.WalletCommand;
import net._2237yh.pasuta_ScoreRecorder.config.MainConfig;
import net._2237yh.pasuta_ScoreRecorder.config.MessageConfig;
import net._2237yh.pasuta_ScoreRecorder.config.MobScoreConfig;
import net._2237yh.pasuta_ScoreRecorder.listener.MobKillListener;
import net._2237yh.pasuta_ScoreRecorder.manager.WalletManager;
import net._2237yh.pasuta_ScoreRecorder.placeholder.ScorePlaceholder;
import net._2237yh.pasuta_ScoreRecorder.placeholder.WalletPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net._2237yh.pasuta_ScoreRecorder.manager.PanelManager;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreManager;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreboardManager;
import net._2237yh.pasuta_ScoreRecorder.config.BlockScoreConfig;
import net._2237yh.pasuta_ScoreRecorder.listener.BlockBreakListener;
import net._2237yh.pasuta_ScoreRecorder.listener.PlayerConnectionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PasutaScoreRecorder extends JavaPlugin {
    private ScoreManager scoreManager;
    private BlockScoreConfig blockScoreConfig;
    private MobScoreConfig mobScoreConfig;
    private MessageConfig messageConfig;
    private MainConfig mainConfig;
    private WalletManager walletManager;
    private PanelManager panelManager;
    private ScoreboardManager scoreboardManager;


    private void printAsciiArt() {
        try (InputStream in = getResource("ascii.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Bukkit.getConsoleSender().sendMessage(line);
            }
        } catch (IOException | NullPointerException e) {
            getLogger().warning("AAの読み込みに失敗: " + e.getMessage());
        }
    }

    @Override
    public void onEnable() {
        printAsciiArt();
        saveResource("blocks.yml", false);
        saveResource("mobs.yml", false);
        saveResource("message.yml", false);

        scoreManager = new ScoreManager(this);
        blockScoreConfig = new BlockScoreConfig(this);
        mobScoreConfig = new MobScoreConfig(this);
        messageConfig = new MessageConfig(this);
        mainConfig = new MainConfig(this);
        walletManager = new WalletManager(this);
        panelManager = new PanelManager(this);
        scoreboardManager = new ScoreboardManager(scoreManager, walletManager);

        scoreManager.setScoreboardManager(scoreboardManager);
        walletManager.setScoreboardManager(scoreboardManager);
        scoreManager.setWalletManager(walletManager);

        scoreManager.setMessageConfig(messageConfig);

        getServer().getPluginManager().registerEvents(
                new BlockBreakListener(this, scoreManager, blockScoreConfig, mainConfig), this
        );
        getServer().getPluginManager().registerEvents(
                new MobKillListener(scoreManager, mobScoreConfig, mainConfig), this
        );
        getServer().getPluginManager().registerEvents(
                new PlayerConnectionListener(panelManager, scoreboardManager), this
        );

        getLogger().info("Pasuta_ScoreRecorder が起動！");

        ScoreGiveCommand scoreGiveCommand = new ScoreGiveCommand(scoreManager, walletManager, mainConfig);
        getCommand("psr").setExecutor(scoreGiveCommand);
        getCommand("psr").setTabCompleter(scoreGiveCommand);

        WalletCommand walletCommand = new WalletCommand(walletManager);
        getCommand("wallet").setExecutor(walletCommand);
        getCommand("wallet").setTabCompleter(walletCommand);

        getCommand("panel").setExecutor(new PanelCommand(panelManager, scoreboardManager));
        getCommand("psr-migrate").setExecutor(new MigrateCommand(mainConfig, scoreManager, walletManager));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ScorePlaceholder(this, scoreManager).register();
            new WalletPlaceholder(walletManager).register();
        }
    }


    @Override
    public void onDisable() {
        scoreManager.saveScores();
        walletManager.saveWallets();
        getLogger().info("Pasuta_ScoreRecorder が停止！");
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }
}
