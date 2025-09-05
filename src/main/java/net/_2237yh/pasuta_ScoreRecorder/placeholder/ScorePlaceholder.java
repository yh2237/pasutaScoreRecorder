package net._2237yh.pasuta_ScoreRecorder.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net._2237yh.pasuta_ScoreRecorder.PasutaScoreRecorder;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScorePlaceholder extends PlaceholderExpansion {
    private final PasutaScoreRecorder plugin;
    private final ScoreManager scoreManager;

    public ScorePlaceholder(PasutaScoreRecorder plugin, ScoreManager scoreManager) {
        this.plugin = plugin;
        this.scoreManager = scoreManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pasutascore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "yh";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("score")) {
            if (player != null) {
                return String.valueOf(scoreManager.getScore(player));
            }
        }

        if (params.startsWith("top_")) {
            String[] parts = params.split("_");
            if (parts.length == 3) {
                try {
                    int rank = Integer.parseInt(parts[2]);
                    if (rank > 0 && rank <= 10) {
                        List<Map.Entry<UUID, Integer>> topScores = scoreManager.getTopScores(10);
                        if (rank <= topScores.size()) {
                            Map.Entry<UUID, Integer> entry = topScores.get(rank - 1);
                            if (parts[1].equalsIgnoreCase("name")) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
                                return offlinePlayer.getName();
                            } else if (parts[1].equalsIgnoreCase("score")) {
                                return String.valueOf(entry.getValue());
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        return null;
    }
}
