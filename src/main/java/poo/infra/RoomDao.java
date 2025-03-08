package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.Room;

public class RoomDao extends BaseDao<Room> {
  public RoomDao(Connection connection) {
    super(connection);
  }

  public void create(Room room) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
      "INSERT INTO rooms (number, capacity, price_per_night) VALUES (?, ?, ?)"
    );
    stmt.setInt(1, room.getNumber());
    stmt.setInt(2, room.getCapacity());
    stmt.setDouble(3, room.getPricePerNight());
    stmt.execute();
    stmt.close();
  }

  public void update(Room room) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
      "UPDATE rooms SET number = ?, capacity = ?, price_per_night = ? WHERE id = ?"
    );
    stmt.setInt(1, room.getNumber());
    stmt.setInt(2, room.getCapacity());
    stmt.setDouble(3, room.getPricePerNight());
    stmt.setInt(4, room.getId());
    stmt.execute();
    stmt.close();
  }

  public void delete(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("DELETE FROM rooms WHERE id = ?");
    stmt.setInt(1, id);
    stmt.execute();
    stmt.close();
  }

  public Optional<Room> find(Integer id) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM rooms WHERE id = ?");
    stmt.setInt(1, id);
    ResultSet rs = stmt.executeQuery();
    if (!rs.next())
      return Optional.empty();
    Room room = new Room(
      rs.getInt("id"),
      rs.getInt("number"),
      rs.getInt("capacity"),
      rs.getDouble("price_per_night")
    );
    stmt.close();
    return Optional.of(room);
  }

  public ArrayList<Room> find() throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM rooms");
    ResultSet rs = stmt.executeQuery();
    ArrayList<Room> rooms = new ArrayList<>();
    while (rs.next())
      rooms.add(
        new Room(
          rs.getInt("id"),
          rs.getInt("number"),
          rs.getInt("capacity"),
          rs.getDouble("price_per_night")
        )
      );
    stmt.close();
    return rooms;
  }
}
