package net.xdproston.tiderep.commands;

import java.util.ArrayList;
import java.util.List;
import net.xdproston.tiderep.Main;
import net.xdproston.tiderep.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.audience.Audience;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.xdproston.tiderep.Files;
import org.jetbrains.annotations.NotNull;

public class AReputationCommand implements CommandExecutor, TabCompleter
{
    private static final Database database = Main.getCurrentDatabase();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Audience audience = (Audience) sender;

        if (args.length < 3) {
            audience.sendMessage(Files.message("commands.areputation.usage", Files.Config.AREPUTATION_CMD_USAGE,
                    Placeholder.parsed("label", label)));
            return true;
        }

        StringReader reader = new StringReader(String.join(" ", args));
        String targetName;
        try {
            targetName = StringArgumentType.word().parse(reader);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            audience.sendMessage(Files.message("commands.areputation.usage", Files.Config.AREPUTATION_CMD_USAGE,
                    Placeholder.parsed("label", label)));
            return true;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            audience.sendMessage(Files.message("global-messages.player-not-found", Files.Config.GLOBAL_PLAYER_NOT_FOUND));
            return true;
        }

        String operation;
        int amount;
        try {
            operation = StringArgumentType.word().parse(reader);
            amount = Integer.parseInt(StringArgumentType.word().parse(reader));
        } catch (Exception e) {
            audience.sendMessage(Files.message("commands.areputation.usage", Files.Config.AREPUTATION_CMD_USAGE,
                    Placeholder.parsed("label", label)));
            return true;
        }

        switch (operation.toLowerCase()) {
            case "take": {
                database.setPlayerReputation(target, database.getPlayerReputation(target) - amount);
                audience.sendMessage(Files.message("commands.areputation.take", Files.Config.AREPUTATION_CMD_TAKE,
                        Placeholder.parsed("player", target.getName()),
                        Placeholder.parsed("amount", String.valueOf(amount))));
                return true;
            }
            case "give": {
                database.setPlayerReputation(target, database.getPlayerReputation(target) + amount);
                audience.sendMessage(Files.message("commands.areputation.give", Files.Config.AREPUTATION_CMD_GIVE,
                        Placeholder.parsed("player", target.getName()),
                        Placeholder.parsed("amount", String.valueOf(amount))));
                return true;
            }
            case "set": {
                database.setPlayerReputation(target, amount);
                audience.sendMessage(Files.message("commands.areputation.set", Files.Config.AREPUTATION_CMD_SET,
                        Placeholder.parsed("player", target.getName()),
                        Placeholder.parsed("amount", String.valueOf(amount))));
                return true;
            }
            default: {
                audience.sendMessage(Files.message("commands.areputation.usage", Files.Config.AREPUTATION_CMD_USAGE,
                        Placeholder.parsed("label", label)));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) return database.getPlayerNamesInDatabase();
        if (args.length == 2) return Lists.newArrayList("take", "give", "set", "reload");
        if (args.length == 3) return Lists.newArrayList("10");
        return new ArrayList<>();
    }
}