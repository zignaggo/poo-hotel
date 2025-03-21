package poo.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import poo.domain.entities.Consumption;
import poo.domain.entities.ConsumptionItem;
import poo.domain.entities.Item;
import poo.domain.entities.Reservation;
import poo.domain.entities.ReservationEnum;
import poo.domain.expections.ConsumptionException;
import poo.domain.expections.NotFoundItemException;
import poo.infra.ConsumptionDao;
import poo.infra.ConsumptionItemDao;
import poo.infra.ItemDao;
import poo.infra.ReservationDao;

@ExtendWith(MockitoExtension.class)
class ConsumptionServiceTest {

	@Mock
	private ConsumptionDao consumptionDao;

	@Mock
	private ConsumptionItemDao consumptionItemDao;

	@Mock
	private ItemDao itemDao;

	@Mock
	private ReservationDao reservationDao;

	@InjectMocks
	private ConsumptionService consumptionService;

	@Mock
	private Connection connection;

	@Mock
	private PreparedStatement preparedStatement;

	@Mock
	private ResultSet resultSet;

	@Mock
	private Statement statement;

	@BeforeEach
	void setUp() throws SQLException {
		lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		lenient().when(connection.createStatement()).thenReturn(statement);
		lenient().when(preparedStatement.execute()).thenReturn(false);
		lenient().when(preparedStatement.executeQuery()).thenReturn(resultSet);
		lenient().when(resultSet.next()).thenReturn(false);
	}

	@Test
	@DisplayName("Should get all consumptions successfully")
	void getAllConsumptionsSuccessfully() throws SQLException, ConsumptionException {
		int reservationId = 1;
		Date consumptionDate = new Date();
		Consumption newConsumption = new Consumption(reservationId, consumptionDate);
		Consumption savedConsumption = new Consumption(1, reservationId, consumptionDate);
		
		when(consumptionDao.create(any(Consumption.class))).thenReturn(savedConsumption);
		
		Consumption createdConsumption = consumptionDao.create(newConsumption);
		
		ArrayList<Consumption> expectedConsumptions = new ArrayList<>();
		expectedConsumptions.add(createdConsumption);
		when(consumptionDao.find()).thenReturn(expectedConsumptions);

		ArrayList<Consumption> result = consumptionDao.find();

		assertNotNull(result);
		assertEquals(expectedConsumptions.size(), result.size());
		assertEquals(createdConsumption.getId(), result.get(0).getId());
		assertEquals(createdConsumption.getReservationId(), result.get(0).getReservationId());
		
		verify(consumptionDao, times(1)).create(any(Consumption.class));
		verify(consumptionDao, times(1)).find();
	}

	@Test
	@DisplayName("Should throw exception when retrieving consumptions fails")
	void getAllConsumptionsFailure() throws SQLException {
		when(consumptionDao.find()).thenThrow(new SQLException("Database error"));

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.getAllConsumptions());

