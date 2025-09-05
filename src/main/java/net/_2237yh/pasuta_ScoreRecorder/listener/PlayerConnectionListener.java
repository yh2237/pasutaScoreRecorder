package net._2237yh.pasuta_ScoreRecorder.listener;

import net._2237yh.pasuta_ScoreRecorder.manager.PanelManager;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final PanelManager panelManager;
    private final ScoreboardManager scoreboardManager;

    public PlayerConnectionListener(PanelManager panelManager, ScoreboardManager scoreboardManager) {
        this.panelManager = panelManager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (panelManager.isPanelEnabled(player)) {
            scoreboardManager.updateScoreboard(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        scoreboardManager.removeScoreboard(event.getPlayer());
    }
}
