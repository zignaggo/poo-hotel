package poo.domain.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import poo.domain.entities.Guest;
import poo.domain.entities.Reservation;
import poo.domain.entities.Room;
import poo.utils.Getter;

public class HotelService {
  private Connection connection;
  private Getter getter;

  public HotelService(Connection connection, Getter getter) {
    this.connection = connection;
    this.getter = getter;
  }

  private final String[] options = {
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

  private final IFunctionality[] methods = {
      this::createGuest,
      this::makeReservation,
      this::listGuests,
      this::listRooms,
      this::listReservations,
      this::makeCheckin,
      this::makeCheckout,
  };

  public String[] getOptions() {
    return this.options;
  }

  public IFunctionality[] getMethods() {
    return this.methods;
  }

  public void start() {
    System.out.println(this.getLogo());
    int option = -1;
    while (true) {
      System.out.println("\n\n\n-----------Menu-----------\n" + String.join("\n", this.getOptions()));
      option = getter.getInt("Choose an option: ");
      System.out.print("\033[H\033[2J");
      System.out.flush();
      System.out.println("\n\n\n[Result]:");
      if (option > this.options.length || option < 1) {
        System.out.println("Invalid option");
        continue;
      }

      if (option == this.options.length) {
        System.out.println("Bye");
        getter.close();
        break;
      }

      this.methods[option - 1].run();
    }
  }

  private String readFile(File file) throws FileNotFoundException {
    Scanner scanner = new Scanner(file);
    String result = "";
    while (scanner.hasNextLine()) {
      result += scanner.nextLine() + "\n";
    }
    scanner.close();
    return result;
  }

  public String getLogo() {
    try {
      return this.readFile(new File("./logo.txt"));
    } catch (Exception e) {
      return "Hotel";
    }
  }

  public void createGuest() {
    try {
      GuestService guestService = new GuestService(connection);
      System.out.println("Creating guest\n");
      String cpf = getter.getString("CPF: ", "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$",
          "Invalid CPF");
      String name = getter.getString("Name: ");
      String email = getter.getString("Email: ", "^\\w+@\\w+\\.\\w+$", "Invalid email");
      String phone = getter.getString("Phone: ", "^\\d{2}\\d{5}\\d{4}$", "Invalid phone");
      String address = getter.getString("Address: ");
      Date birthDate = getter.getDate("Birth date: ");
      Guest guest = guestService.create(name, cpf, email, phone, address, birthDate);
      System.out.println("Guest created successfully\n");
      System.out.println(guest.toString());
    } catch (Exception e) {
      System.out.println("Failed to create guest: " + e.getMessage());
    }
  }

  public void makeReservation() {
    ReservationService reservationService = new ReservationService(connection);
    GuestService guestService = new GuestService(connection);
    try {
      ArrayList<Guest> guests = guestService.getAllGuests();
      Guest selectedGuest = null;
      guests.forEach(guest -> System.out.println(guest.getId() + ": " + guest.getFullName()));

      int guestId = getter.getInt("Guest ID: ");

      for (Guest guest : guests) {
        if (guest.getId() == guestId) {
          selectedGuest = guest;
          break;
        }
      }

      if (selectedGuest == null) {
        System.out.println("Guest not found");
        return;
      }

      int numberOfGuests = getter.getInt("Number of guests: ");
      Date checkIn = getter.getDate("Check-in date: ");
      Date checkOut = getter.getDate("Check-out date: ");
      String paymentMethod = getter.getString("Payment method: ");

      reservationService.createReservation(selectedGuest.getId(), numberOfGuests, checkIn, checkOut, paymentMethod);
    } catch (Exception e) {
      System.out.println("Failed to create reservation: " + e.getMessage());
    }
  }

  public void listGuests() {
    GuestService guestService = new GuestService(connection);
    try {
      ArrayList<Guest> guests = guestService.getAllGuests();
      guests.forEach(guest -> System.out.printf("ID: %d, CPF: %s, Name: %s\n", guest.getId(), guest.getCpf(),
          guest.getFullName()));
    } catch (Exception e) {
      System.out.println("Failed to list guests: " + e.getMessage());
    }
  }

  public void listRooms() {
    RoomService roomService = new RoomService(connection);
    try {
      ArrayList<Room> rooms = roomService.getAllRooms();
      if (rooms.isEmpty()) {
        System.out.println("No rooms found");
        return;
      }
      rooms.forEach(room -> System.out.printf("ID: %d, Type: %s, Price: %.2f\n", room.getId(), room.getRoomType(), room.getPricePerNight()));
    } catch (Exception e) {
      System.out.println("Failed to list rooms: " + e.getMessage());
    }
  }

  public void listReservations() {
    ReservationService reservationService = new ReservationService(connection);
    try {
      ArrayList<Reservation> reservations = reservationService.getAllReservations();
      reservations.forEach(
          reservation -> System.out.printf("ID: %d, Number of guests: %d\n", reservation.getId(),
              reservation.getNumberOfGuests()));
    } catch (Exception e) {
      System.out.println("Failed to list reservations: " + e.getMessage());
    }
  }

  public void makeCheckin() {
  }

  public void makeCheckout() {
  }

  @FunctionalInterface
  public interface IFunctionality {
    void run();
  }
}
