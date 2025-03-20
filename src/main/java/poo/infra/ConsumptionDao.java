package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import poo.domain.entities.Consumption;

public class ConsumptionDao extends BaseDao<Consumption> {

	public ConsumptionDao(Connection connection) {
		super(connection);
	}

	public Consumption create(Consumption consumption) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO consumptions (reservation_id, date) VALUES (?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, consumption.getReservationId());
		stmt.setTimestamp(2, new java.sql.Timestamp(consumption.getDate().getTime()));
		stmt.execute();

		ResultSet rs = stmt.getGeneratedKeys();
		if (!rs.next())
			throw new SQLException("Failed to create consumption");
		consumption.setId(rs.getInt(1));
		stmt.close();
		return consumption;
	}

	public void update(Consumption consumption) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"UPDATE consumptions SET reservation_id = ?, date = ? WHERE id = ?");
		stmt.setInt(1, consumption.getReservationId());
		stmt.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
		stmt.setInt(3, consumption.getId());
		stmt.execute();
		stmt.close();
	}

	public void delete(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM consumptions WHERE id = ?");
		stmt.setInt(1, id);
		stmt.execute();
		stmt.close();
	}

	public Optional<Consumption> find(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM consumptions WHERE id = ?");
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			return Optional.empty();
		Consumption consumption = new Consumption(
				rs.getInt("id"),
				rs.getInt("reservation_id"),
				rs.getTimestamp("date"));
		stmt.close();
		return Optional.of(consumption);
	}

	public ArrayList<Consumption> findByReservationId(Integer reservationId) throws SQLException {
		PreparedStatement stmt = this.getConnection()
				.prepareStatement(
						"SELECT c.id, c.reservation_id, c.date, (SELECT SUM(ci.price * ci.quantity) as total FROM consumptions_item ci) as total from consumptions c where c.reservation_id = ?");
		stmt.setInt(1, reservationId);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Consumption> consumptions = new ArrayList<>();
		while (rs.next())
			consumptions.add(new Consumption(
					rs.getInt("id"),
					rs.getInt("reservation_id"),
					rs.getTimestamp("date"),
					rs.getDouble("total")));
		stmt.close();
		return consumptions;
	}

	public ArrayList<Consumption> find() throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"SELECT * FROM consumptions");
		ResultSet rs = stmt.executeQuery();
		ArrayList<Consumption> consumptions = new ArrayList<>();
		while (rs.next())
			consumptions.add(new Consumption(
					rs.getInt("id"),
					rs.getInt("reservation_id"),
					rs.getTimestamp("date")));
		stmt.close();
		return consumptions;
	}
}
