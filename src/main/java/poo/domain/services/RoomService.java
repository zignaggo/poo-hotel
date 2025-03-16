package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import poo.domain.entities.Room;
import poo.domain.expections.GuestException;
import poo.infra.RoomDao;

public class RoomService {
  private RoomDao roomDao;

  public RoomService(RoomDao roomDao) {
    this.roomDao = roomDao;
  }
  
  public RoomService(Connection connection) {
    this.roomDao = new RoomDao(connection);
  }

  public ArrayList<Room> getAllRooms() throws GuestException {
    try {
      return roomDao.find();
    } catch (SQLException e) {
      throw new GuestException("Failed to retrieve guests: " + e.getMessage());
    }
  }
}
