package net._2237yh.pasuta_ScoreRecorder.listener;

import net._2237yh.pasuta_ScoreRecorder.config.BlockScoreConfig;
import net._2237yh.pasuta_ScoreRecorder.config.MainConfig;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockBreakListener implements Listener {
    private final JavaPlugin plugin;
    private final ScoreManager scoreManager;
    private final BlockScoreConfig blockScoreConfig;
    private final MainConfig mainConfig;

    public BlockBreakListener(JavaPlugin plugin, ScoreManager scoreManager, BlockScoreConfig blockScoreConfig, MainConfig mainConfig) {
        this.plugin = plugin;
        this.scoreManager = scoreManager;
        this.blockScoreConfig = blockScoreConfig;
        this.mainConfig = mainConfig;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.getBlock().setMetadata("placed_by_player", new FixedMetadataValue(plugin, true));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("placed_by_player")) {
            return;
        }

        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        int score = blockScoreConfig.getScore(blockType);

        if (score > 0) {
            scoreManager.addScore(player, score);
            if (mainConfig.isShowScoreMessages()) {
                player.sendMessage("+" + score + " ポイント獲得（" + blockType.name() + "）");
            }
        }
    }
}