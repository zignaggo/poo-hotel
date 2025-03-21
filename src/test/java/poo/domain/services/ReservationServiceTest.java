package poo.domain.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

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
import poo.infra.RoomDao;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private Connection conn;

    @Mock
    private ReservationDao reservationDao;

    @Mock
    private GuestDao guestDao;

    @Mock
    private RoomDao roomDao;

    @Mock
    private ReservationRoomDao reservationRoomDao;

    @Mock
    private MovementDao movementsDao;

    @InjectMocks
    private ReservationService reservationService;

    @InjectMocks
    private RoomService roomService;

    private Date getValidCheckInDate() {
        return Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getValidCheckOutDate() {
        return Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test
    @DisplayName("Should create a reservation successfully")
    void createReservationSuccessfully() throws ReservationException, SQLException {
        int guestId = 1;
        int numberOfGuests = 2;
        Date checkIn = getValidCheckInDate();
        Date checkOut = getValidCheckOutDate();
        String paymentMethod = "Credit Card";
        
        Guest guest = new Guest("12345678901", "John Doe", "john@example.com", "123456789", "123 Main St", new Date());
        guest.setId(guestId);
        
        Room room = new Room(1, 101, 2, 100.0, "Standard");
   
        ArrayList<Guest> guests = new ArrayList<>();
        guests.add(guest);
        
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);
        
        Reservation expectedReservation = new Reservation(
            guest.getCpf(),
            checkIn,
            checkOut,
            ReservationEnum.OPENED,
            numberOfGuests,
            200.0,
            paymentMethod
        );
        expectedReservation.setId(1);
        
        when(guestDao.find(guestId)).thenReturn(Optional.of(guest));
        when(roomDao.find(numberOfGuests)).thenReturn(rooms);
        when(reservationDao.create(any(Reservation.class))).thenReturn(expectedReservation);
        
        Reservation result = reservationService.create(guestId, numberOfGuests, checkIn, checkOut, paymentMethod);
        
        assertNotNull(result);
        assertEquals(expectedReservation.getId(), result.getId());
        assertEquals(guest.getCpf(), result.getGuestCpf());
        assertEquals(ReservationEnum.OPENED, result.getStatus());
        
        verify(guestDao, times(1)).find(guestId);
        verify(roomDao, times(1)).find(numberOfGuests);
        verify(reservationDao, times(1)).create(any(Reservation.class));
        verify(reservationRoomDao, times(1)).create(any(ReservationRoom.class));
    }

    @Test
    @DisplayName("Should throw exception when guest is not found")
    void createReservationGuestNotFound() throws SQLException {
        int guestId = 999; 
        int numberOfGuests = 2;
        Date checkIn = getValidCheckInDate();
        Date checkOut = getValidCheckOutDate();
        String paymentMethod = "Credit Card";
        
        ArrayList<Guest> guests = new ArrayList<>(); 
        
        when(guestDao.find(guestId)).thenReturn(Optional.empty());
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.create(guestId, numberOfGuests, checkIn, checkOut, paymentMethod)
        );
        
        assertEquals("Guest not found with ID: 999", exception.getMessage());
        verify(guestDao, times(1)).find(guestId);
        verify(roomDao, never()).find(anyInt());
        verify(reservationDao, never()).create(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when no available rooms")
    void createReservationNoAvailableRooms() throws SQLException {
        int guestId = 1;
        int numberOfGuests = 10; 
        Date checkIn = getValidCheckInDate();
        Date checkOut = getValidCheckOutDate();
        String paymentMethod = "Credit Card";
        
        Guest guest = new Guest("12345678901", "John Doe", "john@example.com", "123456789", "123 Main St", new Date());
        guest.setId(guestId);
        
        ArrayList<Guest> guests = new ArrayList<>();
        guests.add(guest);
        
        ArrayList<Room> rooms = new ArrayList<>();
        
        when(guestDao.find(guestId)).thenReturn(Optional.of(guest));
        when(roomDao.find(numberOfGuests)).thenReturn(rooms);
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.create(guestId, numberOfGuests, checkIn, checkOut, paymentMethod)
        );
        
        assertEquals("No available rooms for 10 guests", exception.getMessage());
        verify(guestDao, times(1)).find(guestId);
        verify(roomDao, times(1)).find(numberOfGuests);
        verify(reservationDao, never()).create(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when check-in date is after check-out date")
    void createReservationInvalidDates() throws ReservationException, SQLException {
        int guestId = 1;
        int numberOfGuests = 2;
        Date checkIn = getValidCheckOutDate();
        Date checkOut = getValidCheckInDate();
        String paymentMethod = "Credit Card";
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.create(guestId, numberOfGuests, checkIn, checkOut, paymentMethod)
        );
        
        assertEquals("Check-in date must be before check-out date", exception.getMessage());
        verify(guestDao, never()).find(guestId);
        verify(roomDao, never()).find(anyInt());
        verify(reservationDao, never()).create(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when number of guests is not positive")
    void createReservationInvalidNumberOfGuests() throws ReservationException, SQLException {
        int guestId = 1;
        int numberOfGuests = 0; 
        Date checkIn = getValidCheckInDate();
        Date checkOut = getValidCheckOutDate();
        String paymentMethod = "Credit Card";
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.create(guestId, numberOfGuests, checkIn, checkOut, paymentMethod)
        );
        
        assertEquals("Number of guests must be positive", exception.getMessage());
        verify(guestDao, never()).find();
        verify(roomDao, never()).find(anyInt());
        verify(reservationDao, never()).create(any(Reservation.class));
    }

    @Test
    @DisplayName("Should list all reservations successfully")
    void getAllReservationsSuccessfully() throws ReservationException, SQLException {
        ArrayList<Reservation> expectedReservations = new ArrayList<>();
        when(reservationDao.find(true)).thenReturn(expectedReservations);
        
        ArrayList<Reservation> result = reservationService.getAllReservations();
        
        assertNotNull(result);
        assertEquals(expectedReservations, result);
        verify(reservationDao, times(1)).find(true);
    }

    @Test
    @DisplayName("Should check-in successfully")
    void checkInSuccessfully() throws ReservationException, SQLException {
        int reservationId = 1;
        double amount = 100.0;
        
        Reservation reservation = new Reservation(
            reservationId,
            "12345678901",
            getValidCheckInDate(),
            getValidCheckOutDate(),
            ReservationEnum.OPENED,
            2,
            200.0,
            "Credit Card"
        );
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));
        doNothing().when(reservationDao).update(any(Reservation.class));
        
        Reservation result = reservationService.checkIn(reservationId, amount);
        
        assertNotNull(result);
        assertEquals(ReservationEnum.IN_PROGRESS, result.getStatus());
        
        verify(reservationDao, times(1)).find(reservationId);
        verify(reservationDao, times(1)).update(any(Reservation.class));
        verify(movementsDao, times(1)).create(any(Movement.class));
    }

    @Test
    @DisplayName("Should throw exception when reservation not found during check-in")
    void checkInReservationNotFound() throws SQLException {
        int reservationId = 999; 
        double amount = 100.0;
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.empty());
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.checkIn(reservationId, amount)
        );
        
        assertTrue(exception.getMessage().contains("Reservation not found with ID: 999"));
        verify(reservationDao, times(1)).find(reservationId);
        verify(reservationDao, never()).update(any(Reservation.class));
        verify(movementsDao, never()).create(any(Movement.class));
    }

    @Test
    @DisplayName("Should throw exception when reservation is not in OPENED status during check-in")
    void checkInReservationNotOpened() throws SQLException {
        int reservationId = 1;
        double amount = 100.0;
        
        Reservation reservation = new Reservation(
            reservationId,
            "12345678901",
            getValidCheckInDate(),
            getValidCheckOutDate(),
            ReservationEnum.IN_PROGRESS,
            2,
            200.0,
            "Credit Card"
        );
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.checkIn(reservationId, amount)
        );
        
        assertTrue(exception.getMessage().contains("Reservation is not opened"));
        verify(reservationDao, times(1)).find(reservationId);
        verify(reservationDao, never()).update(any(Reservation.class));
        verify(movementsDao, never()).create(any(Movement.class));
    }

    @Test
    @DisplayName("Should check-out successfully")
    void checkOutSuccessfully() throws ReservationException, SQLException {
        int reservationId = 1;
        double totalAmount = 200.0;
        double checkInAmount = 100.0;
        
        Reservation reservation = new Reservation(
            reservationId,
            "12345678901",
            getValidCheckInDate(),
            getValidCheckOutDate(),
            ReservationEnum.IN_PROGRESS,
            2,
            totalAmount,
            "Credit Card"
        );
        
        Movement checkInMovement = new Movement(reservationId, MovementEnum.CHECK_IN, new Date(), checkInAmount);
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));
        when(movementsDao.find(reservationId, MovementEnum.CHECK_IN)).thenReturn(Optional.of(checkInMovement));
        doNothing().when(reservationDao).update(any(Reservation.class));
        
        Reservation result = reservationService.checkOut(reservationId);
        
        assertNotNull(result);
        assertEquals(ReservationEnum.FINISHED, result.getStatus());
        
        verify(reservationDao, times(1)).find(reservationId);
        verify(movementsDao, times(1)).find(reservationId, MovementEnum.CHECK_IN);
        verify(reservationDao, times(1)).update(any(Reservation.class));
        verify(movementsDao, times(1)).create(any(Movement.class));
    }

    @Test
    @DisplayName("Should throw exception when reservation not found during check-out")
    void checkOutReservationNotFound() throws SQLException {
        int reservationId = 999;
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.empty());
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.checkOut(reservationId)
        );
        
        assertTrue(exception.getMessage().contains("Reservation not found with ID: 999"));
        verify(reservationDao, times(1)).find(reservationId);
        verify(movementsDao, never()).find(anyInt(), any(MovementEnum.class));
        verify(reservationDao, never()).update(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when reservation is not in IN_PROGRESS status during check-out")
    void checkOutReservationNotInProgress() throws SQLException {
        int reservationId = 1;
        
        Reservation reservation = new Reservation(
            reservationId,
            "12345678901",
            getValidCheckInDate(),
            getValidCheckOutDate(),
            ReservationEnum.OPENED,
            2,
            200.0,
            "Credit Card"
        );
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.checkOut(reservationId)
        );
        
        assertTrue(exception.getMessage().contains("Reservation is not in progress"));
        verify(reservationDao, times(1)).find(reservationId);
        verify(movementsDao, never()).find(anyInt(), any(MovementEnum.class));
        verify(reservationDao, never()).update(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when check-in movement not found during check-out")
    void checkOutCheckInMovementNotFound() throws SQLException {
        int reservationId = 1;
        
        Reservation reservation = new Reservation(
            reservationId,
            "12345678901",
            getValidCheckInDate(),
            getValidCheckOutDate(),
            ReservationEnum.IN_PROGRESS,
            2,
            200.0,
            "Credit Card"
        );
        
        when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));
        when(movementsDao.find(reservationId, MovementEnum.CHECK_IN)).thenReturn(Optional.empty());
        
        ReservationException exception = assertThrows(ReservationException.class, () -> 
            reservationService.checkOut(reservationId)
        );
        
        assertTrue(exception.getMessage().contains("Check-in movement not found for reservation ID"));
        verify(reservationDao, times(1)).find(reservationId);
        verify(movementsDao, times(1)).find(reservationId, MovementEnum.CHECK_IN);
        verify(reservationDao, never()).update(any(Reservation.class));
    }
}
