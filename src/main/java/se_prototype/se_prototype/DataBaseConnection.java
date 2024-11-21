package se_prototype.se_prototype;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    public static void main(String[] args) {
        DataBaseConnection connectNow = new DataBaseConnection();

        System.out.println(connectNow.get_connection());
    }

    public Connection get_connection() {
        Connection connectDB = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Locate the database file in resources
            URL resource = getClass().getClassLoader().getResource("database.db");
            if (resource != null) {
                // Construct the SQLite database URL
                String dbUrl = "jdbc:sqlite:" + resource.getPath();

                // Establish the connection
                connectDB = DriverManager.getConnection(dbUrl);

                if (connectDB != null) {
                    System.out.println("Connection OK");
                } else {
                    System.out.println("Connection Failed");
                }
            } else {
                System.out.println("Database file not found!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Exception: " + e.getMessage());
        }
        return connectDB;
    }
}