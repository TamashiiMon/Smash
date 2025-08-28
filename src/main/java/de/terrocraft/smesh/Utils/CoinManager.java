package de.terrocraft.smesh.Utils;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoinManager {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/smash";
    private static final String USER = "smash";
    private static final String PASSWORD = "Smash123!";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static CompletableFuture<Void> savePlayerData(UUID uuid, int coins, int wins) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO player_data (uuid, coins, wins) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE coins = VALUES(coins), wins = VALUES(wins);";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, uuid.toString());
                statement.setInt(2, coins);
                statement.setInt(3, wins);

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Integer> getCoins(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT coins FROM player_data WHERE uuid = ?;";
            int coins = 0;

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, uuid.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        coins = resultSet.getInt("coins");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return coins;
        });
    }

    public static CompletableFuture<Integer> getWins(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT wins FROM player_data WHERE uuid = ?;";
            int wins = 0;

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, uuid.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        wins = resultSet.getInt("wins");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return wins;
        });
    }

    public static CompletableFuture<Void> addCoins(UUID uuid, int amount) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE player_data SET coins = coins + ? WHERE uuid = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> removeCoins(UUID uuid, int amount) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE player_data SET coins = coins - ? WHERE uuid = ? AND coins >= ?;";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());
                statement.setInt(3, amount);

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> addWins(UUID uuid, int amount) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE player_data SET wins = wins + ? WHERE uuid = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> removeWins(UUID uuid, int amount) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE player_data SET wins = wins - ? WHERE uuid = ? AND wins >= ?;";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());
                statement.setInt(3, amount);

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Boolean> doesPlayerExist(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT 1 FROM player_data WHERE uuid = ?;";
            boolean exists = false;

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, uuid.toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    exists = resultSet.next();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return exists;
        });
    }
}
