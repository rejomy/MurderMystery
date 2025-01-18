package me.rejomy.murder.database;

import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.util.Logger;

import java.sql.*;
import java.util.UUID;

public abstract class DataBase {
    
    protected abstract Connection getConnection() throws SQLException;

    public Connection connection;

    public void set(UUID uuid, int wins, int games, int murderChance, int detectiveChance, long time) throws SQLException {
        try {
            executeUpdate("INSERT OR REPLACE INTO users VALUES ('" + uuid + "', '" + wins + "', '" + games + "', '"
                    + murderChance + "', '" + detectiveChance + "', '" + time + "')");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public String get(UUID uuid, int column) {
        try {
            ResultSet set = executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");
            String value = set.getString(column);
            set.close();
            return value;
        } catch (SQLException | NullPointerException exception) {
        }

        return "";
    }

    public void remove(UUID uuid) throws SQLException {
        try {
            executeUpdate("DELETE FROM users WHERE uuid='" + uuid + "'");
        } catch (SQLException exception) {
            Logger.severe("Something went wrong! Please tell it to Rejomy :)");
            exception.printStackTrace();
        }
    }

    private void executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    private ResultSet executeQuery(String query) throws SQLException {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException exception) {
            throw exception;
        }
    }

    public PlayerData loadDataFromDataBase(UUID uuid) {
        PlayerData playerData = null;

        try {
            Statement statement = connection.createStatement();
            // Выполняем запрос, который выбирает все записи из таблицы users
            ResultSet resultSet = executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");

            if (resultSet == null) return playerData;

            // Перебираем все записи в result set
            while (resultSet.next()) {
                playerData = new PlayerData(uuid);

                long time = Long.parseLong(resultSet.getString("changeTime"));

                if (System.currentTimeMillis() - time > 14 * 24 * 60 * 60 * 1000) {
                    remove(uuid);
                    continue;
                }

                // Заполняем его поля значениями из result set
                playerData.wins = resultSet.getInt("wins");
                playerData.games = resultSet.getInt("games");

                playerData.gamesWithoutMurderRole = resultSet.getInt("murderChance");
                playerData.gamesWithoutDetectiveRole = resultSet.getInt("detectiveChance");
            }

            // Закрываем statement и resultSet
            statement.close();
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return playerData;
    }

    public void savePlayerData(PlayerData playerData) {
        try {
            // Перебираем все элементы списка playersData
            set(playerData.uuid, playerData.wins, playerData.games, playerData.gamesWithoutMurderRole, playerData.gamesWithoutDetectiveRole,
                    System.currentTimeMillis());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
