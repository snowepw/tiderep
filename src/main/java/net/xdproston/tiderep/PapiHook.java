package net.xdproston.tiderep;

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
        if (params.equalsIgnoreCase("reputation")) {
            return Database.hasPlayerInDatabase((Player)player) ? String.format("%d", Database.getPlayerReputation((Player)player)) : null;
        }

        if (params.equalsIgnoreCase("advanced_reputation")) {
            if (!Database.hasPlayerInDatabase((Player)player)) return null;

            int reputation = Database.getPlayerReputation((Player)player);
            if (reputation == 0) return ChatColor.translateAlternateColorCodes('&', String.format("&e%d", reputation));
            else if (reputation < 0) return ChatColor.translateAlternateColorCodes('&', String.format("&c%d", reputation));
            else return ChatColor.translateAlternateColorCodes('&', String.format("&a+%d", reputation));
        }

        return null;
    }
}