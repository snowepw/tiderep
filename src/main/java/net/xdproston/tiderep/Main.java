package net.xdproston.tiderep;

import me.clip.placeholderapi.events.ExpansionsLoadedEvent;
import net.xdproston.tiderep.impl.MySQL;
import net.xdproston.tiderep.impl.SQLite;
import net.xdproston.tiderep.interfaces.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.xdproston.tiderep.commands.AReputationCommand;
import net.xdproston.tiderep.commands.ReputationCommand;
import net.xdproston.tiderep.logger.Logger;
import net.xdproston.tiderep.logger.LoggerType;

public final class Main extends JavaPlugin implements Listener
{
    private static Main instance;
    private static Database database;
    private static PlaceholderExpansion pe;

    public static void reload() {
        Files.initFolder();
        Files.initPermsFile();
        Files.initPlaceholdersFile();
        Files.initConfig();
        Files.Config.initValues();
    }

    @Override
    public void onLoad() {
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();

        ccs.sendMessage(" ");
        ccs.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &6&lTideReputation &7- &f" + getDescription().getVersion()));
        ccs.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &fThe plugin is loading..."));
        ccs.sendMessage(" ");
    }

    private static void initCommands() {
        PluginCommand pc1 = instance.getCommand("reputation");
        PluginCommand pc2 = instance.getCommand("adminreputation");
        PluginCommand pc3 = instance.getCommand("reputationreload");

        if (pc1 != null) {
            pc1.setExecutor(new ReputationCommand());
            pc1.setTabCompleter(new ReputationCommand());
            pc1.setPermissionMessage(Files.Config.GLOBAL_NO_PERMISSION);
        }

        if (pc2 != null) {
            pc2.setExecutor(new AReputationCommand());
            pc2.setTabCompleter(new AReputationCommand());
            pc2.setPermissionMessage(Files.Config.GLOBAL_NO_PERMISSION);
        }

        if (pc3 != null) {
            pc3.setPermissionMessage(Files.Config.GLOBAL_NO_PERMISSION);
        }
    }

    @Override 
    public void onEnable() {
        instance = this;
        PluginManager pm = Bukkit.getPluginManager();

        Files.initFolder();
        Files.initPermsFile();
        Files.initPlaceholdersFile();
        Files.initConfig();
        Files.Config.initValues();

        String databaseType = Files.Config.DATABASE_TYPE.toLowerCase();

        if (databaseType.equals("sqlite")) database = new SQLite();
        else if (databaseType.equals("mysql")) database = new MySQL();
        else {
            Logger.send(LoggerType.WARNING, "The wrong database type is entered in the configuration! existing: sqlite, mysql. The default is sqlite");
            database = new SQLite();
        }

        database.init();
        initCommands();

        pm.registerEvents(this, instance);

        PlaceholderExpansion pe = Main.pe = new PapiHook();
        pe.register();
    }

    @EventHandler
    public void ExpansionLoadedEvent(ExpansionsLoadedEvent event) {
        if (!pe.isRegistered()) pe.register();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!database.hasPlayerInDatabase(player)) database.addPlayerToDatabase(player);
    }

    @Override
    public void onDisable() {
        database.close();
        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }

    public static Database getCurrentDatabase() {
        return database;
    }
}