package net._2237yh.pasuta_ScoreRecorder.command;

import net._2237yh.pasuta_ScoreRecorder.config.MainConfig;
import net._2237yh.pasuta_ScoreRecorder.manager.ScoreManager;
import net._2237yh.pasuta_ScoreRecorder.manager.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreGiveCommand implements CommandExecutor, TabCompleter {
    private final ScoreManager scoreManager;
    private final WalletManager walletManager;
    private final MainConfig mainConfig;
    private static final String ADMIN_PERMISSION = "pasutascore.admin";

    public ScoreGiveCommand(ScoreManager scoreManager, WalletManager walletManager, MainConfig mainConfig) {
        this.scoreManager = scoreManager;
        this.walletManager = walletManager;
        this.mainConfig = mainConfig;
    }

    private boolean noPermission(CommandSender sender) {
        sender.sendMessage("§cこのコマンドを実行する権限がありません。");
        return true;
    }

    public static Player getPlayerFromSelector(CommandSender sender, String selector) {
        if (selector.startsWith("@")) {
            try {
                List<Entity> entities = Bukkit.selectEntities(sender, selector);
                for (Entity entity : entities) {
                    if (entity instanceof Player) {
                        return (Player) entity;
                    }
                }
                return null;
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c不正なセレクター構文です: " + selector);
                return null;
            }
        } else {
            return Bukkit.getPlayer(selector);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e使い方: /psr <give|list|add|remove> ...");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "toggle": {
                if (!sender.hasPermission(ADMIN_PERMISSION)) return noPermission(sender);
                boolean currentState = mainConfig.isShowScoreMessages();
                mainConfig.setShowScoreMessages(!currentState);
                sender.sendMessage("§aスコア獲得メッセージを " + (!currentState ? "§eON" : "§cOFF") + " §aにしました。");
                return true;
            }
            case "list": {
                if (args.length < 2) {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cプレイヤーを指定してください");
                        return true;
                    }
                    int score = scoreManager.getScore(player);
                    int wallet = walletManager.getWallet(player);
                    sender.sendMessage("§aあなたのスコア: §e" + score);
                    sender.sendMessage("§aあなたのウォレット: §e" + wallet);
                    return true;
                }
                Player target = getPlayerFromSelector(sender, args[1]);
                if (target == null) {
                    sender.sendMessage("§cプレイヤーが見つかりません: " + args[1]);
                    return true;
                }
                int score = scoreManager.getScore(target);
                int wallet = walletManager.getWallet(target);
                sender.sendMessage("§a" + target.getName() + "のスコア: §e" + score);
                sender.sendMessage("§a" + target.getName() + "のウォレット: §e" + wallet);
                return true;
            }

            case "add": {
                if (!sender.hasPermission(ADMIN_PERMISSION)) return noPermission(sender);
                if (args.length < 3) {
                    sender.sendMessage("§e使い方: /psr add <player> <score>");
                    return true;
                }
                Player target = getPlayerFromSelector(sender, args[1]);
                if (target == null) {
                    sender.sendMessage("§cプレイヤーが見つかりません: " + args[1]);
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    scoreManager.addScore(target, amount);
                    sender.sendMessage("§a" + target.getName() + " に §b+" + amount + " §aスコアを追加しました");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cスコアは整数で指定してください");
                }
                return true;
            }

            default: {
                sender.sendMessage("§c不明なサブコマンド: " + sub);
                sender.sendMessage("§e使い方: /psr <toggle|list|add>");
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            return Arrays.asList("toggle", "list", "add");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("add")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
            }
        }
        return completions;
    }
}
