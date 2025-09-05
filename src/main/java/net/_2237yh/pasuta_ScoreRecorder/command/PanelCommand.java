package net._2237yh.pasuta_ScoreRecorder.command;

import net._2237yh.pasuta_ScoreRecorder.manager.PanelManager;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreboardManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PanelCommand implements CommandExecutor {
    private final PanelManager panelManager;
    private final ScoreboardManager scoreboardManager;

    public PanelCommand(PanelManager panelManager, ScoreboardManager scoreboardManager) {
        this.panelManager = panelManager;
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§e使い方: /panel <true|false>");
            return true;
        }

        boolean enabled = Boolean.parseBoolean(args[0]);
        panelManager.setPanelEnabled(player, enabled);

        if (enabled) {
            scoreboardManager.updateScoreboard(player);
            player.sendMessage("§aスコアボードを有効にしました。");
        } else {
            scoreboardManager.removeScoreboard(player);
            player.sendMessage("§cスコアボードを無効にしました。");
        }

        return true;
    }
}