		assertEquals("Failed to retrieve consumptions: Database error", exception.getMessage());
		verify(consumptionDao, times(1)).find();
	}
	
	@Test
	@DisplayName("Should validate consumption details successfully")
	void validateConsumptionDetailsSuccessfully() throws SQLException, ConsumptionException, NotFoundItemException {
		int itemId = 1;
		int reservationId = 1;
		int quantity = 2;

		Item item = new Item(itemId, "Food", "Pizza", "Delicious pizza", 10, 20.0);
		Reservation reservation = new Reservation(reservationId, "12345678901", new Date(), new Date(),
				ReservationEnum.IN_PROGRESS, 2, 100.0, "Credit Card");

		when(itemDao.find(itemId)).thenReturn(Optional.of(item));
		when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));

		Item result = consumptionService.validateConsumptionDetails(itemId, reservationId, quantity);

		assertNotNull(result);
		assertEquals(item.getId(), result.getId());
		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, times(1)).find(reservationId);
	}

	@Test
	@DisplayName("Should throw exception when item is not found")
	void validateConsumptionDetailsItemNotFound() throws SQLException {
		int itemId = 999;
		int reservationId = 999;
		int quantity = 2;

		when(itemDao.find(itemId)).thenReturn(Optional.empty());

		assertThrows(NotFoundItemException.class,
				() -> consumptionService.validateConsumptionDetails(itemId, reservationId, quantity));

		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, never()).find(anyInt());
	}

	@Test
	@DisplayName("Should throw exception when reservation is not found")
	void validateConsumptionDetailsReservationNotFound() throws SQLException {
		int itemId = 1;
		int reservationId = 999;
		int quantity = 2;

		Item item = new Item(itemId, "Food", "Pizza", "Delicious pizza", 10, 20.0);

		when(itemDao.find(itemId)).thenReturn(Optional.of(item));
		when(reservationDao.find(reservationId)).thenReturn(Optional.empty());

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.validateConsumptionDetails(itemId, reservationId, quantity));

		assertEquals("Reservation not found", exception.getMessage());
		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, times(1)).find(reservationId);
	}

	@Test
	@DisplayName("Should throw exception when reservation is not in progress")
	void validateConsumptionDetailsReservationNotInProgress() throws SQLException {
		int itemId = 1;
		int reservationId = 1;
		int quantity = 2;

		Item item = new Item(itemId, "Food", "Pizza", "Delicious pizza", 10, 20.0);
		Reservation reservation = new Reservation(reservationId, "12345678901", new Date(), new Date(),
				ReservationEnum.OPENED, 2, 100.0, "Credit Card");

		when(itemDao.find(itemId)).thenReturn(Optional.of(item));
		when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.validateConsumptionDetails(itemId, reservationId, quantity));

		assertEquals("Reservation is not in progress", exception.getMessage());
		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, times(1)).find(reservationId);
	}

	@Test
	@DisplayName("Should throw exception when quantity is not positive")
	void validateConsumptionDetailsQuantityNotPositive() throws SQLException {
		int itemId = 1;
		int reservationId = 1;
		int quantity = 0;

		Item item = new Item(itemId, "Food", "Pizza", "Delicious pizza", 10, 20.0);
		Reservation reservation = new Reservation(reservationId, "12345678901", new Date(), new Date(),
				ReservationEnum.IN_PROGRESS, 2, 100.0, "Credit Card");

		when(itemDao.find(itemId)).thenReturn(Optional.of(item));
		when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.validateConsumptionDetails(itemId, reservationId, quantity));

		assertEquals("Quantity must be positive", exception.getMessage());
		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, times(1)).find(reservationId);
	}

	@Test
	@DisplayName("Should throw exception when not enough quantity available")
	void validateConsumptionDetailsNotEnoughQuantity() throws SQLException {
		int itemId = 1;
		int reservationId = 1;
		int quantity = 20; // More than available

		Item item = new Item(itemId, "Food", "Pizza", "Delicious pizza", 10, 20.0);
		Reservation reservation = new Reservation(reservationId, "12345678901", new Date(), new Date(),
				ReservationEnum.IN_PROGRESS, 2, 100.0, "Credit Card");

		when(itemDao.find(itemId)).thenReturn(Optional.of(item));
		when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.validateConsumptionDetails(itemId, reservationId, quantity));

		assertEquals("Not enough quantity", exception.getMessage());
		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, times(1)).find(reservationId);
	}

	@Test
	@DisplayName("Should consume item successfully")
	void consumeSuccessfully() throws SQLException, ConsumptionException, NotFoundItemException {
		int itemId = 1;
		int reservationId = 1;
		int quantity = 2;

		Item item = new Item(itemId, "Food", "Pizza", "Delicious pizza", 10, 20.0);
		Reservation reservation = new Reservation(reservationId, "12345678901", new Date(), new Date(),
				ReservationEnum.IN_PROGRESS, 2, 100.0, "Credit Card");
		Consumption consumption = new Consumption(1, reservationId, new Date());
		ConsumptionItem consumptionItem = new ConsumptionItem(consumption.getId(), itemId, quantity, item.getPrice());

		when(itemDao.find(itemId)).thenReturn(Optional.of(item));
		when(reservationDao.find(reservationId)).thenReturn(Optional.of(reservation));
		when(consumptionDao.create(any(Consumption.class))).thenReturn(consumption);
		when(consumptionItemDao.create(any(ConsumptionItem.class))).thenReturn(consumptionItem);

		Optional<Consumption> result = consumptionService.consume(itemId, reservationId, quantity);

		assertTrue(result.isPresent());
		assertEquals(consumption.getId(), result.get().getId());
		assertEquals(reservationId, result.get().getReservationId());

		verify(itemDao, times(1)).find(itemId);
		verify(reservationDao, times(1)).find(reservationId);
		verify(itemDao, times(1)).update(any(Item.class));
		verify(consumptionDao, times(1)).create(any(Consumption.class));
		verify(consumptionItemDao, times(1)).create(any(ConsumptionItem.class));
	}

	@Test
	@DisplayName("Should throw exception when consume fails")
	void consumeFailure() throws SQLException, ConsumptionException, NotFoundItemException {
		int itemId = 1;
		int reservationId = 1;
		int quantity = 2;

		when(itemDao.find(itemId)).thenThrow(new SQLException("Database error"));

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.consume(itemId, reservationId, quantity));

		assertEquals("Database error", exception.getMessage());
	}

	@Test
	@DisplayName("Should list consumptions successfully")
	void listConsumptionsSuccessfully() throws SQLException, ConsumptionException {
		int reservationId = 1;
		ArrayList<Consumption> consumptions = new ArrayList<>();
		Consumption consumption = new Consumption(1, reservationId, new Date());
		consumptions.add(consumption);

		ArrayList<ConsumptionItem> consumptionItems = new ArrayList<>();
		ConsumptionItem consumptionItem = new ConsumptionItem(consumption.getId(), 1, 2, 20.0,
				new Item(1, "Pizza", 20.0));
		consumptionItems.add(consumptionItem);

		when(consumptionDao.findByReservationId(reservationId)).thenReturn(consumptions);
		when(consumptionItemDao.findByConsumptionIds(anyList())).thenReturn(consumptionItems);

		consumptionService.listConsumptions(reservationId);

		verify(consumptionDao, times(1)).findByReservationId(reservationId);
		verify(consumptionItemDao, times(1)).findByConsumptionIds(anyList());
	}

	@Test
	@DisplayName("Should handle no consumptions found when listing")
	void listConsumptionsNoConsumptionsFound() throws SQLException, ConsumptionException {
		int reservationId = 1;
		ArrayList<Consumption> consumptions = new ArrayList<>();

		when(consumptionDao.findByReservationId(reservationId)).thenReturn(consumptions);

		consumptionService.listConsumptions(reservationId);

		verify(consumptionDao, times(1)).findByReservationId(reservationId);
		verify(consumptionItemDao, never()).findByConsumptionIds(anyList());
	}

	@Test
	@DisplayName("Should throw exception when listing consumptions fails")
	void listConsumptionsFailure() throws SQLException {
		int reservationId = 1;

		when(consumptionDao.findByReservationId(reservationId)).thenThrow(new SQLException("Database error"));

		ConsumptionException exception = assertThrows(ConsumptionException.class,
				() -> consumptionService.listConsumptions(reservationId));

		assertEquals("Failed to list consumptions: Database error", exception.getMessage());
		verify(consumptionDao, times(1)).findByReservationId(reservationId);
	}
}
