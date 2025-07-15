package net.xdproston.tiderep;

import net.xdproston.tiderep.interfaces.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
            return database.hasPlayerInDatabase((Player) player)
                    ? String.valueOf(database.getPlayerReputation((Player) player))
                    : null;
        }

        if (params.equalsIgnoreCase("advanced_reputation")) {
            if (!database.hasPlayerInDatabase((Player) player)) return null;

            int reputation = database.getPlayerReputation((Player) player);
            Component comp;
            if (reputation == 0) comp = MiniMessage.miniMessage().deserialize("<yellow>" + reputation);
            else if (reputation < 0) comp = MiniMessage.miniMessage().deserialize("<red>" + reputation);
            else comp = MiniMessage.miniMessage().deserialize("<green>+" + reputation);
            return MiniMessage.miniMessage().serialize(comp);
        }

        return null;
    }
}