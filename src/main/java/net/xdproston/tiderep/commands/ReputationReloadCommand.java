package net.xdproston.tiderep.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.xdproston.tiderep.Files;
import net.xdproston.tiderep.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReputationReloadCommand implements CommandExecutor, TabCompleter
{
    private static final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Audience audience = (Audience) sender;

        if (args.length == 0) {
            Main.reload();
            audience.sendMessage(mm.deserialize(Files.Config.REPUTATIONRELOAD_CMD_RELOADED));
            return true;
        }

        audience.sendMessage(mm.deserialize(Files.Config.REPUTATIONRELOAD_CMD_USAGE));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}