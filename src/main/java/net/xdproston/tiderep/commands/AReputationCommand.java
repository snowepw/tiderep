package net.xdproston.tiderep.commands;

import java.util.ArrayList;
import java.util.List;
import me.clip.placeholderapi.libs.kyori.adventure.audience.Audience;
import me.clip.placeholderapi.libs.kyori.adventure.text.ComponentLike;
import net.xdproston.tiderep.Main;
import net.xdproston.tiderep.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.xdproston.tiderep.Files;
import org.jetbrains.annotations.NotNull;

public class AReputationCommand implements CommandExecutor, TabCompleter
{
    private static final Database database = Main.getCurrentDatabase();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    private static @NotNull String rc(String target) {
        return ChatColor.translateAlternateColorCodes('&', target);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        Audience audience = null;

        if (isPlayer) audience = (Audience)sender;

        if (args.length < 3) {
            if (isPlayer) {
                audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label))));
                return true;
            }
            sender.sendMessage(rc(mm.stripTags(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label))));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            if (isPlayer) {
                audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.GLOBAL_PLAYER_NOT_FOUND)));
                return true;
            }
            sender.sendMessage(rc(mm.stripTags(Files.Config.GLOBAL_PLAYER_NOT_FOUND)));
            return true;
        }

        int amount;
        try { amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            if (isPlayer) {
                audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label))));
                return true;
            }
            sender.sendMessage(rc(mm.stripTags(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label))));
            return true;
        }

        switch (args[1]) {
            case "take": {
                database.setPlayerReputation(target, database.getPlayerReputation(target) - amount);
                if (isPlayer) {
                    audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.AREPUTATION_CMD_TAKE.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount)))));
                    return true;
                }
                sender.sendMessage(rc(mm.stripTags(Files.Config.AREPUTATION_CMD_TAKE.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount)))));
                return true;
            }
            case "give": {
                database.setPlayerReputation(target, database.getPlayerReputation(target) + amount);
                if (isPlayer) {
                    audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.AREPUTATION_CMD_GIVE.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount)))));
                    return true;
                }
                sender.sendMessage(rc(mm.stripTags(Files.Config.AREPUTATION_CMD_GIVE.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount)))));
                return true;
            }
            case "set": {
                database.setPlayerReputation(target, amount);
                if (isPlayer) {
                    audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.AREPUTATION_CMD_SET.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount)))));
                    return true;
                }
                sender.sendMessage(rc(mm.stripTags(Files.Config.AREPUTATION_CMD_SET.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount)))));
                return true;
            }
            default: {
                if (isPlayer) {
                    audience.sendMessage((ComponentLike)mm.deserialize(ChatColor.stripColor(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label))));
                    return true;
                }
                sender.sendMessage(rc(mm.stripTags(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label))));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) return database.getPlayerNamesInDatabase();
        if (args.length == 2) return Lists.newArrayList("take", "give", "set");
        if (args.length == 3) return Lists.newArrayList("10");
        return new ArrayList<>();
    }
}