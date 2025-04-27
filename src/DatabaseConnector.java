
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static Connection conn;
    static final String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Cinema;user=User1;password=root;encrypt=true;trustServerCertificate=true";

    public static Connection getConnection() throws SQLException {
        try {
            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("Connected to SQL Server successfully.");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (conn == null || conn.isClosed()) return;
            conn.close();
            System.out.println("Connected closed successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws SQLException {
        getConnection();
    }
}