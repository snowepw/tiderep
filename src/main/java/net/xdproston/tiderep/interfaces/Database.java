package net.xdproston.tiderep.interfaces;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public interface Database {
    void init();
    boolean hasPlayerInDatabase(Player target);
    void addPlayerToDatabase(Player target);
    int getPlayerReputation(Player target);
    void addPlayerToSends(Player target, Player player);
    boolean containsPlayerInSends(Player target, Player player);
    ArrayList<String> getPlayerNamesInDatabase();
    void setPlayerReputation(Player target, int reputation);
    void close();
}
