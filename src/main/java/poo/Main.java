package poo;

import poo.domain.entities.Guest;
import poo.infra.ConnectionFactory;
import poo.infra.GuestDao;
import poo.utils.Migrator;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            Migrator migrator = new Migrator("./src/migrations", connection);
            migrator.run();
            Guest guestToCreate = new Guest("99999999999", "Zignago", "zignago@zignago", "82988776655", "my home", new Date());
            GuestDao guestDao = new GuestDao(connection);
            guestDao.create(guestToCreate);
            ArrayList<Guest> guests = guestDao.find();
            for (Guest guest : guests) {
                System.out.println(guest);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}