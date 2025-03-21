package poo.domain.services;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import poo.domain.entities.Guest;
import poo.domain.entities.Item;
import poo.domain.entities.Reservation;
import poo.domain.entities.Room;
import poo.domain.expections.ReservationException;
import poo.infra.GuestDao;
import poo.infra.MovementDao;
import poo.infra.ReservationDao;
import poo.infra.ReservationRoomDao;
import poo.infra.RoomDao;
import poo.utils.Getter;

public class HotelService {
  private Connection connection;
  private Getter getter;
  private ReservationService reservationService = new ReservationService(connection);
  private GuestService guestService = new GuestService(connection);
  private RoomService roomService = new RoomService(connection);
  private ItemService itemService = new ItemService(connection);
  private ConsumptionService consumptionService = new ConsumptionService(connection);

  public HotelService(Connection connection, Getter getter) {
    this.connection = connection;
    this.getter = getter;
  }

  private final String[] options = {
      "1.  Cadastrar Hospede",
      "2.  Fazer Reserva",
      "3.  Fazer Checkin",
      "4.  Fazer Checkout",
      "5.  Fazer Pedido",
      "6.  Listar Hospedes",
      "7.  Listar Quartos",
      "8.  Listar Reservas",
      "9.  Listar Itens",
      "10. Listar Consumos",
      "11. Sair",
  };

  private final IFunctionality[] methods = {
      this::createGuest,
      this::makeReservation,
      this::makeCheckIn,
      this::makeCheckOut,
      this::makeOrder,
      this::listGuests,
      this::listRooms,
      this::listReservations,
      this::listItems,
      this::listConsumptions,
  };

  public void start() {
    System.out.println(this.getLogo());
    int option = -1;
    while (true) {
      System.out.println("\n\n\n-----------Menu-----------\n" + String.join("\n", this.options));
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

  public String getLogo() {
    try {
      return this.getter.readFile(new File("./logo.txt"));
    } catch (Exception e) {
      return "Hotel";
    }
  }

  public void createGuest() {
    try {
      GuestService guestService = new GuestService(connection, new GuestDao(connection));
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
    GuestService guestService = new GuestService(connection, new GuestDao(connection));
    try {
      ArrayList<Guest> guests = guestService.getAllGuests();
      Guest selectedGuest = null;
      guests.forEach(guest -> System.out.printf("ID: %d, CPF: %s, Name: %s\n", guest.getId(), guest.getCpf(),
          guest.getFullName()));

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

      reservationService.create(selectedGuest.getId(), numberOfGuests, checkIn, checkOut, paymentMethod);
    } catch (Exception e) {
      System.out.println("Failed to create reservation: " + e.getMessage());
    }
  }

  public void listGuests() {
    try {
      ArrayList<Guest> guests = guestService.getAllGuests();
      if (guests.isEmpty()) {
        System.out.println("No guests found");
        return;
      }
      System.out.printf("Listing %d guests:\n", guests.size());
      guests.forEach(guest -> System.out.println(guest.toString()));
    } catch (Exception e) {
      System.out.println("Failed to list guests: " + e.getMessage());
    }
  }

  public void listRooms() {
    try {
      ArrayList<Room> rooms = roomService.getAllRooms();
      if (rooms.isEmpty()) {
        System.out.println("No rooms found");
        return;
      }
      System.out.printf("Listing %d rooms: \n", rooms.size());
      rooms.forEach(room -> System.out.println(room.toString()));
    } catch (Exception e) {
      System.out.println("Failed to list rooms: " + e.getMessage());
    }
  }

  public void listReservations() {
    try {
      ArrayList<Reservation> reservations = reservationService.getAllReservations();
      if (reservations.isEmpty()) {
        System.out.println("No reservations found");
        return;
      }
      System.out.printf("\nListing %d reservations:\n", reservations.size());
      reservations.forEach(reservation -> System.out.println(reservation.toString()));
    } catch (Exception e) {
      System.out.println("Failed to list reservations: " + e.getMessage());
    }
  }

  public void makeCheckIn() {
    try {
      reservationService.getAllReservations()
          .forEach(reservation -> System.out.println(reservation.toString()));
      int reservationId = getter.getInt("Reservation ID: ");
      reservationService.checkIn(reservationId, 0.0);
      System.out.println("Check-in completed successfully");
    } catch (Exception e) {
      System.out.println("Failed to list reservations: " + e.getMessage());
    }
  }

  public void makeCheckOut() {
    try {
      reservationService.getAllReservations()
          .forEach(reservation -> System.out.println(reservation.toString()));
      int reservationId = getter.getInt("Reservation ID: ");
      reservationService.checkOut(reservationId);
      System.out.println("Check-out completed successfully");
    } catch (Exception e) {
      System.out.println("Failed to list reservations: " + e.getMessage());
    }
  }

  public void makeOrder() {
    try {
      this.listItems();
      int itemId = getter.getInt("Item ID: ");
      int quantity = getter.getInt("Quantity: ");

      ReservationService reservationService = new ReservationService(connection, new ReservationDao(connection),
          new GuestDao(connection), new RoomDao(connection), new ReservationRoomDao(connection),
          new MovementDao(connection));
      ArrayList<Reservation> reservations = reservationService.getAllReservations();
      if (reservations.isEmpty()) {
        throw new ReservationException("No reservations found");
      }
      System.out.printf("Listing %d reservations:\n", reservations.size());
      reservations.forEach(reservation -> System.out.println(reservation.toString()));
      int reservationId = getter.getInt("Reservation ID: ");

      consumptionService.consume(itemId, reservationId, quantity);
      System.out.println("Consumption completed successfully");
    } catch (Exception e) {
      System.out.println("Failed to consume item: " + e.getMessage());
    }
  }

  public void listItems() {
    try {
      ArrayList<Item> items = itemService.getAllItems();
      if (items.isEmpty()) {
        System.out.println("No items found");
        return;
      }
      System.out.printf("Listing %d items:\n", items.size());
      items.forEach(item -> System.out.println(item.toString()));
    } catch (Exception e) {
      System.out.println("Failed to list items: " + e.getMessage());
    }
  }

  public void listConsumptions() {
    try {
      this.listReservations();
      int reservationId = getter.getInt("Reservation ID: ");
      consumptionService.listConsumptions(reservationId);
    } catch (Exception e) {
      System.out.println("Failed to list consumptions: " + e.getMessage());
    }
  }

  @FunctionalInterface
  public interface IFunctionality {
    void run();
  }
}
