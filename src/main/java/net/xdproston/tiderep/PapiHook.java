package net.xdproston.tiderep;

import net.xdproston.tiderep.interfaces.Database;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PapiHook extends PlaceholderExpansion
{
    @Override
    public @NotNull String getAuthor() {
        return Main.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tiderep";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Database database = Main.getCurrentDatabase();

        if (params.equalsIgnoreCase("reputation")) {
            return database.hasPlayerInDatabase((Player)player) ? String.format("%d", database.getPlayerReputation((Player)player)) : null;
        }

        if (params.equalsIgnoreCase("advanced_reputation")) {
            if (!database.hasPlayerInDatabase((Player)player)) return null;

            int reputation = database.getPlayerReputation((Player)player);
            if (reputation == 0) return ChatColor.translateAlternateColorCodes('&', String.format("&e%d", reputation));
            else if (reputation < 0) return ChatColor.translateAlternateColorCodes('&', String.format("&c%d", reputation));
            else return ChatColor.translateAlternateColorCodes('&', String.format("&a+%d", reputation));
        }

        return null;
    }
}