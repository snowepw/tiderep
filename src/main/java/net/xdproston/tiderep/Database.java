package net.xdproston.tiderep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.xdproston.tiderep.logger.Logger;
import net.xdproston.tiderep.logger.LoggerType;

public final class Database implements Listener
{
    private static final String DB_PATH_STR = "jdbc:sqlite:" + Main.getInstance().getDataFolder().getAbsolutePath() + "/users.db";
    private static Connection connection;

    private static void execute(String sqlscript) {
        try (Statement statement = connection.createStatement()) {statement.execute(sqlscript);}
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the statement:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
    }

    private static ResultSet executeQuery(String sqlscript, int stmtTimeToClose) {
        if (stmtTimeToClose < 1) {
            Logger.send(LoggerType.SEVERE, "Database(executeQuery): The time cannot be less than one second.");
            return null;
        }

        try {
            Statement statement = connection.createStatement();

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                int seconds = stmtTimeToClose;
                while (seconds != 0) {
                    seconds--;
                    try {Thread.sleep(1000);}
                    catch (Exception e) {}
                }

                try {statement.close();}
                catch (Exception e) {
                    Logger.send(LoggerType.SEVERE, "An error occurred while closing the statement:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
                }
            });

            return statement.executeQuery(sqlscript);
        }
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the statement:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
        return null;
    }

    private static ResultSet executeQuery(String sqlscript) {
        return executeQuery(sqlscript, 5);
    }

    public static void initDatabase() {
        try {connection = DriverManager.getConnection(DB_PATH_STR);}
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the connection of the database:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }

        execute("""
                CREATE TABLE IF NOT EXISTS users(
                    \"name\" TEXT UNIQUE NOT NULL,
                    \"reputation\" INT NOT NULL,
                    \"sends\" TEXT DEFAULT 'NONE'
                );
                """);
    }

    public static boolean hasPlayerInDatabase(Player target) {
        try (ResultSet rs = executeQuery(String.format("SELECT EXISTS(SELECT name FROM users WHERE name = '%s');", target.getName()))) {
            return rs.getString(1).equalsIgnoreCase("1");
        }
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the execution of the sql script:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
        return false;
    }

    public static void addPlayerToDatabase(Player target) {
        execute(String.format("INSERT INTO users(name, reputation) VALUES('%s', %d);", target.getName(), Files.Config.STARTUP_REPUTATION));
    }

    public static int getPlayerReputation(Player target) {
        try (ResultSet rs = executeQuery(String.format("SELECT reputation FROM users WHERE name = '%s';", target.getName()))) {
            return rs.getInt(1);
        }
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the execution of the sql script:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
        return -927817563;
    }

    public static void addPlayerToSends(Player target, Player player) {
        try (ResultSet rs = executeQuery(String.format("SELECT sends FROM users WHERE name = '%s';", target.getName()))) {
            String sends = rs.getString(1);

            if (sends.equalsIgnoreCase("NONE")) {
                execute(String.format("UPDATE users SET sends = '%s' WHERE name = '%s';", player.getName(), target.getName()));
            } else {
                execute(String.format("UPDATE users SET sends = '%s, %s' WHERE name = '%s';", sends, player.getName(), target.getName()));
            }
        }
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the execution of the sql script:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
    }

    public static boolean containsPlayerInSends(Player target, Player player) {
        try (ResultSet rs = executeQuery(String.format("SELECT sends FROM users WHERE name = '%s';", target.getName()))) {
            String sends = rs.getString(1);

            String[] listOfSends = sends.split(", ");
            for (String name : listOfSends) {
                if (player.getName().equalsIgnoreCase(name)) return true;
            }
        }
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the execution of the sql script:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
        return false;
    }

    public static ArrayList<String> getPlayersInDatabase() {
        try (ResultSet rs = executeQuery("SELECT name FROM users;")) {
            ArrayList<String> list = new ArrayList<>();
            do {list.add(rs.getString(1));} while (rs.next());

            return list;
        }
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the execution of the sql script:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
        return null;
    }

    public static void setPlayerReputation(Player target, int reputation) {
       execute(String.format("UPDATE users SET reputation = %d WHERE name = '%s';", reputation, target.getName())); 
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!hasPlayerInDatabase(player)) addPlayerToDatabase(player);
    }
}