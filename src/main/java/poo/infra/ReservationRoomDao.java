package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.ReservationRoom;

public class ReservationRoomDao extends BaseDao<ReservationRoom> {
  public ReservationRoomDao(Connection connection) {
    super(connection);
  }

  public ReservationRoom create(ReservationRoom reservationRoom) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "INSERT INTO reservation_room (reservation_id, room_number, amount) VALUES (?, ?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS);
    stmt.setInt(1, reservationRoom.getReservationId());
    stmt.setInt(2, reservationRoom.getRoomId());
    stmt.setDouble(3, reservationRoom.getAmount());
    stmt.execute();
    stmt.close();

    return reservationRoom;
  }

  public void update(ReservationRoom reservationRoom) throws SQLException {
  }

  public void delete(Integer id) throws SQLException {
  }

  public Optional<ReservationRoom> find(Integer id) throws SQLException {
    return Optional.empty();
  }

  public ArrayList<ReservationRoom> find() throws SQLException {
    return new ArrayList<>();
  }
}
