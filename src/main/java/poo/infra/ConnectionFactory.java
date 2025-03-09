package poo.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory implements AutoCloseable {
    private static Connection connection = null;
    private final static String dbUrl = System.getenv("DB_URL").toString().trim();
    private final static String user = System.getenv("DB_USER").toString().trim();
    private final static String password = System.getenv("DB_PASSWORD").toString().trim();

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(dbUrl, user, password);
        }
        return connection;
    }

    @Override
    public void close() throws Exception {
        ConnectionFactory.connection.close();
    }
}