package net.xdproston.tiderep;

import me.clip.placeholderapi.events.ExpansionsLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    private static PlaceholderExpansion pe;

    public static void reload() {
        Files.initFolder();
        Files.initPermsFile();
        Files.initPlaceholdersFile();
        Files.initConfig();
        Files.Config.initValues();
        initCommands();
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

        Database.initDatabase();
        initCommands();

        pm.registerEvents(new Database(), instance);
        pm.registerEvents(this, instance);

        if (pm.getPlugin("PlaceholderAPI") == null) {
            Logger.send(LoggerType.SEVERE, "PlaceholderAPI not found! The plugin will not work without it.");
            pm.disablePlugin(instance);
        }

        PlaceholderExpansion pe = Main.pe = new PapiHook();
        pe.register();
    }

    @EventHandler
    public void ExpansionLoadedEvent(ExpansionsLoadedEvent event) {
        if (!pe.isRegistered()) pe.register();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }
}