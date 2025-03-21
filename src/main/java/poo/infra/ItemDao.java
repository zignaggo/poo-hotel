package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.Item;

public class ItemDao extends BaseDao<Item> {
	public ItemDao(Connection connection) {
		super(connection);
	}

	public Item create(Item entity) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO items (type, name, description, available_quantity, price) VALUES (?, ?, ?, ?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, entity.getType());
		stmt.setString(2, entity.getName());
		stmt.setString(3, entity.getDescription());
		stmt.setInt(4, entity.getAvailableQuantity());
		stmt.setDouble(5, entity.getPrice());
		stmt.execute();
		ResultSet rs = stmt.getGeneratedKeys();
		if (!rs.next())
			throw new SQLException("Failed to create item");
		entity.setId(rs.getInt(1));
		stmt.close();
		return entity;
	}

	public void delete(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM items WHERE id = ?");
		stmt.setInt(1, id);
		stmt.execute();
		stmt.close();
	}

	public void update(Item entity) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"UPDATE items SET type = ?, name = ?, description = ?, available_quantity = ?, price = ? WHERE id = ?");
		stmt.setString(1, entity.getType());
		stmt.setString(2, entity.getName());
		stmt.setString(3, entity.getDescription());
		stmt.setInt(4, entity.getAvailableQuantity());
		stmt.setDouble(5, entity.getPrice());
		stmt.setInt(6, entity.getId());
		stmt.execute();
		stmt.close();
	}

	public ArrayList<Item> find() throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM items");
		ResultSet rs = stmt.executeQuery();
		ArrayList<Item> items = new ArrayList<>();
		while (rs.next())
			items.add(new Item(rs.getInt("id"), rs.getString("type"), rs.getString("name"), rs.getString("description"),
					rs.getInt("available_quantity"), rs.getDouble("price")));
		stmt.close();
		return items;
	}

	public Optional<Item> find(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM items WHERE id = ?");
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			return Optional.empty();
		Item item = new Item(rs.getInt("id"), rs.getString("type"), rs.getString("name"), rs.getString("description"),
			rs.getInt("available_quantity"), rs.getDouble("price"));
		stmt.close();
		return Optional.of(item);
	}
}
