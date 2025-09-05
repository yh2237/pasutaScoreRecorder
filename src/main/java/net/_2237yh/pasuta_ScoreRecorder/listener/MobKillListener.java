package net._2237yh.pasuta_ScoreRecorder.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import net._2237yh.pasuta_ScoreRecorder.manager.ScoreManager;
import net._2237yh.pasuta_ScoreRecorder.config.MainConfig;
import net._2237yh.pasuta_ScoreRecorder.config.MobScoreConfig;

public class MobKillListener implements Listener {
    private final ScoreManager scoreManager;
    private final MobScoreConfig mobScoreConfig;
    private final MainConfig mainConfig;

    public MobKillListener(ScoreManager scoreManager, MobScoreConfig mobScoreConfig, MainConfig mainConfig) {
        this.scoreManager = scoreManager;
        this.mobScoreConfig = mobScoreConfig;
        this.mainConfig = mainConfig;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Entity mob = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            EntityType type = mob.getType();
            int score = mobScoreConfig.getScore(type);

            if (score > 0) {
                scoreManager.addScore(killer, score);
                if (mainConfig.isShowScoreMessages()) {
                    killer.sendMessage("+" + score + " ポイント獲得（Mob: " + type.name() + "）");
                }
            }
        }
    }
}
