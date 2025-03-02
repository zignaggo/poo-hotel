package poo.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory implements AutoCloseable {
    private static Connection connection = null;
    private final static String dbUrl = "jdbc:postgresql://localhost:5432/hotel";
    private final static String user = "postgres";
    private final static String password = "postgres";

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