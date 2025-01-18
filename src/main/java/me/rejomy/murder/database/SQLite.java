package me.rejomy.murder.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends DataBase {

    public SQLite() {

        try {
            Class.forName("org.sqlite.JDBC").newInstance();

            connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (uuid TEXT PRIMARY KEY, " +
                    "wins INT, " +
                    "games INT, " +
                    "murderChance INT, " +
                    "detectiveChance INT, " +
                    "changeTime LONG)");

            statement.close();
        } catch (Exception ignored) {}

    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:plugins/MurderMystery/users.db");
    }

}
