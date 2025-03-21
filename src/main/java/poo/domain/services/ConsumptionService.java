package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Date;

import poo.domain.entities.Consumption;
import poo.domain.entities.ConsumptionItem;
import poo.domain.entities.Item;
import poo.domain.entities.Reservation;
import poo.domain.entities.ReservationEnum;
import poo.domain.exceptions.ConsumptionException;
import poo.domain.exceptions.NotFoundItemException;
import poo.infra.ConsumptionDao;
import poo.infra.ConsumptionItemDao;
import poo.infra.ItemDao;
import poo.infra.ReservationDao;

public class ConsumptionService extends BaseService {

	private final ConsumptionDao consumptionDao;
	private final ConsumptionItemDao consumptionItemDao;
	private final ReservationDao reservationDao;
	private final ItemDao itemDao;

	public ConsumptionService(Connection connection, ConsumptionDao consumptionDao,
			ConsumptionItemDao consumptionItemDao, ReservationDao reservationDao, ItemDao itemDao) {
		super(connection);
		this.consumptionDao = consumptionDao;
		this.consumptionItemDao = consumptionItemDao;
		this.itemDao = itemDao;
		this.reservationDao = reservationDao;
	}

	public ConsumptionService(Connection connection) {
		super(connection);
		this.consumptionDao = new ConsumptionDao(connection);
		this.consumptionItemDao = new ConsumptionItemDao(connection);
		this.itemDao = new ItemDao(connection);
		this.reservationDao = new ReservationDao(connection);
	}

	public ArrayList<Consumption> getAllConsumptions() throws ConsumptionException {
		try {
			return consumptionDao.find();
		} catch (SQLException e) {
			throw new ConsumptionException("Failed to retrieve consumptions: " + e.getMessage());
		}
	}

	public Item validateConsumptionDetails(Integer itemId, Integer reservationId, Integer quantity)
			throws ConsumptionException, NotFoundItemException {
		try {
			Optional<Item> item = itemDao.find(itemId);
			if (item.isEmpty()) {
				throw new NotFoundItemException();
			}
			Optional<Reservation> reservation = reservationDao.find(reservationId);
			if (reservation.isEmpty()) {
				throw new ConsumptionException("Reservation not found");
			}
			if (reservation.get().getStatus() != ReservationEnum.IN_PROGRESS) {
				throw new ConsumptionException("Reservation is not in progress");
			}
			if (quantity <= 0) {
				throw new ConsumptionException("Quantity must be positive");
			}
			if (item.get().getAvailableQuantity() < quantity) {
				throw new ConsumptionException("Not enough quantity");
			}
			return item.get();
		} catch (SQLException e) {
			throw new ConsumptionException(e.getMessage(), e);
		}

	}

	public Consumption _consume(Integer itemId, Integer reservationId, Integer quantity)
			throws ConsumptionException, NotFoundItemException, SQLException {
		Item item = this.validateConsumptionDetails(itemId, reservationId, quantity);
		item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
		itemDao.update(item);
		Consumption consumption = consumptionDao.create(new Consumption(reservationId, new Date()));
		ConsumptionItem consumptionItem = consumptionItemDao
				.create(new ConsumptionItem(consumption.getId(), itemId, quantity, item.getPrice()));
		consumption.addItem(consumptionItem);
		return consumption;
	}

	public Optional<Consumption> consume(Integer itemId, Integer reservationId, Integer quantity)
			throws ConsumptionException, NotFoundItemException, SQLException {
		try {
			return this.runTransaction(this::_consume, itemId, reservationId, quantity);
		} catch (Exception e) {
			throw new ConsumptionException(e.getMessage(), e);
		}
	}

	public void listConsumptions(Integer reservationId) throws ConsumptionException {
		try {
			ArrayList<Consumption> consumptions = consumptionDao.findByReservationId(reservationId);
			if (consumptions.isEmpty()) {
				System.out.println("No consumptions found");
				return;
			}
			ArrayList<ConsumptionItem> consumptionItems = consumptionItemDao
					.findByConsumptionIds(consumptions.stream().map(Consumption::getId).toList());
			if (consumptionItems.isEmpty()) {
				System.out.println("No consumptions found");
				return;
			}
			System.out.printf("Listing %d consumptions:\n", consumptions.size());
			consumptions.forEach(consumption -> {
				consumption.setItems(consumptionItems.stream()
						.filter(consumptionItem -> consumptionItem.getConsumptionId() == consumption.getId())
						.toList());
				System.out.println(consumption.toString());
			});
		} catch (Exception e) {
			throw new ConsumptionException("Failed to list consumptions: " + e.getMessage(), e);
		}
	}
}
