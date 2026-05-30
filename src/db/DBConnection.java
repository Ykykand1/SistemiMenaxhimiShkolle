package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static Connection connection = null;

    private DBConnection() {
        // Private constructor for singleton
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                String url = EnvConfig.get("DB_URL", "jdbc:postgresql://localhost:5432/shkolla_db");
                String user = EnvConfig.get("DB_USER", "postgres");
                String password = EnvConfig.get("DB_PASSWORD", "");
                connection = DriverManager.getConnection(url, user, password);
                warnIfUsersTableMissing(connection);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed! Ensure PostgreSQL is running on port 5432 and database 'shkolla_db' exists.");
            e.printStackTrace();
        }
        return connection;
    }

    private static void warnIfUsersTableMissing(Connection conn) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema='public' AND table_name='users')")) {
            if (rs.next() && !rs.getBoolean(1)) {
                String db = conn.getCatalog();
                System.err.println(
                        "Schema missing in database '" + db + "': table 'users' not found in schema 'public'. "
                                + "Run seed.sql against shkolla_db (pgAdmin/psql), not the default 'postgres' database.");
            }
        } catch (SQLException ignored) {
        }
    }
}
