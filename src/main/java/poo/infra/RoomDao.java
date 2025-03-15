package poo.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import poo.domain.entities.DeluxeRoom;
import poo.domain.entities.Room;
import poo.domain.entities.RoomFactory;

public class RoomDao extends BaseDao<Room> {
  public RoomDao(Connection connection) {
    super(connection);
  }

  public Room create(Room room) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "INSERT INTO rooms (number, capacity, price_per_night, room_type, has_jacuzzi) VALUES (?, ?, ?, ?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS);
    stmt.setInt(1, room.getNumber());
    stmt.setInt(2, room.getCapacity());
    stmt.setDouble(3, room.getPricePerNight());
    stmt.setString(4, room.getRoomType());

    if (room instanceof DeluxeRoom) {
      DeluxeRoom deluxeRoom = (DeluxeRoom) room;
      stmt.setBoolean(5, deluxeRoom.hasJacuzzi());
    } else {
      stmt.setBoolean(5, false);
    }
   
    stmt.execute();
    stmt.close();

    ResultSet rs = stmt.getGeneratedKeys();
    room.setId(rs.getInt(1));
    return room;
  }

  public void update(Room room) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement(
        "UPDATE rooms SET number = ?, capacity = ?, price_per_night = ?, room_type = ?, has_jacuzzi = ? WHERE id = ?");
    stmt.setInt(1, room.getNumber());
    stmt.setInt(2, room.getCapacity());
    stmt.setDouble(3, room.getPricePerNight());
    stmt.setString(4, room.getRoomType());
    stmt.setBoolean(5, room instanceof DeluxeRoom ? ((DeluxeRoom) room).hasJacuzzi() : false);
    stmt.setInt(6, room.getId());
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

    Room room = RoomFactory.create(
        rs.getString("room_type"),
        rs.getInt("id"),
        rs.getInt("number"),
        rs.getInt("capacity"),
        rs.getDouble("price_per_night"),
        rs.getBoolean("has_jacuzzi"),
        rs.getBoolean("has_room_service"));
    stmt.close();
    return Optional.of(room);
  }

  public ArrayList<Room> find() throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM rooms");
    ResultSet rs = stmt.executeQuery();
    ArrayList<Room> rooms = new ArrayList<>();
    while (rs.next())
      rooms.add(
          RoomFactory.create(
              rs.getString("room_type"),
              rs.getInt("id"),
              rs.getInt("number"),
              rs.getInt("capacity"),
              rs.getDouble("price_per_night"),
              rs.getBoolean("has_jacuzzi"),
              rs.getBoolean("has_room_service")));
    stmt.close();
    return rooms;
  }

  public ArrayList<Room> find(int numberOfGuests) throws SQLException {
    PreparedStatement stmt = this.getConnection().prepareStatement("SELECT * FROM rooms WHERE capacity >= ?");
    stmt.setInt(1, numberOfGuests);
    ResultSet rs = stmt.executeQuery();
    ArrayList<Room> rooms = new ArrayList<>();
    while (rs.next())
      rooms.add(
          RoomFactory.create(
              rs.getString("room_type"),
              rs.getInt("id"),
              rs.getInt("number"),
              rs.getInt("capacity"),
              rs.getDouble("price_per_night"),
              rs.getBoolean("has_jacuzzi"),
              rs.getBoolean("has_room_service")));
    stmt.close();
    return rooms;
  }
}
