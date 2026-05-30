package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("Attempting to connect to the database...");
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("Connection successful!");
            try {
                // Let's run a test query to check database catalogs
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT current_database();");
                if (rs.next()) {
                    System.out.println("Connected database: " + rs.getString(1));
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                System.out.println("Query execution failed:");
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Connection was NULL.");
        }
    }
}
