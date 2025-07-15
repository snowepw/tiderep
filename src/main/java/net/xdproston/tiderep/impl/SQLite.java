package net.xdproston.tiderep.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import net.xdproston.tiderep.Main;

public class SQLite extends OrmLiteDatabase {
    @Override
    protected String connectionString() {
        return "jdbc:sqlite:" + Main.getInstance().getDataFolder().getAbsolutePath() + "/users.db";
    }

    @Override
    protected void configureConnection(JdbcConnectionSource source) {
        // no additional configuration
    }
}
