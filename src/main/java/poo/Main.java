package poo;

import poo.infra.ConnectionFactory;
import poo.utils.Migrator;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            Migrator migrator = new Migrator("./src/migrations", connection);
            migrator.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}   