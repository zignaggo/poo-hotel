package poo.domain.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import poo.domain.entities.Room;
import poo.domain.exceptions.GuestException;
import poo.domain.exceptions.ReservationException;
import poo.infra.RoomDao;

public class RoomService {
  private RoomDao roomDao;

  public RoomService(RoomDao roomDao) {
    this.roomDao = roomDao;
  }

  public RoomService(Connection connection) {
    this.roomDao = new RoomDao(connection);
  }

  public RoomService(Connection connection, RoomDao roomDao) {
    this.roomDao = roomDao;
  }

  public ArrayList<Room> getAllRooms() throws GuestException {
    try {
      return roomDao.find();
    } catch (SQLException e) {
      throw new GuestException("Failed to retrieve guests: " + e.getMessage());
    }
  }

  public ArrayList<Room> findAvailableRooms(int numberOfGuests) throws ReservationException {
    try {
      return roomDao.find(numberOfGuests);
    } catch (SQLException e) {
      throw new ReservationException("Failed to find available rooms: " + e.getMessage(), e);
    }
  }
}
