package net.xdproston.tiderep.impl;

import net.xdproston.tiderep.Files;
import net.xdproston.tiderep.interfaces.Database;
import net.xdproston.tiderep.logger.Logger;
import net.xdproston.tiderep.logger.LoggerType;
import java.sql.DriverManager;

public class MySQL extends SQLite implements Database
{
    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", Files.Config.DATABASE_MYSQL_IP_AND_PORT, Files.Config.DATABASE_MYSQL_USE_DB), Files.Config.DATABASE_MYSQL_USER, Files.Config.DATABASE_MYSQL_PASSWORD);
            stmt = connect.createStatement();
        } catch (Exception e) {
            Logger.send(LoggerType.SEVERE, "An error occurred during the connection of the mysql database:" + String.format("%s - %s", e.getClass().getName(), e.getMessage()));
        }

        execute(stmt, "CREATE TABLE IF NOT EXISTS users(name VARCHAR(256) UNIQUE NOT NULL, reputation INT NOT NULL, sends LONGTEXT);");
    }
}