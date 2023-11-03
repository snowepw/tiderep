package net.xdproston.tiderep.interfaces;

import net.xdproston.tiderep.logger.Logger;
import net.xdproston.tiderep.logger.LoggerType;
import org.bukkit.entity.Player;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public interface Database
{
    default void execute(Statement stmt, String sql) {
        try { stmt.execute(sql);
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the statement:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
    }
    default ResultSet executeQuery(Statement stmt, String sql) {
        try { return stmt.executeQuery(sql);}
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the statement:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
        return null;
    }
    void init();
    boolean hasPlayerInDatabase(Player target);
    void addPlayerToDatabase(Player target);
    int getPlayerReputation(Player target);
    void addPlayerToSends(Player target, Player player);
    boolean containsPlayerInSends(Player target, Player player);
    ArrayList<String> getPlayerNamesInDatabase();
    void setPlayerReputation(Player target, int reputation);

}