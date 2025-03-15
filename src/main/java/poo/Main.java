package poo;

import java.sql.Connection;
import java.sql.SQLException;

import poo.domain.services.HotelService;
import poo.infra.ConnectionFactory;
import poo.utils.Getter;
import poo.utils.Migrator;

public class Main {
    static final HotelService hotelService = new HotelService();
    static final String[] options = {
            "1. Cadastrar Hospede",
            "2. Fazer reserva",
            "3. Listar hospedes",
            "4. Listar Quartos",
            "5. Listar Reservas",
            "6. Fazer Checkin",
            "7. Fazer Checkout",
            "8. Limpar terminal",
            "9. Sair",
    };

    static final IFunctionality[] methods = {
            hotelService::createGuest,
            hotelService::makeReservation,
            hotelService::listGuests,
            hotelService::listRooms,
            hotelService::listReservations,
            hotelService::makeCheckin,
            hotelService::makeCheckout,
            Main::clearScreen,
    };

    public static void clearScreen(Connection connection, Getter getter) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(hotelService.getLogo());
    }

    public static void main(String[] args) {

        try (Connection connection = ConnectionFactory.getConnection();) {
            Migrator migrator = new Migrator(connection, "./src/migrations", "__migrations");
            Migrator seedMigrator = new Migrator(connection, "./src/seeds", "__seeds");
            migrator.run();
            seedMigrator.run();

            System.out.println(hotelService.getLogo());
            Getter getter = new Getter();
            int option = -1;
            while (true) {
                System.out.println("\n\n\n-----------Menu-----------\n" + String.join("\n", options));
                option = getter.getInt("Choose an option: ");

                if (option > options.length || option < 1) {
                    System.out.println("Invalid option");
                    continue;
                }

                if (option == options.length) {
                    System.out.println("Bye");
                    break;
                }

                methods[option - 1].run(connection, getter);
            }
        } catch (SQLException e) {
            System.out.println("Cannot connect to the database: " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface IFunctionality {
        void run(Connection connection, Getter getter);
    }
}