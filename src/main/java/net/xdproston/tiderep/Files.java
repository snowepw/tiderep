package net.xdproston.tiderep;

import java.io.File;
import java.io.FileOutputStream;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import net.xdproston.tiderep.logger.Logger;
import net.xdproston.tiderep.logger.LoggerType;

public final class Files
{
    private final static File folder = Main.getInstance().getDataFolder();
    private final static File file = new File(Main.getInstance().getDataFolder(), "config.yml");
    private final static File perms = new File(Main.getInstance().getDataFolder(), "permissions.txt");
    private final static File placeholders = new File(Main.getInstance().getDataFolder(), "placeholders.txt");
    private final static YamlConfiguration config = new YamlConfiguration();

    public static void initFolder() {
        if (!folder.exists()) folder.mkdirs();
    }

    public static void initConfig() {
        if (!file.exists()) {
            try {file.createNewFile();}
            catch (Exception e) {
                Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the configuration file:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
            }
        }

        try {config.load(file);}
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred while loading the configuration file:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
    }

    public static void initPlaceholdersFile() {
        if (!placeholders.exists()) {
            try {placeholders.createNewFile();}
            catch (Exception e) {
                Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the permissions file:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
            }
        }

        String toWrite = "placeholders: %tiderep_reputation%, %tiderep_advanced_reputation%";

        try (FileOutputStream fos = new FileOutputStream(placeholders)) {fos.write(toWrite.getBytes());} 
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred while writing a string to file 'placeholders.txt':" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
    }

    public static void initPermsFile() {
        if (!perms.exists()) {
            try {perms.createNewFile();}
            catch (Exception e) {
                Logger.send(LoggerType.SEVERE, "An error occurred during the creation of the permissions file:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
            }
        }

        String toWrite = "tiderep.cmd.reputation - /reputation\ntiderep.cmd.adminreputation - /adminreputation\ntiderep.cmd.reputationreload - /repuationreload";

        try (FileOutputStream fos = new FileOutputStream(perms)) {fos.write(toWrite.getBytes());} 
        catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred while writing a string to file 'permissions.txt':" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();

    public static java.util.function.Function<TagResolver[], Component> messageFunction(String path, String def) {
        String raw = config.getString(path, def);
        return resolvers -> MM.deserialize(raw, resolvers);
    }

    public static Component message(String path, String def, TagResolver... resolvers) {
        return messageFunction(path, def).apply(resolvers);
    }

    public static final class Config
    {
        public static int STARTUP_REPUTATION;
        public static int DISLIKE_MODIFICATOR, LIKE_MODIFICATOR;
        public static String DATABASE_TYPE, DATABASE_MYSQL_IP_AND_PORT, DATABASE_MYSQL_USER, DATABASE_MYSQL_PASSWORD, DATABASE_MYSQL_USE_DB;

        public static String GLOBAL_NO_PERMISSION, GLOBAL_PLAYER_NOT_FOUND, GLOBAL_ONLY_PLAYER, GLOBAL_USE_SELF;

        public static String REPUTATION_CMD_USAGE, REPUTATION_CMD_ALREADY, REPUTATION_CMD_TO_SENDER_UP, REPUTATION_CMD_TO_SENDER_DOWN, REPUTATION_CMD_TO_RECIPIENT_UP, REPUTATION_CMD_TO_RECIPIENT_DOWN;

        public static String AREPUTATION_CMD_USAGE, AREPUTATION_CMD_TAKE, AREPUTATION_CMD_GIVE, AREPUTATION_CMD_SET;

        public static String REPUTATIONRELOAD_CMD_USAGE, REPUTATIONRELOAD_CMD_RELOADED;

        public static void initValues() {
            try {config.load(file);}
            catch (Exception e) {
                Logger.send(LoggerType.SEVERE, "An error occurred while loading the configuration file:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
            }

            STARTUP_REPUTATION = config.getInt("settings.startup-reputation", 0);
            DISLIKE_MODIFICATOR = config.getInt("settings.dislike-modificator", 1);
            LIKE_MODIFICATOR = config.getInt("settings.like-modificator", 1);
            DATABASE_TYPE = config.getString("settings.database.type", "sqlite");
            DATABASE_MYSQL_IP_AND_PORT = config.getString("settings.database.mysql-ip-and-port", "localhost:3306");
            DATABASE_MYSQL_USER = config.getString("settings.database.mysql-user", "root");
            DATABASE_MYSQL_PASSWORD = config.getString("settings.database.mysql-password", "123123");
            DATABASE_MYSQL_USE_DB = config.getString("settings.database.mysql-use-database", "mydb");
            GLOBAL_NO_PERMISSION = config.getString("global-messages.no-perms", "<red>You dont have permission!");
            GLOBAL_USE_SELF = config.getString("global-messages.self-use", "<red>You can't use it on yourself.");
            GLOBAL_ONLY_PLAYER = config.getString("global-messages.only-player", "&cOnly player!");
            GLOBAL_PLAYER_NOT_FOUND = config.getString("global-messages.player-not-found", "<red>Player not found!");
            REPUTATION_CMD_USAGE = config.getString("commands.reputation.usage", "<white>Usage <grey>- <gold>/%label% <player name> <+/->");
            REPUTATION_CMD_ALREADY = config.getString("commands.reputation.already", "<white>You've already thrown him a reputation.");
            REPUTATION_CMD_TO_SENDER_UP = config.getString("commands.reputation.message.to-sender-up", "<white>You have successfully increased the reputation of the player <gold>%player%<white>.");
            REPUTATION_CMD_TO_SENDER_DOWN = config.getString("commands.reputation.message.to-sender-down", "<white>You have successfully lowered the reputation of the player <gold>%player%<white>.");
            REPUTATION_CMD_TO_RECIPIENT_UP = config.getString("commands.reputation.message.to-recipient-up", "<white>The <gold>%player% <white>player has boosted your reputation.");
            REPUTATION_CMD_TO_RECIPIENT_DOWN = config.getString("commands.reputation.message.to-recipient-down", "<white>The <gold>%player% <white>player has lowered your reputation.");
            AREPUTATION_CMD_USAGE = config.getString("commands.areputation.usage", "<white>Usage <gray>- <gold>/%label% <player name> <take/give/set> <amount>");
            AREPUTATION_CMD_TAKE = config.getString("commands.areputation.take", "<white>You have <red>removed <white>a reputation to the player <gold>%player% <white>in the amount of <gold>%amount%<white>.");
            AREPUTATION_CMD_GIVE = config.getString("commands.areputation.give", "<white>You have <green>added <white>a reputation to the player <gold>%player% <white>in the amount of <gold>%amount%<white>.");
            AREPUTATION_CMD_SET = config.getString("commands.areputation.set", "<white>You have <yellow>set <white>a reputation to the player <gold>%player% <white>in the amount of <gold>%amount%<white>.");
            REPUTATIONRELOAD_CMD_RELOADED = config.getString("commands.reputationreload.reloaded", "<white>Plugin successfully reloaded!");
            REPUTATIONRELOAD_CMD_USAGE = config.getString("commands.reputationreload.usage", "<white>usage: <gold>/reputationreload");
        }
    }
}