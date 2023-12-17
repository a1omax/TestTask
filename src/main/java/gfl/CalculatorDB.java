package gfl;

import java.sql.*;
import java.util.ArrayList;

public class CalculatorDB {
    final static String url = "jdbc:postgresql://localhost:5432/";
    final static String user = "postgres";
    final static String password = "admin";
    final static String databaseName = "calculator_db";
    final static String equationsTableName = "equations";
    final static String rootsTableName = "roots";

    CalculatorDB() {
        setupDB();
    }

    public void setupDB() {
        createDBIfNotExists();
        createEquationTableIfNotExists();
        createRootsTableIfNotExists();
    }

    private Connection connectToPostgres() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private Connection connectToPostgresDatabase() throws SQLException {
        return DriverManager.getConnection(url + databaseName, user, password);
    }

    private void createDBIfNotExists() {

        try (Connection connection = connectToPostgres()) {
            String checkDatabaseQuery = "SELECT datname FROM pg_database WHERE datname = ?";
            try (PreparedStatement getDBpreparedStatement = connection.prepareStatement(checkDatabaseQuery)) {
                getDBpreparedStatement.setString(1, databaseName);

                if (!getDBpreparedStatement.executeQuery().next()) {
                    // Database does not exist, so create it
                    String createDatabaseQuery = "CREATE DATABASE " + databaseName;
                    try (PreparedStatement createDBPreparedStatement = connection.prepareStatement(createDatabaseQuery)) {
                        createDBPreparedStatement.executeUpdate();
                        System.out.println("Database created successfully.");
                    }
                } else {
                    System.out.println("Database already exists.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void createEquationTableIfNotExists() {
        try (Connection connection = connectToPostgresDatabase()) {
            try (Statement statement = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS " + equationsTableName + "( " +
                        "id SERIAL PRIMARY KEY," +
                        "equation VARCHAR(255) UNIQUE NOT NULL)";

                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createRootsTableIfNotExists() {
        try (Connection connection = connectToPostgresDatabase()) {
            try (Statement statement = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS " + rootsTableName + "( " +
                        "id SERIAL PRIMARY KEY," +
                        "value DOUBLE PRECISION NOT NULL," +
                        "equation_id INTEGER NOT NULL," +
                        "CONSTRAINT fk_equation_id FOREIGN KEY(equation_id) REFERENCES " + equationsTableName + "(id)," +
                        "UNIQUE (value, equation_id))";

                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createEquation(String equation) {
        try (Connection connection = connectToPostgresDatabase()) {
            String sql = "INSERT INTO " + equationsTableName + "(equation) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, equation);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    public void createRoot(int equationId, double value) {
        try (Connection connection = connectToPostgresDatabase()) {
            String sql = "INSERT INTO " + rootsTableName + "(value, equation_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDouble(1, value);
                preparedStatement.setInt(2, equationId);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    public String readEquation(int equationId) {
        String equation = null;
        try (Connection connection = connectToPostgresDatabase()) {

            String sql = "SELECT equation FROM " + equationsTableName + " WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, equationId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        equation = resultSet.getString("equation");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equation;
    }

    public Integer readEquationId(String equation) {
        Integer equationId = null;
        try (Connection connection = connectToPostgresDatabase()) {

            String sql = "SELECT id FROM " + equationsTableName + " WHERE equation = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, equation);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        equationId = resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
        return equationId;
    }

    public Integer[] readAllEquationIdMatchingRoot(Double root) {

        ArrayList<Integer> equationIdArrayList = new ArrayList<>();

        String sql;

        try (Connection connection = connectToPostgresDatabase()) {
            sql = "SELECT equation_id FROM " + rootsTableName + " WHERE value = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDouble(1, root);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        equationIdArrayList.add(resultSet.getInt("equation_id"));
                    }

                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
        return equationIdArrayList.toArray(new Integer[0]);
    }

}
