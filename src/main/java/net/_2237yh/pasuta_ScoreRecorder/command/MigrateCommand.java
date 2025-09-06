package net._2237yh.pasuta_ScoreRecorder.command;

import net._2237yh.pasuta_ScoreRecorder.config.MainConfig;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreManager;
import net._2237yh.pasuta_ScoreRecorder.manager.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class MigrateCommand implements CommandExecutor {
    private final MainConfig mainConfig;
    private final ScoreManager scoreManager;
    private final WalletManager walletManager;
    private static final String ADMIN_PERMISSION = "pasutascore.admin";

    public MigrateCommand(MainConfig mainConfig, ScoreManager scoreManager, WalletManager walletManager) {
        this.mainConfig = mainConfig;
        this.scoreManager = scoreManager;
        this.walletManager = walletManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(ADMIN_PERMISSION)) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (mainConfig.isMigrationCompleted()) {
            sender.sendMessage("§cスコアの移行処理は既に完了しています。");
            return true;
        }

        sender.sendMessage("§aスコアからウォレットへのデータ移行を開始します...");

        Map<UUID, Integer> allScores = scoreManager.getAllScores();
        int migratedCount = 0;

        for (Map.Entry<UUID, Integer> entry : allScores.entrySet()) {
            UUID uuid = entry.getKey();
            int score = entry.getValue();

            if (score > 0) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                int walletToAdd = score / 100;
                if (walletToAdd > 0) {
                    walletManager.addWallet(player, walletToAdd);
                    migratedCount++;
                }
            }
        }

        mainConfig.setMigrationCompleted(true);
        sender.sendMessage("§aデータ移行が完了しました。" + migratedCount + "人のプレイヤーのウォレットを更新しました。");
        sender.sendMessage("§e注意: この処理は一度しか実行できません。");

        return true;
    }
}
