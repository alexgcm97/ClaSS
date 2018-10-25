package da;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private String url = "jdbc:derby://localhost:1527/schedule";
    private String login = "schedule";
    private String pass = "schedule";

    private DBConnection() {

    }

    public static Connection getConnection() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
            System.out.println("Connection  - - - - - - - -  New DBConnection created");
        }
        try {
            return DriverManager.getConnection(instance.url, instance.login, instance.pass);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
