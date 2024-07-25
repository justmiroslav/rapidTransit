package org.rapidTransit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/rapidTransit",
                    System.getenv("DB_USER"), System.getenv("DB_PASS"));
        } catch (SQLException e) {
            System.out.println(STR."Database Connection Creation Failed: \{e.getMessage()}");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
