package net.xdproston.tiderep.commands;

import java.util.ArrayList;
import java.util.List;
import net.xdproston.tiderep.Main;
import net.xdproston.tiderep.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;
import net.xdproston.tiderep.Files;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.jetbrains.annotations.NotNull;

public class ReputationCommand implements CommandExecutor, TabCompleter
{
    private static final Database database = Main.getCurrentDatabase();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            ((Audience) sender).sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.GLOBAL_ONLY_PLAYER));
            return true;
        }

        Audience cmdSender = (Audience) sender;
        if (args.length < 2) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.REPUTATION_CMD_USAGE, Placeholder.parsed("label", label)));
            return true;
        }

        StringReader reader = new StringReader(String.join(" ", args));
        String targetName;
        try {
            targetName = StringArgumentType.word().parse(reader);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.REPUTATION_CMD_USAGE, Placeholder.parsed("label", label)));
            return true;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.GLOBAL_PLAYER_NOT_FOUND));
            return true;
        }
        if (target == sender) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.GLOBAL_USE_SELF));
            return true;
        }

        if (database.containsPlayerInSends((Player)sender, target)) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.REPUTATION_CMD_ALREADY));
            return true;
        }

        String operation;
        try {
            operation = StringArgumentType.word().parse(reader);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.REPUTATION_CMD_USAGE, Placeholder.parsed("label", label)));
            return true;
        }
        if (operation.equalsIgnoreCase("+")) {
            database.setPlayerReputation(target, database.getPlayerReputation(target) + Files.Config.LIKE_MODIFICATOR);
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    Files.Config.REPUTATION_CMD_TO_SENDER_UP,
                    Placeholder.parsed("player", target.getName())));
            ((Audience) target).sendMessage(MiniMessage.miniMessage().deserialize(
                    Files.Config.REPUTATION_CMD_TO_RECIPIENT_UP,
                    Placeholder.parsed("player", sender.getName())));
            database.addPlayerToSends((Player)sender, target);
            return true;
        } else if (operation.equalsIgnoreCase("-")) {
            database.setPlayerReputation(target, database.getPlayerReputation(target) - Files.Config.DISLIKE_MODIFICATOR);
            cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(
                    Files.Config.REPUTATION_CMD_TO_SENDER_DOWN,
                    Placeholder.parsed("player", target.getName())));
            ((Audience) target).sendMessage(MiniMessage.miniMessage().deserialize(
                    Files.Config.REPUTATION_CMD_TO_RECIPIENT_DOWN,
                    Placeholder.parsed("player", sender.getName())));
            database.addPlayerToSends((Player)sender, target);
            return true;
        }

        cmdSender.sendMessage(MiniMessage.miniMessage().deserialize(Files.Config.REPUTATION_CMD_USAGE, Placeholder.parsed("label", label)));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) return database.getPlayerNamesInDatabase();
        if (args.length == 2) return Lists.newArrayList("+", "-");
        return new ArrayList<>();
    }
}