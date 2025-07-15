package net.xdproston.tiderep.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.xdproston.tiderep.Files;
import net.xdproston.tiderep.User;
import net.xdproston.tiderep.interfaces.Database;
import net.xdproston.tiderep.logger.Logger;
import net.xdproston.tiderep.logger.LoggerType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class OrmLiteDatabase implements Database {
    protected JdbcConnectionSource connection;
    protected Dao<User, String> userDao;

    protected abstract String connectionString();

    protected abstract void configureConnection(JdbcConnectionSource source);

    @Override
    public void init() {
        try {
            connection = new JdbcConnectionSource(connectionString());
            configureConnection(connection);
            userDao = com.j256.ormlite.dao.DaoManager.createDao(connection, User.class);
            TableUtils.createTableIfNotExists(connection, User.class);
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "Database init error:", e.getMessage());
        }
    }

    @Override
    public boolean hasPlayerInDatabase(Player target) {
        try {
            return userDao.idExists(target.getName());
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
            return false;
        }
    }

    @Override
    public void addPlayerToDatabase(Player target) {
        try {
            userDao.createIfNotExists(new User(target.getName(), Files.Config.STARTUP_REPUTATION, "NONE"));
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
        }
    }

    @Override
    public int getPlayerReputation(Player target) {
        try {
            User u = userDao.queryForId(target.getName());
            return u != null ? u.getReputation() : 0;
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
            return 0;
        }
    }

    @Override
    public void addPlayerToSends(Player target, Player player) {
        try {
            User u = userDao.queryForId(target.getName());
            if (u == null) return;
            String sends = u.getSends();
            if (sends == null || sends.equalsIgnoreCase("NONE")) {
                u.setSends(player.getName());
            } else {
                u.setSends(sends + ", " + player.getName());
            }
            userDao.update(u);
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
        }
    }

    @Override
    public boolean containsPlayerInSends(Player target, Player player) {
        try {
            User u = userDao.queryForId(target.getName());
            if (u == null) return false;
            String sends = u.getSends();
            if (sends == null) return false;
            for (String name : sends.split(", ")) {
                if (name.equalsIgnoreCase(player.getName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<String> getPlayerNamesInDatabase() {
        try {
            ArrayList<String> list = new ArrayList<>();
            for (User u : userDao.queryForAll()) {
                list.add(u.getName());
            }
            return list;
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void setPlayerReputation(Player target, int reputation) {
        try {
            User u = userDao.queryForId(target.getName());
            if (u != null) {
                u.setReputation(reputation);
                userDao.update(u);
            }
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "SQL error:", e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "Error closing database:", e.getMessage());
        }
    }
}
