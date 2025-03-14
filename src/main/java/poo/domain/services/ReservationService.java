package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import poo.domain.entities.Guest;
import poo.domain.entities.Reservation;
import poo.domain.entities.ReservationEnum;
import poo.domain.entities.ReservationRoom;
import poo.domain.entities.Room;
import poo.infra.GuestDao;
import poo.infra.ReservationDao;
import poo.infra.ReservationRoomDao;
import poo.infra.RoomDao;
import poo.utils.Getter;

public class ReservationService {
  private ReservationDao reservationDao;
  private Connection connection;

  public ReservationService(ReservationDao reservationDao, Connection connection) {
    this.reservationDao = reservationDao;
    this.connection = connection;
  }

  public void list() {
    try {
      ArrayList<Reservation> reservations = reservationDao.find();
      if (reservations.isEmpty()) {
        System.out.println("No reservations found");
        return;
      }
      reservations.forEach(reservation -> System.out.println(reservation.toString()));
    } catch (SQLException e) {
      System.out.println("Failed to list reservations: " + e.getMessage());
    }
  }

  public void makeReservation(Getter getter) {
    System.out.println("\nMaking reservation");
    try {
      GuestDao guestDao = new GuestDao(this.connection);
      ArrayList<Guest> guests = guestDao.find();
      if (guests.isEmpty()) {
        System.out.println("No guests found");
        return;
      }
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

      ReservationDao reservationDao = new ReservationDao(this.connection);
      ReservationRoomDao reservationRoomDao = new ReservationRoomDao(this.connection);
      RoomDao roomDao = new RoomDao(this.connection);

      int numberOfGuests = getter.getInt("Number of guests: ");
      Date checkIn = getter.getDate("Check-in date: ");
      Date checkOut = getter.getDate("Check-out date: ");
      String paymentMethod = getter.getString("Payment method: ");

      ArrayList<Room> rooms = roomDao.find(numberOfGuests);

      if (rooms.isEmpty()) {
        System.out.println("No available rooms");
        return;
      }

      Room room = rooms.get(0);
      double amount = room.calculatePrice(numberOfGuests);
      Reservation reservation = reservationDao.create(
          new Reservation(
              selectedGuest.getCpf(),
              checkIn,
              checkOut,
              ReservationEnum.OPENED,
              numberOfGuests,
              amount,
              paymentMethod));

      reservationRoomDao.create(new ReservationRoom(reservation.getId(), room.getId()));

      System.out.println("Reservation created successfully\n");
      System.out.println(reservation.toString());
    } catch (SQLException e) {
      System.out.println("Failed to make reservation: " + e.getMessage());
    }
  }
}
