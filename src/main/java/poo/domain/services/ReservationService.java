package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import poo.domain.entities.Guest;
import poo.domain.entities.Reservation;
import poo.domain.entities.ReservationEnum;
import poo.domain.entities.ReservationRoom;
import poo.domain.entities.Room;
import poo.domain.expections.ReservationException;
import poo.infra.GuestDao;
import poo.infra.ReservationDao;
import poo.infra.ReservationRoomDao;
import poo.infra.RoomDao;

public class ReservationService {
  private final ReservationDao reservationDao;
  private final GuestDao guestDao;
  private final RoomDao roomDao;
  private final ReservationRoomDao reservationRoomDao;

  public ReservationService(Connection connection) {
    this.reservationDao = new ReservationDao(connection);
    this.guestDao = new GuestDao(connection);
    this.roomDao = new RoomDao(connection);
    this.reservationRoomDao = new ReservationRoomDao(connection);
  }

  public ReservationService(ReservationDao reservationDao, GuestDao guestDao,
      RoomDao roomDao, ReservationRoomDao reservationRoomDao) {
    this.reservationDao = reservationDao;
    this.guestDao = guestDao;
    this.roomDao = roomDao;
    this.reservationRoomDao = reservationRoomDao;
  }

  private void validateReservationDetails(Date checkIn, Date checkOut, int numberOfGuests) throws ReservationException {
    if (checkIn == null || checkOut == null) {
      throw new ReservationException("Check-in and check-out dates are required");
    }

    if (checkIn.after(checkOut)) {
      throw new ReservationException("Check-in date must be before check-out date");
    }

    if (checkIn.before(new Date())) {
      throw new ReservationException("Check-in date must be in the future");
    }

    if (numberOfGuests <= 0) {
      throw new ReservationException("Number of guests must be positive");
    }
  }

  public ArrayList<Reservation> getAllReservations() throws ReservationException {
    try {
      return reservationDao.find();
    } catch (SQLException e) {
      throw new ReservationException("Failed to retrieve reservations: " + e.getMessage(), e);
    }
  }

  private Optional<Guest> findGuestById(int guestId) throws ReservationException {
    try {
      for (Guest guest : guestDao.find()) {
        if (guest.getId() == guestId) {
          return Optional.of(guest);
        }
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new ReservationException("Failed to find guest: " + e.getMessage(), e);
    }
  }

  private ArrayList<Room> findAvailableRooms(int numberOfGuests) throws ReservationException {
    try {
      return roomDao.find(numberOfGuests);
    } catch (SQLException e) {
      throw new ReservationException("Failed to find available rooms: " + e.getMessage(), e);
    }
  }

  public Reservation createReservation(
      int guestId,
      int numberOfGuests,
      Date checkIn,
      Date checkOut,
      String paymentMethod)
      throws ReservationException {
    try {
      validateReservationDetails(checkIn, checkOut, numberOfGuests);

      Optional<Guest> guestOptional = findGuestById(guestId);

      if (!guestOptional.isPresent()) {
        throw new ReservationException("Guest not found with ID: " + guestId);
      }
      Guest guest = guestOptional.get();

      ArrayList<Room> availableRooms = findAvailableRooms(numberOfGuests);
      if (availableRooms.isEmpty()) {
        throw new ReservationException("No available rooms for " + numberOfGuests + " guests");
      }

      Room selectedRoom = availableRooms.get(0);

      double amount = selectedRoom.calculatePrice(numberOfGuests);

      Reservation reservation = reservationDao.create(
          new Reservation(
              guest.getCpf(),
              checkIn,
              checkOut,
              ReservationEnum.OPENED,
              numberOfGuests,
              amount,
              paymentMethod));

      reservationRoomDao.create(new ReservationRoom(reservation.getId(), selectedRoom.getId()));

      return reservation;
    } catch (SQLException e) {
      throw new ReservationException("Database error: " + e.getMessage(), e);
    }
  }
}