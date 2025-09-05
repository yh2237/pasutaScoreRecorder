package net._2237yh.pasuta_ScoreRecorder.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net._2237yh.pasuta_ScoreRecorder.manager.WalletManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class WalletPlaceholder extends PlaceholderExpansion {
    private final WalletManager walletManager;

    public WalletPlaceholder(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pasutawallet";
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
        if (params.equalsIgnoreCase("balance")) {
            if (player != null) {
                return String.valueOf(walletManager.getWallet(player));
            }
        }
        return null;
    }
}
