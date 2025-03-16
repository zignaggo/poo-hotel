package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.Movement;
import poo.domain.entities.MovementEnum;

public class MovementDao extends BaseDao<Movement> {

	public MovementDao(Connection connection) {
		super(connection);
	}

	public Movement create(Movement movements) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO movements (type, reservation_id, amount, date) VALUES (?::movement_type, ?, ?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
		stmt.setString(1, movements.getType().name());
		stmt.setInt(2, movements.getReservationId());
		stmt.setDouble(3, movements.getAmount());
		stmt.setTimestamp(4, new java.sql.Timestamp(movements.getDate().getTime()));
		stmt.execute();

		ResultSet rs = stmt.getGeneratedKeys();
		if (!rs.next())
			throw new SQLException("Failed to create movement");
		movements.setId(rs.getInt(1));
		stmt.close();
		return movements;
	}

	public void update(Movement movements) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement(
				"UPDATE movements SET type = ?, reservation_id = ?, amount = ?, date = ? WHERE id = ?");
		stmt.setString(1, movements.getType().name());
		stmt.setInt(2, movements.getReservationId());
		stmt.setDouble(3, movements.getAmount());
		stmt.setTimestamp(4, new java.sql.Timestamp(movements.getDate().getTime()));
		stmt.setInt(5, movements.getId());
		stmt.execute();
		stmt.close();
	}

	public void delete(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM movements WHERE id = ?");
		stmt.setInt(1, id);
		stmt.execute();
		stmt.close();
	}

	public Optional<Movement> find(Integer id) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM movements WHERE id = ?");
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			return Optional.empty();
		Optional<Movement> movement = Optional.of(new Movement(
				rs.getInt("id"),
				rs.getInt("reservation_id"),
				MovementEnum.valueOf(rs.getString("type")),
				rs.getTimestamp("date"),
				rs.getDouble("amount")));
		stmt.close();
		return movement;
	}

	public Optional<Movement> find(Integer reservationId, MovementEnum type) throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM movements WHERE reservation_id = ? AND type = ?::movement_type");
		stmt.setInt(1, reservationId);
		stmt.setString(2, type.name());
		ResultSet rs = stmt.executeQuery();
		if (!rs.next())
			return Optional.empty();
		Optional<Movement> movement = Optional.of(new Movement(
			rs.getInt("id"),
			rs.getInt("reservation_id"),
			MovementEnum.valueOf(rs.getString("type")),
			rs.getTimestamp("date"),
			rs.getDouble("amount")));
		stmt.close();
		return movement;
	}

	public ArrayList<Movement> find() throws SQLException {
		PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM movements");
		ResultSet rs = stmt.executeQuery();
		ArrayList<Movement> movements = new ArrayList<>();
		while (rs.next())
			movements.add(new Movement(
					rs.getInt("id"),
					rs.getInt("reservation_id"),
					MovementEnum.valueOf(rs.getString("type")),
					rs.getTimestamp("date"),
					rs.getDouble("amount")));
		stmt.close();
		return movements;
	}
}
