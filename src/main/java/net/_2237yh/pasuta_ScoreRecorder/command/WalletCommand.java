package net._2237yh.pasuta_ScoreRecorder.command;

import net._2237yh.pasuta_ScoreRecorder.manager.WalletManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WalletCommand implements CommandExecutor, TabCompleter {
    private final WalletManager walletManager;
    private static final String ADMIN_PERMISSION = "pasutascore.admin";

    public WalletCommand(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    private boolean noPermission(CommandSender sender) {
        sender.sendMessage("§cこのコマンドを実行する権限がありません。");
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e使い方: /wallet <balance|give|add|remove|buy> ...");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "balance": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cこのコマンドはプレイヤー専用です");
                    return true;
                }
                int balance = walletManager.getWallet(player);
                player.sendMessage("§aあなたのウォレット残高: §e" + balance);
                return true;
            }

            case "give": {
                if (args.length < 3) {
                    sender.sendMessage("§e使い方: /wallet give <player> <amount>");
                    return true;
                }
                if (!(sender instanceof Player giver)) {
                    sender.sendMessage("§cこのコマンドはプレイヤー専用です。コマンドブロック等は /wallet add を使用してください。");
                    return true;
                }
                Player target = ScoreGiveCommand.getPlayerFromSelector(sender, args[1]);
                if (target == null) {
                    sender.sendMessage("§cプレイヤーが見つかりません: " + args[1]);
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    if (amount <= 0) {
                        sender.sendMessage("§c正の値を指定してください");
                        return true;
                    }
                    int giverBalance = walletManager.getWallet(giver);
                    if (giverBalance < amount) {
                        sender.sendMessage("§cウォレット残高が不足しています");
                        return true;
                    }
                    walletManager.removeWallet(giver, amount);
                    walletManager.addWallet(target, amount);
                    sender.sendMessage("§a" + target.getName() + "に " + amount + " ウォレットを送りました");
                    target.sendMessage("§a" + giver.getName() + "から " + amount + " ウォレットを受け取りました");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c量は整数で指定してください");
                }
                return true;
            }

            case "add":
            case "remove": {
                if (!sender.hasPermission(ADMIN_PERMISSION)) return noPermission(sender);
                if (args.length < 3) {
                    sender.sendMessage("§e使い方: /wallet " + sub + " <player> <amount>");
                    return true;
                }
                Player target = ScoreGiveCommand.getPlayerFromSelector(sender, args[1]);
                if (target == null) {
                    sender.sendMessage("§cプレイヤーが見つかりません: " + args[1]);
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    if (sub.equals("add")) {
                        walletManager.addWallet(target, amount);
                        sender.sendMessage("§a" + target.getName() + "のウォレットに " + amount + " 追加しました");
                    } else {
                        walletManager.removeWallet(target, amount);
                        sender.sendMessage("§a" + target.getName() + "のウォレットから " + amount + " 削除しました");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c量は整数で指定してください");
                }
                return true;
            }

            case "buy": {
                if (!sender.hasPermission(ADMIN_PERMISSION)) return noPermission(sender);
                if (args.length < 5) {
                    sender.sendMessage("§e使い方: /wallet buy <player> <item> <amount> <cost>");
                    return true;
                }
                Player target = ScoreGiveCommand.getPlayerFromSelector(sender, args[1]);
                if (target == null) {
                    sender.sendMessage("§cプレイヤーが見つかりません: " + args[1]);
                    return true;
                }
                Material material = Material.matchMaterial(args[2].toUpperCase());
                if (material == null) {
                    sender.sendMessage("§cアイテムが見つかりません");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[3]);
                    int cost = Integer.parseInt(args[4]);
                    if (amount <= 0) {
                        sender.sendMessage("§c個数は1以上で指定してください");
                        return true;
                    }
                    long totalCost = (long) cost * amount;
                    int balance = walletManager.getWallet(target);
                    if (balance < totalCost) {
                        sender.sendMessage("§c" + target.getName() + "のウォレット残高が不足しています。必要: " + totalCost + ", 所持: " + balance);
                        return true;
                    }
                    walletManager.removeWallet(target, (int) totalCost);
                    target.getInventory().addItem(new ItemStack(material, amount));
                    sender.sendMessage("§a" + target.getName() + "が " + totalCost + " ウォレットで " + material.name() + " を " + amount + "個購入しました");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c個数とコストは整数で指定してください");
                }
                return true;
            }

            default: {
                sender.sendMessage("§c不明なサブコマンドです");
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            return Arrays.asList("balance", "give", "add", "remove", "buy");
        }
        if (args.length == 2) {
            if (Arrays.asList("give", "add", "remove", "buy").contains(args[0].toLowerCase())) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
            }
        }
        return completions;
    }
}
