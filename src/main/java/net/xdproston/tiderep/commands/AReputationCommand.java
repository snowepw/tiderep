package net.xdproston.tiderep.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.xdproston.tiderep.Database;
import net.xdproston.tiderep.Files;

public class AReputationCommand implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Files.Config.GLOBAL_ONLY_PLAYER));
            return true;
        }

        Audience cmdSender = (Audience)sender;
        if (args.length < 3) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label)));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.GLOBAL_PLAYER_NOT_FOUND));
            return true;
        }

        int amount = 0;
        try {amount = Integer.parseInt(args[2]);}
        catch (Exception e) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label)));
            return true;
        }

        switch (args[1]) {
            case "take": {
                Database.setPlayerReputation(target, Database.getPlayerReputation(target) - amount);
                cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.AREPUTATION_CMD_TAKE.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount))));
                return true;
            }
            case "give": {
                Database.setPlayerReputation(target, Database.getPlayerReputation(target) + amount);
                cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.AREPUTATION_CMD_GIVE.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount))));
                return true;
            }
            case "set": {
                Database.setPlayerReputation(target, amount);
                cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.AREPUTATION_CMD_SET.replace("%player%", target.getName()).replace("%amount%", String.format("%d", amount))));
                return true;
            }
            default: {
                cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.AREPUTATION_CMD_USAGE.replace("%label%", label)));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return Database.getPlayersInDatabase();
        if (args.length == 2) return Lists.newArrayList("take", "give", "set");
        if (args.length == 3) return Lists.newArrayList("10");
        return new ArrayList<>();
    }
}