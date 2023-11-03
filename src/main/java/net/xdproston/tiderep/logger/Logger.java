package net.xdproston.tiderep.logger;

import org.bukkit.Bukkit;
import net.xdproston.tiderep.Main;

public final class Logger
{
    private static final java.util.logging.Logger logger = Bukkit.getLogger();
    private static final String prefix = Main.getInstance().getDescription().getName();

    public static void send(LoggerType type, String ...messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) sb.append(message).append(' ');

        String format = String.format("[%s] %s", prefix, sb.toString());
        switch (type) {
            case INFO:
                logger.info(format);
                break;
            case SEVERE:
                logger.severe(format);
                break;
            case WARNING:
                logger.warning(format);
                break;
        }
    }
}