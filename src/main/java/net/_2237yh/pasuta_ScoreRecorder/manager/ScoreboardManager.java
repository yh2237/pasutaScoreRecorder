package net._2237yh.pasuta_ScoreRecorder.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {
    private final ScoreManager scoreManager;
    private final WalletManager walletManager;

    public ScoreboardManager(ScoreManager scoreManager, WalletManager walletManager) {
        this.scoreManager = scoreManager;
        this.walletManager = walletManager;
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("pasuta_stats", "dummy", ChatColor.AQUA + "" + ChatColor.BOLD + "INFORMATION");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = scoreManager.getScore(player);
        int wallet = walletManager.getWallet(player);

        Score scoreLine = objective.getScore(ChatColor.AQUA + "Score:");
        scoreLine.setScore(score);

        Score walletLine = objective.getScore(ChatColor.GREEN + "Wallet:");
        walletLine.setScore(wallet);

        player.setScoreboard(board);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
