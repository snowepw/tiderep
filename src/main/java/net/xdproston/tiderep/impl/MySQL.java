package net.xdproston.tiderep.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import net.xdproston.tiderep.Files;

public class MySQL extends OrmLiteDatabase {
    @Override
    protected String connectionString() {
        return String.format("jdbc:mysql://%s/%s", Files.Config.DATABASE_MYSQL_IP_AND_PORT, Files.Config.DATABASE_MYSQL_USE_DB);
    }

    @Override
    protected void configureConnection(JdbcConnectionSource source) {
        source.setUsername(Files.Config.DATABASE_MYSQL_USER);
        source.setPassword(Files.Config.DATABASE_MYSQL_PASSWORD);
    }
}
