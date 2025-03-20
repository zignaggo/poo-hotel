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
import poo.domain.expections.ComsumptionException;
import poo.domain.expections.NotFoundItemException;
import poo.infra.ConsumptionDao;
import poo.infra.ConsumptionItemDao;
import poo.infra.ItemDao;
import poo.infra.ReservationDao;

public class ComsumptionService extends BaseService {

	private final ConsumptionDao consumptionDao;
	private final ConsumptionItemDao consumptionItemDao;
	private final ReservationDao reservationDao;
	private final ItemDao itemDao;

	public ComsumptionService(Connection connection) {
		super(connection);
		this.consumptionDao = new ConsumptionDao(connection);
		this.consumptionItemDao = new ConsumptionItemDao(connection);
		this.itemDao = new ItemDao(connection);
		this.reservationDao = new ReservationDao(connection);
	}

	public ArrayList<Consumption> getAllConsumptions() throws ComsumptionException {
		try {
			return consumptionDao.find();
		} catch (SQLException e) {
			throw new ComsumptionException("Failed to retrieve consumptions: " + e.getMessage());
		}
	}

	public Item validateConsumptionDetails(Integer itemId, Integer reservationId, Integer quantity)
			throws ComsumptionException, NotFoundItemException {
		try {
			Optional<Item> item = itemDao.find(itemId);
			Optional<Reservation> reservation = reservationDao.find(reservationId);
			if (item.isEmpty()) {
				throw new NotFoundItemException();
			}
			if (reservation.isEmpty()) {
				throw new ComsumptionException("Reservation not found");
			}
			if (reservation.get().getStatus() != ReservationEnum.IN_PROGRESS) {
				throw new ComsumptionException("Reservation is not in progress");
			}
			if (quantity <= 0) {
				throw new ComsumptionException("Quantity must be positive");
			}
			if (item.get().getAvailableQuantity() < quantity) {
				throw new ComsumptionException("Not enough quantity");
			}
			return item.get();
		} catch (SQLException e) {
			throw new ComsumptionException(e.getMessage(), e);
		}

	}

	public Consumption _consume(Integer itemId, Integer reservationId, Integer quantity)
			throws ComsumptionException, NotFoundItemException, SQLException {
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
			throws ComsumptionException, NotFoundItemException, SQLException {
		try {
			return this.runTransaction(this::_consume, itemId, reservationId, quantity);
		} catch (Exception e) {
			throw new ComsumptionException(e.getMessage(), e);
		}
	}

	public void listConsumptions(Integer reservationId) throws ComsumptionException {
		try {
			ArrayList<Consumption> consumptions = consumptionDao.findByReservationId(reservationId);
			if(consumptions.isEmpty()) {
				System.out.println("No consumptions found");
				return;
			}
			ArrayList<ConsumptionItem> consumptionItems = consumptionItemDao
					.findByConsumptionIds(consumptions.stream().map(Consumption::getId).toList());
			if (consumptions.isEmpty()) {
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
			throw new ComsumptionException("Failed to list consumptions: " + e.getMessage(), e);
		}
	}
}
