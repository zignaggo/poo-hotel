package poo;

import java.sql.Connection;
import java.sql.SQLException;

import poo.domain.services.HotelService;
import poo.infra.ConnectionFactory;
import poo.utils.Getter;
import poo.utils.Migrator;

public class Main {
    public static void main(String[] args) {

        try (Connection connection = ConnectionFactory.getConnection(); Getter getter = new Getter()) {
            Migrator migrator = new Migrator(connection, "./src/migrations", "__migrations");
            Migrator seedMigrator = new Migrator(connection, "./src/seeds", "__seeds");
            migrator.run();
            seedMigrator.run();
            final HotelService hotelService = new HotelService(connection, getter);
            hotelService.start();
        } catch (SQLException e) {
            System.out.println("Cannot connect to the database: " + e.getMessage());
        }
    }

}