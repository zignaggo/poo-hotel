package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import poo.domain.entities.Item;
import poo.domain.expections.ItemException;
import poo.infra.ItemDao;

public class ItemService {
	private ItemDao itemDao;

	public ItemService(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public ItemService(Connection connection) {
		this.itemDao = new ItemDao(connection);
	}

	public ArrayList<Item> getAllItems() throws ItemException {
		try {
			return itemDao.find();
		} catch (SQLException e) {
			throw new ItemException("Failed to retrieve items: " + e.getMessage());
		}
	}

}
