package poo.domain.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.Scanner;
import poo.infra.GuestDao;
import poo.infra.ReservationDao;
import poo.infra.RoomDao;
import poo.utils.Getter;

public class HotelService {
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

  public void createGuest(Connection connection, Getter getter) {
    GuestDao guestDao = new GuestDao(connection);
    GuestService guestService = new GuestService(guestDao);
    try {
      guestService.create(connection, getter);
    } catch (Exception e) {
      System.out.println("Failed to create guest: " + e.getMessage());
    }
  }

  public void makeReservation(Connection connection, Getter getter) {
    ReservationDao reservationDao = new ReservationDao(connection);
    ReservationService reservationService = new ReservationService(reservationDao, connection);
    reservationService.makeReservation(getter);
  }

  public void listGuests(Connection connection, Getter getter) {
    GuestDao guestDao = new GuestDao(connection);
    GuestService guestService = new GuestService(guestDao);
    try {
      guestService.list();
    } catch (Exception e) {
      System.out.println("Failed to list guests: " + e.getMessage());
    }
  }

  public void listRooms(Connection connection, Getter getter) {
    RoomDao roomDao = new RoomDao(connection);
    RoomService roomService = new RoomService(roomDao);
    roomService.list();
  }

  public void listReservations(Connection connection, Getter getter) {
    ReservationDao reservationDao = new ReservationDao(connection);
    ReservationService reservationService = new ReservationService(reservationDao, connection);
    reservationService.list();
  }

  public void makeCheckin(Connection connection, Getter getter) {}
  public void makeCheckout(Connection connection, Getter getter) {}
}
