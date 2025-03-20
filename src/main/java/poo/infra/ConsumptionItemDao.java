package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import poo.domain.entities.ConsumptionItem;
import poo.domain.entities.Item;

public class ConsumptionItemDao extends BaseDao<ConsumptionItem> {

	public ConsumptionItemDao(Connection connection) {
		super(connection);
	}

	public ConsumptionItem create(ConsumptionItem consumptionItem) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO consumptions_item (consumption_id, item_id, quantity, price) VALUES (?, ?, ?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, consumptionItem.getConsumptionId());
		stmt.setInt(2, consumptionItem.getItemId());
		stmt.setInt(3, consumptionItem.getQuantity());
		stmt.setDouble(4, consumptionItem.getPrice());
		stmt.execute();

		ResultSet rs = stmt.getGeneratedKeys();
		if (!rs.next())
			throw new SQLException("Failed to create consumption item");
		stmt.close();
		return consumptionItem;
	}

	public void update(ConsumptionItem consumptionItem) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"UPDATE consumptions_item SET consumption_id = ?, item_id = ?, quantity = ?");
		stmt.setInt(1, consumptionItem.getConsumptionId());
		stmt.setInt(2, consumptionItem.getItemId());
		stmt.setInt(3, consumptionItem.getQuantity());
		stmt.execute();
		stmt.close();
	}

	public void delete(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM consumptions_item WHERE id = ?");
		stmt.setInt(1, id);
		stmt.execute();
		stmt.close();
	}

	public Optional<ConsumptionItem> find(Integer consumptionId) throws SQLException {
		PreparedStatement stmt = this.getConnection()
				.prepareStatement("SELECT * FROM consumptions_item WHERE consumption_id = ?");
		stmt.setInt(1, consumptionId);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			return Optional.empty();
		ConsumptionItem consumptionItem = new ConsumptionItem(
				rs.getInt("consumption_id"),
				rs.getInt("item_id"),
				rs.getInt("quantity"),
				rs.getDouble("price"));
		stmt.close();
		return Optional.of(consumptionItem);
	}

	public ArrayList<ConsumptionItem> findByItemId(Integer itemId) throws SQLException {
		PreparedStatement stmt = this.getConnection()
				.prepareStatement(
						"SELECT ci.consumption_id, ci.item_id, i.name, ci.quantity, ci.price, (ci.price * ci.quantity) as item_total FROM consumptions_item ci JOIN items i ON ci.item_id = i.id WHERE item_id = ?");
		stmt.setInt(1, itemId);
		ResultSet rs = stmt.executeQuery();
		ArrayList<ConsumptionItem> consumptionItems = new ArrayList<>();
		while (rs.next())
			consumptionItems.add(new ConsumptionItem(
					rs.getInt("consumption_id"),
					rs.getInt("item_id"),
					rs.getInt("quantity"),
					rs.getDouble("price")));
		stmt.close();
		return consumptionItems;
	}

	public ArrayList<ConsumptionItem> findByConsumptionIds(List<Integer> consumptionIds) throws SQLException {
		PreparedStatement stmt = this.getConnection()
				.prepareStatement(
						"SELECT ci.consumption_id, ci.item_id, i.name, ci.quantity, ci.price, (ci.price * ci.quantity) as item_total FROM consumptions_item ci JOIN items i ON ci.item_id = i.id WHERE consumption_id in ("
								+ consumptionIds.stream().map(i -> i.toString()).collect(Collectors.joining(","))
								+ ")");
		ResultSet rs = stmt.executeQuery();
		ArrayList<ConsumptionItem> consumptionItems = new ArrayList<>();
		while (rs.next())
			consumptionItems.add(new ConsumptionItem(
					rs.getInt("consumption_id"),
					rs.getInt("item_id"),
					rs.getInt("quantity"),
					rs.getDouble("price"),
					new Item(rs.getInt("item_id"), rs.getString("name"), rs.getDouble("price"))));
		stmt.close();
		return consumptionItems;
	}

	public ArrayList<ConsumptionItem> find() throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM consumptions_item");
		ResultSet rs = stmt.executeQuery();
		ArrayList<ConsumptionItem> consumptionItems = new ArrayList<>();
		while (rs.next())
			consumptionItems.add(new ConsumptionItem(
					rs.getInt("consumption_id"),
					rs.getInt("item_id"),
					rs.getInt("quantity"),
					rs.getDouble("price")));
		stmt.close();
		return consumptionItems;
	}
}
