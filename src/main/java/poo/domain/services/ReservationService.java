package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import poo.domain.entities.Guest;
import poo.domain.entities.Movement;
import poo.domain.entities.MovementEnum;
import poo.domain.entities.Reservation;
import poo.domain.entities.ReservationEnum;
import poo.domain.entities.ReservationRoom;
import poo.domain.entities.Room;
import poo.domain.expections.ReservationException;
import poo.infra.GuestDao;
import poo.infra.MovementDao;
import poo.infra.ReservationDao;
import poo.infra.ReservationRoomDao;

public class ReservationService extends BaseService {
  private final ReservationDao reservationDao;
  private final GuestDao guestDao;
  private final RoomService roomService;
  private final ReservationRoomDao reservationRoomDao;
  private final MovementDao movementsDao;

  public ReservationService(Connection connection) {
    super(connection);
    this.reservationDao = new ReservationDao(connection);
    this.guestDao = new GuestDao(connection);
    this.roomService = new RoomService(connection);
    this.reservationRoomDao = new ReservationRoomDao(connection);
    this.movementsDao = new MovementDao(connection);
  }

  public ReservationService(ReservationDao reservationDao, GuestDao guestDao,
      RoomService roomService, ReservationRoomDao reservationRoomDao, MovementDao movementsDao, Connection connection) {
    super(connection);
    this.reservationDao = reservationDao;
    this.guestDao = guestDao;
    this.roomService = roomService;
    this.reservationRoomDao = reservationRoomDao;
    this.movementsDao = movementsDao;
  }

  private void validateReservationDetails(Date checkIn, Date checkOut, int numberOfGuests) throws ReservationException {
    
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    Date today = calendar.getTime();

    if(checkIn.before(today)) {
      throw new ReservationException("Check-in date must be today or in the future");
    }
    
    if (checkIn == null || checkOut == null) {
      throw new ReservationException("Check-in and check-out dates are required");
    }

    if (checkIn.after(checkOut)) {
      throw new ReservationException("Check-in date must be before check-out date");
    }

    if (numberOfGuests <= 0) {
      throw new ReservationException("Number of guests must be positive");
    }
  }

  private Optional<Guest> findGuestById(int guestId) throws ReservationException {
    try {
      return guestDao.find(guestId);
    } catch (SQLException e) {
      throw new ReservationException("Failed to find guest: " + e.getMessage(), e);
    }
  }

  public ArrayList<Reservation> getAllReservations() throws ReservationException {
    try {
      return reservationDao.find(true);
    } catch (SQLException e) {
      throw new ReservationException("Failed to retrieve reservations: " + e.getMessage(), e);
    }
  }

  public Reservation create(
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

      ArrayList<Room> availableRooms = roomService.findAvailableRooms(numberOfGuests);
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

      reservationRoomDao
          .create(new ReservationRoom(reservation.getId(), selectedRoom.getId(), selectedRoom.getPricePerNight()));

      return reservation;
    } catch (SQLException e) {
      throw new ReservationException("Database error: " + e.getMessage(), e);
    }
  }

  private Reservation _checkIn(Integer reservationId, Double amount) throws SQLException {
    Optional<Reservation> reservation = this.reservationDao.find(reservationId);
    if (!reservation.isPresent()) {
      throw new SQLException("Reservation not found with ID: " + reservationId);
    }
    System.out.println(reservation.get().getStatus());
    if (reservation.get().getStatus() != ReservationEnum.OPENED) {
      throw new SQLException("Reservation is not opened: " + reservation.get().getStatus());
    }
    reservation.get().setStatus(ReservationEnum.IN_PROGRESS);
    this.reservationDao.update(reservation.get());
    this.movementsDao
        .create(new Movement(reservationId, MovementEnum.CHECK_IN, new Date(), amount));
    return reservation.get();
  }

  public Reservation checkIn(int reservationId, double amount) throws ReservationException {
    try {
      return this.runTransaction(this::_checkIn, reservationId, amount).get();
    } catch (Exception e) {
      throw new ReservationException("Check-in error: " + e.getMessage(), e);
    }
  }

  private Reservation _checkOut(Integer reservationId) throws SQLException {
    Optional<Reservation> reservation = this.reservationDao.find(reservationId);
    if (!reservation.isPresent()) {
      throw new SQLException("Reservation not found with ID: " + reservationId);
    }
    if (reservation.get().getStatus() != ReservationEnum.IN_PROGRESS) {
      throw new SQLException("Reservation is not in progress: " + reservation.get().getStatus());
    }
    Optional<Movement> movement = this.movementsDao.find(reservationId, MovementEnum.CHECK_IN);
    if (!movement.isPresent()) {
      throw new SQLException("Check-in movement not found for reservation ID: " + reservationId);
    }
    double missingAmount = reservation.get().getAmount() - movement.get().getAmount();
    reservation.get().setStatus(ReservationEnum.FINISHED);
    this.reservationDao.update(reservation.get());
    this.movementsDao
    .create(new Movement(reservationId, MovementEnum.CHECK_OUT, new Date(), missingAmount));
    return reservation.get();
  }

  public Reservation checkOut(int reservationId) throws ReservationException {
    try {
      return this.runTransaction(this::_checkOut, reservationId).get();
    } catch (Exception e) {
      throw new ReservationException("Checkout error: " + e.getMessage(), e);
    }
  }
}